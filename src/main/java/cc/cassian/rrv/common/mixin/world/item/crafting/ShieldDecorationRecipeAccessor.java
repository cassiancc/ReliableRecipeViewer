package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShieldDecorationRecipe.class)
public interface ShieldDecorationRecipeAccessor {
    //? if >26 {
    @Accessor
    Ingredient getBanner();

    @Accessor
    Ingredient getTarget();

    @Accessor
    ItemStackTemplate getResult();
    //?}
}
