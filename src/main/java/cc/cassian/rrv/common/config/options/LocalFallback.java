package cc.cassian.rrv.common.config.options;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum LocalFallback implements StringRepresentable {
	ENABLED, WHEN_NEEDED, DISABLED;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<LocalFallback> CODEC = StringRepresentable.fromEnum(LocalFallback::values);
}
