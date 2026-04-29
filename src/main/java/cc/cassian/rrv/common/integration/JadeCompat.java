package cc.cassian.rrv.common.integration;

import snownee.jade.Jade;

public class JadeCompat {
	public static boolean modNamespaceEnabled() {
		return Jade.config().general().showItemModNameTooltip();
	}
}
