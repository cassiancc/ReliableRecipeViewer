package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.overlay.itemlist.unlock.UnlockManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStackTemplate;

public class UnlockedItemsConfig extends AbstractRrvConfig {


    private boolean enabled = true;

    public UnlockedItemsConfig() {
        super("unlocked_items");
    }

    @Override
    protected void loadData() {
        UnlockManager.INSTANCE.availableItems().clear();

        if (this.data().has("unlockedItems")) {
            this.data().getAsJsonArray("unlockedItems").forEach(element -> {
                JsonObject encodedItem = element.getAsJsonObject();
                try {
                    UnlockManager.INSTANCE.availableItems().add(ItemStackTemplate.CODEC.decode(JsonOps.INSTANCE, encodedItem).getOrThrow().getFirst());
                } catch (Exception e) {
                    ReliableRecipeViewer.LOGGER.error("Failed to load unlocked item from json: {}", encodedItem);
                }
            });
        }

    }

    @Override
    protected void saveData() {

        JsonArray itemList = new JsonArray();
        UnlockManager.INSTANCE.availableItems().forEach(itemStack -> {
            try {
                itemList.add(ItemStackTemplate.CODEC.encode(itemStack, JsonOps.INSTANCE, new JsonObject()).getOrThrow().getAsJsonObject());
            } catch (Exception e) {
                ReliableRecipeViewer.LOGGER.error("Could not save unlocked item: {}", itemStack.toString());
            }
        });

        this.data().add("unlockedItems", itemList);

    }


	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
