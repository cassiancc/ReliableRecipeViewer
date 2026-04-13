package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.item.crafting.ImbueRecipe.class)
public interface ImbueRecipeAccessor {
    @Accessor
    Ingredient getMaterial();

    @Accessor
    ItemStackTemplate getResult();

    @Accessor
    Ingredient getSource();
}
