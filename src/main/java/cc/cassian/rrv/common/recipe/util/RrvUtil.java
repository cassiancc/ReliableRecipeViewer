package cc.cassian.rrv.common.recipe.util;

import cc.cassian.rrv.common.api.recipe.IRrvClientRecipe;
import net.minecraft.client.gui.screens.Screen;

public class RrvUtil {


    public static boolean matchesAnyTransferClass(IRrvClientRecipe viewRecipe, Screen playerScreen) {
        if (playerScreen == null)
            return false;

        return viewRecipe.getTransferClasses().stream().anyMatch(screenClass -> screenClass.isInstance(playerScreen));
    }


}
