package de.crafty.eiv.common.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransmuteRecipe.class)
public interface TransmuteRecipeAccessor {

    @Accessor("input")
    Ingredient getInput();

    @Accessor("material")
    Ingredient getMaterial();

    @Accessor("result")
    TransmuteResult getResult();

}
