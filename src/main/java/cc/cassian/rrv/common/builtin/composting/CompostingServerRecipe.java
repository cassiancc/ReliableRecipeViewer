package cc.cassian.rrv.common.builtin.composting;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class CompostingServerRecipe implements ReliableServerRecipe {

	public static final ReliableServerRecipeType<CompostingServerRecipe> TYPE = ReliableServerRecipeType.register(
			Identifier.withDefaultNamespace("composting"),
			() -> new CompostingServerRecipe(null, 0)
	);

	private Item compostedItem;
	private float layers;

	public CompostingServerRecipe(Item compostedItem, float layers) {
		this.compostedItem = compostedItem;
		this.layers = layers;
	}

	public Item getCompostedItem() {
		return this.compostedItem;
	}

	public float getLayers() {
		return this.layers;
	}

	@Override
	public void writeToTag(CompoundTag tag) {

		tag.putString("item", TagUtil.itemToString(this.compostedItem));
		tag.putFloat("layers", this.layers);

	}

	@Override
	public void loadFromTag(CompoundTag tag) {

		this.compostedItem = TagUtil.itemFromString(tag.getStringOr("item", ""));
		this.layers = tag.getFloatOr("layers", -1);
	}

	@Override
	public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
		return TYPE;
	}
}