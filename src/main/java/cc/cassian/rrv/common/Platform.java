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

import java.util.ArrayList;

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

    String getModNamespaceForItem(ItemStack stack);

    ArrayList<String> getMods();

    RRVClientResolver.UVInfo getUVInfo(TextureAtlasSprite sprite);

    boolean isClientSide();

}
