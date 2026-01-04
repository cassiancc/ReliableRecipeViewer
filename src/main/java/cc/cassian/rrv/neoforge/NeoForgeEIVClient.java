//? neoforge {
/*package cc.cassian.rrv.neoforge;

import cc.cassian.rrv.common.CommonRRV;
import cc.cassian.rrv.common.CommonRRVClient;
import cc.cassian.rrv.common.extra.FluidItemModel;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
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

@EventBusSubscriber(modid = CommonRRV.MODID, value = Dist.CLIENT)
public class NeoForgeRRVClient {


    @SubscribeEvent
    public static void onMenuRegistry(RegisterEvent event) {
        event.register(Registries.MENU, menuTypeRegisterHelper -> {
            menuTypeRegisterHelper.register(Identifier.fromNamespaceAndPath(CommonRRV.MODID, "recipe_view"), CommonRRVClient.RECIPE_VIEW_MENU);
        });
    }

    @SubscribeEvent
    public static void onMenuScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(CommonRRVClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);
    }

    @SubscribeEvent
    public static void onModelLayerRegistry(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CommonRRVClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
    }

    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        CommonRRVClient.boostrap();
        CommonRRVClient.loadConfigs();
    }

    @SubscribeEvent
    public static void onKeyMappingRegistry(RegisterKeyMappingsEvent event) {
        CommonRRVClient.RRV_KEY_MAPPINGS.forEach(event::register);
    }


}
*///?}