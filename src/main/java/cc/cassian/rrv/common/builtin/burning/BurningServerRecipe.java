package cc.cassian.rrv.common.builtin.burning;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class BurningServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<BurningServerRecipe> TYPE = ReliableServerRecipeType.register(
            ResourceLocation.withDefaultNamespace("burning"),
            () -> new BurningServerRecipe(null, 0)
    );

    private Item fuel;
    private int burnTime;

    public BurningServerRecipe(Item fuel, int burnTime) {
        this.fuel = fuel;
        this.burnTime = burnTime;
    }

    public Item getFuel() {
        return this.fuel;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putString("fuel", TagUtil.itemToString(this.fuel));
        tag.putInt("burnTime", this.burnTime);

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.fuel = TagUtil.itemFromString(tag.getStringOr("fuel", ""));
        this.burnTime = tag.getIntOr("burnTime", AbstractFurnaceBlockEntity.BURN_TIME_STANDARD);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
