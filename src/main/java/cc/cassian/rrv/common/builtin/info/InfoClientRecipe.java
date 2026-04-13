package cc.cassian.rrv.common.builtin.info;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class InfoClientRecipe implements ReliableClientRecipe {
	private final SlotContent key;
	private final MutableComponent text;

	public InfoClientRecipe(SlotContent key, String text) {
		this.key = key;
		this.text = Component.translatableWithFallback(text, text).withColor(-16777216);
	}

	public InfoClientRecipe(SlotContent key, Component text) {
		this.key = key;
		if (text.getStyle().isEmpty())
			this.text = text.copy().withColor(-16777216);
		else
			this.text = text.copy();
	}

	@Override
	public ReliableClientRecipeType getType() {
		return InfoClientRecipeType.INSTANCE;
	}

	@Override
	public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
		slotFillContext.bindSlot(0, this.key);
	}

	@Override
	public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		MultiLineTextWidget widget = new MultiLineTextWidget(5, 20, text.withoutShadow(), Minecraft.getInstance().font).setMaxRows(10).setMaxWidth(112);
		widget.setX(recipePosition.left() + 5);
		widget.setY(recipePosition.top() + 20);
		screen.addRecipeWidget(widget);
	}

	@Override
	public List<SlotContent> getIngredients() {
		return List.of(this.key);
	}

	@Override
	public List<SlotContent> getResults() {
		return List.of(this.key);
	}

	@Override
	public boolean isVisualOnly() {
		return true;
	}
}
