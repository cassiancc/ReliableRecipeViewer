package cc.cassian.rrv.common.mixin.client.gui.screens.inventory;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractContainerScreen.class, priority = 900)
public abstract class MixinAbstractContainerScreen extends Screen
        implements RRVExtendedContainerScreen {

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
        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo(this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        OverlayManager.INSTANCE.setExclusionArea(new BlockingGuiComponent(
                RRVClientUtil.CONTAINER,
                info.leftPos(),
                info.topPos(),
                info.imageWidth(),
                info.imageHeight()
        ));
        OverlayManager.INSTANCE.setCurrentInvInfo(info);
        RRVExtendedContainerScreen.updateWidgets(this);
    }

    //~ if >26 'render' ->'extract' {
    @Inject(method = "extractContents", at = @At("TAIL"))
    //~}
    private void injectOverlay$1(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (minecraft == null) return;
        AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo((AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this, this.width, this.height, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        RRVExtendedContainerScreen.extractOverlay(info, guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Inject(method = "mouseScrolled", at = @At("TAIL"), cancellable = true)
    private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
        if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
            cir.setReturnValue(true);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void injectOverlay$3(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        var pressed = handleKeyPress(this, event);
        if (pressed) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public ItemStack rrv$hoveredStack() {
        return hoveredSlot != null ? hoveredSlot.getItem() : ItemStack.EMPTY;
    }

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"))
    private boolean injectOverlay$3(AbstractContainerScreen<?> instance, MouseButtonEvent mouseButtonEvent, boolean b, Operation<Boolean> original){
        return super.mouseClicked(mouseButtonEvent, b) | OverlayManager.INSTANCE.mouseClicked(mouseButtonEvent, b);
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void injectOverlay$4(CallbackInfo ci) {
        RRVExtendedContainerScreen.clearOverlay();
    }

    @Override
    public boolean rrv$triggerInitLater() {
        return false;
    }
}
