package de.crafty.eiv.common.builtin.smoking;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class SmokingServerRecipe extends SmeltingServerRecipe {

    public static final EivRecipeType<SmokingServerRecipe> TYPE = EivRecipeType.register(
            Identifier.withDefaultNamespace("smoking"),
            () -> new SmokingServerRecipe(null, ItemStack.EMPTY)
    );


    public SmokingServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }


    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
