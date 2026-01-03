package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.payload.ServerboundRequestEivUpdate;
import de.crafty.eiv.common.recipe.cache.LowEndRecipeCache;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class ClientRecipeManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecipeManager");

    public static final ClientRecipeManager INSTANCE = new ClientRecipeManager();

    private volatile LinkedList<Runnable> queuedRecipeTasks = new LinkedList<>();

    private volatile Status status;

    private ClientRecipeManager() {
        this.status = new Status("EIV - ", 20 * 60 * 5);
    }

    public Status status() {
        return this.status;
    }

    public void queueTask(Runnable runnable) {
        this.queuedRecipeTasks.add(runnable);
    }

    public void runTasks(){
        CompletableFuture.runAsync(() -> {
            this.queuedRecipeTasks.forEach(Runnable::run);
            this.queuedRecipeTasks.clear();
        }).thenRun(() -> LOGGER.info("All recipe updates finished"));

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

        }, "EIV-Network-Timeout-Handler Thread").start();
    }

    public void processRecipes() {

            this.status.setStatusStep("Processing Recipes");
            this.status.setStatusProgress("0%");

            boolean success = LowEndRecipeCache.INSTANCE.processRecipes();

            LowEndRecipeCache.INSTANCE.clear();


            if (!success)
                LOGGER.error("Something went wrong while processing recipes, there might be some strange appearances");

            this.status.setIdle(true);
    }

    public void requestServerEivData() {
        //TODO only send when not caching
        if (this.status.isIdle())
            CommonEIV.networkManager().sendPacketToServer(new ServerboundRequestEivUpdate());

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
