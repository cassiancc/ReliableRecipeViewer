package cc.cassian.rrv.client.util;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
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

    /// Modified copy of [Screen#getTooltipFromItem] that supports extended tooltip flags.
    public static List<Component> getTooltipFromItem(final Minecraft minecraft, final ItemStack itemStack) {
        ExtendedTooltipFlag tooltipFlag = minecraft.options.advancedItemTooltips ? ExtendedTooltipFlag.ADVANCED : ExtendedTooltipFlag.NORMAL;
        return itemStack.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, tooltipFlag);
    }

    public static ItemStack applyPolymerCheck(ItemStack i) {
        if (ModCompat.POLYMER && ClientPolymerItemUtils.isPolyItem(i)) {
            return ClientPolymerItemUtils.getServerItem(i);
        }
        return i;
    }
}
