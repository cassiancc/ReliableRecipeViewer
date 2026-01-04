package cc.cassian.rrv.common.overlay.itemlist.view;

import com.mojang.blaze3d.platform.InputConstants;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class SearchBar extends EditBox {

    private final ItemViewOverlay itemViewOverlay;

    public SearchBar(Font font, int x, int y, int width, int height, Component message, ItemViewOverlay itemViewOverlay) {
        super(font, x, y, width, height, message);
        this.itemViewOverlay = itemViewOverlay;
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
