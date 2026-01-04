package de.crafty.eiv.common;

//? fabric {
import de.crafty.eiv.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import de.crafty.eiv.neoforge.NeoforgePlatformImpl;
*///?}
import de.crafty.eiv.common.resolver.IEivClientResolver;
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

    IEivClientResolver.UVInfo getUVInfo(TextureAtlasSprite sprite);

}
