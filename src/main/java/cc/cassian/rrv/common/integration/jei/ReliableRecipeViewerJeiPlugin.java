package cc.cassian.rrv.common.integration.jei;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class ReliableRecipeViewerJeiPlugin implements IModPlugin {

	public static HashMap<ReliableClientRecipeType, IRecipeType<ReliableClientRecipe>> RECIPE_CATEGORIES = new HashMap<>();

	@Override
	public Identifier getPluginUid() {
		return ReliableRecipeViewer.of("jrrv");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		for (ReliableClientRecipe recipe : ClientRecipeCache.INSTANCE.getRecipes()) {
			ReliableClientRecipeType type = recipe.getType();
			if (!RECIPE_CATEGORIES.containsKey(type)) {
				IRecipeType<ReliableClientRecipe> recipeType = IRecipeType.create(type.getId().withPrefix("rrv/"), recipe.getClass()); //FIXME RRV TYPES SHOULD BE DE-DUPLICATED
				registration.addRecipeCategories(new JeiReliableRecipeCategory(type, recipeType));
				RECIPE_CATEGORIES.put(type, recipeType);
			}
		}

	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RECIPE_CATEGORIES.forEach((type, recipeType) -> {
			registration.addRecipes(recipeType, ClientRecipeCache.INSTANCE.getRecipes().stream().filter(p->p.getType().equals(type)).toList());
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

}
