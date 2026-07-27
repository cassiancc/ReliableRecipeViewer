//? neoforge {
/*package cc.cassian.rrv.neoforge;

import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.command.RrvCommand;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
//? >26
import cc.cassian.rrv.fabric.FabricUtil;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.ArrayList;
import java.util.Optional;

@Mod(ReliableRecipeViewer.MOD_ID)
@EventBusSubscriber
public class NeoForgeEntrypoint {

    public static final ArrayList<RecipeType<?>> SYNCHRONIZED_RECIPES = new ArrayList<>();

    public NeoForgeEntrypoint(IEventBus eventBus) {
    }

    @SubscribeEvent
    private static void setupIntegrations(FMLCommonSetupEvent event) {
        ReliableRecipeViewer.LOGGER.info("RRV: Scanning for integrations...");
        ModList.get().getMods().forEach(modInfo -> {
            Optional<String> optional = modInfo.getConfig().getConfigElement("rrv");
            if (optional.isPresent()) {
                try {
                    Class<?> clazz = Class.forName(optional.get());
                    ReliableRecipeViewerPlugin integration = ((ReliableRecipeViewerPlugin) clazz.getConstructor().newInstance());
                    RrvUtil.initializeEntrypoint(modInfo.getModId(), integration);
				} catch (Exception ignored) {}
            }
        });
        //? if >26 {
        if (ModCompat.LAUNCHPAD && ModCompat.FABRIC_RECIPE_API) {
            ReliableRecipeViewer.LOGGER.info("Initializing RRV integration for Fabric mods through Launchpad.");
            FabricUtil.initialize();
        }
        //?}
    }

    @SubscribeEvent
    public static void onCommandRegistry(RegisterCommandsEvent event) {
        RrvCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        RrvNetworkManager.INSTANCE.registerPayloads(event);
    }

    @SubscribeEvent
    public static void sendRecipes(OnDatapackSyncEvent event) {
        SYNCHRONIZED_RECIPES.forEach(event::sendRecipes);
    }
}
*///?}