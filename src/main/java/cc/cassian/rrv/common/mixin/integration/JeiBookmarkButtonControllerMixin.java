package cc.cassian.rrv.common.mixin.integration;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.gui.overlay.ConfigButtonController;
import mezz.jei.gui.overlay.bookmarks.BookmarkButtonController;
import mezz.jei.gui.overlay.bookmarks.history.LookupHistoryButtonController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BookmarkButtonController.class, LookupHistoryButtonController.class, ConfigButtonController.class})
public class JeiBookmarkButtonControllerMixin {
	@Inject(method = "updateState", at = @At(value = "HEAD"))
	private void modifyParentScreen(IButtonState state, CallbackInfo ci) {
		if (!Configs.CLIENT_SETTINGS.isJeiPanel())
			state.setVisible(false);
	}
}
