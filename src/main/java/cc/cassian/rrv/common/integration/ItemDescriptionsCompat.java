package cc.cassian.rrv.common.integration;

import cc.cassian.item_descriptions.client.DescriptionKey;
import cc.cassian.item_descriptions.client.ModClient;
import cc.cassian.item_descriptions.client.descriptions.EntityDescriptions;
import cc.cassian.item_descriptions.client.descriptions.ItemDescriptions;
import cc.cassian.item_descriptions.client.helpers.ModHelpers;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class ItemDescriptionsCompat {
	public static boolean modNamespaceEnabled() {
		return ModClient.CONFIG.showModName.value();
	}

    public static void addTagDescription(List<Component> tooltip, String tagTranslation, Component name) {
		if (ItemDescriptions.showItemDescriptions()) {
			String loreKey = "tag.%s.description".formatted(tagTranslation);
			if (RrvUtil.has(loreKey)) {
				tooltip.addAll(ModHelpers.createTooltip(name, loreKey));
			}
		}
	}

	public static void addEntityDescription(List<Component> tooltip, EntityType<?> type, Component entityName) {
		if (EntityDescriptions.showEntityDescriptions()) {
			DescriptionKey loreKey = EntityDescriptions.findLoreKey(type);
			if (loreKey.hasTranslation()) {
				tooltip.addAll(ModHelpers.createTooltip(entityName, loreKey));
			}
		}
	}
}
