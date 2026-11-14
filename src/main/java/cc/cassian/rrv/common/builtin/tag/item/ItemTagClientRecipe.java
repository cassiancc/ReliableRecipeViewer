package cc.cassian.rrv.common.builtin.tag.item;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.SlotPlacementDefinition;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemTagClientRecipe implements ReliableClientRecipe {

	private final TagKey<Item> tagKey;
	private final SlotContent tagKeyContent;
	private final List<SlotContent> items;
	private final Identifier id;

	public ItemTagClientRecipe(TagKey<Item> serverRecipe) {
		this.id = serverRecipe.location().withPrefix("/item_tag/");
		this.tagKey = serverRecipe;

		List<SlotContent> drops = new ArrayList<>();
		BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).forEach(itemHolder -> {
			if (!ItemView.isExcludedItem(itemHolder)) {
				drops.add(SlotContent.of(itemHolder.value()));
			}
		});


		this.items = drops;
		this.tagKeyContent = SlotContent.of(items.getFirst()).bindItemTag(tagKey);
	}

	public TagKey<?> getTagKey() {
		return this.tagKey;
	}

	@Override
	public ReliableClientRecipeType getType() {
		return ItemTagClientRecipeType.INSTANCE;
	}

	@Override
	public int getPriority() {
		return StackGroupManager.hasGroup(tagKey.location()) ? 0 : 1;
	}

	@Override
	public void addSlots(SlotPlacementDefinition builder) {
		builder.setHighlightWithoutContents(false);

		builder.addItemSlot(tagKeyContent,73,8);
		int index = 0;
		for (int row = 0; row < 6; row++) {
			for (int i = 0; i < 9; i++) {
				if (index >= this.items.size()) {
					break;
				}
				var slot = builder.addItemSlot(this.items.get(index), i * 18 + 1, 45 + row * 18);
				if (index >= 9) {
					slot.bindOptionalSlot(RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
				}
				index++;
			}
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

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public boolean isVisualOnly() {
		return true;
	}
}
