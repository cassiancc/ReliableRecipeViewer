package cc.cassian.rrv.api.util;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public record MobFood(SlotContent slotContent, Optional<RecipeViewMenu.AdditionalStackModifier> lore) {
	public MobFood(SlotContent slotContent) {
		this(slotContent, Optional.empty());
	}
}
