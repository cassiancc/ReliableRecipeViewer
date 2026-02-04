package cc.cassian.rrv.common.integration.polymer.recipe;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
import cc.cassian.rrv.common.integration.polymer.network.StackActionPayload;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PolydexClientRecipe implements ReliableClientRecipe {
	private final ActionType openType;
	private final ItemStack origin;

	public PolydexClientRecipe(ActionType openType, ItemStack origin) {
		this.openType = openType;
		this.origin = origin;
	}

	@Override
	public ReliableClientRecipeType getViewType() {
		return PolydexClientRecipeType.INSTANCE;
	}

	@Override
	public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

	}

	@Override
	public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		tryOpen(openType, origin);
	}

	public static void tryOpen(ActionType type, ItemStack stack) {
		tryOpen(type, ClientPolymerItemUtils.getRealItemId(stack));
	}

	public static void tryOpen(ActionType type, String stringId) {
		RrvClientNetworkManager.sendPacketToServer(new StackActionPayload(type, stringId));
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
}
