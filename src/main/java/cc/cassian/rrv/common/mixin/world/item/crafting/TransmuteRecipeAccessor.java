package cc.cassian.rrv.common.mixin.world.item.crafting;


import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;
//? if >26 {
/*import net.minecraft.world.item.ItemStackTemplate;
*///?} else {
import net.minecraft.world.item.crafting.TransmuteResult;
//?}

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransmuteRecipe.class)
public interface TransmuteRecipeAccessor {

    @Accessor("input")
    Ingredient getInput();

    @Accessor("material")
    Ingredient getMaterial();

    //? if >26 {
    /*@Accessor("result")
    ItemStackTemplate getResult();
    *///?} else {
    @Accessor("result")
    TransmuteResult getResult();
    //?}


}
