package cc.cassian.rrv.common.builtin.info;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.SlotPlacementDefinition;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

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
	public void addSlots(SlotPlacementDefinition builder) {
		builder.addItemSlot(this.key, 53, 3);
	}

	@Override
	public void addRecipeWidgets(RecipeScreenContext context) {
		MultiLineTextWidget widget = new MultiLineTextWidget(5, 20, text.withoutShadow(), context.font()).setMaxRows(10).setMaxWidth(112);
		widget.setX(context.recipePosition().left() + 5);
		widget.setY(context.recipePosition().top() + 20);
		context.widgets().addRecipeWidget(widget);
	}

	@Override
	public List<SlotContent> getIngredients() {
		return List.of(this.key);
	}

	@Override
	public List<SlotContent> getResults() {
		return List.of(this.key);
	}

	public MutableComponent getText() {
		return text;
	}

	@Override
	public boolean isVisualOnly() {
		return true;
	}
}
