package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.OverlayManager;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SidePanel implements StringRepresentable {
	BOOKMARKS(0),
	CRAFTABLES(1),
	UNLOCKED(2),
	DISABLED(3);

	private final int id;

	SidePanel(int id) {
		this.id = id;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<SidePanel> CODEC = StringRepresentable.fromEnum(SidePanel::values);

	private static synchronized SidePanel get(int id) {
		SidePanel[] values = values();
		for (SidePanel type : values) {
			if (type.getId() == id) {
				return type;
			}
		}
		return SidePanel.DISABLED;
	}

	public int getId() {
		return id;
	}

    public void next() {
		int id1 = Configs.CLIENT_SETTINGS.getSidePanel().id + 1;
		if (id1 > 3) id1 = 0;
		Configs.CLIENT_SETTINGS.setSidePanel(get(id1));
    }

	public void back() {
		int id1 = Configs.CLIENT_SETTINGS.getSidePanel().id - 1;
		if (id1 < 0) id1 = 3;
		Configs.CLIENT_SETTINGS.setSidePanel(get(id1));
	}
}
