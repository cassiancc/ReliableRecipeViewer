package cc.cassian.rrv.common.builtin.blasting;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BlastingServerRecipe extends SmeltingServerRecipe {

    public static final ReliableServerRecipeType<BlastingServerRecipe> TYPE = ReliableServerRecipeType.register(
            ResourceLocation.withDefaultNamespace("blasting"),
            () -> new BlastingServerRecipe(null, ItemStack.EMPTY)
    );


    public BlastingServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
