package cc.cassian.rrv.fabric;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;

/// Separated out from standard Fabric classes so it can be reused for Launchpad.
@ApiStatus.Internal
public class FabricClientUtil {

	/// Initialize client entrypoints from `fabric.mod.json` files.
	public static void initializeClient() {
		for (EntrypointContainer<ReliableRecipeViewerClientPlugin> container : FabricLoader.getInstance().getEntrypointContainers("rrv_client", ReliableRecipeViewerClientPlugin.class)) {
			RRVClientUtil.initializeEntrypoint(container.getProvider().getMetadata().getId(), container.getEntrypoint());
		}
	}

	public static void registerFabricRecipeEvents() {
		ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
			Collection<RecipeHolder<?>> newRecipes = new ArrayList<>(recipes.recipes());
			newRecipes.addAll(ReliableRecipeViewerClient.LOCAL_RECIPES.values());
			ReliableRecipeViewerClient.LOCAL_RECIPES = RrvUtil.createRecipeMap(newRecipes);
			ClientRecipeCache.INSTANCE.buildRecipeCache(true);
		});
	}
}
