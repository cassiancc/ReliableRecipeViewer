package cc.cassian.rrv.api.client;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.util.GuiWidgetAccess;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;

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
}
