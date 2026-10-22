package cc.cassian.rrv.api.client;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.util.GuiWidgetAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.List;

/// @param screen       The current screen
/// @param widgets		The [GuiWidgetAccess] used to add [net.minecraft.client.gui.components.Renderable] widgets to the screen.
/// @param guiGraphics  The [GuiGraphicsExtractor] supplied by Minecraft
/// @param absoluteMouseX       The current x-position of the mouse relative to the screen. In most cases, you'll want [RecipeScreenContext#mouseX()] instead.
/// @param absoluteMouseY       The current y-position of the mouse relative to the screen. In most cases, you'll want [RecipeScreenContext#mouseX()] instead.
/// @param partialTicks partialTicks
public record RecipeScreenContext(Screen screen, GuiWidgetAccess widgets, Font font, ReliableClientRecipe.RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int absoluteMouseX, int absoluteMouseY, float partialTicks) {

	/// The current x-position of the mouse **relative to the position of the rendered recipe.**
	public int mouseX() {
		return absoluteMouseX()-recipePosition().left();
	}

	/// The current x-position of the mouse **relative to the position of the rendered recipe.**
	public int mouseY() {
		return absoluteMouseY() - recipePosition().top();
	}

	/// Render a tooltip.
	public void setTooltip(Component tooltip) {
		guiGraphics().setTooltipForNextFrame(font(), tooltip, absoluteMouseX(), absoluteMouseY());
	}

	public void setTooltip(Tooltip tooltip) {
		guiGraphics().setTooltipForNextFrame(font(), tooltip.toCharSequence(Minecraft.getInstance()), absoluteMouseX(), absoluteMouseY());
	}

	public void setTooltip(Tooltip tooltip, Rect2i rectangle) {
		if (rectangle.contains(mouseX(), mouseY())) {
			setTooltip(tooltip);
		}
	}

	/// Render a tooltip.
	public void setTooltip(List<Component> tooltip) {
		guiGraphics().setComponentTooltipForNextFrame(font(), tooltip, absoluteMouseX(), absoluteMouseY());
	}

	/// Render a tooltip if the provided rectangle contains the mouse position.
	public void setTooltip(Component tooltip, Rect2i rectangle) {
		if (rectangle.contains(mouseX(), mouseY())) {
			setTooltip(tooltip);
		}
	}

	/// Render a tooltip if the provided rectangle contains the mouse position.
	public void setTooltip(List<Component> tooltip, Rect2i rectangle) {
		if (rectangle.contains(mouseX(), mouseY())) {
			setTooltip(tooltip);
		}
	}
}
