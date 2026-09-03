package cc.cassian.rrv.client.util;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.gui.ClientConfigScreen;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.jei.JeiCompatibilityUtil;
import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ApiStatus.Internal
public class RRVClientUtil {
    private static final ArrayList<String> INITIALIZED_MODS = new ArrayList<>();
    public static final Identifier CONTAINER = Identifier.withDefaultNamespace("container");
    public static Boolean hasPermissions = null;

    public static ArrayList<String> getInitializedMods() {
        return INITIALIZED_MODS;
    }

    public static void setScreen(Screen newScreen) {
        //? if >26.1 {
        /*Minecraft.getInstance().gui.setScreen(newScreen);
         *///?} else {
        Minecraft.getInstance().setScreen(newScreen);
        //?}
    }

    public static void setToParentScreen() {
        setScreen(getParentScreen());
    }

    private static Screen getParentScreen() {
        var oldScreen = currentScreen();
        if (oldScreen instanceof RecipeViewScreen recipeViewScreen) {
            return recipeViewScreen.getMenu().getParentScreen();
        }
        else if (oldScreen instanceof ClientConfigScreen clientConfigScreen) {
            return clientConfigScreen.getParentScreen();
        }
        else if (ModCompat.JEI) {
            return JeiCompatibilityUtil.getJeiParentScreen(oldScreen);
        }
        else return null;
    }

    public static Screen currentScreen() {
        //? if >26.1 {
        /*return Minecraft.getInstance().gui.screen();
         *///?} else {
        return Minecraft.getInstance().screen;
        //?}
    }

    public static boolean isKeyDown(KeyMapping key) {
        if (key.isUnbound()) return false;
        return InputConstants.isKeyDown(
                //? if <26.3
                Minecraft.getInstance().getWindow(),
                key.key.getValue());
    }

    public static boolean showDebugScreen() {
        return Configs.CLIENT_SETTINGS.showExclusionAreas();
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
    public static List<Component> getTooltipFromItem(final ItemStack itemStack) {
        return getTooltipFromItem(Minecraft.getInstance(), itemStack);
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

    /// Initialize client entrypoints from `fabric.mod.json` or `neoforge.mods.toml` files.
    public static void initializeEntrypoint(String modId, ReliableRecipeViewerClientPlugin plugin) {
        ReliableRecipeViewer.LOGGER.debug("RRV: Loading client integration from mod: {}", modId);
        try {
            if (!INITIALIZED_MODS.contains(modId)) {
                plugin.onIntegrationInitialize();
                ReliableRecipeViewer.LOGGER.info("RRV: Client integration initialized for mod: {}", modId);
                INITIALIZED_MODS.add(modId);
            } else {
                ReliableRecipeViewer.LOGGER.debug("RRV: Skipped initializing client integration for multi-loader mod: {}", modId);
            }
            return;
        } catch (Exception e) {
            ReliableRecipeViewer.LOGGER.error("RRV: Failed to load client integration from mod: {} due to {}", modId, e);
        }
    }

    public static void sendMessage(Component message) {
        Minecraft.getInstance().player
                //? if >26 {
                .sendSystemMessage(message);
                 //?} else {
                /*.displayClientMessage(message, false);
        *///?}
    }

    public static int compare(ReliableClientRecipe first, ReliableClientRecipe second) {
        return Comparator.comparingInt((ReliableClientRecipe value) -> value.getType().getPriority()).thenComparingInt(ReliableClientRecipe::getPriority).thenComparing(ReliableClientRecipe::entryId).compare(first, second);
    }

	public static Player player() {
		return Minecraft.getInstance().player;
	}

	public static boolean showOperatorItems(Player player) {
        if (hasPermissions != null) {
            return hasPermissions;
        }
		return player.canUseGameMasterBlocks() && Minecraft.getInstance().options.operatorItemsTab().get();
	}
}
