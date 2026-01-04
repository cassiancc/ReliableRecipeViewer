package cc.cassian.rrv.common.builtin.blasting;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BlastingServerRecipe extends SmeltingServerRecipe {

    public static final RrvRecipeType<BlastingServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("blasting"),
            () -> new BlastingServerRecipe(null, ItemStack.EMPTY)
    );


    public BlastingServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
