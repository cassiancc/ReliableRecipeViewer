package cc.cassian.rrv.common.integration.controlify;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import dev.isxander.controlify.api.vmousesnapping.SnapPoint;
import dev.isxander.controlify.bindings.ControlifyBindings;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Optional;

public class RecipeViewScreenProcessor extends ScreenProcessor<RecipeViewScreen> {

	public RecipeViewScreenProcessor(RecipeViewScreen screen) {
		super(screen);
	}

	@Override
	protected void handleButtons(ControllerEntity controller) {
		super.handleButtons(controller);
		// handle binds and other button actions here.
		// THIS IS ONLY CALLED WHEN VMOUSE IS OFF
	}

	@Override
	protected void handleScreenVMouse(ControllerEntity controller, VirtualMouseHandler vmouse) {
		// do what you want here, called every tick only if vmouse is enabled
	}

	@Override
	protected void handleTabNavigation(ControllerEntity controller) {
		super.handleTabNavigation(controller);
		// handles the vanilla tab widget found in the create new world screen
		// called regardless of vmouse state
		if (ControlifyBindings.GUI_PREV_TAB.on(controller).justReleased()) {
			screen.getMenu().prevPage();
		}
		if (ControlifyBindings.GUI_NEXT_TAB.on(controller).justReleased()) {
			screen.getMenu().nextRecipe();
		}
	}

	@Override
	public void onWidgetRebuild() {
		super.onWidgetRebuild();
		// called after the Screen#init() method
	}

	@Override
	protected void render(ControllerEntity controller, GuiGraphicsExtractor graphics, float tickDelta, Optional<VirtualMouseHandler> vmouse) {

	}

	@Override
	public VirtualMouseBehaviour virtualMouseBehaviour() {
		// specifies how the vmouse should be used in this screen.
		// options are: DEFAULT, DISABLED, ENABLED, CURSOR_ONLY
		// DEFAULT allows the user to turn on/off, and is off by default.
		return VirtualMouseBehaviour.DEFAULT;
	}
}
