package cc.cassian.rrv.fabric;

//? fabric {
import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.resolver.RRVClientResolver;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

public class FabricPlatformImpl implements Platform {

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public String loader() {
        return "fabric";
    }

    @Override
    public String getModNameForNamespace(String namespace) {
        String key = "modmenu.nameTranslation."+namespace;
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(namespace);
        if (modContainer.isPresent()) {
            return modContainer.get().getMetadata().getName();
        } else if (I18n.exists(key)) {
            return I18n.get(key);
        } else {
            return WordUtils.capitalize(namespace);
        }
    }

    @Override
    public String getModNamespaceForItem(ItemStack stack) {
        return stack.getCreatorNamespace();
    }

    @Override
    public ArrayList<String> getMods() {
        ArrayList<String> modContainers = new ArrayList<>();
        FabricLoader.getInstance().getAllMods().forEach((mod)-> modContainers.add(mod.getMetadata().getId()));
        return modContainers;
    }


    @Override
    public RRVClientResolver.UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new RRVClientResolver.UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

    @Override
    public boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @Override
    public boolean isLoadingLoaded(String mod) {
        return isModLoaded(mod);
    }

    @Override
    public boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getDataDirectory() {
        return FabricLoader.getInstance().getGameDir().resolve("data");
    }


}
//?}