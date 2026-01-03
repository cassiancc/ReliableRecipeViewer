package de.crafty.eiv.common.recipe.cache;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.recipe.ClientRecipeCache;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LowEndRecipeCache {

    public static final LowEndRecipeCache INSTANCE = new LowEndRecipeCache();

    private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecipeManager - LowEndRecipeCache");


    private CacheData cachingData;
    private final List<CacheData> receivedData;
    private int expectedTypes;

    private int expectedStackSensitives;
    private int receivedStackSensitives;

    private LowEndRecipeCache() {
        this.cachingData = CacheData.EMPTY;
        this.receivedData = new ArrayList<>();
    }

    public void clear() {
        this.cachingData = CacheData.EMPTY;
        this.receivedData.clear();
        this.expectedTypes = 0;
        this.expectedStackSensitives = 0;
        this.receivedStackSensitives = 0;
    }


    public void stackSensitiveStartReceived(int amount) {
        this.expectedStackSensitives = amount;
        ClientRecipeCache.INSTANCE.clearStackSensitives();
        ClientRecipeManager.INSTANCE.status().setStatusStep("Caching Stack-Sensitives");
    }

    public void stackSensitiveReceived(ItemView.StackSensitive stackSensitive) {
        this.receivedStackSensitives++;
        ClientRecipeCache.INSTANCE.addStackSensitive(stackSensitive);
        ClientRecipeManager.INSTANCE.status().setStatusProgress(this.receivedStackSensitives + "/" + this.expectedStackSensitives);
    }

    public void stackSensitiveEndReceived() {
        if(this.receivedStackSensitives == this.expectedStackSensitives){
            LOGGER.info("Successfully updated Stack-Sensitives");
            this.receivedStackSensitives = 0;
            this.expectedStackSensitives = 0;
        }
        else
            LOGGER.warn("Received {} stack-sensitives, but expected {}; There might be some strange behaviour", this.receivedStackSensitives, this.expectedStackSensitives);
    }


    public boolean processRecipes() {
        boolean success = this.receivedData.size() == this.expectedTypes;

        for (int i = 0; i < this.receivedData.size(); i++) {
            CacheData data = this.receivedData.get(i);
            ClientRecipeManager.INSTANCE.status().setStatusStep("Processing Recipes (" + data.type().getId() + ")");
            ClientRecipeCache.INSTANCE.sortModType(data.type());
            ClientRecipeManager.INSTANCE.status().setStatusProgress(Math.round(((i + 1.0F) / this.receivedData.size()) * 100.0F) + "%");
        }
        this.receivedData.clear();
        return success;
    }


    public void cacheStartReceived(int expectedTypes) {
        this.expectedTypes = expectedTypes;
    }

    public void startCaching(EivRecipeType<?> type, int amount) {
        if (this.cachingData != CacheData.EMPTY) {
            LOGGER.error("Received new update while caching, skipping request...");
            return;
        }

        if (EivRecipeType.idFromType(type) == null) {
            LOGGER.error("Received unknown recipe type: {}", type);
        }

        LOGGER.info("Received recipe update for type: {}, caching {} Recipes...", type.getId(), amount);

        this.cachingData = new CacheData(type, amount, new ArrayList<>());

        ClientRecipeManager.INSTANCE.status().setStatusStep("Caching Recipes (" + type.getId() + ")");
        ClientRecipeManager.INSTANCE.status().setStatusProgress(0 + "/" + amount);
    }

    public void cacheModRecipe(ServerRecipeManager.ServerRecipeEntry entry) {
        if (this.cachingData == CacheData.EMPTY) {
            LOGGER.error("Received recipe while idling, skipping request...");
            return;
        }

        if (this.cachingData.type() != entry.recipe().getRecipeType()) {
            LOGGER.error("Received recipe for type: {} while caching type: {}", entry.recipe().getRecipeType().getId(), this.cachingData.type().getId());
            return;
        }

        this.cachingData.received().add(entry);
        ClientRecipeManager.INSTANCE.status().setStatusProgress(this.cachingData.received().size() + "/" + this.cachingData.expectedAmount());
    }

    public void endCaching(EivRecipeType<?> type) {
        if (this.cachingData == CacheData.EMPTY) {
            LOGGER.error("Received end-packet while idling => bad request");
            return;
        }

        if (this.cachingData.type() != type) {
            LOGGER.error("Received caching-end packet for type: {} while caching type: {} => ???", type, this.cachingData.type().getId());
            return;
        }

        if (this.cachingData.finishedSuccessfully()) {
            CacheData cachedCache = this.cachingData;
            this.receivedData.add(cachedCache);

            this.cachingData = CacheData.EMPTY;
            LOGGER.info("Successfully updated recipes for type: {}", cachedCache.type().getId());
            ClientRecipeCache.INSTANCE.updateType(cachedCache.type(), cachedCache.received());
        } else {
            this.cachingData = CacheData.EMPTY;
            LOGGER.error("Expected amount of recipes does not match the amount of recipes received => Update failed");
        }

    }


    record CacheData(EivRecipeType<?> type, int expectedAmount, List<ServerRecipeManager.ServerRecipeEntry> received) {

        static final CacheData EMPTY = null;

        boolean finishedSuccessfully() {
            return this.received.size() == this.expectedAmount;
        }
    }
}
