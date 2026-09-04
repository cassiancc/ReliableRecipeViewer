package cc.cassian.rrv.common.config.widgets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class ColorEditBox extends EditBox {

    public boolean valid;

    public ColorEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.addFormatter((text, offset) -> {
            var newColor = TextColor.parseColor(text);
            if (newColor.isError()) {
                valid = false;
                return Component.literal(text).withStyle(ChatFormatting.RED, ChatFormatting.ITALIC).getVisualOrderText();
            } else {
                valid = true;
                return Component.literal(text).withColor(newColor.getOrThrow().getValue()).getVisualOrderText();
            }
        });
    }
}
