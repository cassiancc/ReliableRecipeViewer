package cc.cassian.rrv.common.builtin.smoking;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class SmokingServerRecipe extends SmeltingServerRecipe {

    public static final RrvRecipeType<SmokingServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("smoking"),
            () -> new SmokingServerRecipe(null, ItemStack.EMPTY)
    );


    public SmokingServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }


    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
