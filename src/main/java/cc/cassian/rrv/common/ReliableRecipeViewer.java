package cc.cassian.rrv.common;

import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReliableRecipeViewer {

    public static final String MOD_ID = "rrv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Reliable Recipe Viewer");

    public static final String CONFIG_PATH = "config/rrv/";

    public static final MenuType<RecipeViewMenu> RECIPE_VIEW_MENU = new MenuType<>(RecipeViewMenu::new, FeatureFlagSet.of());

    public static RrvNetworkManager networkManager(){
        return RrvNetworkManager.INSTANCE;
    }

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

}
