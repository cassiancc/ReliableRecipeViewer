package cc.cassian.rrv.neoforge;

//? neoforge {
/*import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.resolver.IRrvClientResolver;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

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
    public String getModNameForItem(ItemStack item) {
        if(FMLLoader.getCurrentOrNull() == null)
            return "???";

        return FMLLoader.getCurrent().getLoadingModList().getMods().stream().filter(modInfo -> modInfo.getModId().equals(BuiltInRegistries.ITEM.getKey(item.getItem()).getNamespace())).findFirst().get().getDisplayName();
    }

    @Override
    public IRrvClientResolver.UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new IRrvClientResolver.UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

}
*///?}