package cc.cassian.rrv.common.integration.jei;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.sharing.RecipeSharing;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.tag.block.BlockTagClientRecipeType;
import cc.cassian.rrv.common.builtin.tag.item.ItemTagClientRecipeType;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.IRecipeButtonControllerFactory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.gui.elements.DrawableSprite;
import mezz.jei.library.plugins.jei.tags.TagInfoRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static cc.cassian.rrv.common.integration.jei.JeiHelpers.doesNotHaveNativePlugin;

@JeiPlugin
@NullMarked
public class ReliableRecipeViewerJeiPlugin implements IModPlugin {

	public static HashMap<ReliableClientRecipeType, IRecipeType<ReliableClientRecipe>> RECIPE_CATEGORIES = new HashMap<>();

	@Override
	public Identifier getPluginUid() {
		return ReliableRecipeViewer.of("jrrv");
	}

	@Override
	public void registerRuntime(IRuntimeRegistration registration) {
//		registration.setIngredientListOverlay(new JRRVIngredientListOverlay());

	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		for (ReliableClientRecipe recipe : ClientRecipeCache.INSTANCE.getRecipes()) {
			ReliableClientRecipeType type = recipe.getType();
			if (!RECIPE_CATEGORIES.containsKey(type) && doesNotHaveNativePlugin(type.getId().getNamespace())) {
				IRecipeType<ReliableClientRecipe> recipeType = IRecipeType.create(type.getId().withPrefix("rrv/"), recipe.getClass());
				registration.addRecipeCategories(new JeiReliableRecipeCategory(type, recipeType));
				RECIPE_CATEGORIES.put(type, recipeType);
			}
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RECIPE_CATEGORIES.forEach((type, recipeType) -> {
			List<ReliableClientRecipe> recipes = ClientRecipeCache.INSTANCE.getRecipes().stream().filter(p -> p.getType().equals(type)).toList();
			if (type.getId().equals(ReliableRecipeViewer.of("info"))) {
				recipes.forEach(recipe -> {
					if (doesNotHaveNativePlugin(recipe.getId().getNamespace())) {
						InfoClientRecipe info = ((InfoClientRecipe) recipe);
						ArrayList<ItemStack> stacks = new ArrayList<>();
						info.getIngredients().stream().map(SlotContent::getValidContents).forEach(stacks::addAll);
						registration.addItemStackInfo(stacks, info.getText());
					}
				});
			}
			else if (type.getId().equals(ReliableRecipeViewer.of("anvil_combining"))) {
				recipes.forEach(recipe -> {
					if (doesNotHaveNativePlugin(recipe.getId().getNamespace())) {
						AnvilCombiningClientRecipe anvilRecipe = ((AnvilCombiningClientRecipe) recipe);
						registration.getVanillaRecipeFactory().createAnvilRecipe(anvilRecipe.getLeft().getValidContents(), anvilRecipe.getRight().getValidContents(), anvilRecipe.getResult().getValidContents(), anvilRecipe.getId());
					}
				});
			}
			else if (type.getId().equals(ItemTagClientRecipeType.INSTANCE.getId())) {
				return;
			}
			else if (Configs.CATEGORIES.CATEGORIES.get(type.getId()).enabled()) {
				registration.addRecipes(recipeType, recipes);
			}
		});
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		RECIPE_CATEGORIES.forEach((category, recipeType) -> {
			for (ItemStack craftReference : category.getCraftReferences()) {
				registration.addCraftingStation(recipeType, craftReference);
			}
		});
	}

	public static <T> @Nullable Identifier getId(T recipe) {
		return switch (recipe) {
			case ReliableClientRecipe clientRecipe -> clientRecipe.entryId();
			case RecipeHolder<?> holder -> holder.id().identifier();
			case TagInfoRecipe<?, ?> tagInfoRecipe -> {
				if (tagInfoRecipe.getTag().isFor(Registries.ITEM) && Configs.CATEGORIES.enabled(ItemTagClientRecipeType.INSTANCE))
					yield tagInfoRecipe.getTag().location().withPrefix("/item_tag/");
				else if (tagInfoRecipe.getTag().isFor(Registries.BLOCK) && Configs.CATEGORIES.enabled(BlockTagClientRecipeType.INSTANCE))
					yield tagInfoRecipe.getTag().location().withPrefix("/block_tag/");
				else yield null;
			}
			default -> null;
		};
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		registration.addRecipeButtonFactory(new IRecipeButtonControllerFactory() {
			@Override
			public @Nullable <T> IIconButtonController createButtonController(IRecipeLayoutDrawable<T> recipeLayoutDrawable) {
				T recipe = recipeLayoutDrawable.getRecipe();
				Identifier id = getId(recipe);
				if (id == null) return null;
				return new JeiRRVLookupButtonController(id, recipe instanceof ReliableClientRecipe);
			}
		});
		registration.addRecipeButtonFactory(new IRecipeButtonControllerFactory() {
			@Override
			public @Nullable <T> IIconButtonController createButtonController(IRecipeLayoutDrawable<T> recipeLayoutDrawable) {
				T recipe = recipeLayoutDrawable.getRecipe();
				Identifier id = getId(recipe);
				if (id == null) return null;
				return new JeiRRVShareButtonController(id, recipe instanceof ReliableClientRecipe);
			}
		});
		registration.addRecipeButtonFactory(new IRecipeButtonControllerFactory() {
			@Override
			public @Nullable <T> IIconButtonController createButtonController(IRecipeLayoutDrawable<T> recipeLayoutDrawable) {
				T recipe = recipeLayoutDrawable.getRecipe();
				if (recipe instanceof TagInfoRecipe<?, ?> tagInfoRecipe && tagInfoRecipe.getTag().isFor(Registries.ITEM))
					return new JeiRRVStackGroupButtonController(tagInfoRecipe.getTag().location());
				return null;
			}
		});
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		JeiHelpers.runtime = jeiRuntime;
	}

	private static class JRRVIngredientListOverlay implements IIngredientListOverlay {
		@Override
		public Optional<ITypedIngredient<?>> getIngredientUnderMouse() {
			return Optional.empty();
		}

		@Override
		public @Nullable <T> T getIngredientUnderMouse(IIngredientType<T> ingredientType) {
			return null;
		}

		@Override
		public boolean isListDisplayed() {
			return ItemViewOverlay.INSTANCE.isEnabled();
		}

		@Override
		public boolean hasKeyboardFocus() {
			return ItemViewOverlay.INSTANCE.getSearchbar().isFocused();
		}

		@Override
		public <T> List<T> getVisibleIngredients(IIngredientType<T> ingredientType) {
			return List.of();
		}
	}

	private record JeiRRVLookupButtonController(Identifier id, boolean providedByReliableRecipeViewer) implements IIconButtonController {

		@Override
		public boolean onPress(IJeiUserInput input) {
			if (!input.isSimulate()) {
				ItemViewOverlay.INSTANCE.openRecipeView(id, false);
			}
			return true;
		}

		@Override
		public void initState(IButtonState state) {
			state.setIcon(new DrawableSprite(Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI), ReliableRecipeViewer.of("recipe_view")));
			updateState(state);
			if (ClientRecipeCache.INSTANCE.getRecipes(id).isEmpty()) {
				state.setVisible(false);
			}
		}

		@Override
		public void getTooltips(ITooltipBuilder tooltip) {
			MutableComponent lookupText = Component.translatable("rrv.jrrv.lookup").withStyle(ChatFormatting.GOLD);
			if (RRVPlatform.INSTANCE.isDevelopment() || Configs.CLIENT_SETTINGS.isShowRecipeId()) {
				tooltip.add(lookupText.append(":"));
				tooltip.add(Component.literal(id.toString()).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	private record JeiRRVShareButtonController(Identifier id, boolean providedByReliableRecipeViewer) implements IIconButtonController {

		@Override
		public boolean onPress(IJeiUserInput input) {
			if (!input.isSimulate()) {
				RecipeSharing.shareRecipe(id);
				RRVClientUtil.setToParentScreen();
			}
			return true;
		}

		@Override
		public void initState(IButtonState state) {
			state.setIcon(new DrawableSprite(Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI), ReliableRecipeViewer.of("widget/share")));
			updateState(state);
			if (ClientRecipeCache.INSTANCE.getRecipes(id).isEmpty() || !Configs.CLIENT_SETTINGS.isRecipeSharing()) {
				state.setVisible(false);
			}
			ReliableClientRecipe recipeEntry = ClientRecipeCache.INSTANCE.getRecipeEntry(id);
			if (providedByReliableRecipeViewer && recipeEntry != null && !recipeEntry.getType().placeRecipeShareButton(new RecipeViewMenu.DisplayInfo(0, 0, 0, 0)).visible()) {
				state.setVisible(false);
			}
		}

		@Override
		public void getTooltips(ITooltipBuilder tooltip) {
			tooltip.add(Component.translatable("rrv.sharing.share_jei").withStyle(ChatFormatting.GOLD));
			tooltip.add(Component.literal(id.toString()).withStyle(ChatFormatting.GRAY));
		}
	}

	private static final class JeiRRVStackGroupButtonController implements IIconButtonController {
		private final Identifier tagId;
		private boolean exists;

		private JeiRRVStackGroupButtonController(Identifier tagId) {
			this.tagId = tagId;
			this.exists = StackGroupManager.hasGroup(tagId);
		}

		@Override
		public boolean onPress(IJeiUserInput input) {
			if (!input.isSimulate()) {
				StackGroupManager.toggleTagGroup(tagId);
				ItemViewOverlay.INSTANCE.updateDisplayedItems();
				exists = !exists;
			}
			return true;
		}

		@Override
		public void initState(IButtonState state) {
			updateIcon(state);
		}

		private void updateIcon(IButtonState state) {
			Identifier base = ReliableRecipeViewer.of("widget/tag_stack_group_disabled");
			state.setIcon(new DrawableSprite(Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI), base));
		}

		@Override
		public void drawExtras(GuiGraphicsExtractor guiGraphics, Rect2i buttonArea, int mouseX, int mouseY, float partialTicks) {
			if (exists) {
				guiGraphics.blitSprite(
						RenderPipelines.GUI_TEXTURED,
						ReliableRecipeViewer.of("widget/tag_stack_group_enabled"),
						buttonArea.getX()+1,
						buttonArea.getY()+1,
						buttonArea.getWidth()-2,
						buttonArea.getHeight()-2
				);
			}
		}

		@Override
		public void getTooltips(ITooltipBuilder tooltip) {
			var component = (Component.translatable(exists ? "rrv.tag_recipe.stack_group.enabled" : "rrv.tag_recipe.stack_group.disabled").withStyle(ChatFormatting.GOLD));
			tooltip.add(component);
		}
	}
}
