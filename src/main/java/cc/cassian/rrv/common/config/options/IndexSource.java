package cc.cassian.rrv.common.config.options;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum IndexSource implements StringRepresentable {
	CREATIVE_AND_REGISTRY,
	CREATIVE,
	REGISTRY;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<IndexSource> CODEC = StringRepresentable.fromEnum(IndexSource::values);
}
