package cc.cassian.rrv.common.overlay.itemlist.view;

import com.mojang.blaze3d.platform.InputConstants;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchBar extends EditBox {

    private final ItemViewOverlay itemViewOverlay;

    public SearchBar(Font font, int x, int y, int width, int height, Component message, ItemViewOverlay itemViewOverlay) {
        super(font, x, y, width, height, message);
        this.itemViewOverlay = itemViewOverlay;
        this.addFormatter((text, offset) -> {
			MutableComponent component = Component.empty();
			for (String s : text.splitWithDelimiters(" ", 0)) {
				if (s.contains("@")) component.append(Component.literal(s).withStyle(ChatFormatting.GOLD));
				else if (s.contains("#")) component.append(Component.literal(s).withStyle(ChatFormatting.GREEN));
				else if (s.contains(":")) component.append(Component.literal(s).withStyle(ChatFormatting.LIGHT_PURPLE));
				else component.append(s);
			}
			return component.getVisualOrderText();
		});
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
            this.setFocused(true);
            OverlayManager.INSTANCE.currentInfo().screen().setFocused(this);
            this.setValue("");
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
}
