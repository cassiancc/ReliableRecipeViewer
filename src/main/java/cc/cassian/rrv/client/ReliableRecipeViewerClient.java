package cc.cassian.rrv.client;

import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.options.NamespaceTooltip;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.mojang.blaze3d.platform.InputConstants;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeMap;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ReliableRecipeViewerClient {

    public static final ModelLayerLocation FLUID_ITEM_MODEL_LAYER = new ModelLayerLocation(ReliableRecipeViewer.of("fluiditem"), "inventory");

    public static final KeyMapping.Category RRV_CATEGORY = KeyMapping.Category.register(ReliableRecipeViewer.of("rrv"));
    public static final KeyMapping.Category RRV_ADMIN_CATEGORY = KeyMapping.Category.register(ReliableRecipeViewer.of("rrv_admin"));
    
    public static final KeyMapping USAGE_KEYBIND = new KeyMapping("key.rrv.usage", GLFW.GLFW_KEY_U, RRV_CATEGORY);

    public static final KeyMapping RECIPE_KEYBIND = new KeyMapping("key.rrv.recipe", GLFW.GLFW_KEY_R, RRV_CATEGORY);

    public static final KeyMapping TOGGLE_OVERLAY_KEYBIND = new KeyMapping("key.rrv.toggle_overlay", GLFW.GLFW_KEY_UNKNOWN, RRV_CATEGORY);

    public static final KeyMapping ADD_BOOKMARK_KEYBIND = new KeyMapping("key.rrv.bookmark", GLFW.GLFW_KEY_A, RRV_CATEGORY);

    public static final KeyMapping GO_BACK_RECIPE = new KeyMapping("key.rrv.go_back", InputConstants.Type.MOUSE, 3, RRV_CATEGORY);
    public static final KeyMapping GO_FORWARD_RECIPE = new KeyMapping("key.rrv.go_forward", InputConstants.Type.MOUSE, 4, RRV_CATEGORY);

    public static final KeyMapping USE_CHEATMODE = new KeyMapping("key.rrv.cheatmode", GLFW.GLFW_KEY_LEFT_ALT, RRV_ADMIN_CATEGORY);

    public static final List<KeyMapping> RRV_KEY_MAPPINGS = List.of(USAGE_KEYBIND, RECIPE_KEYBIND, TOGGLE_OVERLAY_KEYBIND, ADD_BOOKMARK_KEYBIND, GO_BACK_RECIPE, GO_FORWARD_RECIPE, USE_CHEATMODE);
    public static RecipeMap LOCAL_RECIPES = RecipeMap.EMPTY;

    public static void bootstrap() {
        OverlayManager.registerOverlay(ItemViewOverlay.INSTANCE);
        OverlayManager.registerOverlay(SidePanelOverlay.INSTANCE);
    }

    public static Platform resolver() {
        return Platform.INSTANCE;
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

    public static Component addNamespaceTooltip(ItemStack stack, List<Component> tooltip, boolean inItemView) {
        String modName = Platform.INSTANCE.getModNameForItem(stack);
        if (!ModCompat.hasModNamespaceModsInstalled()) {
            MutableComponent namespace = Component.literal(modName).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC);
            if (((inItemView && Configs.CLIENT_SETTINGS.showNamespaceTooltip().equals(NamespaceTooltip.IN_ITEM_VIEW)) ||
                    Configs.CLIENT_SETTINGS.showNamespaceTooltip().equals(NamespaceTooltip.SHOW))) {
                if (!tooltip.contains(namespace)) {
                    tooltip.addLast(namespace);
                    return namespace;
                }
            }
        }
        for (Component component : tooltip) {
            if (modName.equals(component.getString())) {
                return component;
            }
        }
        return null;
    }
}
