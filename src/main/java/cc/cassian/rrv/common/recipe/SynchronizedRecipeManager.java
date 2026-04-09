package cc.cassian.rrv.common.recipe;

import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

public class SynchronizedRecipeManager {
    public static SynchronizedRecipes getSynchronizedRecipes() {
        return Minecraft.getInstance().level.recipeAccess().getSynchronizedRecipes();
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> getAllOfType(RecipeType<T> type) {
        return getSynchronizedRecipes().getAllOfType(type);
    }
}
