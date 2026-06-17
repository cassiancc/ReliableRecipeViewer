package cc.cassian.rrv.common.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RrvGuiRenderHelper {

    public static void renderEntityOnScreen(GuiGraphicsExtractor guiGraphics, LivingEntity livingEntity, int x0, int y0, int x1, int y1, float scale, Vector3f translation, Quaternionf rotation, Quaternionf cameraAngleOverride) {
        //? >26.1 {
        /*livingEntity.setId(1);
        *///?}
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<LivingEntity, EntityRenderState> entityRenderer = (EntityRenderer<LivingEntity, EntityRenderState>) entityRenderDispatcher.getRenderer(livingEntity);

        EntityRenderState entityRenderState = entityRenderer.createRenderState();
        entityRenderer.extractRenderState(livingEntity, entityRenderState, 1.0F);
        IRrvWrappedRenderState wrappedState = (IRrvWrappedRenderState) entityRenderState;
        wrappedState.rrv$enableMultiRendering();

        entityRenderState.lightCoords = 15728880;
        guiGraphics.entity(entityRenderState, scale, translation, rotation, cameraAngleOverride, x0, y0, x1, y1);
    }

}
