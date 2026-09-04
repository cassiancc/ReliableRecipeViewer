package cc.cassian.rrv.fabric;

//? fabric {
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

public class FabricPlatformImpl implements RRVPlatform {

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
        } else if (RrvUtil.has(key)) {
            return RrvUtil.get(key);
        } else {
            return WordUtils.capitalize(namespace);
        }
    }

    @Override
    public String getModNamespaceForItem(ItemStack stack) {
        //~ if >26 'getItemHolder'-> 'typeHolder'
        var holderNamespace = stack.typeHolder().unwrapKey().map(ResourceKey::identifier).map(Identifier::getNamespace).orElse("");
        if (holderNamespace.isEmpty()) {
            return "";
        }
        String creatorNamespace = stack.getCreatorNamespace();
        if (holderNamespace.equals(creatorNamespace) && stack.hasNonDefault(DataComponents.ITEM_MODEL)) {
            return stack.get(DataComponents.ITEM_MODEL).getNamespace();
        }
        return creatorNamespace;
    }

    @Override
    public ArrayList<String> getMods() {
        ArrayList<String> modContainers = new ArrayList<>();
        FabricLoader.getInstance().getAllMods().forEach((mod)-> modContainers.add(mod.getMetadata().getId()));
        return modContainers;
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