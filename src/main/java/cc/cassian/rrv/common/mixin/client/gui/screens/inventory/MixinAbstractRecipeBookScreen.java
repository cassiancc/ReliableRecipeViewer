package cc.cassian.rrv.common.mixin.client.gui.screens.inventory;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class MixinAbstractRecipeBookScreen<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {

    public MixinAbstractRecipeBookScreen(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "init", at = @At("TAIL"))
    private void injectOverlay$0(CallbackInfo ci) {

        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo((AbstractRecipeBookScreen<T>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                Identifier.withDefaultNamespace("container"),
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));

        OverlayManager.INSTANCE.checkForScreenChange(info);
        OverlayManager.INSTANCE.updateOverlaysAndWidgets();
        this.rrv$updateWidgets();

    }

    @Inject(method = "lambda$initButton$0", at = @At("HEAD"), cancellable = true)
    private void injectButton(CallbackInfo ci) {
        if (Configs.CLIENT_SETTINGS.isRecipeBookButton()) {
            OverlayManager.toggleOverlays();
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$1(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box) {
            box.keyPressed(keyEvent);

            if ((keyEvent.key() != 256 && keyEvent.key() != 258))
                cir.setReturnValue(true);
        }

    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$2(CharacterEvent characterEvent, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box && box.charTyped(characterEvent))
            cir.setReturnValue(true);
    }


    @Unique
    private void rrv$updateWidgets() {
        OverlayManager.INSTANCE.oldWidgets().forEach(eventListener -> {

            if (eventListener.isFocused())
                this.setFocused(null);

            this.removeWidget(eventListener);
        });
        OverlayManager.INSTANCE.oldWidgets().clear();

        OverlayManager.INSTANCE.screenContextMap().forEach((abstractRrvOverlay, screenContext) -> {
            screenContext.renderables().forEach(eventListener -> this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) eventListener));
            screenContext.nonRenderables().forEach(eventListener -> this.addWidget((GuiEventListener & NarratableEntry) eventListener));
        });

        OverlayManager.INSTANCE.setQueuedWidgetUpdate(false);

    }

}
