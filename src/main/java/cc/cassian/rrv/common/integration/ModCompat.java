package cc.cassian.rrv.common.integration;

import cc.cassian.rrv.common.Platform;

public class ModCompat {
	public static final boolean POLYDEX = Platform.INSTANCE.isModLoaded("polydex");
	public static final boolean POLYMER = Platform.INSTANCE.isModLoaded("polymer-common");

    public static boolean hasModNamespaceModsInstalled() {
        return (Platform.INSTANCE.isModLoaded("item_descriptions") && ItemDescriptionsCompat.modNamespaceEnabled()) || (Platform.INSTANCE.isModLoaded("jade")) || (Platform.INSTANCE.isModLoaded("wthit"));
    }
}
