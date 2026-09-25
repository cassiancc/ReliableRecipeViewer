package cc.cassian.rrv.common;

import cc.cassian.rrv.common.config.ServerConfigs;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.network.payload.reload.ClientboundServerConfigPayload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ReliableRecipeViewer {

    public static final String MOD_ID = "rrv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Reliable Recipe Viewer");

    public static final Path CONFIG_PATH = RRVPlatform.INSTANCE.getConfigDirectory().resolve("rrv");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static RrvNetworkManager networkManager(){
        return RrvNetworkManager.INSTANCE;
    }

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void loadServerConfigs() {
        ServerConfigs.SERVER_SETTINGS.load();
    }

    public static void saveServerConfigs(MinecraftServer server) {
        ServerConfigs.SERVER_SETTINGS.save();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ReliableRecipeViewer.networkManager().sendPacket(player, new ClientboundServerConfigPayload(ServerConfigs.SERVER_SETTINGS));
        }
    }

}
