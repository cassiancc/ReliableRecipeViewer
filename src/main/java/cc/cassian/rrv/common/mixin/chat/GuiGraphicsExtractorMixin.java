package cc.cassian.rrv.common.mixin.chat;

import cc.cassian.rrv.client.sharing.RecipeSharing;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Shadow
    public abstract void setComponentTooltipForNextFrame(Font font, List<Component> lines, int xo, int yo);

    @Inject(method = "componentHoverEffect", at = @At("HEAD"))
    private void hoverRecipe(Font font, Style hoveredStyle, int xMouse, int yMouse, CallbackInfo ci) {
        if (hoveredStyle.getHoverEvent() instanceof RecipeSharing.ShowRecipe recipe) {
            this.setComponentTooltipForNextFrame(font, recipe.getTooltipLines(), xMouse, yMouse);
        }
    }
}
