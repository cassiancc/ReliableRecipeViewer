package cc.cassian.rrv.common.builtin.burning;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class BurningServerRecipe implements ReliableServerRecipe {

	public static final ReliableServerRecipeType<BurningServerRecipe> TYPE = ReliableServerRecipeType.register(
			Identifier.withDefaultNamespace("burning"),
			() -> new BurningServerRecipe(null, 0)
	);

	private Item fuel;
	private float burnTime;

	public BurningServerRecipe(Item fuel, float burnTime) {
		this.fuel = fuel;
		this.burnTime = burnTime;
	}

	public Item getFuel() {
		return this.fuel;
	}

	public float getBurnTime() {
		return this.burnTime;
	}

	@Override
	public void writeToTag(CompoundTag tag) {

		tag.putString("fuel", TagUtil.itemToString(this.fuel));
		tag.putFloat("burnTime", this.burnTime);

	}

	@Override
	public void loadFromTag(CompoundTag tag) {

		this.fuel = TagUtil.itemFromString(tag.getStringOr("fuel", ""));
		this.burnTime = tag.getFloatOr("burnTime", AbstractFurnaceBlockEntity.BURN_TIME_STANDARD);
	}

	@Override
	public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
		return TYPE;
	}
}