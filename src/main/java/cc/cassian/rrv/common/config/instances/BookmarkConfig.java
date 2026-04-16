package cc.cassian.rrv.common.config.instances;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import net.minecraft.world.item.ItemStackTemplate;

public class BookmarkConfig extends AbstractRrvConfig {


    public BookmarkConfig() {
        super("bookmarks");
    }

    @Override
    protected void loadData() {
        BookmarkManager.INSTANCE.availableItems().clear();

        if (this.data().has("bookmarkedItems")) {
            this.data().getAsJsonArray("bookmarkedItems").forEach(element -> {
                JsonObject encodedItem = element.getAsJsonObject();
                try {
                    BookmarkManager.INSTANCE.availableItems().add(ItemStackTemplate.CODEC.decode(JsonOps.INSTANCE, encodedItem).getOrThrow().getFirst());
                } catch (Exception e) {
                    ReliableRecipeViewer.LOGGER.error("Failed to load bookmarked item from json: {}", encodedItem);
                }
            });
        }

    }

    @Override
    protected void saveData() {

        JsonArray itemList = new JsonArray();
        BookmarkManager.INSTANCE.availableItems().forEach(itemStack -> {
            try {
                itemList.add(ItemStackTemplate.CODEC.encode(itemStack, JsonOps.INSTANCE, new JsonObject()).getOrThrow().getAsJsonObject());
            } catch (Exception e) {
                ReliableRecipeViewer.LOGGER.error("Could not save bookmarked item: {}", itemStack.toString());
            }
        });

        this.data().add("bookmarkedItems", itemList);

    }


}
