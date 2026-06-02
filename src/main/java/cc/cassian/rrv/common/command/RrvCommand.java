package cc.cassian.rrv.common.command;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.ServerConfigs;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.network.payload.sharing.ClientboundShareRecipePayload;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
                        .then(RrvCommand.shareRecipeConfig())
//                        .then(Commands.literal("reloadRecipes").executes(RrvCommand::reloadRecipes)) removed as there's not much use post RRV 8
                        .then(Commands.literal("reload_stack_sensitives").executes(RrvCommand::reloadStackSensitives))
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> shareRecipe() {
        return Commands.literal("share_recipe").then(argument("id", StringArgumentType.string())
                .executes(context -> {
                    if (!ServerConfigs.SERVER_SETTINGS.isRecipeSharing()) {
                        context.getSource().sendFailure(Component.translatableWithFallback("rrv.sharing.denied","Recipe sharing is not enabled on this server"));
                        return -1;
                    }
                    final String text = StringArgumentType.getString(context, "id");
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

    private static ArgumentBuilder<CommandSourceStack, ?> shareRecipeConfig() {
        return Commands.literal("recipe_sharing").then(argument("enabled", BoolArgumentType.bool())
                .executes(context -> {
                    final boolean enabled = BoolArgumentType.getBool(context, "enabled");
                    ServerConfigs.SERVER_SETTINGS.setRecipeSharing(enabled);
                    ReliableRecipeViewer.saveServerConfigs();
                    context.getSource().sendSuccess(()-> Component.literal("Recipe sharing has been %s on this server".formatted(enabled ? "enabled" : "disabled")), true);
                    return 1;
                }));
    }

}
