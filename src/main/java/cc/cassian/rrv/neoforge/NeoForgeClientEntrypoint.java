//? neoforge {
/*package cc.cassian.rrv.neoforge;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.client.extra.FluidItemModel;
import cc.cassian.rrv.common.gui.RrvClientSettingsScreen;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;

@Mod(value = ReliableRecipeViewer.MOD_ID, dist =  Dist.CLIENT)
@EventBusSubscriber(modid = ReliableRecipeViewer.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEntrypoint {

    public NeoForgeClientEntrypoint(IEventBus eventBus) {
        ReliableRecipeViewer.LOGGER.info("RRV: Scanning for client integrations...");
        if (FMLLoader.getCurrentOrNull() != null) {
			FMLLoader.getCurrent().getLoadingModList().getMods().forEach(modInfo -> {
				Optional<String> optional = modInfo.getConfigElement("rrv_client");
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
		}
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ()-> (mod, screen) -> new RrvClientSettingsScreen(screen));

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
        RrvClientNetworkManager.registerPayloads(event);
    }

}
*///?}