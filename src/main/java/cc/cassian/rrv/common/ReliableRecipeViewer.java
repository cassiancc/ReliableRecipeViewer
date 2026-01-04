package cc.cassian.rrv.common;

import cc.cassian.rrv.common.network.RrvNetworkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReliableRecipeViewer {

    public static final String MOD_ID = "rrv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Extended ItemView");

    public static final String CONFIG_PATH = "config/rrv/";

    public static RrvNetworkManager networkManager(){
        return RrvNetworkManager.INSTANCE;
    }

}
