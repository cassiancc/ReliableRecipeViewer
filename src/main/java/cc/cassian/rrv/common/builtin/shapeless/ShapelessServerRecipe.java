package cc.cassian.rrv.common.builtin.shapeless;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
/**
 * Server recipes for vanilla recipe types are unneeded post RRV 8.0.0.
 */
@Deprecated
public class ShapelessServerRecipe extends cc.cassian.rrv.common.builtin.crafting.recipes.ShapelessServerRecipe {

	public ShapelessServerRecipe(List<Ingredient> ingredients, ItemStack result) {
		super(ingredients, ItemStackTemplate.fromNonEmptyStack(result));
	}
}