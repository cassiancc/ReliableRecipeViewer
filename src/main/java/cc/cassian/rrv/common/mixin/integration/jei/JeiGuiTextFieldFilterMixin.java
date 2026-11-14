package cc.cassian.rrv.common.mixin.integration.jei;

import cc.cassian.rrv.common.config.Configs;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.input.GuiTextFieldFilter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiTextFieldFilter.class)
public class JeiGuiTextFieldFilterMixin extends EditBox {
	public JeiGuiTextFieldFilterMixin(Font font, int width, int height, Component narration) {
		super(font, width, height, narration);
	}

	@Inject(method = "updateBounds", at = @At(value = "HEAD"))
	private void hideJeiSearch(ImmutableRect2i area, CallbackInfo ci) {
		this.setVisible(Configs.CLIENT_SETTINGS.isJeiPanel());
	}
}
