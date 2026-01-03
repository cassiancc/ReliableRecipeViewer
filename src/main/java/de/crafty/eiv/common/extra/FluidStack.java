package de.crafty.eiv.common.extra;

import de.crafty.eiv.common.recipe.ItemViewRecipes;
import de.crafty.eiv.common.recipe.item.FluidItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * A representation of a fluid-item including its fluid type and stored fluid amount
 */
public record FluidStack(Fluid fluid, int amount) {

    public static final int AMOUNT_FULL = 1000;
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    public FluidStack(final Fluid fluid) {
        this(fluid, FluidStack.AMOUNT_FULL);
    }

    /**
     * The fluid this stack holds
     */
    @Override
    public Fluid fluid() {
        return this.fluid;
    }

    /**
     * @return The amount of fluid this stack holds
     */
    @Override
    public int amount() {
        return this.amount;
    }

    /**
     * Creates a FluidStack from an ItemStack
     * @param stack
     * @return
     */
    public static FluidStack fromItemStack(ItemStack stack) {
        if (!(stack.getItem() instanceof FluidItem fluidItem))
            return FluidStack.EMPTY;

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int amount = FluidStack.AMOUNT_FULL;
        if (tag.contains("fluidAmount"))
            amount = tag.getInt("fluidAmount").orElseGet(() -> FluidStack.AMOUNT_FULL);

        return new FluidStack(fluidItem.getFluid(), amount);
    }

    /**
     * Creates an ItemStack from this FluidStack
     */
    public ItemStack createItemStack() {
        Item item = ItemViewRecipes.INSTANCE.itemForFluid(this.fluid);
        if (item == Items.AIR)
            return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(item);
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("fluidAmount", this.amount);
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        return stack;
    }
}
