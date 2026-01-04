//? fabric {
package cc.cassian.rrv.fabric;

import cc.cassian.rrv.common.CommonRRV;
import cc.cassian.rrv.common.CommonRRVClient;
import cc.cassian.rrv.common.extra.FluidItemModel;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.fabricmc.api.ClientModInitializer;
//? >26 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
*///?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
//?}
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class FabricRRVClient implements ClientModInitializer {


    private static FabricRRVClient instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        CommonRRVClient.boostrap();

        //? >26 {
        /*CommonRRVClient.RRV_KEY_MAPPINGS.forEach(KeyMappingHelper::registerKeyMapping);
        ModelLayerRegistry.registerModelLayer(CommonRRVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
        *///?} else {
        CommonRRVClient.RRV_KEY_MAPPINGS.forEach(KeyBindingHelper::registerKeyBinding);
        EntityModelLayerRegistry.registerModelLayer(CommonRRVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
        //?}

        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(CommonRRV.MODID, "recipe_view"), CommonRRVClient.RECIPE_VIEW_MENU);
        MenuScreens.register(CommonRRVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        CommonRRVClient.loadConfigs();
    }


    public static FabricRRVClient getInstance() {
        return instance;
    }

}
//?}