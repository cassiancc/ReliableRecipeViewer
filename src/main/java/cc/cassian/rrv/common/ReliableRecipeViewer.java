package cc.cassian.rrv.common;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Random;

public class ReliableRecipeViewer {

    public static final String MOD_ID = "rrv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Reliable Recipe Viewer");

    public static final Path CONFIG_PATH = Platform.INSTANCE.getConfigDirectory().resolve("rrv");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final MenuType<RecipeViewMenu> RECIPE_VIEW_MENU = new MenuType<>(RecipeViewMenu::new, FeatureFlagSet.of());

    public static RrvNetworkManager networkManager(){
        return RrvNetworkManager.INSTANCE;
    }

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void loadServerConfigs() {
        Configs.SERVER_SETTINGS.load();
    }

    /// This is currently called whenever the server config changes as it is only used for recipe sharing, need to find a better target in MinecraftServer.
    public static void saveServerConfigs() {
        Configs.SERVER_SETTINGS.save();
    }

}
