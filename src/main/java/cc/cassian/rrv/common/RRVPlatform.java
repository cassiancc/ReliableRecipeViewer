package cc.cassian.rrv.common;

//? fabric {
import cc.cassian.rrv.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import cc.cassian.rrv.neoforge.NeoforgePlatformImpl;
*///?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.ArrayList;

@ApiStatus.Internal
public interface RRVPlatform {

    //? fabric {
    RRVPlatform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*RRVPlatform INSTANCE = new NeoforgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);

    String loader();

    String getModNameForNamespace(String namespace);

    default String getModNameForItem(ItemStack stack) {
        return getModNameForNamespace(getModNamespaceForItem(stack));
    }

    String getModNamespaceForItem(ItemStack stack);

    ArrayList<String> getMods();

    boolean isClientSide();

	boolean isLoadingLoaded(String mod);

	boolean isDevelopment();

    Path getConfigDirectory();

    Path getDataDirectory();


}
