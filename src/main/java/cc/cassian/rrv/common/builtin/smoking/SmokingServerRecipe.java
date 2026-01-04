package cc.cassian.rrv.common.builtin.smoking;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class SmokingServerRecipe extends SmeltingServerRecipe {

    public static final ReliableServerRecipeType<SmokingServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("smoking"),
            () -> new SmokingServerRecipe(null, ItemStack.EMPTY)
    );


    public SmokingServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }


    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
