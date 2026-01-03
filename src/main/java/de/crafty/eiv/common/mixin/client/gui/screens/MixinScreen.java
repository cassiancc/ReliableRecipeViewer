package de.crafty.eiv.common.mixin.client.gui.screens;

import de.crafty.eiv.common.recipe.ClientRecipeManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractContainerEventHandler implements Renderable {


    @Shadow protected Font font;

    @Inject(method = "render", at = @At("RETURN"))
    private void renderRecipeProgress(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci){
        String statusMsg = ClientRecipeManager.INSTANCE.status().get();

        if(!ClientRecipeManager.INSTANCE.status().isIdle())
            guiGraphics.drawString(this.font, statusMsg, guiGraphics.guiWidth() - this.font.width(statusMsg) - 2, 2, -1);
    }
}
