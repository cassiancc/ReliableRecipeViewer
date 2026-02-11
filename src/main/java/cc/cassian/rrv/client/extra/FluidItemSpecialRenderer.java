package cc.cassian.rrv.client.extra;

import cc.cassian.rrv.common.extra.FluidStack;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.MapCodec;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import cc.cassian.rrv.common.resolver.RRVClientResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Set;
import org.joml.Vector3f;

/**
 * A special renderer used for rendering the fluid-item in the world
 */
public class FluidItemSpecialRenderer implements SpecialModelRenderer<ItemStack> {

    private final FluidItemModel model;

    private FluidItemSpecialRenderer(FluidItemModel model) {
        this.model = model;
    }


    @Override
    public void render(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay, boolean bl) {
        if (!(stack.getItem() instanceof FluidItem fluidItem))
            return;

        FluidStack fluidStack = FluidStack.fromItemStack(stack);
        Fluid fluid = fluidStack.fluid();

        float renderHeight = Math.max(Math.min((float) fluidStack.amount() / (float) FluidStack.AMOUNT_FULL, 1.0F), 0.1F);


        int color = getColor(fluid);
        Color unmodified = new Color(color);
        color = new Color(unmodified.getRed(), unmodified.getGreen(), unmodified.getBlue(), 255).getRGB();

        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(fluid.defaultFluidState().createLegacyBlock()).particleIcon();
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
        VertexConsumer vertexConsumer = sprite.wrap(ItemRenderer.getFoilBuffer(multiBufferSource, RenderType.entityTranslucent(sprite.atlasLocation()), itemDisplayContext == ItemDisplayContext.GUI, bl));
        Matrix4f matrix4f = poseStack.last().pose();
        vertexConsumer.addVertex(matrix4f, 0, 0, 0).setUv(u0, v0).setOverlay(overlay).setLight(light).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(matrix4f, 0, renderHeight, 0).setUv(u0, v0 + height).setOverlay(overlay).setLight(light).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(matrix4f, 1.0F, renderHeight, 0).setUv(u0 + width, v0 + height).setOverlay(overlay).setLight(light).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(matrix4f, 1.0F, 0, 0).setUv(u0 + width, v0).setOverlay(overlay).setLight(light).setColor(color).setNormal(0.0F, 0.0F, 1.0F);
        poseStack.popPose();
    }

    private static int getColor(Fluid fluid) {
        return fluid == Fluids.WATER ? Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS).value().getWaterColor() : -1;
    }

    @Override
    public void getExtents(
            //? if >1.21.10 {
            /*Consumer<Vector3fc>
            *///?} else {
            Set<Vector3f>
            //?}
             consumer) {

    }

    @Override
    public @Nullable ItemStack extractArgument(ItemStack itemStack) {
        return itemStack;
    }


    public record Unbaked() implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);


        @Override
        public @Nullable SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return new FluidItemSpecialRenderer(new FluidItemModel(modelSet.bakeLayer(ReliableRecipeViewerClient.FLUID_ITEM_MODEL_LAYER)));
        }

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
