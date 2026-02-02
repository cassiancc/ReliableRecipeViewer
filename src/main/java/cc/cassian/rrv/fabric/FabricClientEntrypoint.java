//? fabric {
package cc.cassian.rrv.fabric;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.client.extra.FluidItemModel;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.fabricmc.api.ClientModInitializer;
//? >26 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
*///?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import cc.cassian.rrv.common.integration.polymer.client.PolydexClientIntegration;
//?}
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

        RrvClientNetworkManager.registerPayloads();

        FabricLoader.getInstance().invokeEntrypoints("rrv_client", ReliableRecipeViewerClientPlugin.class, ReliableRecipeViewerClientPlugin::onIntegrationInitialize);

        //? >26 {
        /*ReliableRecipeViewerClient.RRV_KEY_MAPPINGS.forEach(KeyMappingHelper::registerKeyMapping);
        ModelLayerRegistry.registerModelLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
        *///?} else {
        ReliableRecipeViewerClient.RRV_KEY_MAPPINGS.forEach(KeyBindingHelper::registerKeyBinding);
        EntityModelLayerRegistry.registerModelLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
        //?}

        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "recipe_view"), ReliableRecipeViewer.RECIPE_VIEW_MENU);
        MenuScreens.register(ReliableRecipeViewer.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        ReliableRecipeViewerClient.loadConfigs();

        //? if <26.1 {
        if (ModCompat.POLYDEX) {
            PolydexClientIntegration.onInitializeClient();
        }
        //?}
    }


    public static FabricClientEntrypoint getInstance() {
        return instance;
    }

}
//?}