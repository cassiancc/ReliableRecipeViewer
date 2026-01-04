package cc.cassian.rrv.common.recipe.util;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RrvUtil {


    public static boolean matchesAnyTransferClass(ReliableClientRecipe viewRecipe, Screen playerScreen) {
        if (playerScreen == null)
            return false;

        return viewRecipe.getTransferClasses().stream().anyMatch(screenClass -> screenClass.isInstance(playerScreen));
    }


}
