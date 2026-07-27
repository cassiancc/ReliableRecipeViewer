package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//~ if >26 '.ArmorDyeRecipe'-> '.DyeRecipe' {
@Mixin(net.minecraft.world.item.crafting.DyeRecipe.class)
//~}
public interface DyeRecipeAccessor {

	//? if >26 {
	@Accessor("target")
	Ingredient getTarget();

	@Accessor("dye")
	Ingredient getDye();

	@Accessor("result")
	ItemStackTemplate getResult();
	//?}


}
