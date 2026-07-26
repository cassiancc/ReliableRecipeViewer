package cc.cassian.rrv.common.extra;

import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.item.FluidItem;
//? fabric
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/// A representation of a [FluidItem] including its fluid type, data components, and stored fluid amount in millibuckets
public record FluidStack(Fluid fluid, int amount, DataComponentPatch patch) {

    /// One bucket's worth of fluid.
    public static final int AMOUNT_FULL = 1000;
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0, DataComponentPatch.EMPTY);

    public FluidStack(final Fluid fluid) {
        this(fluid, AMOUNT_FULL, DataComponentPatch.EMPTY);
    }

    public FluidStack(final Fluid fluid, int amount) {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    /// The fluid this stack holds
    @Override
    public Fluid fluid() {
        return this.fluid;
    }

    /// @return The amount of fluid this stack holds in millibuckets
    @Override
    public int amount() {
        return this.amount;
    }

    /// @return The data components associated with this Fluid Stack.
    public DataComponentPatch patch() {
        return this.patch;
    }

    /// Creates a FluidStack from an ItemStack
    /// @param stack An ItemStack representing a fluid
    /// @return A FluidStack.
    public static FluidStack fromItemStack(ItemStack stack) {
        if (!(stack.getItem() instanceof FluidItem fluidItem))
            return FluidStack.EMPTY;

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int amount = FluidStack.AMOUNT_FULL;
        if (tag.contains("fluidAmount"))
            amount = tag.getInt("fluidAmount").orElseGet(() -> FluidStack.AMOUNT_FULL);

        return new FluidStack(fluidItem.getFluid(), amount, stack.getComponentsPatch());
    }


    //? neoforge {
    /*/// Creates an RRV FluidStack from a NeoForge FluidStack
    /// @param stack: A NeoForge Fluid Stack
    /// @return A RRV Fluid Stack
    public static FluidStack fromFluidStack(net.neoforged.neoforge.fluids.FluidStack stack) {
        return new FluidStack(stack.getFluid(), stack.getAmount(), stack.getComponentsPatch());
    }

    /// Creates an RRV FluidStack from a NeoForge FluidStackTemplate
    /// @param stack: A NeoForge Fluid Stack
    /// @return A RRV Fluid Stack
    public static FluidStack fromFluidStack(net.neoforged.neoforge.fluids.FluidStackTemplate stack) {
        return FluidStack.fromFluidStack(stack.create());
    }

    /// Creates a NeoForge FluidStack from an RRV FluidStack
    /// @return A NeoForge Fluid Stack
    public net.neoforged.neoforge.fluids.FluidStack toLoaderFluidStack() {
        return new net.neoforged.neoforge.fluids.FluidStack(this.fluid, this.amount, this.patch);
    }

    /// Creates a NeoForge FluidStack from an RRV FluidStack
    /// @return A NeoForge Fluid Stack
    public net.neoforged.neoforge.fluids.FluidStack toLoaderFluidStack(FluidStack stack) {
        return stack.toLoaderFluidStack();
    }
    *///?} else {
    /// Creates an RRV FluidStack from a Fabric FluidVariant
    /// @param stack: A Fabric Fluid Variant
    /// @return A RRV Fluid Stack
    public static FluidStack fromFluidVariant(FluidVariant stack) {
        return new FluidStack(stack.getFluid(), AMOUNT_FULL, stack.getComponentsPatch());
    }

    /// Creates a Fabric FluidVariant from an RRV FluidStack
    /// @return A Fabric Fluid Variant
    public FluidVariant toFluidVariant() {
        return FluidVariant.of(this.fluid, this.patch);
    }

    /// Creates a Fabric FluidVariant from an RRV FluidStack
    /// @return A Fabric Fluid Variant
    public FluidVariant toFluidVariant(FluidStack stack) {
        return stack.toFluidVariant();
    }
    //?}

    /// Creates an [ItemStack] from this FluidStack.
    public ItemStack createItemStack() {
        Item item = ItemViewRecipes.INSTANCE.itemForFluid(this.fluid);
        if (item == Items.AIR)
            return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(item), 1, patch);
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("fluidAmount", this.amount);
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        return stack;
    }
}
