package cc.cassian.rrv.common.recipe.item;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.extra.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;


import java.util.function.Consumer;

public class FluidItem extends BlockItem {

    private final Fluid fluid;

    public FluidItem(Block block, FluidItemProperties properties) {
        super(block, properties);

        this.fluid = properties.fluid;
    }


    public Fluid getFluid() {
        return this.fluid;
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);

        FluidStack fluidStack = FluidStack.fromItemStack(itemStack);
        consumer.accept(Component.translatable("rrv.fluid.unit", String.valueOf(fluidStack.amount())).withStyle(ChatFormatting.GRAY));
    }




    public static class FluidItemProperties extends Properties {

        private Fluid fluid = Fluids.EMPTY;

        public FluidItemProperties fluid(Fluid fluid) {
            this.fluid = fluid;
            return this;
        }

        public FluidItemProperties setItemId(ResourceKey<Item> id) {
            this.setId(id).useBlockDescriptionPrefix();
            return this;
        }

        @Override
        public Identifier effectiveModel() {
            return Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "fluiditem");
        }

    }
}
