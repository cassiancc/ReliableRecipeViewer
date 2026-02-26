package cc.cassian.rrv.common.overlay.itemlist.view;

import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;import net.minecraft.resources.ResourceLocation;

public class ReliableSpriteIconButton extends SpriteIconButton.CenteredIcon {

	protected ReliableSpriteIconButton(int width, int height, Component message, int spriteWidth, int spriteHeight, ResourceLocation sprite, OnPress onPress) {
		super(width, height, message, spriteWidth, spriteHeight, sprite, onPress, null);
	}

	protected ReliableSpriteIconButton(int size, Component message, int spriteSize, ResourceLocation sprite, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, sprite, onPress, null);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.setFocused(false);
		return super.mouseReleased(mouseX, mouseY, button);
	}
}
