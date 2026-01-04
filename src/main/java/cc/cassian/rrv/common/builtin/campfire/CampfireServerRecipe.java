package cc.cassian.rrv.common.builtin.campfire;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CampfireServerRecipe extends SmeltingServerRecipe {

    public static final RrvRecipeType<CampfireServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("campfire_cooking"),
            () -> new CampfireServerRecipe(null, ItemStack.EMPTY)
    );

    public CampfireServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
