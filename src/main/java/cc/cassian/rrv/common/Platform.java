package cc.cassian.rrv.common;

//? fabric {
import cc.cassian.rrv.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import cc.cassian.rrv.neoforge.NeoforgePlatformImpl;
*///?}
import cc.cassian.rrv.common.resolver.RRVClientResolver;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new NeoforgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);
    String loader();

    String getModNameForItem(ItemStack stack);

    RRVClientResolver.UVInfo getUVInfo(TextureAtlasSprite sprite);

    boolean isClientSide();

}
