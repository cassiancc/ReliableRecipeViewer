package cc.cassian.rrv.common.mixin.integration;

import cc.cassian.rrv.common.integration.controlify.RecipeViewScreenProcessor;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeViewScreen.class)
public class ControlifyRecipeViewScreenMixin implements ScreenProcessorProvider {
	@Unique
	private final ScreenProcessor<?> processor =
			new RecipeViewScreenProcessor((RecipeViewScreen) (Object) this);

	@Override
	public ScreenProcessor<?> screenProcessor() {
		return this.processor;
	}
}
