package cc.cassian.rrv.common.mixin.client;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.overlay.itemlist.view.SearchBar;
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

    @Inject(method = "disconnectFromWorld", at = @At("RETURN"))
    private void cleanup(CallbackInfo ci) {
        ItemViewOverlay.INSTANCE.getSearchbar().clear();
        ItemViewOverlay.INSTANCE.getSearchbar().setFocused(false);
    }


    //? <26.1 {

    /*@Inject(method = "setScreen", at = @At("HEAD"))
    private void clearBlockings(Screen screen, CallbackInfo ci){
        OverlayManager.INSTANCE.allGuiBlockings().clear();
    }
    *///?}
}
