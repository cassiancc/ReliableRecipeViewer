package cc.cassian.rrv.common.integration.controlify;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import dev.isxander.controlify.api.ControlifyEntrypoint;
import dev.isxander.controlify.api.screenop.ScreenopEvents;

public class RRVControlifyEntrypoint implements ControlifyEntrypoint {
    @Override
    public void onControlifyInitialize() {
        ScreenopEvents.REGISTER_SCREEN_BEHAVIOURS.subscribeForever(registry -> {
            registry.<RecipeViewScreen>register(RecipeViewScreen.class, RecipeViewScreenBehaviour::new);
        });
    }
}
