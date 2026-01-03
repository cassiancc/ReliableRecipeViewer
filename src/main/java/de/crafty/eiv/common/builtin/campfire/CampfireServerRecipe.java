package de.crafty.eiv.common.builtin.campfire;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.builtin.blasting.BlastingServerRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CampfireServerRecipe extends SmeltingServerRecipe {

    public static final EivRecipeType<CampfireServerRecipe> TYPE = EivRecipeType.register(
            Identifier.withDefaultNamespace("campfire_cooking"),
            () -> new CampfireServerRecipe(null, ItemStack.EMPTY)
    );

    public CampfireServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
