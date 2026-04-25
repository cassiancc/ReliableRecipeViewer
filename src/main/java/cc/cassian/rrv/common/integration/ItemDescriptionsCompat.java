package cc.cassian.rrv.common.integration;

import cc.cassian.item_descriptions.client.ModClient;
import cc.cassian.item_descriptions.client.config.ModConfig;
import cc.cassian.item_descriptions.client.descriptions.ItemDescriptions;
import cc.cassian.item_descriptions.client.helpers.ModHelpers;
import cc.cassian.item_descriptions.client.helpers.ModStyle;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ItemDescriptionsCompat {
	public static boolean modNamespaceEnabled() {
		return ModClient.CONFIG.showModName.value();
	}

    public static void addTagDescription(List<Component> tooltip, String tagTranslation) {
		if (ItemDescriptions.showItemDescriptions()) {
			String loreKey = "tag.%s.description".formatted(tagTranslation);
			if (I18n.exists(loreKey)) {
				tooltip.add(Component.translatable(loreKey).withStyle(ModStyle.ITEM_DESCRIPTIONS));
			}
		}
	}
}
