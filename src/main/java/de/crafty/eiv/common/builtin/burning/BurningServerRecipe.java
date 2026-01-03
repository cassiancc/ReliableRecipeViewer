package de.crafty.eiv.common.builtin.burning;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class BurningServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<BurningServerRecipe> TYPE = EivRecipeType.register(
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

        tag.putString("fuel", EivTagUtil.itemToString(this.fuel));
        tag.putInt("burnTime", this.burnTime);

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.fuel = EivTagUtil.itemFromString(tag.getStringOr("fuel", ""));
        this.burnTime = tag.getIntOr("burnTime", AbstractFurnaceBlockEntity.BURN_TIME_STANDARD);
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
