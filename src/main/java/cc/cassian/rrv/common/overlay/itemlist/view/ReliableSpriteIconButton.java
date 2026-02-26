package cc.cassian.rrv.common.overlay.itemlist.view;

import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class ReliableSpriteIconButton extends SpriteIconButton.CenteredIcon {

	protected ReliableSpriteIconButton(int width, int height, Component message, int spriteWidth, int spriteHeight, WidgetSprites sprite, OnPress onPress) {
		super(width, height, message, spriteWidth, spriteHeight, sprite, onPress, message, null);
	}

	protected ReliableSpriteIconButton(int size, Component message, int spriteSize, WidgetSprites sprite, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, sprite, onPress, message, null);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		this.setFocused(false);
		return super.mouseReleased(event);
	}
}
