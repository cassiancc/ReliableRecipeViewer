package cc.cassian.rrv.common.recipe.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class ReliablePlainButton extends Button.Plain {
	public ReliablePlainButton(MutableComponent literal, OnPress o, int width, int height) {
		this(literal, o, 0, 0, width, height);
	}

	public ReliablePlainButton(MutableComponent literal, OnPress o, int x, int y, int width, int height) {
		super(x, y, width, height, literal, o, Supplier::get);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		this.setFocused(false);
		return super.mouseReleased(event);
	}
}
