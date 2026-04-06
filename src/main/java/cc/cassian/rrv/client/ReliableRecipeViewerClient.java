package cc.cassian.rrv.client;

import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.mojang.blaze3d.platform.InputConstants;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static cc.cassian.rrv.common.ReliableRecipeViewer.MOD_ID;

public class ReliableRecipeViewerClient {

    public static final ModelLayerLocation FLUID_ITEM_MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(MOD_ID, "fluiditem"), "inventory");

    public static final KeyMapping.Category RRV_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "rrv"));
    public static final KeyMapping.Category RRV_ADMIN_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "rrv_admin"));
    
    public static final KeyMapping USAGE_KEYBIND = new KeyMapping("key.rrv.usage", GLFW.GLFW_KEY_U, RRV_CATEGORY);

    public static final KeyMapping RECIPE_KEYBIND = new KeyMapping("key.rrv.recipe", GLFW.GLFW_KEY_R, RRV_CATEGORY);

    public static final KeyMapping TOGGLE_OVERLAY_KEYBIND = new KeyMapping("key.rrv.toggle_overlay", GLFW.GLFW_KEY_UNKNOWN, RRV_CATEGORY);

    public static final KeyMapping ADD_BOOKMARK_KEYBIND = new KeyMapping("key.rrv.bookmark", GLFW.GLFW_KEY_A, RRV_CATEGORY);

    public static final KeyMapping GO_BACK_RECIPE = new KeyMapping("key.rrv.go_back", InputConstants.Type.MOUSE, 3, RRV_CATEGORY);
    public static final KeyMapping GO_FORWARD_RECIPE = new KeyMapping("key.rrv.go_forward", InputConstants.Type.MOUSE, 4, RRV_CATEGORY);

    public static final KeyMapping USE_CHEATMODE = new KeyMapping("key.rrv.cheatmode", GLFW.GLFW_KEY_LEFT_ALT, RRV_ADMIN_CATEGORY);

    public static final List<KeyMapping> RRV_KEY_MAPPINGS = List.of(USAGE_KEYBIND, RECIPE_KEYBIND, TOGGLE_OVERLAY_KEYBIND, ADD_BOOKMARK_KEYBIND, GO_BACK_RECIPE, GO_FORWARD_RECIPE, USE_CHEATMODE);

    private static final Platform HELPER = Platform.INSTANCE;


    public static void bootstrap() {
        OverlayManager.registerOverlay(ItemViewOverlay.INSTANCE);
        OverlayManager.registerOverlay(SidePanelOverlay.INSTANCE);
    }

    public static Platform resolver() {
        return HELPER;
    }


    public static void loadConfigs() {
        Configs.CLIENT_SETTINGS.load();
        Configs.BOOKMARKS.load();
        Configs.CATEGORIES.load();
    }

    public static void saveConfigs() {
        Configs.CLIENT_SETTINGS.save();
        Configs.BOOKMARKS.save();
        Configs.CATEGORIES.save();
    }

    public static boolean isCheatmodeActive() {
        return Minecraft.getInstance().player != null && RrvUtil.hasPermission(Minecraft.getInstance().player) && InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), ReliableRecipeViewerClient.USE_CHEATMODE.key.getValue());
    }

}
