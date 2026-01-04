//? neoforge {
/*package de.crafty.eiv.neoforge;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.extra.FluidItemModel;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = CommonEIV.MODID, value = Dist.CLIENT)
public class NeoForgeEIVClient {


    @SubscribeEvent
    public static void onMenuRegistry(RegisterEvent event) {
        event.register(Registries.MENU, menuTypeRegisterHelper -> {
            menuTypeRegisterHelper.register(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "recipe_view"), CommonEIVClient.RECIPE_VIEW_MENU);
        });
    }

    @SubscribeEvent
    public static void onMenuScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(CommonEIVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);
    }

    @SubscribeEvent
    public static void onModelLayerRegistry(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CommonEIVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
    }

    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        CommonEIVClient.boostrap();
        CommonEIVClient.loadConfigs();
    }

    @SubscribeEvent
    public static void onKeyMappingRegistry(RegisterKeyMappingsEvent event) {
        CommonEIVClient.EIV_KEY_MAPPINGS.forEach(event::register);
    }


}
*///?}