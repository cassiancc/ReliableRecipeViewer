package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(targets = "net.minecraft.world.item.crafting.ImbueRecipe")
public interface ImbueRecipeAccessor {
    //? if >26 {
    @Accessor
    Ingredient getMaterial();

    @Accessor
    ItemStackTemplate getResult();

    @Accessor
    Ingredient getSource();
    //?}
}
