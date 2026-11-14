package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.common.recipe.inventory.*;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SlotPlacementDefinition {

	public final HashMap<Integer, SlotPlacement> itemSlots;
	protected final ViewContainer viewContainer;
	protected boolean highlightWithoutContents = true;
	int slots = 0;
	private final HashMap<Integer, SlotContent> contents;

	private final HashMap<Integer, Supplier<Integer>> contentDependencies;
	private final HashMap<Integer, RecipeViewMenu.AdditionalStackModifier> additionalTooltips;
	private final HashMap<Integer, RecipeViewMenu.StackValidator> stackValidators;

	protected final HashMap<Integer, RecipeViewMenu.OptionalSlotRenderer> optionalSlotRenderers;

	public SlotPlacementDefinition(ViewContainer viewContainer) {
		this.viewContainer = viewContainer;
		this.itemSlots = new HashMap<>();

		this.contents = new HashMap<>();
		this.contentDependencies = new HashMap<>();

		this.additionalTooltips = new HashMap<>();
		this.stackValidators = new HashMap<>();

		this.optionalSlotRenderers = new HashMap<>();
	}

	public SlotBuilder addItemSlot(SlotContent content, int x, int y) {
		return addItemSlot(x, y, RecipeSlot::new).bindSlot(content);
	}

	public SlotBuilder addItemSlot(int x, int y) {
		return addItemSlot(x, y, RecipeSlot::new);
	}

	public SlotBuilder addItemSlot(int x, int y, Function<RecipeSlot.Properties, RecipeSlot> recipeSlot) {
		this.itemSlots.put(slots, new SlotPlacement(new RecipeSlot.Properties(viewContainer,slots, x, y, highlightWithoutContents), recipeSlot));
		return new SlotBuilder(this, this.slots++);
	}

	//? if <26.4 {
	/// Hardcoding item slot ids is not recommended and only available to preserve 8.0 compatibility.
	@Deprecated(since = "9.0.0", forRemoval = true)
	public SlotBuilder addItemSlot(int slotId, int x, int y) {
		return addItemSlot(slotId, x, y, RecipeSlot::new);
	}

	/// Hardcoding item slot ids is not recommended and only available to preserve 8.0 compatibility.
	@Deprecated(since = "9.0.0", forRemoval = true)
	public SlotBuilder addItemSlot(int slotId, int x, int y, Function<RecipeSlot.Properties, RecipeSlot> recipeSlot) {
		this.itemSlots.put(slotId, new SlotPlacement(new RecipeSlot.Properties(viewContainer, slotId, x, y, highlightWithoutContents), recipeSlot));
		this.slots = Math.max(this.slots, slotId + 1);
		return new SlotBuilder(this, slotId);
	}
	//?}

	public void setHighlightWithoutContents(boolean highlightWithoutContents) {
		this.highlightWithoutContents = highlightWithoutContents;
	}

	public List<SlotPlacement> getItemSlots() {
		return this.itemSlots.values().stream().toList();
	}

	public HashMap<Integer, Supplier<Integer>> getContentDependencies() {
		return this.contentDependencies;
	}

	public HashMap<Integer, SlotContent> getContents() {
		return this.contents;
	}

	public HashMap<Integer, RecipeViewMenu.OptionalSlotRenderer> getOptionalSlotRenderers() {
		return this.optionalSlotRenderers;
	}

	public SlotContent contentBySlot(int slotId) {
		return this.contents.getOrDefault(slotId, SlotContent.of(List.of()));
	}

	public HashMap<Integer, RecipeViewMenu.AdditionalStackModifier> getAdditionalTooltips() {
		return this.additionalTooltips;
	}

	public HashMap<Integer, RecipeViewMenu.StackValidator> getStackValidators() {
		return this.stackValidators;
	}

	public int getSlotCount() {
		return this.itemSlots.keySet().stream().max(Integer::compareTo).orElse(-1) + 1;
	}
}
