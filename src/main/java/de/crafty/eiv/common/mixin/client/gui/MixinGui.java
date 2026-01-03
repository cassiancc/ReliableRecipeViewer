package de.crafty.eiv.common.mixin.client.gui;

import de.crafty.eiv.common.recipe.ClientRecipeManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui {


    @Shadow public abstract Font getFont();

    @Inject(method = "render", at = @At("RETURN"))
    private void renderRecipeProgress(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Font font = this.getFont();
        String statusMsg = ClientRecipeManager.INSTANCE.status().get();

        if(!ClientRecipeManager.INSTANCE.status().isIdle())
            guiGraphics.drawString(font, statusMsg, guiGraphics.guiWidth() - font.width(statusMsg) - 2, 2, -1);
    }
}
