package cc.cassian.rrv.common.mixin.client.gui;

import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.client.recipe.InternalRecipeManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui {


    //? <26.1 {
    /*@Shadow public abstract Font getFont();

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void extractRenderStateRecipeProgress(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Font font = this.getFont();
        String statusMsg = InternalRecipeManager.INSTANCE.status().get();

        if(!InternalRecipeManager.INSTANCE.status().isIdle()) {
			guiGraphics.text(font, statusMsg, guiGraphics.guiWidth() - font.width(statusMsg) - 2, 2, -1);
		}
    }
    *///?}

    //? >26.1 {

    /*@Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void clearBlockings(Screen screen, CallbackInfo ci){
        OverlayManager.INSTANCE.allGuiBlockings().clear();
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void extractRenderStateRecipeProgress(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci, @Local GuiGraphicsExtractor guiGraphics) {
        String statusMsg = InternalRecipeManager.INSTANCE.status().get();

        if(!InternalRecipeManager.INSTANCE.status().isIdle()) {
            Font font = this.minecraft.font;
            guiGraphics.text(font, statusMsg, guiGraphics.guiWidth() - font.width(statusMsg) - 2, 2, -1);
        }
    }
    *///?}
}
