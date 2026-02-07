package cc.cassian.rrv.common.overlay.itemlist.bookmark;

import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BookmarkManager {

    public static final BookmarkManager INSTANCE = new BookmarkManager();

    protected List<ItemStack> availableItems = new ArrayList<>();

    public void bookmarkItem(ItemStack stack) {
        if (!this.availableItems().contains(stack)) {
            this.availableItems().add(stack);
            if (SidePanelOverlay.showBookmarks()) {
                Minecraft.getInstance().execute(SidePanelOverlay.INSTANCE::updateQuery);
			}
        }
    }

    public List<ItemStack> availableItems() {
        return availableItems;
    }

	public void removeItem(ItemStack stack) {
        if (this.availableItems().contains(stack)) {
            this.availableItems().remove(stack);
            if (SidePanelOverlay.showBookmarks())
                Minecraft.getInstance().execute(SidePanelOverlay.INSTANCE::updateQuery);
        }
	}
}
