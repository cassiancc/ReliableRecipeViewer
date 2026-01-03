package de.crafty.eiv.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

public class EivCommand {


    /**
     * Reloads all recipes and sends them to all clients
     * <br>
     * <br>
     * <b>Note</b>: This does include deleting the cache and loading all recipes again, but it does not
     * include a server reload (so recipes that depend on the vanilla recipe manager might be unaffected until
     * the server is fully reloaded)
     */
    private static int reloadRecipes(CommandContext<CommandSourceStack> context) {
        ServerRecipeManager.INSTANCE.reloadRecipes();
        ServerRecipeManager.INSTANCE.broadcastAllRecipes();
        context.getSource().sendSuccess(() -> Component.translatable("commands.eiv.reloadedRecipes"), true);
        return 1;
    }

    /**
     * Sends the current list of all "item-variants" (stack-sensitives) to all clients
     * <br>
     * <br>
     * <b>Note</b>: Does not actively update the list of stack-sensitives since this is done by the server recipe manager
     * on a server reload (via ReloadCallback)
     */
    private static int reloadStackSensitives(CommandContext<CommandSourceStack> context){
        ServerRecipeManager.INSTANCE.broadcastStackSensitives();
        context.getSource().sendSuccess(() -> Component.translatable("commands.eiv.reloadedStackSensitives"), true);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("eiv")
                        .requires(commandSourceStack -> commandSourceStack.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .then(Commands.literal("reloadRecipes").executes(EivCommand::reloadRecipes))
                        .then(Commands.literal("reloadStackSensitives").executes(EivCommand::reloadStackSensitives))
        );
    }

}
