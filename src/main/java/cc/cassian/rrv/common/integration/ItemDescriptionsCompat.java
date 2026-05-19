package cc.cassian.rrv.common.integration;

import cc.cassian.item_descriptions.client.ModClient;
import cc.cassian.item_descriptions.client.descriptions.ItemDescriptions;
import cc.cassian.item_descriptions.client.helpers.ModStyle;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ItemDescriptionsCompat {
	public static boolean modNamespaceEnabled() {
		return ModClient.CONFIG.showModName.value();
	}

    public static void addTagDescription(List<Component> tooltip, String tagTranslation) {
		if (ItemDescriptions.showItemDescriptions()) {
			String loreKey = "tag.%s.description".formatted(tagTranslation);
			if (RrvUtil.has(loreKey)) {
				tooltip.add(Component.translatable(loreKey).withStyle(ModStyle.ITEM_DESCRIPTIONS));
			}
		}
	}
}
