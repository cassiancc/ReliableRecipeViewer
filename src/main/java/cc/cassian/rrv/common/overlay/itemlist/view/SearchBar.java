package cc.cassian.rrv.common.overlay.itemlist.view;

import com.mojang.blaze3d.platform.InputConstants;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SearchBar extends EditBox {

    private long lastSearchbarClick = -1;

    private final ItemViewOverlay itemViewOverlay;

    public SearchBar(Font font, int x, int y, int width, int height, Component message, ItemViewOverlay itemViewOverlay) {
        super(font, x, y, width, height, message);
        this.itemViewOverlay = itemViewOverlay;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
            this.setFocused(true);
            OverlayManager.INSTANCE.currentInfo().screen().setFocused(this);
            this.setValue("");
            return true;
        }

        if (this.isHovered() && button == InputConstants.MOUSE_BUTTON_LEFT) {
            if (this.lastSearchbarClick != -1 && System.currentTimeMillis() - this.lastSearchbarClick <= 400) {
                itemViewOverlay.itemFilterMode = !itemViewOverlay.itemFilterMode;
                this.lastSearchbarClick = -1;
            } else
                this.lastSearchbarClick = System.currentTimeMillis();

        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
