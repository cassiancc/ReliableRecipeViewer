//? neoforge {
/*package cc.cassian.rrv.neoforge;

import cc.cassian.rrv.common.CommonRRV;
import cc.cassian.rrv.common.api.IExtendedItemViewIntegration;
import cc.cassian.rrv.common.command.RrvCommand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Optional;

@Mod(CommonRRV.MODID)
public class NeoForgeRRV {

    public NeoForgeRRV(IEventBus eventBus) {
        CommonRRV.LOGGER.info("Hello Minecraft!");

        NeoForge.EVENT_BUS.addListener(this::onCommandRegistry);


        CommonRRV.LOGGER.info("Scanning for integrations...");
        if (FMLLoader.getCurrentOrNull() != null)
            FMLLoader.getCurrent().getLoadingModList().getMods().forEach(modInfo -> {
                Optional<String> optional = modInfo.getConfigElement("rrv");
                if (optional.isPresent()) {
                    CommonRRV.LOGGER.info("Loading integration: {}", optional.get());
                    try {
                        Class<?> clazz = Class.forName(optional.get());
                        IExtendedItemViewIntegration integration = ((IExtendedItemViewIntegration) clazz.getConstructor().newInstance());
                        integration.onIntegrationInitialize();
                        CommonRRV.LOGGER.info("Integration initialized for mod: {}", modInfo.getModId());
                        return;

                    } catch (Exception ignored) {
                    }

                    CommonRRV.LOGGER.error("Failed to load integration: {}", optional.get());
                }
            });
    }

    private void onCommandRegistry(RegisterCommandsEvent event) {
        RrvCommand.register(event.getDispatcher());
    }
}
*///?}