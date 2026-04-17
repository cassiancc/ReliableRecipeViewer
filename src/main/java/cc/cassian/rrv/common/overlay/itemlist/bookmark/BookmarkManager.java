package cc.cassian.rrv.common.overlay.itemlist.bookmark;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;

public class BookmarkManager {

    public static final BookmarkManager INSTANCE = new BookmarkManager();

    protected List<ItemStackTemplate> availableItems = new ArrayList<>();

    public void bookmarkItem(ItemStack stack) {
        ItemStackTemplate template = ItemStackTemplate.fromNonEmptyStack(stack);
        if (!this.availableItems.contains(template)) {
            this.availableItems().add(template);
            if (!SidePanelOverlay.showBookmarks()) {
                Configs.CLIENT_SETTINGS.setSidePanel(SidePanel.BOOKMARKS);
            }
            updateIndex("a newly bookmarked item!");
        }
    }
    private static void updateIndex(String reason) {
        Minecraft.getInstance().execute(() -> SidePanelOverlay.INSTANCE.updateSidePanelIndex(reason));
    }

    public List<ItemStackTemplate> availableItems() {
        return availableItems;
    }

    public List<ItemStack> displayItems() {
        return availableItems().stream().map(ItemStackTemplate::create).toList();
    }

	public void removeItem(ItemStack stack) {
        removeItem(ItemStackTemplate.fromNonEmptyStack(stack));
	}

    public void removeItem(ItemStackTemplate stack) {
        if (this.availableItems().contains(stack)) {
            this.availableItems().remove(stack);
            if (SidePanelOverlay.showBookmarks())
                updateIndex("a removed bookmarked item!");
        }
    }
}
