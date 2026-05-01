package cc.cassian.rrv.common.recipe.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class RecipeSlot extends Slot {
	public RecipeSlot(Container viewContainer, int index, int x, int y) {
		super(viewContainer, index, x, y);
	}

	@Override
	public boolean isFake() {
		return true;
	}
}
