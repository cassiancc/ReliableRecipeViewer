package cc.cassian.rrv.common.config.options;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum NamespaceTooltip implements StringRepresentable {
	SHOW,
	IN_ITEM_VIEW,
	HIDE;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<NamespaceTooltip> CODEC = StringRepresentable.fromEnum(NamespaceTooltip::values);
}
