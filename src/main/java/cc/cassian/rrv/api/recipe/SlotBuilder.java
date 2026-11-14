package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;

import java.util.function.Supplier;

public record SlotBuilder(SlotPlacementDefinition slotPlacement, int slotId) {
	/// Bind contents to the slot.
	///
	/// @return this builder for chaining
	public SlotBuilder bindSlot(SlotContent slotContent) {
		if (slotContent == null) return this;
		slotPlacement.getContents().put(slotId, slotContent);
		return this;
	}

	/// Dependent slots will change their contents based on a provided index.
	///
	/// @return this builder for chaining
	public SlotBuilder bindDependentSlot(Supplier<Integer> dependentIndex) {
		slotPlacement.getContentDependencies().put(slotId, dependentIndex);
		return this;
	}

	/// Optional slots are only rendered if they contain an ItemStack.
	///
	/// @param optionalSlotRenderer The renderer used for this slot
	/// @return this builder for chaining
	public SlotBuilder bindOptionalSlot(RecipeViewMenu.OptionalSlotRenderer optionalSlotRenderer) {
		slotPlacement.getOptionalSlotRenderers().put(slotId, optionalSlotRenderer);
		return this;
	}

	public SlotBuilder addAdditionalStackModifier(RecipeViewMenu.AdditionalStackModifier tooltipProvider) {
		slotPlacement.getAdditionalTooltips().put(slotId, tooltipProvider);
		return this;
	}

	public SlotBuilder addStackValidator(RecipeViewMenu.StackValidator stackValidator) {
		slotPlacement.getStackValidators().put(slotId, stackValidator);
		return this;
	}
}
