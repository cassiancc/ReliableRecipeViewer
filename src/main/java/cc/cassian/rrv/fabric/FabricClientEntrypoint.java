//? fabric {
package cc.cassian.rrv.fabric;

import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.client.extra.FluidItemModel;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.LocalFallback;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.client.PolymerClientIntegration;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.fabricmc.api.ClientModInitializer;
//~ if >26 'ClientWorldEvents'-> 'ClientLevelEvents' {
//~ if >26 'KeyBinding'-> 'KeyMapping' {
//~ if >26 '.keybinding.'-> '.keymapping.' {
//~ if >26 'AFTER_CLIENT_WORLD_CHANGE'-> 'AFTER_CLIENT_LEVEL_CHANGE' {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ReliableRecipeViewerClient.bootstrap();

        ClientNetworkManager.registerPayloads();

        FabricClientUtil.initializeClient();

        ReliableRecipeViewerClient.RRV_KEY_MAPPINGS.forEach(KeyMappingHelper::registerKeyMapping);
        //~ if >26 '.EntityModelLayerRegistry'-> '.ModelLayerRegistry' {
        net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry.registerModelLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);

        ReliableRecipeViewerClient.loadConfigs();

        if (ModCompat.POLYMER) {
            PolymerClientIntegration.onInitializeClient();
        }

        ItemTooltipCallback.EVENT.register((stack, context, flag, tooltip) -> ReliableRecipeViewerClient.addNamespaceTooltip(stack, tooltip, false));


        ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
            ReliableRecipeViewerClient.LOCAL_RECIPES = RrvUtil.createRecipeMap(recipes.recipes());
            ClientRecipeCache.INSTANCE.buildRecipeCache(true);
            client.execute(()->{
                if (ItemFilters.needsCache() && !Configs.CLIENT_SETTINGS.isJeiPanel()) {
                    ItemFilters.fullStackList();
                }
            });
        });

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, recipes) -> {
            if (Configs.CLIENT_SETTINGS.localFallbackAllowed().equals(LocalFallback.ENABLED))
                ClientRecipeCache.INSTANCE.buildRecipeCache(false);
        });
        //~}
        //~}
        //~}
        //~}
        //~}
    }

}
//?}