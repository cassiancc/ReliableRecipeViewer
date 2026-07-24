package cc.cassian.rrv.common.integration.jei.recipe;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.integration.jei.JeiHelpers;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class JeiClientRecipe implements ReliableClientRecipe {
	private final ActionType openType;
	private final ItemStack origin;
	int tickCount = 0;

	public JeiClientRecipe(ActionType openType, ItemStack origin) {
		this.openType = openType;
		this.origin = origin;
	}

	@Override
	public ReliableClientRecipeType getType() {
		return JeiClientRecipeType.INSTANCE;
	}

	@Override
	public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

	}

	@Override
	public void renderRecipe(RecipeScreenContext context) {
		if (context.screen() instanceof RecipeViewScreen screen1) {
			tryOpen(openType, origin, screen1.getMenu().getParentScreen());
		}

		tickCount++;
		if (tickCount >= 20) {
			context.guiGraphics().textWithWordWrap(Minecraft.getInstance().font, FormattedText.of("JEI has no recipes for this entry."), 20, 20, 160, -16777216, false);
		}
	}

	public static void tryOpen(ActionType type, ItemStack stack, Screen parentScreen) {
		JeiHelpers.openJEI(stack, type, parentScreen);
	}

	@Override
	public List<SlotContent> getIngredients() {
		return List.of();
	}

	@Override
	public List<SlotContent> getResults() {
		return List.of();
	}

	@Override
	public boolean isVisualOnly() {
		return true;
	}

	@Override
	public Identifier getId() {
		return Identifier.fromNamespaceAndPath("jei", "bridge");
	}
}
