package cc.cassian.rrv.common.resolver;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;


/**
 * Resolvers are used to handle modloader dependent information like mod names or UV infos
 */
public interface RRVClientResolver {

    String getModNameForItem(ItemStack item);

    UVInfo getUVInfo(TextureAtlasSprite sprite);


    record UVInfo(float u0, float u1, float v0, float v1) {
    }

}
