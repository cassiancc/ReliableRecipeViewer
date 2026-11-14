package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;

import java.util.Objects;
import java.util.function.Supplier;

public class SlotBuilder {
	private final SlotPlacementDefinition slotPlacement;
	private final int slotId;

	public SlotBuilder(SlotPlacementDefinition slotPlacement, int slotId) {
		this.slotPlacement = slotPlacement;
		this.slotId = slotId;
	}

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

	public SlotPlacementDefinition slotPlacement() {
		return slotPlacement;
	}

	public int slotId() {
		return slotId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (SlotBuilder) obj;
		return Objects.equals(this.slotPlacement, that.slotPlacement) &&
				this.slotId == that.slotId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(slotPlacement, slotId);
	}

	@Override
	public String toString() {
		return "SlotBuilder[" +
				"slotPlacement=" + slotPlacement + ", " +
				"slotId=" + slotId + ']';
	}

}
