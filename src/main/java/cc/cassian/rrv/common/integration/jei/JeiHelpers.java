package cc.cassian.rrv.common.integration.jei;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.common.mixin.integration.jei.JeiBookmarkOverlayAccessor;
import cc.cassian.rrv.common.overlay.itemlist.view.ReliableSpriteIconButton;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class JeiHelpers {
	public static IJeiRuntime runtime;

	public static boolean hasRecipesForItem(ItemStack stack, ActionType openType) {
		return true;
	}

	public static void openJEI(ItemStack stack, ActionType openType, Screen parentScreen) {
		IFocus<?> focus = runtime.getJeiHelpers().getFocusFactory().createFocus(getRole(openType), VanillaTypes.ITEM_STACK, stack);
		var gui = runtime.getRecipesGui();
		gui.show(focus);
	}

	private static RecipeIngredientRole getRole(ActionType openType) {
		return switch (openType) {
			case INPUT -> RecipeIngredientRole.INPUT;
			case RESULT -> RecipeIngredientRole.OUTPUT;
			case null, default -> RecipeIngredientRole.RENDER_ONLY;
		};
	}

	public static ItemStack getItemUnderMouse() {
		return runtime.getRecipesGui().getIngredientUnderMouse(VanillaTypes.ITEM_STACK).orElse(null);
	}

	public static void placeSidePanelButton(ReliableSpriteIconButton settingsButton) {
		if (runtime.getBookmarkOverlay() instanceof BookmarkOverlay bookmarkOverlay) {
			IconButton button = ((JeiBookmarkOverlayAccessor) bookmarkOverlay).getHistoryButton();
			if (button.isVisible())
				settingsButton.setPosition(button.getX() + button.getWidth() + 3, button.getY());
			else settingsButton.visible = false;
		} else {
			settingsButton.visible = false;
		}
	}
}
