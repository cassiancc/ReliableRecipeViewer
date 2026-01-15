package cc.cassian.rrv.common;

import cc.cassian.rrv.common.network.RrvNetworkManager;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReliableRecipeViewer {

    public static final String MOD_ID = "rrv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Reliable Recipe Viewer");

    public static final String CONFIG_PATH = "config/rrv/";

    public static RrvNetworkManager networkManager(){
        return RrvNetworkManager.INSTANCE;
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}
