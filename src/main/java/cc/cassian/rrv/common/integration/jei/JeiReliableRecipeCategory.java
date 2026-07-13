package cc.cassian.rrv.common.integration.jei;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

final class JeiReliableRecipeCategory implements IRecipeCategory<ReliableClientRecipe> {
	private final ReliableClientRecipeType recipeType;
	private final IRecipeType<ReliableClientRecipe> recipeClass;

	private RecipeViewMenu.SlotDefinition slotDefinition;
	private RecipeViewMenu.SlotFillContext context = new RecipeViewMenu.SlotFillContext();

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
	public void draw(ReliableClientRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		if (recipeType.getGuiTexture() != null)
			guiGraphics.blit(RenderPipelines.GUI_TEXTURED, recipeType.getGuiTexture(), 0, 0, 0, 0, recipeType.getDisplayWidth(), recipeType.getDisplayHeight(), recipeType.getDisplayWidth(), recipeType.getDisplayHeight());
		recipe.renderRecipe(RRVClientUtil.currentScreen(), new ReliableClientRecipe.RecipePosition(200, 100, 0, 0), guiGraphics, (int) mouseX, (int) mouseY, 0);
	}

	@Override
	public boolean needsRecipeBorder() {
		return false;
	}

	@Override
	public @Nullable Identifier getIdentifier(ReliableClientRecipe recipe) {
		return recipe.entryId();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ReliableClientRecipe recipe, IFocusGroup focuses) {
		context = new RecipeViewMenu.SlotFillContext();
		recipe.bindSlots(context);

		slotDefinition = new RecipeViewMenu.SlotDefinition(null) {
			public void addItemSlot(int slotId, int x, int y) {
				var slot = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, y);
				SlotContent slotContent = context.contentBySlot(slotId);
				addSlotContent(slot, slotContent);
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
				slot.add(fluidStack.fluid(), fluidStack.amount(), fluidStack.patch());
			} else {
				slot.add(validContent);
			}
		}
		slot.addItemStacks(slotContent.getValidContents());
	}

}
