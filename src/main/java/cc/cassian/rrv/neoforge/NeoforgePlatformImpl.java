package cc.cassian.rrv.neoforge;

//? neoforge {
/*import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.commons.lang3.text.WordUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

public class NeoforgePlatformImpl implements RRVPlatform {

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
        } else if (RrvUtil.has(key)) {
            return RrvUtil.get(key);
        } else {
            return WordUtils.capitalize(namespace);
        }
    }

    @Override
    public String getModNamespaceForItem(ItemStack stack) {
        String namespace = "minecraft";
        if (Minecraft.getInstance().level != null) {
            namespace = stack.getItem().getCreatorModId(Minecraft.getInstance().level.registryAccess(), stack);
            //~ if >26 'getItemHolder'->'typeHolder'
            var holderNamespace = stack.typeHolder().unwrapKey().orElseThrow().identifier().getNamespace();
            if (holderNamespace.equals(namespace) && stack.hasNonDefault(DataComponents.ITEM_MODEL)) {
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