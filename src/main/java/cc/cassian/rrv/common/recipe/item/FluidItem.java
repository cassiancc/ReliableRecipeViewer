package cc.cassian.rrv.common.recipe.item;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.extra.FluidStack;
//? fabric {
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
//?} else {
/*import net.neoforged.neoforge.event.EventHooks;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;


import java.util.ArrayList;
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
    public Component getName(ItemStack itemStack) {
        //? fabric
        return FluidVariantAttributes.getName(FluidStack.fromItemStack(itemStack).toFluidVariant());
        //? neoforge
        //return super.getName(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        FluidStack fluidStack = FluidStack.fromItemStack(itemStack);
        ArrayList<Component> tooltip = new ArrayList<>();
        //? fabric {
		FluidVariantRendering.getHandlerOrDefault(getFluid()).appendTooltip(fluidStack.toFluidVariant(), tooltip, tooltipFlag);
        //?} else if <26 {
        /*EventHooks.onFluidTooltip(fluidStack.toLoaderFluidStack(), null, tooltip, tooltipFlag, tooltipContext);
        *///?} else {
        /*EventHooks.onFluidTooltip(fluidStack.toLoaderFluidStack(), tooltipContext.player(), tooltip, tooltipFlag, tooltipContext);
         *///?}
        tooltip.forEach(consumer);
        if (Configs.CLIENT_SETTINGS.isFluidUnitDroplets())
            consumer.accept(Component.translatable("rrv.fluid_droplets.unit", String.format("%,d", fluidStack.amount()*81)).withStyle(ChatFormatting.GRAY));
        else
            consumer.accept(Component.translatable("rrv.fluid.unit", String.format("%,d", fluidStack.amount())).withStyle(ChatFormatting.GRAY));
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
