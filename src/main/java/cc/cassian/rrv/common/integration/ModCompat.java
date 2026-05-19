package cc.cassian.rrv.common.integration;

import cc.cassian.rrv.common.Platform;

public class ModCompat {
	public static final boolean POLYDEX = Platform.INSTANCE.isModLoaded("polydex");
	public static final boolean POLYMER = Platform.INSTANCE.isModLoaded("polymer-core") && Platform.INSTANCE.isModLoaded("polymer-resource-pack") && Platform.INSTANCE.isModLoaded("polymer-registry-sync-manipulator");
	public static final boolean ITEM_DESCRIPTIONS = Platform.INSTANCE.isModLoaded("item_descriptions");
	public static final boolean JADE = Platform.INSTANCE.isModLoaded("jade");
	public static final boolean WTHIT = Platform.INSTANCE.isModLoaded("wthit");

    public static boolean hasModNamespaceModsInstalled() {
        return (ITEM_DESCRIPTIONS && ItemDescriptionsCompat.modNamespaceEnabled()) ||
				(JADE && JadeCompat.modNamespaceEnabled()) || (WTHIT && WTHITCompat.modNamespaceEnabled());
    }
}
