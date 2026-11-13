package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FireworkRocketRecipe.class)
public interface FireworkRocketRecipeAccessor {
    @Accessor
    Ingredient getShell();

    @Accessor
    Ingredient getFuel();

    @Accessor
    Ingredient getStar();

    @Accessor
    ItemStackTemplate getResult();
}
