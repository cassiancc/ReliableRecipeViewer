package cc.cassian.rrv.common.recipe;

import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.network.payload.ServerboundRequestRrvUpdate;
import cc.cassian.rrv.common.recipe.cache.LowEndRecipeCache;
//? fabric
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
//? neoforge {
/*import cc.cassian.rrv.neoforge.NeoForgeClientEntrypoint;
*///?}
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public class ClientRecipeManager {

	private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecipeManager");

	public static final ClientRecipeManager INSTANCE = new ClientRecipeManager();

	private volatile LinkedList<Runnable> queuedRecipeTasks = new LinkedList<>();

	private volatile Status status;

	private ClientRecipeManager() {
		this.status = new Status("RRV - ", 20 * 60 * 5);
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

	public void requestServerRrvData() {
		//TODO only send when not caching
		if (this.status.isIdle()) {
			if (RrvClientNetworkManager.canSend(ServerboundRequestRrvUpdate.TYPE)) {
				RrvClientNetworkManager.sendPacketToServer(new ServerboundRequestRrvUpdate());
			} else {
				Minecraft.getInstance().player.sendSystemMessage(Component.translatable("recipe_sync.rrv.denied"));
			}
		}

	}

	public RegistryOps<Tag> createSerializationContext() {
		return createSerializationContext(NbtOps.INSTANCE);
	}

	public <T> RegistryOps<T> createSerializationContext(final DynamicOps<T> parent) {
		return Minecraft.getInstance().level.registryAccess().createSerializationContext(parent);
	}

	//? fabric {
    public static SynchronizedRecipes getSynchronizedRecipes() {
        return Minecraft.getInstance().level.recipeAccess().getSynchronizedRecipes();
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> getRecipesForType(RecipeType<T> type) {
        return getSynchronizedRecipes().getAllOfType(type);
    }
    //?} else {
	/*public static RecipeMap getSynchronizedRecipes() {
		return NeoForgeClientEntrypoint.SYNCHRONIZED_RECIPES;
	}

	public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> getRecipesForType(RecipeType<T> type) {
		return getSynchronizedRecipes().byType(type);
	}
	*///?}

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
