package de.crafty.eiv.common.recipe.util;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import net.minecraft.client.gui.screens.Screen;

public class EivUtil {


    public static boolean matchesAnyTransferClass(IEivViewRecipe viewRecipe, Screen playerScreen) {
        if (playerScreen == null)
            return false;

        return viewRecipe.getTransferClasses().stream().anyMatch(screenClass -> screenClass.isInstance(playerScreen));
    }


}
