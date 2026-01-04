//? fabric {
package de.crafty.eiv.fabric;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
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

public class FabricEIVClient implements ClientModInitializer {


    private static FabricEIVClient instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        CommonEIVClient.boostrap();

        //? >26 {
        /*CommonEIVClient.EIV_KEY_MAPPINGS.forEach(KeyMappingHelper::registerKeyMapping);
        ModelLayerRegistry.registerModelLayer(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
        *///?} else {
        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(KeyBindingHelper::registerKeyBinding);
        EntityModelLayerRegistry.registerModelLayer(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
        //?}

        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(CommonEIV.MODID, "recipe_view"), CommonEIVClient.RECIPE_VIEW_MENU);
        MenuScreens.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);

        CommonEIVClient.loadConfigs();
    }


    public static FabricEIVClient getInstance() {
        return instance;
    }

}
//?}