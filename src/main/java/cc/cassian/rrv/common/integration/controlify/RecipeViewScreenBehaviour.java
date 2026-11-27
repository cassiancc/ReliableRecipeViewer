package cc.cassian.rrv.common.integration.controlify;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import dev.isxander.controlify.api.action.BuiltinActions;
import dev.isxander.controlify.api.controller.Controller;
import dev.isxander.controlify.api.screenop.ScreenBehaviour;
import dev.isxander.controlify.api.screenop.ScreenContext;
import org.jspecify.annotations.NonNull;

public class RecipeViewScreenBehaviour implements ScreenBehaviour {

	private final ScreenContext<? extends RecipeViewScreen> context;

	public RecipeViewScreenBehaviour(ScreenContext<? extends RecipeViewScreen> context) {
		this.context = context;
	}

	@Override
	public void handleNavigation(@NonNull Controller controller, @NonNull Runnable next) {
		while (BuiltinActions.GUI_PREV_TAB.on(controller).pulse().consumePulse()) {
			this.context.screen().getMenu().prevRecipe(null);
			this.context.playClickSound();
		}
		while (BuiltinActions.GUI_NEXT_TAB.on(controller).pulse().consumePulse()) {
			this.context.screen().getMenu().nextRecipe(null);
			this.context.playClickSound();
		}

		next.run();
	}
}
