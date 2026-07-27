package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DecoratedPotRecipe.class)
public interface DecoratedPotRecipeAccessor {
    //? if >26 {
    @Accessor
    Ingredient getBackPattern();

    @Accessor
    Ingredient getLeftPattern();

    @Accessor
    Ingredient getRightPattern();

    @Accessor
    Ingredient getFrontPattern();

    @Accessor
    ItemStackTemplate getResult();
    //?}
}
