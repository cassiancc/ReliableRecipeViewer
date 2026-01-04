package de.crafty.eiv.neoforge;

//? neoforge {
/*import de.crafty.eiv.common.Platform;
import de.crafty.eiv.common.resolver.IEivClientResolver;
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
    public IEivClientResolver.UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new IEivClientResolver.UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

}
*///?}