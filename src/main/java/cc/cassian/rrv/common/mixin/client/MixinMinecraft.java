package cc.cassian.rrv.common.mixin.client;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.Minecraft;
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

    @Inject(method = "disconnectFromWorld", at = @At("HEAD"))
    private void cleanup(CallbackInfo ci) {
        ItemViewOverlay.INSTANCE.setWarned(false);
        if (ItemViewOverlay.INSTANCE.getSearchbar() != null) {
            ItemViewOverlay.INSTANCE.getSearchbar().clear();
            ItemViewOverlay.INSTANCE.getSearchbar().setFocused(false);
        }
    }


    //? <26.1 {

    /*@Inject(method = "setScreen", at = @At("HEAD"))
    private void clearBlockings(Screen screen, CallbackInfo ci){
        OverlayManager.INSTANCE.allGuiBlockings().clear();
    }
    *///?}
}
