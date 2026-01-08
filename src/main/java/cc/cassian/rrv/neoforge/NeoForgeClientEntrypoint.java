//? neoforge {
/*package cc.cassian.rrv.neoforge;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.client.RrvClientNetworkManager;
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
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.client.network.registration.ClientNetworkRegistry;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = ReliableRecipeViewer.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEntrypoint {


    @SubscribeEvent
    public static void onMenuRegistry(RegisterEvent event) {
        event.register(Registries.MENU, menuTypeRegisterHelper -> {
            menuTypeRegisterHelper.register(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "recipe_view"), ReliableRecipeViewerClient.RECIPE_VIEW_MENU);
        });
    }

    @SubscribeEvent
    public static void onMenuScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(ReliableRecipeViewerClient.RECIPE_VIEW_MENU, RecipeViewScreen::new);
    }

    @SubscribeEvent
    public static void onModelLayerRegistry(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
    }

    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        ReliableRecipeViewerClient.boostrap();
        ReliableRecipeViewerClient.loadConfigs();
    }

    @SubscribeEvent
    public static void onKeyMappingRegistry(RegisterKeyMappingsEvent event) {
        ReliableRecipeViewerClient.RRV_KEY_MAPPINGS.forEach(event::register);
    }

    @SubscribeEvent
    public static void onPayloadRegistry(RegisterClientPayloadHandlersEvent event) {
        RrvClientNetworkManager.registerPayloads(event);
    }


}
*///?}