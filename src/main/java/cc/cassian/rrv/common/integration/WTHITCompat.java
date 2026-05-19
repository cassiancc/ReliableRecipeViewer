package cc.cassian.rrv.common.integration;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;

public class WTHITCompat {
	public static boolean modNamespaceEnabled() {
		return PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ITEM_MOD_NAME);
	}
}
