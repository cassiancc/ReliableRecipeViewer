package de.crafty.eiv.common.config.instances;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.config.AbstractEivConfig;
import de.crafty.eiv.common.overlay.itemlist.bookmark.ItemBookmarkOverlay;
import net.minecraft.world.item.ItemStack;

public class BookmarkConfig extends AbstractEivConfig {


    public BookmarkConfig() {
        super("bookmarks");
    }

    @Override
    protected void loadData() {
        ItemBookmarkOverlay.INSTANCE.availableItems().clear();

        if(this.data().has("bookmarkedItems")) {
            this.data().getAsJsonArray("bookmarkedItems").forEach(element -> {
               JsonObject encodedItem = element.getAsJsonObject();

               try {
                   ItemBookmarkOverlay.INSTANCE.availableItems().add(ItemStack.CODEC.decode(JsonOps.INSTANCE, encodedItem).getOrThrow().getFirst());
               }catch (Exception e) {
                   CommonEIV.LOGGER.error("Failed to load item from json: {}", encodedItem);
               }
            });
        }

    }

    @Override
    protected void saveData() {

        JsonArray itemList = new JsonArray();
        ItemBookmarkOverlay.INSTANCE.availableItems().forEach(itemStack -> {

            try {
                itemList.add(ItemStack.CODEC.encode(itemStack, JsonOps.INSTANCE, new JsonObject()).getOrThrow().getAsJsonObject());
            }catch (Exception e) {
                CommonEIV.LOGGER.error("Could not save bookmarked item: {}", itemStack.toString());
            }
        });

        this.data().add("bookmarkedItems", itemList);

    }


}
