package cc.cassian.rrv.common.config.widgets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public class IntegerEditBox extends EditBox {

    public boolean valid;

    public IntegerEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.addFormatter((text, offset) -> {
			try {
                Integer.parseInt(text);
            } catch (NumberFormatException e) {
                valid = false;
                return Component.literal(text).withStyle(ChatFormatting.RED).getVisualOrderText();
            }
            valid = true;
            return null;
        });
    }

    @Override
    public void insertText(String input) {
        try {
            Integer.parseInt(input);
            super.insertText(input);
        } catch (NumberFormatException ignored) {}
    }
}
