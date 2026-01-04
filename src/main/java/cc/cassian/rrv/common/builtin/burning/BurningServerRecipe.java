package cc.cassian.rrv.common.builtin.burning;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class BurningServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<BurningServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("burning"),
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

        tag.putString("fuel", RrvTagUtil.itemToString(this.fuel));
        tag.putInt("burnTime", this.burnTime);

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.fuel = RrvTagUtil.itemFromString(tag.getStringOr("fuel", ""));
        this.burnTime = tag.getIntOr("burnTime", AbstractFurnaceBlockEntity.BURN_TIME_STANDARD);
    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
