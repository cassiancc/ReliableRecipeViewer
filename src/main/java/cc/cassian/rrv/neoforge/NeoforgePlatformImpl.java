package cc.cassian.rrv.neoforge;

//? neoforge {
/*import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.resolver.RRVClientResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
import org.apache.commons.lang3.text.WordUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class NeoforgePlatformImpl implements Platform {

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public String loader() {
        return "neoforge";
    }

    @Override
    public String getModNameForNamespace(String namespace) {
        String key = "modmenu.nameTranslation."+namespace;
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(namespace);
        if (modContainer.isPresent()) {
            return modContainer.get().getModInfo().getDisplayName();
        } else if (I18n.exists(key)) {
            return I18n.get(key);
        } else {
            return WordUtils.capitalize(namespace);
        }
    }

    @Override
    public String getModNamespaceForItem(ItemStack stack) {
        String namespace = "minecraft";
        if (Minecraft.getInstance().level != null) {
            namespace = stack.getItem().getCreatorModId(Minecraft.getInstance().level.registryAccess(), stack);
            var holderNamespace = stack.typeHolder().unwrapKey().orElseThrow().identifier().getNamespace();
            if (holderNamespace.equals(namespace) && stack.has(DataComponents.ITEM_MODEL)) {
                namespace = stack.get(DataComponents.ITEM_MODEL).getNamespace();
            }
        }
        return namespace;
    }

    @Override
    public ArrayList<String> getMods() {
        ArrayList<String> ids = new ArrayList<>();
        ModList.get().getMods().forEach(mod -> ids.add(mod.getModId()));
        return ids;
    }

    @Override
    public RRVClientResolver.UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new RRVClientResolver.UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

    @Override
    public boolean isClientSide() {
        return FMLEnvironment.getDist().isClient();
    }

    @Override
    public boolean isLoadingLoaded(String mod) {
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(mod) != null;
    }

    @Override
    public boolean isDevelopment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getDataDirectory() {
        return FMLPaths.GAMEDIR.get().resolve("data");
    }

}
*///?}