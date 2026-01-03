package de.crafty.eiv.neoforge;

//? neoforge {
/*import de.crafty.eiv.common.Platform;
import net.neoforged.fml.ModList;

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
    public String getModNameForItem(Item item) {
        if(FMLLoader.getCurrentOrNull() == null)
            return "???";

        return FMLLoader.getCurrent().getLoadingModList().getMods().stream().filter(modInfo -> modInfo.getModId().equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())).findFirst().get().getDisplayName();
    }

    @Override
    public UVInfo getUVInfo(TextureAtlasSprite sprite) {
        return new UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

}
*///?}