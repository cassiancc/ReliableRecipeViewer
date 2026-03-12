package cc.cassian.rrv.common.builtin.smoking;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

public class SmokingServerRecipe extends SmeltingServerRecipe {

    public static final ReliableServerRecipeType<SmokingServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("smoking"),
            () -> new SmokingServerRecipe(null, null, null)
    );


    public SmokingServerRecipe(Identifier id, Ingredient input, ItemStackTemplate result) {
        super(id, input, result);
    }


    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
