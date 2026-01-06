package cc.cassian.rrv.common.builtin.tag;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TagClientRecipe implements ReliableClientRecipe {

	private final TagKey<Item> tagKey;
	private final List<SlotContent> items;

	public TagClientRecipe(TagServerRecipe serverRecipe) {
		this.tagKey = serverRecipe.getTagKey();

		List<ItemStack> drops = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).iterator().forEachRemaining(itemHolder -> {
			drops.add(new ItemStack(itemHolder));
		});
		List<SlotContent> dropContents = new ArrayList<>();

		for (int i = 0; i < this.getViewType().getSlotCount(); i++) {
			if (drops.size() > i)
				dropContents.add(SlotContent.of(drops.get(i)));
			else
				dropContents.add(SlotContent.of());
		}

		this.items = dropContents;
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
		slotFillContext.bindSlot(0, SlotContent.of(this.tagKey));
		for (int i = 0; i < this.items.size(); i++) {
			if (i < 9)
				slotFillContext.bindSlot(i+1, this.items.get(i));
			else
				slotFillContext.bindOptionalSlot(i+1, this.items.get(i), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
		}

	}

	@Override
	public List<SlotContent> getIngredients() {
		return List.of(SlotContent.of(tagKey));
	}

	@Override
	public List<SlotContent> getResults() {
		return this.items;
	}

}
