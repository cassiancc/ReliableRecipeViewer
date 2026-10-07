package cc.cassian.rrv.common.overlay.itemlist.view;

import net.minecraft.client.gui.components.*;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

//~ if >26.3 'SpriteIconButton.CenteredIcon'->'SpriteIconButton'
public class ReliableSpriteIconButton.CenteredIcon extends SpriteIconButton.CenteredIcon {

	//? if <26.2 {
	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, new WidgetSprites(sprite), onPress, message, null);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, new WidgetSprites(sprite, hovered), onPress, message, null);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, Identifier disabled, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, new WidgetSprites(sprite, disabled, hovered), onPress, message, null);
	}
	//?} else if <26.4 {
	/*public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, 0, 0, new WidgetSprites(sprite), onPress, message, Supplier::get, false);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, 0, 0, new WidgetSprites(sprite, hovered), onPress, message, Supplier::get, false);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, Identifier disabled, OnPress onPress) {
		super(size, size, message, spriteSize, spriteSize, 0, 0, new WidgetSprites(sprite, disabled, hovered), onPress, message, Supplier::get, false);
	}
	*///?} else {
	/*public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, OnPress onPress, DisplayState displayState) {
		super(0,0,size, size, message, new ScaledWidgetSprites(sprite, spriteSize, spriteSize), displayState, onPress, Tooltip.create(message), Supplier::get, false);
	}
	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, OnPress onPress) {
		super(0,0,size, size, message, new ScaledWidgetSprites(sprite, spriteSize, spriteSize), DisplayState.ICON_ONLY, onPress, Tooltip.create(message), Supplier::get, false);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, OnPress onPress) {
		super(0,0,size, size, message, new ScaledWidgetSprites(new WidgetSprites(sprite, hovered), spriteSize, spriteSize), DisplayState.ICON_ONLY, onPress, Tooltip.create(message), Supplier::get, false);
	}

	public ReliableSpriteIconButton(int size, Component message, int spriteSize, Identifier sprite, Identifier hovered, Identifier disabled, OnPress onPress) {
		super(0,0,size, size, message, new ScaledWidgetSprites(new WidgetSprites(sprite, disabled, hovered), spriteSize, spriteSize), DisplayState.ICON_ONLY, onPress, Tooltip.create(message), Supplier::get, false);
	}
	*///?}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		this.setFocused(false);
		return super.mouseReleased(event);
	}
}
