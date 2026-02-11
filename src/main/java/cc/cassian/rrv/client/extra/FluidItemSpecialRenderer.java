package cc.cassian.rrv.client.extra;

import cc.cassian.rrv.common.extra.FluidStack;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.MapCodec;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import cc.cassian.rrv.common.resolver.RRVClientResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.awt.*;
import java.util.function.Consumer;

//? if >1.21.10 {
import static net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent;
//?} else {
/*import static net.minecraft.client.renderer.rendertype.RenderType.entityTranslucent;
import java.util.Set;
import org.joml.Vector3f;
*///?}

/**
 * A special renderer used for rendering the fluid-item in the world
 */
public class FluidItemSpecialRenderer implements SpecialModelRenderer<ItemStack> {

    private final FluidItemModel model;

    private FluidItemSpecialRenderer(FluidItemModel model) {
        this.model = model;
    }


    @Override
    public void submit(@Nullable ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int j, boolean bl, int k) {
        if(stack == null)
            return;

        if (!(stack.getItem() instanceof FluidItem))
            return;

        FluidStack fluidStack = FluidStack.fromItemStack(stack);
        Fluid fluid = fluidStack.fluid();

        float renderHeight = Math.max(Math.min((float) fluidStack.amount() / (float) FluidStack.AMOUNT_FULL, 1.0F), 0.1F);


        int color = getColor(fluid);
        Color unmodified = new Color(color);
        color = new Color(unmodified.getRed(), unmodified.getGreen(), unmodified.getBlue(), 255).getRGB();

        //? if >26 && fabric {
        /*TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(fluid.defaultFluidState().createLegacyBlock()).particleMaterial().sprite();
        *///?} else {
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(fluid.defaultFluidState().createLegacyBlock()).particleIcon();
        //?}
        RRVClientResolver.UVInfo uvInfo = ReliableRecipeViewerClient.resolver().getUVInfo(sprite);

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
        int finalColor = color;
        submitNodeCollector.submitCustomGeometry(poseStack, entityTranslucent(sprite.atlasLocation()), (pose, vertexConsumer) -> {
            vertexConsumer.addVertex(pose.pose(), 1.0F, 0, 0).setUv(u0 + width, v0).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 1.0F, renderHeight, 0).setUv(u0 + width, v0 + finalHeight).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 0, renderHeight, 0).setUv(u0, v0 + finalHeight).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 0, 0, 0).setUv(u0, v0).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
        });

        poseStack.popPose();
    }

    private static int getColor(Fluid fluid) {
        return fluid == Fluids.WATER ? Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS).value().getWaterColor() : -1;
    }

    @Override
    public void getExtents(
            //? if >1.21.10 {
            Consumer<Vector3fc>
            //?} else {
            /*Set<Vector3f>
            *///?}
             consumer) {

    }

    @Override
    public @Nullable ItemStack extractArgument(ItemStack itemStack) {
        return itemStack;
    }


    public record Unbaked() implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        
        @Override
        public @NotNull SpecialModelRenderer<?> bake(BakingContext bakingContext) {
            return new FluidItemSpecialRenderer(new FluidItemModel(bakingContext.entityModelSet().bakeLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER)));
        }

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
