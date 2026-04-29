package cc.cassian.rrv.client.util;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RRVClientUtil {
    public static final Identifier CONTAINER = Identifier.withDefaultNamespace("container");

    public static void setScreen(Screen newScreen) {
        //? if >26.1 {
        /*Minecraft.getInstance().gui.setScreen(newScreen);
         *///?} else {
        Minecraft.getInstance().setScreen(newScreen);
        //?}
    }

    public static Screen currentScreen() {
        //? if >26.1 {
        /*return Minecraft.getInstance().gui.screen();
         *///?} else {
        return Minecraft.getInstance().screen;
        //?}
    }

    public static boolean showDebugScreen() {
        return false;
    }

    public static boolean matchesAnyTransferClass(ReliableClientRecipe clientRecipe, Screen playerScreen) {
        if (playerScreen == null)
            return false;

        return clientRecipe.getTransferClasses().stream().anyMatch(screenClass -> screenClass.isInstance(playerScreen));
    }

	public static Level level() {
		return Minecraft.getInstance().level;
	}
}
