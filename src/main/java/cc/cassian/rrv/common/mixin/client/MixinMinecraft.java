package cc.cassian.rrv.common.mixin.client;

import cc.cassian.rrv.common.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "close", at = @At("RETURN"))
    private void saveData(CallbackInfo ci) {
        ReliableRecipeViewerClient.saveConfigs();
    }



    @Inject(method = "setScreen", at = @At("HEAD"))
    private void clearBlockings(Screen screen, CallbackInfo ci){
        OverlayManager.INSTANCE.allGuiBlockings().clear();
    }
}
