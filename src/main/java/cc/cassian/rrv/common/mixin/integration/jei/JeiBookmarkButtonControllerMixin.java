package cc.cassian.rrv.common.mixin.integration.jei;

import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.gui.overlay.bookmarks.BookmarkButtonController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookmarkButtonController.class)
public class JeiBookmarkButtonControllerMixin {
	@Inject(method = "onPress", at = @At(value = "HEAD"), cancellable = true)
	private void modifyParentScreen(IJeiUserInput input, CallbackInfoReturnable<Boolean> cir) {

	}
}
