//? neoforge {
/*package cc.cassian.rrv.neoforge;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.client.extra.FluidItemModel;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.LocalFallback;
import cc.cassian.rrv.common.gui.ClientConfigScreen;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
//? if >26
import cc.cassian.rrv.fabric.FabricClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@EventBusSubscriber(modid = ReliableRecipeViewer.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEntrypoint {

    @SubscribeEvent
	private static void setupIntegrations(FMLClientSetupEvent event) {
        ReliableRecipeViewer.LOGGER.info("RRV: Scanning for client integrations...");
		ModList.get().getMods().forEach(modInfo -> {
			Optional<String> optional = modInfo.getConfig().getConfigElement("rrv_client");
			if (optional.isPresent()) {
				try {
					Class<?> clazz = Class.forName(optional.get());
					ReliableRecipeViewerClientPlugin integration = ((ReliableRecipeViewerClientPlugin) clazz.getConstructor().newInstance());
					RRVClientUtil.initializeEntrypoint(modInfo.getModId(), integration);
				} catch (Exception ignored) {}
			}
		});
		//? if >26 {
		if (ModCompat.LAUNCHPAD && ModCompat.FABRIC_RECIPE_API) {
			ReliableRecipeViewer.LOGGER.info("Initializing RRV client integration for Fabric mods through Launchpad.");
			FabricClientUtil.initializeClient();
		}
		//?}
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ()-> (mod, screen) -> new ClientConfigScreen(screen));
    }

    @SubscribeEvent
    public static void extractBackground(ScreenEvent.Render.Background event) {
        if (event.getScreen() instanceof RRVExtendedContainerScreen) {
			OverlayManager.INSTANCE.renderAllBackground(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
		}
    }

	@SubscribeEvent
	public static void extractBackground(ScreenEvent.Render.Pre neoEvent) {
		if (neoEvent.getScreen() instanceof RRVExtendedContainerScreen) {
			OverlayManager.EXCLUSION_AREA_EVENTS.forEach(event-> event.addExclusionAreas(neoEvent.getScreen(), OverlayManager.INSTANCE, neoEvent.getPartialTick()));
		}
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
		Collection<RecipeHolder<?>> newRecipes = new ArrayList<>(event.getRecipeMap().values());
		newRecipes.addAll(ReliableRecipeViewerClient.LOCAL_RECIPES.values());
		ReliableRecipeViewerClient.LOCAL_RECIPES = RrvUtil.createRecipeMap(newRecipes);
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