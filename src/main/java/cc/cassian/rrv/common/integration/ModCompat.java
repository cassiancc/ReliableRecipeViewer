package cc.cassian.rrv.common.integration;

import cc.cassian.rrv.common.RRVPlatform;

public class ModCompat {
	public static final boolean POLYDEX = RRVPlatform.INSTANCE.isModLoaded("polydex");
	public static final boolean POLYMER = RRVPlatform.INSTANCE.isModLoaded("polymer-core") && RRVPlatform.INSTANCE.isModLoaded("polymer-resource-pack") && RRVPlatform.INSTANCE.isModLoaded("polymer-registry-sync-manipulator");
	public static final boolean ITEM_DESCRIPTIONS = RRVPlatform.INSTANCE.isModLoaded("item_descriptions");
	public static final boolean JADE = RRVPlatform.INSTANCE.isModLoaded("jade");
	public static final boolean WTHIT = RRVPlatform.INSTANCE.isModLoaded("wthit");
	public static final boolean INVENTORY_ITEM_GROUPS = RRVPlatform.INSTANCE.isModLoaded("inventory_item_groups");

    public static boolean hasModNamespaceModsInstalled() {
        return (ITEM_DESCRIPTIONS && ItemDescriptionsCompat.modNamespaceEnabled()) ||
				(JADE && JadeCompat.modNamespaceEnabled()) || (WTHIT && WTHITCompat.modNamespaceEnabled());
    }
}
