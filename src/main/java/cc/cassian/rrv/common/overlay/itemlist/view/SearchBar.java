package cc.cassian.rrv.common.overlay.itemlist.view;

import com.mojang.blaze3d.platform.InputConstants;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class SearchBar extends EditBox {

    private final ItemViewOverlay itemViewOverlay;
	private ChatFormatting color = ChatFormatting.WHITE;

    public SearchBar(Font font, int x, int y, int width, int height, Component message, ItemViewOverlay itemViewOverlay) {
        super(font, x, y, width, height, message);
        this.itemViewOverlay = itemViewOverlay;
        this.addFormatter((text, offset) -> {
			ChatFormatting style = color;
			MutableComponent component = Component.empty();
			for (String s : text.splitWithDelimiters(" ", 0)) {
				if (s.contains("@")) {
					style = ChatFormatting.GOLD;
				} else if (s.contains("#")) {
					style = ChatFormatting.GREEN;
				} else if (s.contains(":")) {
					style = ChatFormatting.LIGHT_PURPLE;
				} else if (offset == 0 || s.contains(" ")) {
					style = ChatFormatting.WHITE;
				}
				component.append(Component.literal(s).withStyle(style));
			}
			if (offset == 0)
				color = style;
			return component.getVisualOrderText();
		});
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
            this.setFocused(true);
            OverlayManager.INSTANCE.currentInfo().screen().setFocused(this);
            this.clear();
            return true;
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            if (doubleClick) {
                itemViewOverlay.itemFilterMode = !itemViewOverlay.itemFilterMode;
                this.setFocused(true);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    public void clear() {
        setValue("");
    }
}
