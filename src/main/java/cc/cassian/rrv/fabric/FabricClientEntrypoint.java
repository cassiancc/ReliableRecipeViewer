//? fabric {
package cc.cassian.rrv.fabric;

import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.client.extra.FluidItemModel;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.LocalFallback;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.jei.JeiCompatibilityUtil;
import cc.cassian.rrv.common.integration.polymer.client.PolymerClientIntegration;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
//? if =26.4 {
/*import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
*///?}

import java.util.List;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ReliableRecipeViewerClient.bootstrap();

        ClientNetworkManager.registerPayloads();

        FabricClientUtil.initializeClient();

        ReliableRecipeViewerClient.RRV_KEY_MAPPINGS.forEach(KeyMappingHelper::registerKeyMapping);
        ModelLayerRegistry.registerModelLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);

        ReliableRecipeViewerClient.loadConfigs();

        if (ModCompat.POLYMER) {
            PolymerClientIntegration.onInitializeClient();
        }

        ItemTooltipCallback.EVENT.register((stack, context, flag, tooltip) -> ReliableRecipeViewerClient.addNamespaceTooltip(stack, tooltip, false));


        ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
            ReliableRecipeViewerClient.LOCAL_RECIPES = RrvUtil.createRecipeMap(List.copyOf(recipes.recipes()));
            ClientRecipeCache.INSTANCE.buildRecipeCache(true);
//            client.execute(()->{
//                if (ItemFilters.needsCache() && !Configs.CLIENT_SETTINGS.isJeiPanel()) {
//                    ItemFilters.fullStackList();
//                }
//            });
        });

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, recipes) -> {
            if (Configs.CLIENT_SETTINGS.localFallbackAllowed().equals(LocalFallback.ENABLED))
                ClientRecipeCache.INSTANCE.buildRecipeCache(false);
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (RRVClientUtil.hasOverlay(screen)) {
                ScreenMouseEvents.allowMouseScroll(screen).register((screen1, mouseX, mouseY, horizontalAmount, verticalAmount) -> !OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, horizontalAmount, verticalAmount));
            }
            if (RRVClientUtil.jeiHasOverlay(screen)) {
				ScreenKeyboardEvents.allowKeyPress(screen).register((screen1, event) -> !OverlayManager.INSTANCE.keyPressed(event));
				ScreenMouseEvents.afterMouseClick(screen).register((screen1, event, b) -> !OverlayManager.INSTANCE.mouseClicked(event, b));
            }
            if (screen instanceof RRVExtendedContainerScreen) {
                ScreenEvents.afterBackground(screen).register((screen2, guiGraphics
                       //? if <26.4
                        , mouseX, mouseY, partialTicks
                )->{
                    OverlayManager.INSTANCE.renderAllBackground(guiGraphics
                            //? if <26.4
                            , mouseX, mouseY, partialTicks
                    );
                    if (RRVClientUtil.jeiHasOverlay(screen)) {
                        JeiCompatibilityUtil.extractOverlay(screen, guiGraphics, mouseX, mouseY, partialTicks);
                    }
                });
                ScreenEvents.beforeExtract(screen).register((screen2, guiGraphics
                         //? if <26.4
                        , mouseX, mouseY, partialTicks
                )->{
                    //? if >26.3 {
                    /*var partialTicks = guiGraphics.partialTicks();
                    *///?}
                    OverlayManager.EXCLUSION_AREA_EVENTS.forEach(event-> event.addExclusionAreas(screen2, OverlayManager.INSTANCE, partialTicks));
                });
            }
        });

        //? if >=26.4 {
        /*Registry.register(BuiltInClientRegistries.SPECIAL_MODEL_RENDERER_TYPE, Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "fluiditem"), FluidItemSpecialRenderer.Unbaked.MAP_CODEC);
        *///?}
    }

}
//?}