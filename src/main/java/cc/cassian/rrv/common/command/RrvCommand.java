package cc.cassian.rrv.common.command;

import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.network.payload.sharing.ClientboundShareRecipePayload;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import static net.minecraft.commands.Commands.argument;

public class RrvCommand {


    /// Reloads all recipes and sends them to all clients
    ///
    /// **Note**: This does include deleting the cache and loading all recipes again, but it does not
    /// include a server reload (so recipes that depend on the vanilla recipe manager might be unaffected until
    /// the server is fully reloaded)
    private static int reloadRecipes(CommandContext<CommandSourceStack> context) {
        ServerRecipeManager.INSTANCE.reloadRecipes();
        ServerRecipeManager.INSTANCE.broadcastAllRecipes();
        context.getSource().sendSuccess(() -> Component.translatable("commands.rrv.reloadedRecipes"), true);
        return 1;
    }

    /// Sends the current list of all "item-variants" (stack-sensitives) to all clients
    ///
    /// **Note**: Does not actively update the list of stack-sensitives since this is done by the server recipe manager
    /// on a server reload (via ReloadCallback)
    private static int reloadStackSensitives(CommandContext<CommandSourceStack> context){
        ServerRecipeManager.INSTANCE.broadcastStackSensitives();
        context.getSource().sendSuccess(() -> Component.translatable("commands.rrv.reloadedStackSensitives"), true);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("rrv")
                        .then(RrvCommand.shareRecipe())
        );
        commandDispatcher.register(
                Commands.literal("rrv_admin")
                        .requires(RrvUtil::hasPermission)
//                        .then(Commands.literal("reloadRecipes").executes(RrvCommand::reloadRecipes)) removed as there's not much use post RRV 8
                        .then(Commands.literal("reloadStackSensitives").executes(RrvCommand::reloadStackSensitives))
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> shareRecipe() {
        return Commands.literal("shareRecipe").then(argument("shareRecipe", StringArgumentType.string())
                .executes(context -> {
                    final String text = StringArgumentType.getString(context, "shareRecipe");
                    final Identifier id = Identifier.tryParse(text);
                    if (id == null) return -1;
                    ServerRecipeManager.INSTANCE.getServer().getPlayerList().getPlayers().forEach(player -> {
                        if (RrvNetworkManager.canSend(player, ClientboundShareRecipePayload.TYPE)) {
                            Entity source = context.getSource().getEntity();
                            var uuid = source != null ? source.getUUID().toString() : "";
                            RrvNetworkManager.INSTANCE.sendPacket(player, new ClientboundShareRecipePayload(id, uuid, context.getSource().getDisplayName()));
                        }
                    });
                    return 1;
        }));
    }

}
