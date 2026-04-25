package cc.cassian.rrv.common.mixin.client.gui.screens.inventory;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractContainerScreen.class, priority = 900)
public abstract class MixinAbstractContainerScreen<T extends AbstractContainerMenu> extends Screen
        implements MenuAccess<T>, RRVExtendedContainerScreen {


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
        // In some screens we initialize after the screen button init
        if (this.rrv$triggerInitLater()) return;

        this.rrv$callInit();
    }

    @Override
    public final void rrv$callInit() {
        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo(
                (AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this,
                this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                RRVClientUtil.CONTAINER,
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));
        OverlayManager.INSTANCE.setCurrentInvInfo(info);
        this.updateWidgets();
    }

    @Inject(method = "extractContents", at = @At("TAIL"))
    private void injectOverlay$1(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (minecraft == null) return;


        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                Identifier.withDefaultNamespace("container"),
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));

        if (OverlayManager.INSTANCE.checkForScreenChange(info))
            OverlayManager.INSTANCE.updateOverlaysAndWidgets(false);

        if (OverlayManager.INSTANCE.hasQueuedWidgetUpdate())
            this.updateWidgets();

        OverlayManager.INSTANCE.renderAllBackground(guiGraphics, mouseX, mouseY, partialTicks);
        OverlayManager.INSTANCE.renderAll(guiGraphics, mouseX, mouseY, partialTicks);

    }


    @Inject(method = "mouseScrolled", at = @At("TAIL"), cancellable = true)
    private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
            cir.setReturnValue(true);
    }


    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {

        if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box) {
            box.keyPressed(keyEvent);

            if (!keyEvent.isEscape() && !keyEvent.isCycleFocus())
                cir.setReturnValue(true);

            return;
        }


        if (!((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this instanceof CreativeModeInventoryScreen) && OverlayManager.INSTANCE.keyPressed(keyEvent))
            cir.setReturnValue(true);

        if (this.hoveredSlot == null)
            return;

        if (ReliableRecipeViewerClient.USAGE_KEYBIND.matches(keyEvent) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ActionType.INPUT);

        if (ReliableRecipeViewerClient.RECIPE_KEYBIND.matches(keyEvent) && this.hoveredSlot.hasItem())
            ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot.getItem(), ActionType.RESULT);

        if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(keyEvent) && this.hoveredSlot.hasItem()) {
            BookmarkManager.INSTANCE.bookmarkItem(this.hoveredSlot.getItem());

        }
    }

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"))
    private boolean injectOverlay$3(AbstractContainerScreen<?> instance, MouseButtonEvent mouseButtonEvent, boolean b, Operation<Boolean> original){
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

            RRVClientUtil.setScreen(viewScreen.getMenu().getParentScreen());
            ci.cancel();
        }
    }

    //Optional Slots

    @Inject(method = "extractSlotHighlightBack", at = @At("HEAD"), cancellable = true)
    private void preventFromRender$0(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem() && ((AbstractContainerScreen) (Object) this) instanceof RecipeViewScreen viewScreen && viewScreen.getMenu().isOptionalSlot(this.hoveredSlot.index))
            ci.cancel();
    }

    @Inject(method = "extractSlotHighlightFront", at = @At("HEAD"), cancellable = true)
    private void preventFromRender$1(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
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

    @Override
    public boolean rrv$triggerInitLater() {
        return false;
    }
}
