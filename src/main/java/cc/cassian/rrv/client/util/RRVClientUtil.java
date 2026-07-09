package cc.cassian.rrv.client.util;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

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

    /// Modified copy of [Screen#getTooltipFromItem].
    /// TODO: In the future, this will support NeoForge's [TooltipFlag] extension for recipe viewers ([#3286](https://github.com/neoforged/NeoForge/pull/3286)).
    public static List<Component> getTooltipFromItem(final Minecraft minecraft, final ItemStack itemStack) {
        return itemStack.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
    }
}
