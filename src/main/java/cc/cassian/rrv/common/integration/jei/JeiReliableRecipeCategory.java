package cc.cassian.rrv.common.integration.jei;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.util.GuiWidgetAccess;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

final class JeiReliableRecipeCategory implements IRecipeCategory<ReliableClientRecipe> {
	private final ReliableClientRecipeType recipeType;
	private final IRecipeType<ReliableClientRecipe> recipeClass;

	private RecipeViewMenu.SlotFillContext slotFillContext = new RecipeViewMenu.SlotFillContext();

	JeiReliableRecipeCategory(ReliableClientRecipeType recipeType, IRecipeType<ReliableClientRecipe> recipeClass) {
		this.recipeType = recipeType;
		this.recipeClass = recipeClass;
	}

	@Override
	public IRecipeType<ReliableClientRecipe> getRecipeType() {
		return recipeClass;
	}

	@Override
	public Component getTitle() {
		return recipeType.getDisplayName();
	}

	@Override
	public int getWidth() {
		return recipeType.getDisplayWidth();
	}

	@Override
	public int getHeight() {
		return recipeType.getDisplayHeight();
	}

	@Override
	public IDrawable getIcon() {
		return new IDrawable() {
			@Override
			public int getWidth() {
				return 16;
			}

			@Override
			public int getHeight() {
				return 16;
			}

			@Override
			public void draw(GuiGraphicsExtractor guiGraphics, int xOffset, int yOffset) {
				guiGraphics.fakeItem(recipeType.getIcon(), xOffset, yOffset);
			}
		};
	}

	@Override
	public void createRecipeExtras(IRecipeExtrasBuilder builder, ReliableClientRecipe recipe, IFocusGroup focuses) {
		int xpos = (int) Minecraft.getInstance().mouseHandler.xpos();
		int ypos = (int) Minecraft.getInstance().mouseHandler.ypos();
		GuiWidgetAccess guiWidgetAccess = new GuiWidgetAccess() {
			public <T extends GuiEventListener & Renderable & NarratableEntry> T addRecipeWidget(T widget) {
				builder.addGuiEventListener(new IJeiGuiEventListener() {
					@Override
					public ScreenRectangle getArea() {
						return widget.getRectangle();
					}

					@Override
					public boolean mouseClicked(double mouseX, double mouseY, int button) {
						return widget.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), false);
					}

					@Override
					public void mouseMoved(double mouseX, double mouseY) {
						widget.mouseMoved(mouseX, mouseY);
					}

					@Override
					public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
						return widget.mouseDragged(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), dragX, dragY);
					}

					@Override
					public boolean mouseReleased(double mouseX, double mouseY, int button) {
						return widget.mouseReleased(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)));
					}

					@Override
					public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
						return widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
					}

					@Override
					public boolean keyPressed(double mouseX, double mouseY, int keyCode, int scanCode, int modifiers) {
						return widget.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
					}
				});
				builder.addDrawable(new IDrawable() {
					@Override
					public int getWidth() {
						return widget.getRectangle().width();
					}

					@Override
					public int getHeight() {
						return widget.getRectangle().height();
					}

					@Override
					public void draw(GuiGraphicsExtractor guiGraphicsExtractor, int xOffset, int yOffset) {
						widget.extractRenderState(guiGraphicsExtractor, xpos, ypos, 0);
					}
				});
				return widget;
			}
		};
		recipe.addRecipeWidgets(new RecipeScreenContext(RRVClientUtil.currentScreen(), guiWidgetAccess, RRVClientUtil.currentScreen().getFont(), new ReliableClientRecipe.RecipePosition(0, 0, recipeType.getDisplayWidth(), recipeType.getDisplayHeight()), null, xpos, ypos, 0));
	}

	@Override
	public void draw(ReliableClientRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {

		var left = 0;
		var top = 0;
		if (RRVClientUtil.currentScreen() instanceof RecipesGui recipesGui) {
			left+= (int) (recipesGui.getArea().x()*1.2);
			top+= (int) (recipesGui.getArea().y()*1.75);
		}
		if (recipeType.getGuiTexture() != null)
			guiGraphics.blit(RenderPipelines.GUI_TEXTURED, recipeType.getGuiTexture(), 0, 0, 0, 0, recipeType.getDisplayWidth(), recipeType.getDisplayHeight(), recipeType.getDisplayWidth(), recipeType.getDisplayHeight());
		Screen screen = RRVClientUtil.currentScreen();
		recipe.renderRecipe(new RecipeScreenContext(screen, (GuiWidgetAccess) screen, screen.getFont(), new ReliableClientRecipe.RecipePosition(left, top, 0, 0), guiGraphics, (int) mouseX, (int) mouseY, 0));
	}

	@Override
	public boolean needsRecipeBorder() {
		return false;
	}

	@Override
	public @Nullable Identifier getIdentifier(ReliableClientRecipe recipe) {
		return recipe.entryId();
	}

	private final HashMap<Integer, RecipeViewMenu.OptionalSlotRenderer> optionalSlotRenderers  = new HashMap<>();

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ReliableClientRecipe recipe, IFocusGroup focuses) {
		slotFillContext = new RecipeViewMenu.SlotFillContext();
		recipe.bindSlots(slotFillContext);

		// TODO do this better
		RecipeViewMenu.SlotDefinition slotDefinition = new RecipeViewMenu.SlotDefinition(null) {
			public void addItemSlot(int slotId, int x, int y) {
				var slot = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, y);
				SlotContent slotContent = slotFillContext.contentBySlot(slotId);
				addSlotContent(slot, slotContent);
				if (slotFillContext.getOptionalSlotRenderers().containsKey(slotId)) // TODO do this better
					slot.setStandardSlotBackground();
			}
		};

		for (SlotContent ingredient : recipe.getIngredients()) {
			addSlotContent(builder.addInvisibleIngredients(RecipeIngredientRole.INPUT), ingredient);
		}
		for (SlotContent ingredient : recipe.getResults()) {
			addSlotContent(builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT), ingredient);
		}

		recipeType.placeSlots(slotDefinition);

	}

	public static void addSlotContent(IIngredientAcceptor<?> slot, SlotContent slotContent) {
		for (ItemStack validContent : slotContent.getValidContents()) {
			if (validContent.getItem() instanceof FluidItem) {
				FluidStack fluidStack = FluidStack.fromItemStack(validContent);
				int amount = fluidStack.amount();
				//? fabric
				amount = amount * 81;
				slot.add(fluidStack.fluid(), amount, fluidStack.patch());
			} else {
				slot.add(validContent);
			}
		}
	}

}
