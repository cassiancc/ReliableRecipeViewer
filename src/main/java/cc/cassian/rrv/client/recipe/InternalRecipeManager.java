package cc.cassian.rrv.client.recipe;

import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.network.payload.ServerboundRequestRrvUpdate;
import cc.cassian.rrv.common.recipe.cache.LowEndRecipeCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

/// Handles synchronization between legacy Server Recipes and Client Recipes.
@ApiStatus.Internal
public class InternalRecipeManager {

    private InternalRecipeManager() {
        this.status = new InternalRecipeManager.Status("RRV - ", 20 * 60 * 5);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecipeManager");
    public static final InternalRecipeManager INSTANCE = new InternalRecipeManager();
    private volatile InternalRecipeManager.Status status;

    private volatile LinkedList<Runnable> queuedRecipeTasks = new LinkedList<>();

    public void queueTask(Runnable runnable) {
        this.queuedRecipeTasks.add(runnable);
    }

    public void runTasks(){
        CompletableFuture.runAsync(() -> {
            this.queuedRecipeTasks.forEach(Runnable::run);
            this.queuedRecipeTasks.clear();
        }).thenRun(() -> LOGGER.info("RRV: All recipe updates finished"));

    }

    public void startUpdate() {
        if (!this.status().isIdle())
            return;

        this.status().setIdle(false);
        this.status().setUpdateStartTimestamp();

        new Thread(() -> {

            while (!this.status().isIdle()) {
                //Cleanup on timeout
                if (this.status().networkTimeout()) {
                    this.status().setIdle(true);
                    LowEndRecipeCache.INSTANCE.clear();
                    this.queuedRecipeTasks.clear();
                    return;
                }
            }

        }, "RRV-Network-Timeout-Handler Thread").start();
    }

    public InternalRecipeManager.Status status() {
        return status;
    }

    public void requestServerRrvData() {
        //TODO only send when not caching
        if (this.status.isIdle()) {
            if (ClientNetworkManager.canSend(ServerboundRequestRrvUpdate.TYPE)) {
                ClientNetworkManager.sendPacketToServer(new ServerboundRequestRrvUpdate());
            } else {
                Minecraft.getInstance().player.sendSystemMessage(Component.translatable("recipe_sync.rrv.denied"));
            }
        }
    }

    public void processRecipes() {

        this.status.setStatusStep("Processing Recipes");
        this.status.setStatusProgress("0%");

        boolean success = LowEndRecipeCache.INSTANCE.processRecipes();

        LowEndRecipeCache.INSTANCE.clear();
        Configs.CATEGORIES.addNewCategories();


        if (!success)
            LOGGER.error("RRV: Something went wrong while processing recipes, there might be some strange appearances");

        this.status.setIdle(true);
    }


    public static class Status {

        final String prefix;
        String statusStep, statusProgress;
        boolean idle;
        long updateStartTimestamp, networkTimeout;

        Status(String prefix, long networkTimeout) {
            this.prefix = prefix;
            this.statusStep = "";
            this.statusProgress = "";
            this.idle = true;

            this.updateStartTimestamp = -1;
            this.networkTimeout = networkTimeout;
        }

        public void setIdle(boolean idle) {
            this.idle = idle;
            if (idle)
                this.updateStartTimestamp = -1;

        }

        public boolean isIdle() {
            return this.idle;
        }

        public void setUpdateStartTimestamp() {
            if (Minecraft.getInstance().level != null)
                this.updateStartTimestamp = System.currentTimeMillis() / 50;
        }

        public boolean networkTimeout() {
            if (Minecraft.getInstance().level == null)
                return true;

            return System.currentTimeMillis() / 50 - this.updateStartTimestamp > this.networkTimeout;
        }

        public void setStatusStep(String statusStep) {
            this.statusStep = statusStep;
        }

        public void setStatusProgress(String statusProgress) {
            this.statusProgress = statusProgress;
        }


        public String get() {
            return this.prefix + this.statusStep + ": " + this.statusProgress;
        }
    }
}
