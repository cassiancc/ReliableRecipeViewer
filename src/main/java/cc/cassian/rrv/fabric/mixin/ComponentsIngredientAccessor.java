//? fabric {
package cc.cassian.rrv.fabric.mixin;

import net.fabricmc.fabric.impl.recipe.ingredient.builtin.ComponentsIngredient;
import net.minecraft.core.component.DataComponentPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ComponentsIngredient.class)
public interface ComponentsIngredientAccessor {
	@Invoker
	DataComponentPatch callGetComponents();
}
//?}