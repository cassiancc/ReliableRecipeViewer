package cc.cassian.rrv.common.mixin.integration.jei;

import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookmarkOverlay.class)
public interface JeiBookmarkOverlayAccessor {
	@Accessor
	IconButton getHistoryButton();
}
