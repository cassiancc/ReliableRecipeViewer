package cc.cassian.rrv.common.integration.polymer.recipe;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
import cc.cassian.rrv.common.integration.polymer.network.StackActionPayload;
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

public class PolydexClientRecipe implements ReliableClientRecipe {
	private final ActionType openType;
	private final ItemStack origin;
	int tickCount = 0;

	public PolydexClientRecipe(ActionType openType, ItemStack origin) {
		this.openType = openType;
		this.origin = origin;
	}

	@Override
	public ReliableClientRecipeType getType() {
		return PolydexClientRecipeType.INSTANCE;
	}

	@Override
	public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

	}

	@Override
	public void renderRecipe(RecipeScreenContext context) {
		tryOpen(openType, origin);
		tickCount++;
		if (tickCount >= 20) {
			context.guiGraphics().textWithWordWrap(Minecraft.getInstance().font, FormattedText.of("Polydex has no recipes for this entry."), 20, 20, 160, -16777216, false);
		}
	}

	public static void tryOpen(ActionType type, ItemStack stack) {
		tryOpen(type, ClientPolymerItemUtils.getRealItemId(stack));
	}

	public static void tryOpen(ActionType type, String stringId) {
		ClientNetworkManager.sendPacketToServer(new StackActionPayload(type, stringId));
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
		return Identifier.fromNamespaceAndPath("polydex", "bridge");
	}
}
