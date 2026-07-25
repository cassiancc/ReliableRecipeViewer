package cc.cassian.rrv.common.integration.jei;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.mixin.integration.jei.JeiBookmarkOverlayAccessor;
import cc.cassian.rrv.common.mixin.integration.jei.RecipesGuiAccessor;
import cc.cassian.rrv.common.overlay.itemlist.view.ReliableSpriteIconButton;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import mezz.jei.api.constants.VanillaTypes;
//? fabric {
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.JeiFluidIngredient;
//?} else {
/*import mezz.jei.api.neoforge.NeoForgeTypes;
*///?}
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JeiHelpers {
	public static IJeiRuntime runtime;
	public static final ArrayList<String> PLUGINS = new ArrayList<>();

	public static boolean hasRecipesForItem(ItemStack stack, ActionType openType) {
		if (stack.isEmpty()) return false;
		List<IFocus<?>> focus = getFocuses(stack, openType);
		IFocusGroup checkedFocuses = runtime.getJeiHelpers().getFocusFactory().createFocusGroup(focus);
		return runtime.getRecipesGui() instanceof RecipesGui recipesGui && ((RecipesGuiAccessor) recipesGui).getLogic().showFocus(checkedFocuses);
	}

	public static List<IFocus<?>> getFocuses(ItemStack stack, ActionType openType) {
		IFocus<?> focus = createFocus(getRole(openType), stack);
		ArrayList<IFocus<?>> list = new ArrayList<>();
		list.add(focus);
		if (openType.equals(ActionType.INPUT)) {
			IFocus<?> workstationFocus = createFocus(RecipeIngredientRole.CRAFTING_STATION, stack);
			list.add(workstationFocus);
		}
		return list;
	}

	public static void openJEI(ItemStack stack, ActionType openType, Screen parentScreen) {
		if (stack.isEmpty()) return;
		List<IFocus<?>> focus = getFocuses(stack, openType);
		var gui = runtime.getRecipesGui();
		gui.show(focus);
	}

	private static IFocus<?> createFocus(RecipeIngredientRole role, ItemStack stack) {
		IFocusFactory focusFactory = runtime.getJeiHelpers().getFocusFactory();
		if (stack.getItem() instanceof FluidItem) {
			FluidStack fluidStack = FluidStack.fromItemStack(stack);
			//? fabric {
			return focusFactory.createFocus(role, FabricTypes.FLUID_STACK, new JeiFluidIngredient(fluidStack.toFluidVariant(), fluidStack.amount()* 81L));
			//?} else {
			/*return focusFactory.createFocus(role, NeoForgeTypes.FLUID_STACK, fluidStack.toLoaderFluidStack());
			*///?}
		}

		return focusFactory.createFocus(role, VanillaTypes.ITEM_STACK, stack);
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

	public static String getColorName(ItemStack stack) {
		IColorHelper colorHelper = runtime.getJeiHelpers().getColorHelper();
		var color = colorHelper.getColors(stack, 1);
		if (color.isEmpty()) {
			return "";
		}
		return colorHelper.getClosestColorName(color.getFirst());
	}
}
