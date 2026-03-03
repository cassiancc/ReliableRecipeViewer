package cc.cassian.rrv.common.mixin.world.item.crafting;

import net.minecraft.world.item.crafting.Ingredient;
//? if >26 {
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.DyeRecipe;
//?} else {
/*import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
 *///?}

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if >26
@Mixin(DyeRecipe.class)
//? if <26
//@Mixin(ArmorDyeRecipe.class)
public interface DyeRecipeAccessor {
	//? if >26 {
	
	@Accessor("target")
	Ingredient getTarget();

	@Accessor("dye")
	Ingredient getDye();

	@Accessor("result")
	ItemStackTemplate getResult();
	//?} else {

    //?}


}
