//? neoforge {
/*package de.crafty.eiv.neoforge;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.command.EivCommand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Optional;

@Mod(CommonEIV.MODID)
public class NeoForgeEIV {

    public NeoForgeEIV(IEventBus eventBus) {
        CommonEIV.LOGGER.info("Hello Minecraft!");

        NeoForge.EVENT_BUS.addListener(this::onCommandRegistry);


        CommonEIV.LOGGER.info("Scanning for integrations...");
        if (FMLLoader.getCurrentOrNull() != null)
            FMLLoader.getCurrent().getLoadingModList().getMods().forEach(modInfo -> {
                Optional<String> optional = modInfo.getConfigElement("eiv");
                if (optional.isPresent()) {
                    CommonEIV.LOGGER.info("Loading integration: {}", optional.get());
                    try {
                        Class<?> clazz = Class.forName(optional.get());
                        IExtendedItemViewIntegration integration = ((IExtendedItemViewIntegration) clazz.getConstructor().newInstance());
                        integration.onIntegrationInitialize();
                        CommonEIV.LOGGER.info("Integration initialized for mod: {}", modInfo.getModId());
                        return;

                    } catch (Exception ignored) {
                    }

                    CommonEIV.LOGGER.error("Failed to load integration: {}", optional.get());
                }
            });
    }

    private void onCommandRegistry(RegisterCommandsEvent event) {
        EivCommand.register(event.getDispatcher());
    }
}
*///?}