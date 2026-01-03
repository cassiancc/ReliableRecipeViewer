package de.crafty.eiv.common.builtin.blasting;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BlastingServerRecipe extends SmeltingServerRecipe {

    public static final EivRecipeType<BlastingServerRecipe> TYPE = EivRecipeType.register(
            Identifier.withDefaultNamespace("blasting"),
            () -> new BlastingServerRecipe(null, ItemStack.EMPTY)
    );


    public BlastingServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
