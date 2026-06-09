//? neoforge {
/*package cc.cassian.rrv.neoforge;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.client.extra.FluidItemModel;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.LocalFallback;
import cc.cassian.rrv.common.gui.ClientConfigScreen;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;

@EventBusSubscriber(modid = ReliableRecipeViewer.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEntrypoint {

    @SubscribeEvent
	private static void setupIntegrations(FMLClientSetupEvent event) {
        ReliableRecipeViewer.LOGGER.info("RRV: Scanning for client integrations...");
		ModList.get().getMods().forEach(modInfo -> {
			Optional<String> optional = modInfo.getConfig().getConfigElement("rrv_client");
			if (optional.isPresent()) {
				ReliableRecipeViewer.LOGGER.info("RRV: Loading client integration: {}", optional.get());
				try {
					Class<?> clazz = Class.forName(optional.get());
					ReliableRecipeViewerClientPlugin integration = ((ReliableRecipeViewerClientPlugin) clazz.getConstructor().newInstance());
					integration.onIntegrationInitialize();
					ReliableRecipeViewer.LOGGER.info("RRV: Client integration initialized for mod: {}", modInfo.getModId());
					return;

				} catch (Exception ignored) {
				}

				ReliableRecipeViewer.LOGGER.error("RRV: Failed to load client integration: {}", optional.get());
			}
		});
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ()-> (mod, screen) -> new ClientConfigScreen(screen));
    }

    @SubscribeEvent
    public static void onMenuRegistry(RegisterEvent event) {
        event.register(Registries.MENU, menuTypeRegisterHelper -> {
            menuTypeRegisterHelper.register(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "recipe_view"), ReliableRecipeViewer.RECIPE_VIEW_MENU);
        });
    }

    @SubscribeEvent
    public static void onMenuScreenRegistry(RegisterMenuScreensEvent event) {
        event.register(ReliableRecipeViewer.RECIPE_VIEW_MENU, RecipeViewScreen::new);
    }

    @SubscribeEvent
    public static void onModelLayerRegistry(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER, FluidItemModel::createFluidLayer);
    }

    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        ReliableRecipeViewerClient.bootstrap();
        ReliableRecipeViewerClient.loadConfigs();
    }

    @SubscribeEvent
    public static void onKeyMappingRegistry(RegisterKeyMappingsEvent event) {
        ReliableRecipeViewerClient.RRV_KEY_MAPPINGS.forEach(event::register);
    }

    @SubscribeEvent
    public static void onPayloadRegistry(RegisterClientPayloadHandlersEvent event) {
        ClientNetworkManager.registerPayloads(event);
    }

    @SubscribeEvent
    public static void receiveRecipes(RecipesReceivedEvent event) {
        ReliableRecipeViewerClient.LOCAL_RECIPES = event.getRecipeMap();
		if (!event.getRecipeTypes().isEmpty())
			ClientRecipeCache.INSTANCE.buildRecipeCache(true);
		else if (Configs.CLIENT_SETTINGS.localFallbackAllowed().equals(LocalFallback.ENABLED)) {
			ClientRecipeCache.INSTANCE.buildRecipeCache(false);
		}
    }

    @SubscribeEvent
    public static void receiveRecipes(ItemTooltipEvent event) {
        ReliableRecipeViewerClient.addNamespaceTooltip(event.getItemStack(), event.getToolTip(), false);
    }

}
*///?}