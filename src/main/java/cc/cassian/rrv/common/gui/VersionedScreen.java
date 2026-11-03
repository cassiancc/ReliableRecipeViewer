package cc.cassian.rrv.common.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class VersionedScreen extends Screen {
	private final Screen lastScreen;

	protected VersionedScreen(Component title, Screen parent) {
		super(title);
		this.lastScreen = parent;
	}

	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics
				//? if <26.4
				, mouseX, mouseY, a
		);
	}

	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics
				//? if <26.4
				, mouseX, mouseY, a
		);
	}

	//? if >=26.4 {
	/*public void extractBackground(GuiGraphicsExtractor graphics) {
		this.extractBackground(graphics, graphics.mouseX(), graphics.mouseY(), graphics.partialTicks());
	}

	public void extractRenderState(GuiGraphicsExtractor graphics) {
		this.extractRenderState(graphics, graphics.mouseX(), graphics.mouseY(), graphics.partialTicks());
	}
	*///?}

	public Screen getParentScreen() {
		return lastScreen;
	}
}
