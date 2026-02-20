package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.common.overlay.OverlayManager;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SidePanel implements StringRepresentable {
	BOOKMARKS,
	CRAFTABLES,
	DISABLED;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<SidePanel> CODEC = StringRepresentable.fromEnum(SidePanel::values);
}
