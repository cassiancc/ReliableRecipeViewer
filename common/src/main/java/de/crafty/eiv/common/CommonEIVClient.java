package de.crafty.eiv.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.overlay.itemlist.bookmark.ItemBookmarkOverlay;
import de.crafty.eiv.common.overlay.OverlayManager;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.resolver.IEivClientResolver;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static de.crafty.eiv.common.CommonEIV.LOGGER;
import static de.crafty.eiv.common.CommonEIV.MODID;

public class CommonEIVClient {

    public static final ModelLayerLocation FLUID_ITEM_MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(MODID, "fluiditem"), "inventory");

    public static final MenuType<RecipeViewMenu> RECIPE_VIEW_MENU = new MenuType<>(RecipeViewMenu::new, FeatureFlagSet.of());


    public static final KeyMapping.Category EIV_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MODID, "eiv"));
    public static final KeyMapping.Category EIV_ADMIN_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MODID, "eiv_admin"));
    
    public static final KeyMapping USAGE_KEYBIND = new KeyMapping("key.eiv.usage", GLFW.GLFW_KEY_U, EIV_CATEGORY);

    public static final KeyMapping RECIPE_KEYBIND = new KeyMapping("key.eiv.recipe", GLFW.GLFW_KEY_R, EIV_CATEGORY);

    public static final KeyMapping TOGGLE_OVERLAY_KEYBIND = new KeyMapping("key.eiv.toggle_overlay", GLFW.GLFW_KEY_UNKNOWN, EIV_CATEGORY);

    public static final KeyMapping ADD_BOOKMARK_KEYBIND = new KeyMapping("key.eiv.bookmark", GLFW.GLFW_KEY_A, EIV_CATEGORY);

    public static final KeyMapping GO_BACK_RECIPE = new KeyMapping("key.eiv.go_back", InputConstants.Type.MOUSE, 3, EIV_CATEGORY);
    public static final KeyMapping GO_FORWARD_RECIPE = new KeyMapping("key.eiv.go_forward", InputConstants.Type.MOUSE, 4, EIV_CATEGORY);

    public static final KeyMapping USE_CHEATMODE = new KeyMapping("key.eiv.cheatmode", GLFW.GLFW_KEY_LEFT_ALT, EIV_ADMIN_CATEGORY);

    public static final List<KeyMapping> EIV_KEY_MAPPINGS = List.of(USAGE_KEYBIND, RECIPE_KEYBIND, TOGGLE_OVERLAY_KEYBIND, ADD_BOOKMARK_KEYBIND, GO_BACK_RECIPE, GO_FORWARD_RECIPE, USE_CHEATMODE);

    private static IEivClientResolver HELPER = null;


    public static void boostrap() {
        OverlayManager.registerOverlay(ItemViewOverlay.INSTANCE);
        OverlayManager.registerOverlay(ItemBookmarkOverlay.INSTANCE);
    }

    public static void setResolver(final IEivClientResolver helper) {
        HELPER = helper;
        LOGGER.info("ClientResolver has been set");
    }

    public static IEivClientResolver resolver() {
        if (HELPER != null)
            return HELPER;

        throw new IllegalStateException("ClientResolver not set");
    }


    public static void loadConfigs() {
        Configs.CLIENT_SETTINGS.load();
        Configs.BOOKMARKS.load();
    }

    public static void saveConfigs() {
        Configs.CLIENT_SETTINGS.save();
        Configs.BOOKMARKS.save();
    }

    public static boolean isCheatmodeActive() {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) && InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), CommonEIVClient.USE_CHEATMODE.key.getValue());
    }

}
