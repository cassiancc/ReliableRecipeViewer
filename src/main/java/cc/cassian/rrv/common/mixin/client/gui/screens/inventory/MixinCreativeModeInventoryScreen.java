package cc.cassian.rrv.common.mixin.client.gui.screens.inventory;

import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
//? if >26.2 {
/*import com.mojang.blaze3d.platform.InputConstants;
*///?} else {
import org.lwjgl.glfw.GLFW;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class MixinCreativeModeInventoryScreen extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {


    @Shadow
    private static CreativeModeTab selectedTab;

    public MixinCreativeModeInventoryScreen(CreativeModeInventoryScreen.ItemPickerMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void injectSearchBar$0(CharacterEvent characterEvent, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box && box.charTyped(characterEvent))
            cir.setReturnValue(true);

    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectSearchBar$1(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (this.getFocused() != null && this.getFocused().isFocused() && this.getFocused() instanceof EditBox box) {

            //We don't want to affect other mods compat
            if(OverlayManager.INSTANCE.isTextWidgetFocused()) {
                box.keyPressed(keyEvent);

                //~ if >26.2 'GLFW.GLFW_KEY'->'InputConstants.KEY' {
                if ((keyEvent.key() != GLFW.GLFW_KEY_ESCAPE && keyEvent.key() != GLFW.GLFW_KEY_TAB))
                    cir.setReturnValue(true);
                //~}
            }

        }
        else if (selectedTab.getType() != CreativeModeTab.Type.SEARCH && OverlayManager.INSTANCE.keyPressed(keyEvent))
            cir.setReturnValue(true);
    }
}
