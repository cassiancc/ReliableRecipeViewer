package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.overlay.AbstractEivOverlay;
import de.crafty.eiv.common.overlay.BlockingGuiComponent;
import de.crafty.eiv.common.overlay.itemlist.bookmark.ItemBookmarkOverlay;
import de.crafty.eiv.common.overlay.OverlayManager;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {


    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageWidth;

    @Shadow
    protected int imageHeight;


    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Shadow
    public abstract T getMenu();

    @Shadow protected abstract void onStopHovering(Slot slot);

    protected MixinAbstractContainerScreen(Component component) {
        super(component);
    }


    @Inject(method = "init", at = @At("TAIL"))
    private void injectOverlay$0(CallbackInfo ci) {

        //In recipe book screens we initalize after the recipe button init
        if ((Object) this instanceof AbstractRecipeBookScreen)
            return;

        AbstractEivOverlay.InventoryPositionInfo info = new AbstractEivOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                Identifier.withDefaultNamespace("container"),
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));

        OverlayManager.INSTANCE.checkForScreenChange(info);
        OverlayManager.INSTANCE.updateOverlaysAndWidgets();
        this.updateWidgets();

    }


    @Inject(method = "renderBackground", at = @At("HEAD"))
    private void injectOverlayBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        OverlayManager.INSTANCE.renderAllBackground(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Inject(method = "renderContents", at = @At("TAIL"))
    private void injectOverlay$1(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (minecraft == null) return;


        AbstractEivOverlay.InventoryPositionInfo info = new AbstractEivOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                Identifier.withDefaultNamespace("container"),
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));

        if (OverlayManager.INSTANCE.checkForScreenChange(info))
            OverlayManager.INSTANCE.updateOverlaysAndWidgets();

        if (OverlayManager.INSTANCE.hasQueuedWidgetUpdate())
            this.updateWidgets();


        OverlayManager.INSTANCE.renderAll(guiGraphics, mouseX, mouseY, partialTicks);

    }


    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
            cir.setReturnValue(true);
    }


    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {

        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box) {
            box.keyPressed(keyEvent);

            if ((keyEvent.key() != 256 && keyEvent.key() != 258))
                cir.setReturnValue(true);

            return;
        }


        if (!((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this instanceof CreativeModeInventoryScreen) && OverlayManager.INSTANCE.keyPressed(keyEvent))
            cir.setReturnValue(true);

        if (this.hoveredSlot == null)
            return;

        if (CommonEIVClient.USAGE_KEYBIND.matches(keyEvent) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.INPUT);

        if (CommonEIVClient.RECIPE_KEYBIND.matches(keyEvent) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ItemViewOverlay.ItemViewOpenType.RESULT);

        if (CommonEIVClient.ADD_BOOKMARK_KEYBIND.matches(keyEvent) && this.hoveredSlot.hasItem()) {
            ItemBookmarkOverlay.INSTANCE.bookmarkItem(this.hoveredSlot.getItem());

        }
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"))
    private boolean injectOverlay$3(Screen instance, MouseButtonEvent mouseButtonEvent, boolean b){
        return super.mouseClicked(mouseButtonEvent, b) | OverlayManager.INSTANCE.mouseClicked(mouseButtonEvent, b);

    }


    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$4(CallbackInfo ci) {
        OverlayManager.INSTANCE.oldWidgets().clear();
        OverlayManager.INSTANCE.screenContextMap().clear();


        if (((Object) this instanceof RecipeViewScreen viewScreen)) {
            if (this.hoveredSlot != null) {
                this.onStopHovering(this.hoveredSlot);
            }

            Minecraft.getInstance().setScreen(viewScreen.getMenu().getParentScreen());
            ci.cancel();
        }
    }

    //Optional Slots

    @Inject(method = "renderSlotHighlightBack", at = @At("HEAD"), cancellable = true)
    private void preventFromRender$0(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && ((AbstractContainerScreen) (Object) this) instanceof RecipeViewScreen viewScreen && viewScreen.getMenu().isOptionalSlot(this.hoveredSlot.index))
            ci.cancel();
    }

    @Inject(method = "renderSlotHighlightFront", at = @At("HEAD"), cancellable = true)
    private void preventFromRender$1(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && ((AbstractContainerScreen) (Object) this) instanceof RecipeViewScreen viewScreen && viewScreen.getMenu().isOptionalSlot(this.hoveredSlot.index))
            ci.cancel();
    }


    @Unique
    private void updateWidgets() {
        OverlayManager.INSTANCE.oldWidgets().forEach(eventListener -> {

            if (eventListener.isFocused())
                this.setFocused(null);

            this.removeWidget(eventListener);
        });
        OverlayManager.INSTANCE.oldWidgets().clear();

        OverlayManager.INSTANCE.screenContextMap().forEach((abstractEivOverlay, screenContext) -> {
            screenContext.renderables().forEach(eventListener -> this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) eventListener));
            screenContext.nonRenderables().forEach(eventListener -> this.addWidget((GuiEventListener & NarratableEntry) eventListener));
        });

        OverlayManager.INSTANCE.setQueuedWidgetUpdate(false);

    }
}
