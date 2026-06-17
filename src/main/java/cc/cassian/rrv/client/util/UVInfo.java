package cc.cassian.rrv.client.util;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public record UVInfo(float u0, float u1, float v0, float v1) {
	public static UVInfo getUVInfo(TextureAtlasSprite sprite) {
		return new UVInfo(sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
	}
}
