package cc.cassian.rrv.common.config.options;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Map;

public enum IndexSource implements StringRepresentable {
	RESOURCE_PACKS,
	UNIQUE_RECIPE_OUTPUT,
	CREATIVE,
	REGISTRY;

	public static final Map<IndexSource, Boolean> DEFAULT = Map.of(RESOURCE_PACKS, true, UNIQUE_RECIPE_OUTPUT, true, CREATIVE, true, REGISTRY, true);

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	private static final Codec<IndexSource> SOURCE_CODEC = StringRepresentable.fromEnum(IndexSource::values);
	public static final Codec<Map<IndexSource, Boolean>> CODEC = Codec.unboundedMap(IndexSource.SOURCE_CODEC, Codec.BOOL);
}
