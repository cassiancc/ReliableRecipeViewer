package cc.cassian.rrv.common.mixin.integration.jei;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.util.RRVExtendedContainerScreen;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RecipesGui.class)
public abstract class JeiRecipesGuiMixin extends Screen implements RRVExtendedContainerScreen {
	@Shadow
	private ImmutableRect2i area;

	@Shadow
	public abstract <T> Optional<T> getIngredientUnderMouse(IIngredientType<T> ingredientType);

	protected JeiRecipesGuiMixin(Component title) {
		super(title);
	}

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
		this.updateWidgets();
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void injectOverlay$1(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		if (minecraft == null || Configs.CLIENT_SETTINGS.isJeiPanel()) return;


		AbstractRrvOverlay.InventoryPositionInfo info = new AbstractRrvOverlay.InventoryPositionInfo((Screen) this, this.width, this.height, this.area.x(), this.area.y(), this.area.width(), this.area.height());

		OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
				RRVClientUtil.CONTAINER,
				info.leftPos(),
				info.topPos(),
				info.imageWidth(),
				info.imageHeight()
		));

		if (OverlayManager.INSTANCE.checkForScreenChange(info)) {
			OverlayManager.INSTANCE.updateOverlaysAndWidgets(false);
		}

		if (OverlayManager.INSTANCE.hasQueuedWidgetUpdate())
			this.updateWidgets();


		OverlayManager.INSTANCE.renderAll(guiGraphics, mouseX, mouseY, partialTicks);

	}


	@Inject(method = "mouseScrolled", at = @At("TAIL"), cancellable = true)
	private void injectOverlay$2(double mouseX, double mouseY, double scrolledX, double scrolledY, CallbackInfoReturnable<Boolean> cir) {
		if (minecraft == null || Configs.CLIENT_SETTINGS.isJeiPanel()) return;
		if (OverlayManager.INSTANCE.scrollMouse(mouseX, mouseY, scrolledX, scrolledY))
			cir.setReturnValue(true);
	}


	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void injectOverlay$3(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
		if (minecraft == null || Configs.CLIENT_SETTINGS.isJeiPanel()) return;

		if (OverlayManager.INSTANCE.isTextWidgetFocused() && this.getFocused() instanceof EditBox box) {
			box.keyPressed(keyEvent);

			if (!keyEvent.isEscape() && !keyEvent.isCycleFocus())
				cir.setReturnValue(true);

			return;
		}


		if (OverlayManager.INSTANCE.keyPressed(keyEvent))
			cir.setReturnValue(true);

		if (this.hoveredSlot() == null)
			return;

		if (ReliableRecipeViewerClient.USAGE_KEYBIND.matches(keyEvent))
			ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot(), ActionType.INPUT);

		if (ReliableRecipeViewerClient.RECIPE_KEYBIND.matches(keyEvent))
			ItemViewOverlay.INSTANCE.openRecipeView(this.hoveredSlot(), ActionType.RESULT);

		if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(keyEvent)) {
			BookmarkManager.INSTANCE.bookmarkItem(this.hoveredSlot());

		}
	}

	@Unique
	private ItemStack hoveredSlot() {
		return getIngredientUnderMouse(VanillaTypes.ITEM_STACK).orElse(null);
	}

	@WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"))
	private boolean injectOverlay$3(RecipesGui instance, MouseButtonEvent mouseButtonEvent, boolean b, Operation<Boolean> original){
		return super.mouseClicked(mouseButtonEvent, b) | OverlayManager.INSTANCE.mouseClicked(mouseButtonEvent, b);
	}

	@Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
	private void injectOverlay$4(CallbackInfo ci) {
		OverlayManager.INSTANCE.oldWidgets().clear();
		OverlayManager.INSTANCE.screenContextMap().clear();

	}


	@Unique
	private void updateWidgets() {
		if (minecraft == null || Configs.CLIENT_SETTINGS.isJeiPanel()) return;
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
