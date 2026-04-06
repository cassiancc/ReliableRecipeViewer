package cc.cassian.rrv.common.mixin.client.gui.screens.inventory;

import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreen.class)
public abstract class MixinAnvilScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

    protected MixinAnvilScreen(Component title) {
        super(title);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {

        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box) {
            box.keyPressed(keyEvent);

            if (!keyEvent.isEscape() && !keyEvent.isCycleFocus())
                cir.setReturnValue(true);
		}
    }
}
