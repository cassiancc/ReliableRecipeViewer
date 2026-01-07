package cc.cassian.rrv.common.recipe.cache;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LowEndRecipeCache {

    public static final LowEndRecipeCache INSTANCE = new LowEndRecipeCache();

    private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecipeManager - LowEndRecipeCache");


    private CacheData cachingData;
    private final List<CacheData> recievedData;
    private int expectedTypes;

    private int expectedStackSensitives;
    private int recievedStackSensitives;

    private LowEndRecipeCache() {
        this.cachingData = CacheData.EMPTY;
        this.recievedData = new ArrayList<>();
    }

    public void clear() {
        this.cachingData = CacheData.EMPTY;
        this.recievedData.clear();
        this.expectedTypes = 0;
        this.expectedStackSensitives = 0;
        this.recievedStackSensitives = 0;
    }


    public void stackSensitiveStartRecieved(int amount) {
        this.expectedStackSensitives = amount;
        ClientRecipeCache.INSTANCE.clearStackSensitives();
        ClientRecipeManager.INSTANCE.status().setStatusStep("Caching Stack-Sensitives");
    }

    public void stackSensitiveRecrrved(ItemView.StackSensitive stackSensitive) {
        this.recievedStackSensitives++;
        ClientRecipeCache.INSTANCE.addStackSensitive(stackSensitive);
        ClientRecipeManager.INSTANCE.status().setStatusProgress(this.recievedStackSensitives + "/" + this.expectedStackSensitives);
    }

    public void stackSensitiveEndRecrrved() {
        if(this.recievedStackSensitives == this.expectedStackSensitives){
            LOGGER.info("RRV: Successfully updated Stack-Sensitives");
            this.recievedStackSensitives = 0;
            this.expectedStackSensitives = 0;
        }
        else
            LOGGER.warn("RRV: Received {} stack-sensitives, but expected {}; There might be some strange behaviour", this.recievedStackSensitives, this.expectedStackSensitives);
    }


    public boolean processRecipes() {
        boolean success = this.recievedData.size() == this.expectedTypes;

        for (int i = 0; i < this.recievedData.size(); i++) {
            CacheData data = this.recievedData.get(i);
            ClientRecipeManager.INSTANCE.status().setStatusStep("Processing Recipes (" + data.type().getId() + ")");
            ClientRecipeCache.INSTANCE.sortModType(data.type());
            ClientRecipeManager.INSTANCE.status().setStatusProgress(Math.round(((i + 1.0F) / this.recievedData.size()) * 100.0F) + "%");
        }
        this.recievedData.clear();
        return success;
    }


    public void cacheStartRecrrved(int expectedTypes) {
        this.expectedTypes = expectedTypes;
    }

    public void startCaching(ReliableServerRecipeType<?> type, int amount) {
        if (this.cachingData != CacheData.EMPTY) {
            LOGGER.error("RRV: Received new update while caching, skipping request...");
            return;
        }

        if (ReliableServerRecipeType.idFromType(type) == null) {
            LOGGER.error("RRV: Received unknown recipe type: {}", type);
        }

        LOGGER.info("RRV: Received recipe update for type: {}, caching {} Recipes...", type.getId(), amount);

        this.cachingData = new CacheData(type, amount, new ArrayList<>());

        ClientRecipeManager.INSTANCE.status().setStatusStep("Caching Recipes (" + type.getId() + ")");
        ClientRecipeManager.INSTANCE.status().setStatusProgress(0 + "/" + amount);
    }

    public void cacheModRecipe(ServerRecipeManager.ServerRecipeEntry entry) {
        if (this.cachingData == CacheData.EMPTY) {
            LOGGER.error("RRV: Received recipe while idling, skipping request...");
            return;
        }

        if (this.cachingData.type() != entry.recipe().getRecipeType()) {
            LOGGER.error("RRV: Received recipe for type: {} while caching type: {}", entry.recipe().getRecipeType().getId(), this.cachingData.type().getId());
            return;
        }

        this.cachingData.recrrved().add(entry);
        ClientRecipeManager.INSTANCE.status().setStatusProgress(this.cachingData.recrrved().size() + "/" + this.cachingData.expectedAmount());
    }

    public void endCaching(ReliableServerRecipeType<?> type) {
        if (this.cachingData == CacheData.EMPTY) {
            LOGGER.error("RRV: Received end-packet while idling => bad request");
            return;
        }

        if (this.cachingData.type() != type) {
            LOGGER.error("RRV: Received caching-end packet for type: {} while caching type: {} => ???", type, this.cachingData.type().getId());
            return;
        }

        if (this.cachingData.finishedSuccessfully()) {
            CacheData cachedCache = this.cachingData;
            this.recievedData.add(cachedCache);

            this.cachingData = CacheData.EMPTY;
            LOGGER.info("RRV: Successfully updated recipes for type: {}", cachedCache.type().getId());
            ClientRecipeCache.INSTANCE.updateType(cachedCache.type(), cachedCache.recrrved());
        } else {
            this.cachingData = CacheData.EMPTY;
            LOGGER.error("RRV: Expected amount of recipes does not match the amount of recipes received => Update failed");
        }

    }


    record CacheData(ReliableServerRecipeType<?> type, int expectedAmount, List<ServerRecipeManager.ServerRecipeEntry> recrrved) {

        static final CacheData EMPTY = null;

        boolean finishedSuccessfully() {
            return this.recrrved.size() == this.expectedAmount;
        }
    }
}
