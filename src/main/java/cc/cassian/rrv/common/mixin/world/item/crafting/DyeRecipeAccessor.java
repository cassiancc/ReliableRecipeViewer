package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.DyeRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DyeRecipe.class)
public interface DyeRecipeAccessor {

	@Accessor("target")
	Ingredient getTarget();

	@Accessor("dye")
	Ingredient getDye();

	@Accessor("result")
	ItemStackTemplate getResult();


}
