package cc.cassian.rrv.common.integration.jei;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
		System.out.println("JRRV Runtime registration");
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

	private boolean doesNotHaveNativePlugin(String namespace) {
		if (namespace.equals("rrv")) return true;
		else if (namespace.equals("minecraft")) return false;
		return !JeiHelpers.PLUGINS.contains(namespace);
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
			else if (type.getId().equals(ReliableRecipeViewer.of("item_tag"))) {
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
}
