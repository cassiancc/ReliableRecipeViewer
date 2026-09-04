package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelContents;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Function;

public enum SidePanel implements StringRepresentable {
	BOOKMARKS(1, SidePanelContents::bookmarks, ReliableRecipeViewer.of("craftables")),
	CRAFTABLES(2, SidePanelContents::craftables, ReliableRecipeViewer.of("bookmarks")),
	UNLOCKED(3, SidePanelContents::craftables, ReliableRecipeViewer.of("bookmarks")),
	DISABLED(0, SidePanelContents::disabled, ReliableRecipeViewer.of("disabled"));

	private final Function<SidePanelContents, List<ItemStack>> stacks;
	private final Identifier name;

	SidePanel(Function<SidePanelContents, List<ItemStack>> stacks, Identifier id) {
		this.stacks = stacks;
		this.name = id;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<SidePanel> CODEC = StringRepresentable.fromEnum(SidePanel::values);

	public List<ItemStack> getStacks(SidePanelContents sidePanelOverlay) {
		return stacks.apply(sidePanelOverlay);
	}

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

    public static void next(boolean skipDisabled) {
		int id1 = Configs.CLIENT_SETTINGS.getSidePanel().id + 1;
		if (id1 > 3) id1 = 0;
		if (id1 == 0 && skipDisabled) id1 = 1;
		Configs.CLIENT_SETTINGS.setSidePanel(get(id1));
    }

	public static void back() {
		int id1 = Configs.CLIENT_SETTINGS.getSidePanel().id - 1;
		if (id1 < 0) id1 = 3;
		Configs.CLIENT_SETTINGS.setSidePanel(get(id1));
	}

	public Identifier getId() {
		return name;
	}
}
