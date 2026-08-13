package cc.cassian.rrv.common.mixin.integration.jei;

import cc.cassian.rrv.client.util.GuiWidgetAccess;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RecipesGui.class)
public abstract class JeiRecipesGuiMixin extends Screen implements RRVExtendedContainerScreen, GuiWidgetAccess {
	@Shadow
	private ImmutableRect2i area;

	@Shadow
	public abstract <T> Optional<T> getIngredientUnderMouse(IIngredientType<T> ingredientType);

	protected JeiRecipesGuiMixin(Component title) {
		super(title);
	}

	//~ if >26.1 'Lnet/minecraft/client/Minecraft'->'Lnet/minecraft/client/gui/Gui'
	@ModifyArg(method = "onClose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
	private @Nullable Screen modifyParentScreen(@Nullable Screen screen) {
		if (screen instanceof RecipeViewScreen recipeViewScreen) {
			return (recipeViewScreen.getMenu().getParentScreen());
		}
		return screen;
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void injectOverlay$0(CallbackInfo ci) {
		this.rrv$callInit();
	}

	@WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lmezz/jei/common/config/IClientConfig;isCenterSearchBarEnabled()Z"), require = 0)
	private boolean centered(IClientConfig instance, Operation<Boolean> original) {
		if (Configs.CLIENT_SETTINGS.isCenterSearch() && !Configs.CLIENT_SETTINGS.isJeiPanel()) {
			return true;
		}
		return original.call(instance);
	}

	@Override
	public final void rrv$callInit() {
		if (minecraft == null || Configs.CLIENT_SETTINGS.isJeiPanel()) return;
		AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo(
				this,
				this.width, this.height, this.area.x(), this.area.y(), this.area.width(), this.area.height());

		OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
				RRVClientUtil.CONTAINER,
				info.leftPos(),
				info.topPos(),
				info.imageWidth(),
				info.imageHeight()
		));
		OverlayManager.INSTANCE.setCurrentInvInfo(info);
		RRVExtendedContainerScreen.updateWidgets(this);
	}

	//~ if >26 'render'->'extractRenderState'
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void injectOverlay$1(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		if (minecraft == null || Configs.CLIENT_SETTINGS.isJeiPanel()) return;
		AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo(this, this.width, this.height, this.area.x(), this.area.y(), this.area.width(), this.area.height());
		RRVExtendedContainerScreen.extractOverlay(info, guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Inject(method = "mouseScrolled", at = @At("TAIL"), cancellable = true)
	private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
		if (minecraft == null || Configs.CLIENT_SETTINGS.isJeiPanel()) return;
		if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
			cir.setReturnValue(true);
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void injectOverlay$3(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
		var pressed = handleKeyPress(this, keyEvent);
		if (pressed) {
			cir.setReturnValue(true);
		}
	}

	@Override
	public ItemStack rrv$hoveredStack() {
		return getIngredientUnderMouse(VanillaTypes.ITEM_STACK).orElse(null);
	}

	@WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"))
	private boolean injectOverlay$3(RecipesGui instance, MouseButtonEvent mouseButtonEvent, boolean b, Operation<Boolean> original){
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
