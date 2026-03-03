package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.instances.ClientConfig;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum WrapScrolling implements StringRepresentable {
	ENABLED,
	DISABLED,
	ON_BUTTONS;

	public static boolean shouldWrapScroll(Button button) {
		if (Configs.CLIENT_SETTINGS.isWrapScrolling().equals(WrapScrolling.ENABLED)) {
			return true;
		} else if (Configs.CLIENT_SETTINGS.isWrapScrolling().equals(WrapScrolling.ON_BUTTONS) && button != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<WrapScrolling> CODEC = StringRepresentable.fromEnum(WrapScrolling::values);
}
