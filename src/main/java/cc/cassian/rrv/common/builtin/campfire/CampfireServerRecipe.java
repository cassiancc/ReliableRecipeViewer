package cc.cassian.rrv.common.builtin.campfire;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CampfireServerRecipe extends SmeltingServerRecipe {

    public static final ReliableServerRecipeType<CampfireServerRecipe> TYPE = ReliableServerRecipeType.register(
            ResourceLocation.withDefaultNamespace("campfire_cooking"),
            () -> new CampfireServerRecipe(null, ItemStack.EMPTY)
    );

    public CampfireServerRecipe(Ingredient input, ItemStack result) {
        super(input, result);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
