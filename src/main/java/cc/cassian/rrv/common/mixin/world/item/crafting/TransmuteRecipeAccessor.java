package cc.cassian.rrv.common.mixin.world.item.crafting;


import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.TransmuteRecipe;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TransmuteRecipe.class)
public interface TransmuteRecipeAccessor {

    @Accessor("input")
    Ingredient getInput();

    @Accessor("material")
    Ingredient getMaterial();

    @Accessor("result")
    //? if >26 && <26.3 {
     net.minecraft.world.item.ItemStackTemplate
    //?} else {
     /*net.minecraft.world.item.crafting.TransmuteResult
    *///?}
     getResult();


}
