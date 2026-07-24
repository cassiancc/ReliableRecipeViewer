package cc.cassian.rrv.common.mixin.integration;

import cc.cassian.rrv.common.config.Configs;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.bookmarks.history.LookupHistoryOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({IngredientListOverlay.class, BookmarkOverlay.class, LookupHistoryOverlay.class})
public class JeiScreenEventsMixin {
	@ModifyReturnValue(method = "isListDisplayed", at = @At(value = "RETURN"))
	private static boolean modifyParentScreen(boolean original) {
		return original && Configs.CLIENT_SETTINGS.isJeiPanel();
	}
}
