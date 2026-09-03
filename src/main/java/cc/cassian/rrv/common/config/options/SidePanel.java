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
	BOOKMARKS(SidePanelContents::bookmarks, ReliableRecipeViewer.of("craftables")),
	CRAFTABLES(SidePanelContents::craftables, ReliableRecipeViewer.of("bookmarks")),
	DISABLED(SidePanelContents::disabled, ReliableRecipeViewer.of("disabled"));

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

	public Identifier getId() {
		return name;
	}
}
