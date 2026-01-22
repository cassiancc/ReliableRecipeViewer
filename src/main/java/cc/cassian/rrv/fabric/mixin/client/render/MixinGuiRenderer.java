//? fabric {
package cc.cassian.rrv.fabric.mixin.client.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import cc.cassian.rrv.common.rendering.IRrvWrappedRenderState;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Technically, we don't need this mixin for fabric since Fabric implements their own solution by default,
 * but we're prepared for possible changes
 * @param <T>
 */
@Mixin(GuiRenderer.class)
public abstract class MixinGuiRenderer<T extends PictureInPictureRenderState> implements AutoCloseable {


    @Shadow
    @Final
    private Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;

    @Shadow
    @Final
    private GuiRenderState renderState;


    @Unique
    private Object2ObjectMap<T, PictureInPictureRenderer<T>> rrv$renderersLastFrame = new Object2ObjectOpenHashMap<>();

    @Unique
    private Object2ObjectMap<T, PictureInPictureRenderer<T>> rrv$renderersThisFrame = new Object2ObjectOpenHashMap<>();

    @Inject(method = "preparePictureInPictureState", at = @At("HEAD"), cancellable = true)
    private void useFreshRenderers(T renderState, int guiScale, CallbackInfo ci) {

        PictureInPictureRenderer<T> renderer = (PictureInPictureRenderer<T>) this.pictureInPictureRenderers.get(renderState.getClass());

        if (!(renderer instanceof GuiEntityRenderer) || !(renderState instanceof GuiEntityRenderState guiEntityRenderState))
            return;

        if(!(guiEntityRenderState.renderState() instanceof IRrvWrappedRenderState wrappedRenderState) || !wrappedRenderState.rrv$isMultiRenderingEnabled())
            return;

        if (this.rrv$renderersLastFrame.containsKey(renderState))
            this.rrv$renderersThisFrame.put(renderState, this.rrv$renderersLastFrame.remove(renderState));
        else
            this.rrv$renderersThisFrame.put(renderState, (PictureInPictureRenderer<T>) new GuiEntityRenderer(Minecraft.getInstance().renderBuffers().bufferSource(), Minecraft.getInstance().getEntityRenderDispatcher()));

        this.rrv$renderersThisFrame.get(renderState).prepare(renderState, this.renderState, guiScale);
        ci.cancel();
    }


    @Inject(method = "render", at = @At("RETURN"))
    private void clearUnused(GpuBufferSlice p_406940_, CallbackInfo ci){
        this.rrv$renderersLastFrame.values().forEach(PictureInPictureRenderer::close);
        this.rrv$renderersLastFrame.clear();

        Object2ObjectMap<T,  PictureInPictureRenderer<T>> lastFrameCache = this.rrv$renderersLastFrame;
        this.rrv$renderersLastFrame = this.rrv$renderersThisFrame;
        this.rrv$renderersThisFrame = lastFrameCache;

    }

    @Inject(method = "close", at = @At("RETURN"))
    private void closeRenderers(CallbackInfo ci) {
        this.rrv$renderersLastFrame.values().forEach(PictureInPictureRenderer::close);
        this.rrv$renderersThisFrame.values().forEach(PictureInPictureRenderer::close);
    }
}
//?}