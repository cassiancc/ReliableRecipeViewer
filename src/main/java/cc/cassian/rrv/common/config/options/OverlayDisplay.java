package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.common.overlay.OverlayManager;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum OverlayDisplay implements StringRepresentable {
	ENABLED,
	DISABLED,
	WHEN_SEARCHING,
	WITH_ITEM_VIEW;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<OverlayDisplay> CODEC = StringRepresentable.fromEnum(OverlayDisplay::values);
}
