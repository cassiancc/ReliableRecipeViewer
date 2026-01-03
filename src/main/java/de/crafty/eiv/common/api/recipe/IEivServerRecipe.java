package de.crafty.eiv.common.api.recipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Server-Side representation of a recipe used to update the client efficiently
 * <br>
 * <br>
 * Only send neccessary data here (everything else can be done in the {@link de.crafty.eiv.common.recipe.ItemViewRecipes.ClientRecipeWrapper})
 */
public interface IEivServerRecipe {


    /**
     * Responsible for encoding recipes on the <b>server</b>
     * <br><br>
     * <b>Important</b>: Use {@link de.crafty.eiv.common.recipe.util.EivTagUtil#encodeItemStackOnServer(ItemStack)}
     * because you're on the server side
     * @param tag The compoundTag containing the encoded data
     */
    void writeToTag(CompoundTag tag);

    /**
     * Responsible for decoding sent recipes on the <b>client</b>
     * <br><br>
     * <b>Important</b>: Use {@link de.crafty.eiv.common.recipe.util.EivTagUtil#decodeItemStackOnClient(CompoundTag)}
     * because you're on the client side
     * @param tag The compoundTag containing the decoded data
     *
     */
    void loadFromTag(CompoundTag tag);


    /**
     *
     * @return The server recipe's type registered by <b>EivRecipeType.register();</b>
     */
    EivRecipeType<? extends IEivServerRecipe> getRecipeType();
}
