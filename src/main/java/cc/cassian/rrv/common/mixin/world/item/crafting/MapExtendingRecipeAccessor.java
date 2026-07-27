package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapExtendingRecipe.class)
public interface MapExtendingRecipeAccessor {
    //? if >26 {
    @Accessor
    Ingredient getMap();

    @Accessor
    Ingredient getMaterial();

    @Accessor
    ItemStackTemplate getResult();
    //?}
}
