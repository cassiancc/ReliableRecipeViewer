package cc.cassian.rrv.common.builtin.info;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class InfoClientRecipe implements ReliableClientRecipe {
	private final SlotContent key;
	private final MutableComponent text;
	private final Identifier id;

	/// Please add an [Identifier].
	@Deprecated(since = "8.0.0")
	public InfoClientRecipe(SlotContent key, String text) {
		this(null, key, text);
	}

	/// Please add an [Identifier].
	@Deprecated(since = "8.0.0")
	public InfoClientRecipe(SlotContent key, Component text) {
		this(null, key, text);
	}

	public InfoClientRecipe(Identifier identifier, SlotContent key, String text) {
		this.key = key;
		this.id = identifier;
		this.text = Component.translatableWithFallback(text, text).withColor(-16777216);
	}

	public InfoClientRecipe(Identifier identifier, SlotContent key, Component text) {
		this.key = key;
		this.id = identifier;
		if (text.getStyle().isEmpty())
			this.text = text.copy().withColor(-16777216);
		else
			this.text = text.copy();
	}

	@Override
	public Identifier getId() {
		return id;
	}

	boolean rendered = false;

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
		if (!rendered) {
			MultiLineTextWidget widget = new MultiLineTextWidget(5, 20, text.withoutShadow(), Minecraft.getInstance().font).setMaxRows(10).setMaxWidth(112);
			widget.setX(recipePosition.left() + 5);
			widget.setY(recipePosition.top() + 20);
			screen.addRecipeWidget(widget);
			rendered = true;
		}
	}

	@Override
	public void initRecipe() {
		rendered = false;
	}

	@Override
	public void fadeRecipe() {
		rendered = false;
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
