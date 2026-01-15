package cc.cassian.rrv.common.builtin.tag;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TagClientRecipe implements ReliableClientRecipe {

	private final TagKey<Item> tagKey;
	private final SlotContent tagKeyContent;
	private final List<SlotContent> items;

	public TagClientRecipe(TagServerRecipe serverRecipe) {
		this.tagKey = serverRecipe.getTagKey();

		List<SlotContent> drops = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).forEach(itemHolder -> {
			if (!ItemView.isExcludedItem(itemHolder)) {
				drops.add(SlotContent.of(itemHolder.value()));
			}
		});


		this.items = drops;
		this.tagKeyContent = items.getFirst().bindItemTag(tagKey);
	}

	public TagKey<?> getTagKey() {
		return this.tagKey;
	}

	@Override
	public ReliableClientRecipeType getViewType() {
		return TagClientRecipeType.INSTANCE;
	}

	@Override
	public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
		slotFillContext.bindSlot(0, this.tagKeyContent);
		for (int i = 0; i < Math.min(this.getViewType().getSlotCount()-1, this.items.size()); i++) {
			if (i < 9)
				slotFillContext.bindSlot(i+1, this.items.get(i));
			else
				slotFillContext.bindOptionalSlot(i+1, this.items.get(i), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
		}

	}

	@Override
	public List<SlotContent> getIngredients() {
		return List.of(SlotContent.of(this.tagKey));
	}

	@Override
	public List<SlotContent> getResults() {
		return List.of(SlotContent.of(this.tagKey));
	}

}
