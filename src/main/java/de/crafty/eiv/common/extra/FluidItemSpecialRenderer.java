package de.crafty.eiv.common.extra;

import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.MapCodec;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.recipe.item.FluidItem;
import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
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
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.awt.*;
import java.util.Set;
import java.util.function.Consumer;

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


        int color = fluid == Fluids.WATER ? Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS).value().getWaterColor() : -1;
        Color unmodified = new Color(color);
        color = new Color(unmodified.getRed(), unmodified.getGreen(), unmodified.getBlue(), 255).getRGB();

        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(fluid.defaultFluidState().createLegacyBlock()).particleIcon();
        IEivClientResolver.UVInfo uvInfo = CommonEIVClient.resolver().getUVInfo(sprite);

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
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(sprite.atlasLocation()), (pose, vertexConsumer) -> {
            vertexConsumer.addVertex(pose.pose(), 1.0F, 0, 0).setUv(u0 + width, v0).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 1.0F, renderHeight, 0).setUv(u0 + width, v0 + finalHeight).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 0, renderHeight, 0).setUv(u0, v0 + finalHeight).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose.pose(), 0, 0, 0).setUv(u0, v0).setOverlay(j).setLight(i).setColor(finalColor).setNormal(0.0F, 0.0F, 1.0F);
        });

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {

    }

    @Override
    public @Nullable ItemStack extractArgument(ItemStack itemStack) {
        return itemStack;
    }


    public record Unbaked() implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        
        @Override
        public @NotNull SpecialModelRenderer<?> bake(BakingContext bakingContext) {
            return new FluidItemSpecialRenderer(new FluidItemModel(bakingContext.entityModelSet().bakeLayer(CommonEIVClient.FLUID_ITEM_MODEL_LAYER)));
        }

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
