package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.common.config.options.TutorialState;
import com.mojang.blaze3d.platform.InputConstants;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

import java.util.List;

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

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, delta);
        if (isHovered() && TutorialState.showTutorial()) {
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(
                    Component.translatable("rrv.search_bar.description").withStyle(ChatFormatting.GRAY),
                    Component.translatable("rrv.search_bar.search_prefixes").withStyle(ChatFormatting.GRAY),
                    Component.translatable("rrv.search_bar.search_mod_name").withStyle(ChatFormatting.GOLD),
                    Component.translatable("rrv.search_bar.search_tags").withStyle(ChatFormatting.GREEN),
                    Component.translatable("rrv.search_bar.search_id").withStyle(ChatFormatting.LIGHT_PURPLE)
            ), getX()-8, getY()-40);
        }
    }

    public void clear() {
        setValue("");
    }
}
