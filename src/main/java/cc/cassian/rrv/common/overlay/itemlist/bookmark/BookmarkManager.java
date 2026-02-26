package cc.cassian.rrv.common.overlay.itemlist.bookmark;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.SidePanel;
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
            if (!SidePanelOverlay.showBookmarks()) {
                Configs.CLIENT_SETTINGS.setSidePanel(SidePanel.BOOKMARKS);
            }
            updateIndex("a newly bookmarked item!");
        }
    }

    private static void updateIndex(String reason) {
        Minecraft.getInstance().execute(() -> SidePanelOverlay.INSTANCE.updateSidePanelIndex(reason));
    }

    public List<ItemStack> availableItems() {
        return availableItems;
    }

	public void removeItem(ItemStack stack) {
        if (this.availableItems().contains(stack)) {
            this.availableItems().remove(stack);
            if (SidePanelOverlay.showBookmarks())
                updateIndex("a removed bookmarked item!");
        }
	}
}
