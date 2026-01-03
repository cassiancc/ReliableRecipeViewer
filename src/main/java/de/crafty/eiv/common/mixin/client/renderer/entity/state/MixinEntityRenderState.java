package de.crafty.eiv.common.mixin.client.renderer.entity.state;

import de.crafty.eiv.common.rendering.IEivWrappedRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class MixinEntityRenderState implements IEivWrappedRenderState {


    @Unique
    private boolean eiv$multiRendering = false;

    @Override
    public void extendedItemView$enableMultiRendering() {
        this.eiv$multiRendering = true;
    }

    @Override
    public boolean extendedItemView$isMultiRenderingEnabled() {
        return this.eiv$multiRendering;
    }
}
