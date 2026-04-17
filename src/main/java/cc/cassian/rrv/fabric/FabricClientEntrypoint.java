//? fabric {
package cc.cassian.rrv.fabric;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.client.extra.FluidItemModel;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.client.PolymerClientIntegration;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class FabricClientEntrypoint implements ClientModInitializer {


    private static FabricClientEntrypoint instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        ReliableRecipeViewerClient.bootstrap();

        ClientNetworkManager.registerPayloads();

        FabricLoader.getInstance().invokeEntrypoints("rrv_client", ReliableRecipeViewerClientPlugin.class, ReliableRecipeViewerClientPlugin::onIntegrationInitialize);

        ReliableRecipeViewerClient.RRV_KEY_MAPPINGS.forEach(KeyMappingHelper::registerKeyMapping);
        ModelLayerRegistry.registerModelLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);

        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "recipe_view"), ReliableRecipeViewer.RECIPE_VIEW_MENU);
        MenuScreens.register(ReliableRecipeViewer.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        ReliableRecipeViewerClient.loadConfigs();

        if (ModCompat.POLYMER) {
            PolymerClientIntegration.onInitializeClient();
        }

        ItemTooltipCallback.EVENT.register((stack, _, _, tooltip) -> ReliableRecipeViewerClient.addNamespaceTooltip(stack, tooltip, false));
    }


    public static FabricClientEntrypoint getInstance() {
        return instance;
    }

}
//?}