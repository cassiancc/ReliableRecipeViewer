package cc.cassian.rrv.fabric;

import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.ApiStatus;

/// Separated out from standard Fabric classes so it can be reused for Launchpad.
@ApiStatus.Internal
public class FabricUtil {

	/// Initialize entrypoints from `fabric.mod.json` files.
	public static void initialize() {
		for (EntrypointContainer<ReliableRecipeViewerPlugin> container : FabricLoader.getInstance().getEntrypointContainers("rrv", ReliableRecipeViewerPlugin.class)) {
			RrvUtil.initializeEntrypoint(container.getProvider().getMetadata().getId(), container.getEntrypoint());
		}
	}

	/// Synchronize recipe serializer through the Fabric Recipe API.
	public static void synchronizeRecipeType(RecipeSerializer<?> serializer) {
		RecipeSynchronization.synchronizeRecipeSerializer(serializer);
	}
}
