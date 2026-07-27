package cc.cassian.rrv.client.extra;

import cc.cassian.rrv.client.util.UVInfo;
import cc.cassian.rrv.common.extra.FluidStack;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.MapCodec;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.recipe.item.FluidItem;
//? fabric {
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
//?}
import net.minecraft.client.Minecraft;
//? if >26 {
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.FluidModel;
//?}
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

//~ if >26 'entityTranslucent'-> 'itemTranslucent' {
import static net.minecraft.client.renderer.rendertype.RenderTypes.itemTranslucent;

/// A special renderer used for rendering a [FluidItem] in the item index.
public class FluidItemSpecialRenderer implements SpecialModelRenderer<ItemStack> {

    private final FluidItemModel model;

    private FluidItemSpecialRenderer(FluidItemModel model) {
        this.model = model;
    }

    @Override
    //? if >26 {
    public void submit(@Nullable ItemStack stack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
    //?} else {
    /*public void submit(@Nullable ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
    *///?}
        if(stack == null)
            return;

        if (!(stack.getItem() instanceof FluidItem))
            return;

        FluidStack fluidStack = FluidStack.fromItemStack(stack);
        Fluid fluid = fluidStack.fluid();

        float renderHeight = Math.clamp((float) fluidStack.amount() / (float) FluidStack.AMOUNT_FULL, 0.1F, 1.0F);

        TextureAtlasSprite sprite = null;
        //? if >26 {
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
        sprite = fluidModel.stillMaterial().sprite();
        int color = getColor(fluidStack, fluid, fluidModel);
        //?} else {
        /*//? fabric
        sprite = FluidVariantRendering.getSprite(FluidVariant.of(fluid));
        int color = getColor(fluidStack, fluid);
        if (sprite == null) {
            sprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(fluid.defaultFluidState().createLegacyBlock()).particleIcon();
        }
        *///?}
        UVInfo uvInfo = UVInfo.getUVInfo(sprite);

        float u0 = uvInfo.u0();
        float u1 = uvInfo.u1();
        float v0 = uvInfo.v0();
        float v1 = uvInfo.v1();

        float width = (u1 - u0);
        float height = (v1 - v0);

        height *= renderHeight;

        poseStack.pushPose();
        poseStack.scale(1.0F, 1.0F, 1.0F);
        float finalHeight = height;
		submitNodeCollector.submitCustomGeometry(poseStack, itemTranslucent(sprite.atlasLocation()), (pose, vertexConsumer) -> {
            vertexConsumer.addVertex(pose.pose(), 1.0F, 0, 0).setUv(u0 + width, v0).setOverlay(overlayCoords).setLight(lightCoords).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 1.0F, renderHeight, 0).setUv(u0 + width, v0 + finalHeight).setOverlay(overlayCoords).setLight(lightCoords).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 0, renderHeight, 0).setUv(u0, v0 + finalHeight).setOverlay(overlayCoords).setLight(lightCoords).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 0, 0, 0).setUv(u0, v0).setOverlay(overlayCoords).setLight(lightCoords).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
        });
        //~}

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {

    }

    //? if >26 {
    private static int getColor(FluidStack fluidStack, Fluid fluid, FluidModel fluidModel) {
        //? fabric {
        var handler = FluidVariantRendering.getHandler(fluid);
        if (handler != null) {
            return handler.getColor(fluidStack.toFluidVariant(), null, null);
        }
        BlockTintSource blockTintSource = fluidModel.tintSource();
        if (blockTintSource != null) {
            return blockTintSource.color(fluid.defaultFluidState().createLegacyBlock());
        }
        //?} else {
        /*var fluidTintSource = fluidModel.fluidTintSource();
        if (fluidTintSource != null) {
            return fluidTintSource.colorAsStack(fluidStack.toLoaderFluidStack());
        }
        *///?}
		return -1;
	}
    //?} else {
    /*private static int getColor(FluidStack fluidStack, Fluid fluid) {
        //? fabric {
        var handler = FluidVariantRendering.getHandler(fluid);
        if (handler != null) {
            return handler.getColor(fluidStack.toFluidVariant(), null, null);
        }
        return FluidVariantRendering.getColor(FluidVariant.of(fluid));
        //?} else {
        /^return fluid == Fluids.WATER ? Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS).value().getWaterColor() : -1;
        ^///?}
    }
    *///?}


    @Override
    public @Nullable ItemStack extractArgument(ItemStack itemStack) {
        return itemStack;
    }


    public record Unbaked() implements SpecialModelRenderer.Unbaked
            //? if >26
            <ItemStack>
    {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);
        
        @Override
        public SpecialModelRenderer<ItemStack> bake(BakingContext bakingContext) {
            return new FluidItemSpecialRenderer(new FluidItemModel(bakingContext.entityModelSet().bakeLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER)));
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked
                //? if >26
                <ItemStack>
                > type() {
            return MAP_CODEC;
        }
    }
}
