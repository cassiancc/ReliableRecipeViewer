package cc.cassian.rrv.common.recipe.util;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RrvUtil {


    public static boolean matchesAnyTransferClass(ReliableClientRecipe viewRecipe, Screen playerScreen) {
        if (playerScreen == null)
            return false;

        return viewRecipe.getTransferClasses().stream().anyMatch(screenClass -> screenClass.isInstance(playerScreen));
    }

    public static boolean hasPermission(Player sender) {
        //? if <1.21.11 {
        /*return sender.hasPermissions(2);
        *///?} else {
        return sender.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
        //?}
    }

    public static boolean hasPermission(CommandSourceStack sender) {
        //? if <1.21.11 {
        /*return sender.hasPermission(2);
        *///?} else {
        return sender.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
         //?}
    }


}
