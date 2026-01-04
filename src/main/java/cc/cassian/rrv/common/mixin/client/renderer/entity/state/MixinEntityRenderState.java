package cc.cassian.rrv.common.mixin.client.renderer.entity.state;

import cc.cassian.rrv.common.rendering.IRrvWrappedRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class MixinEntityRenderState implements IRrvWrappedRenderState {


    @Unique
    private boolean rrv$multiRendering = false;

    @Override
    public void extendedItemView$enableMultiRendering() {
        this.rrv$multiRendering = true;
    }

    @Override
    public boolean extendedItemView$isMultiRenderingEnabled() {
        return this.rrv$multiRendering;
    }
}
