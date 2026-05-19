package cc.cassian.rrv.common.recipe.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class RecipeSlot extends Slot {
	private final boolean highlightWithoutContents;

	public RecipeSlot(Container viewContainer, int index, int x, int y, boolean highlightWithoutContents) {
		super(viewContainer, index, x, y);
		this.highlightWithoutContents = highlightWithoutContents;
	}

	@Override
	public boolean isFake() {
		return true;
	}

	@Override
	public boolean isHighlightable() {
		return this.hasItem() || highlightWithoutContents;
	}
}
