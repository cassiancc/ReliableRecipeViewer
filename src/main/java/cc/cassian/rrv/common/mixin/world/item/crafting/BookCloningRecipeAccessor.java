package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookCloningRecipe.class)
public interface BookCloningRecipeAccessor {
    @Accessor
    Ingredient getSource();

    @Accessor
    Ingredient getMaterial();

    @Accessor
    ItemStackTemplate getResult();
}
