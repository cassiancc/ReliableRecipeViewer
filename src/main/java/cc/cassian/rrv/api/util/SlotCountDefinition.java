package cc.cassian.rrv.api.util;

import cc.cassian.rrv.common.recipe.inventory.RecipeSlot;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;

import java.util.function.Function;

public class SlotCountDefinition extends RecipeViewMenu.SlotDefinition {
	int slotCount = 0;

	public SlotCountDefinition() {
		super(null);
	}

	public void addItemSlot(int slotId, int x, int y) {
		if (slotId>=slotCount) {
			slotCount = slotId+1;
		}
	}

	public void addItemSlot(int slotId, int x, int y, Function<RecipeSlot.Properties, RecipeSlot> recipeSlot) {
		if (slotId>=slotCount) {
			slotCount = slotId+1;
		}
	}

	public int getSlotCount() {
		return slotCount;
	}
}
