package cc.cassian.rrv.common.mixin.client.gui.screens.inventory;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

        //In recipe book screens we initialize after the recipe button init
        if ((Object) this instanceof AbstractRecipeBookScreen)
            return;

        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                ResourceLocation.withDefaultNamespace("container"),
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


        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                ResourceLocation.withDefaultNamespace("container"),
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


    @Inject(method = "mouseScrolled", at = @At("TAIL"), cancellable = true)
    private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
            cir.setReturnValue(true);
    }


    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {

        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box) {
            box.keyPressed(keyCode, scanCode, modifiers);

            if ((keyCode != 256 && keyCode != 258))
                cir.setReturnValue(true);

            return;
        }


        if (!((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this instanceof CreativeModeInventoryScreen) && OverlayManager.INSTANCE.keyPressed(keyCode, scanCode))
            cir.setReturnValue(true);

        if (this.hoveredSlot == null)
            return;

        if (ReliableRecipeViewerClient.USAGE_KEYBIND.matches(keyCode, scanCode) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ActionType.INPUT);

        if (ReliableRecipeViewerClient.RECIPE_KEYBIND.matches(keyCode, scanCode) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ActionType.RESULT);

        if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(keyCode, scanCode) && this.hoveredSlot.hasItem()) {
            BookmarkManager.INSTANCE.bookmarkItem(this.hoveredSlot.getItem());

        }
    }

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(DDI)Z"))
    private boolean injectOverlay$3(AbstractContainerScreen instance, double k, double v, int i, Operation<Boolean> original){
        return super.mouseClicked(k, v, i) | OverlayManager.INSTANCE.mouseClicked(k, v, i);
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

        OverlayManager.INSTANCE.screenContextMap().forEach((abstractRrvOverlay, screenContext) -> {
            screenContext.renderables().forEach(eventListener -> this.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) eventListener));
            screenContext.nonRenderables().forEach(eventListener -> this.addWidget((GuiEventListener & NarratableEntry) eventListener));
        });

        OverlayManager.INSTANCE.setQueuedWidgetUpdate(false);

    }
}
