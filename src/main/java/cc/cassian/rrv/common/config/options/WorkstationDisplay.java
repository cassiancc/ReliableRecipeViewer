package cc.cassian.rrv.common.config.options;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum WorkstationDisplay implements StringRepresentable {
	IN_FOOTER,
	IN_SIDEBAR,
	HIDE;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<WorkstationDisplay> CODEC = StringRepresentable.fromEnum(WorkstationDisplay::values);
}
