package cc.cassian.rrv.common.builtin.campfire;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

public class CampfireServerRecipe extends SmeltingServerRecipe {

    public static final ReliableServerRecipeType<CampfireServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("campfire_cooking"),
            () -> new CampfireServerRecipe(null, null)
    );

    public CampfireServerRecipe(SlotContent input, SlotContent result) {
        super(input, result);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
