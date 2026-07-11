package cc.cassian.rrv.common.overlay.itemlist.view;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class ReliableSpriteIconButton extends SpriteIconButton.CenteredIcon {

	//? if <26.2 {
	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, new WidgetSprites(sprite), onPress, message, null);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, new WidgetSprites(sprite, hovered), onPress, message, null);
	}
	//?} else {
	/*public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, 0, 0, new WidgetSprites(sprite), onPress, message, Supplier::get, false);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, 0, 0, new WidgetSprites(sprite, hovered), onPress, message, Supplier::get, false);
	}
	*///?}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		this.setFocused(false);
		return super.mouseReleased(event);
	}
}
