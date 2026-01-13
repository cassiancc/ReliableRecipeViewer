package cc.cassian.rrv.neoforge;

//? neoforge {
/*import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.resolver.RRVClientResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
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
    public String getModNameForItem(ItemStack stack) {
        String namespace = getModNamespaceForItem(stack);
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

}
*///?}