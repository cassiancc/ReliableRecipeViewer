package cc.cassian.rrv.client.util;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Unique;

/// This interface is applied to all screens that show RRV's Item View and Side Panel.
@ApiStatus.Internal
public interface RRVExtendedContainerScreen {

	@Unique
	static void updateWidgets(Screen screen) {
		OverlayManager.INSTANCE.oldWidgets().forEach(eventListener -> {

			if (eventListener.isFocused())
				screen.setFocused(null);

			screen.removeWidget(eventListener);
		});
		OverlayManager.INSTANCE.oldWidgets().clear();

		OverlayManager.INSTANCE.screenContextMap().forEach((abstractRrvOverlay, screenContext) -> {
			screenContext.renderables().forEach(eventListener -> screen.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) eventListener));
			screenContext.nonRenderables().forEach(eventListener -> screen.addWidget((GuiEventListener & NarratableEntry) eventListener));
		});

		OverlayManager.INSTANCE.setQueuedWidgetUpdate(false);

	}

	static void extractOverlay(AbstractRrvOverlay.InventoryPositionInfo info, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		OverlayManager.INSTANCE.setExclusionArea(new BlockingGuiComponent(
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
			updateWidgets(info.screen());

		OverlayManager.INSTANCE.renderAll(guiGraphics, mouseX, mouseY, partialTicks);
	}

	static void clearOverlay() {
		OverlayManager.INSTANCE.oldWidgets().clear();
		OverlayManager.INSTANCE.screenContextMap().clear();
	}

	default boolean handleKeyPress(Screen screen, KeyEvent keyEvent) {
		if (OverlayManager.INSTANCE.isTextWidgetFocused() && screen.getFocused() instanceof EditBox box) {
			box.keyPressed(keyEvent);
			return !keyEvent.isEscape() && !keyEvent.isCycleFocus();
		}


		if (!(screen instanceof CreativeModeInventoryScreen) && OverlayManager.INSTANCE.keyPressed(keyEvent))
			return true;

		if (rrv$hoveredStack() == null || rrv$hoveredStack().isEmpty())
			return false;

		if (ReliableRecipeViewerClient.USAGE_KEYBIND.matches(keyEvent))
			ItemViewOverlay.INSTANCE.openRecipeView(rrv$hoveredStack(), ActionType.INPUT);

		if (ReliableRecipeViewerClient.RECIPE_KEYBIND.matches(keyEvent))
			ItemViewOverlay.INSTANCE.openRecipeView(rrv$hoveredStack(), ActionType.RESULT);

		if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(keyEvent)) {
			BookmarkManager.INSTANCE.bookmarkItem(rrv$hoveredStack());
		}
		return false;
	}

	ItemStack rrv$hoveredStack();

	default void rrv$callInit() {
		throw new UnsupportedOperationException();
	}

    boolean rrv$triggerInitLater();
}
