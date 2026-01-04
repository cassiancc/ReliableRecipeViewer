package cc.cassian.rrv.common.api.recipe;

import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Server-Side representation of a recipe used to update the client efficiently
 * <br>
 * <br>
 * Only send neccessary data here (everything else can be done in the {@link ItemViewRecipes.ClientRecipeWrapper})
 */
public interface IRrvServerRecipe {


    /**
     * Responsible for encoding recipes on the <b>server</b>
     * <br><br>
     * <b>Important</b>: Use {@link RrvTagUtil#encodeItemStackOnServer(ItemStack)}
     * because you're on the server side
     * @param tag The compoundTag containing the encoded data
     */
    void writeToTag(CompoundTag tag);

    /**
     * Responsible for decoding sent recipes on the <b>client</b>
     * <br><br>
     * <b>Important</b>: Use {@link RrvTagUtil#decodeItemStackOnClient(CompoundTag)}
     * because you're on the client side
     * @param tag The compoundTag containing the decoded data
     *
     */
    void loadFromTag(CompoundTag tag);


    /**
     *
     * @return The server recipe's type registered by <b>RrvRecipeType.register();</b>
     */
    RrvRecipeType<? extends IRrvServerRecipe> getRecipeType();
}
