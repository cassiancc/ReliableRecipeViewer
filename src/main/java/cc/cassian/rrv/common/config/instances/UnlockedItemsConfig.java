package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.client.recipe.ClientUnlockManager;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.recipe.unlocking.ServerUnlockManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public class UnlockedItemsConfig extends AbstractRrvConfig {


    private boolean enabled = true;
    private boolean indexShowsUnlockedItems = true;

    public UnlockedItemsConfig() {
        super("unlocked_items");
    }

    @Override
    protected void loadData() {
        ClientUnlockManager.INSTANCE.availableItems().clear();
        ClientUnlockManager.INSTANCE.unlockItems(load("unlockedItems", ClientUnlockManager.INSTANCE.availableItems(), ItemStackTemplate.CODEC.listOf()).stream().map(ItemStackTemplate::create).toList());
        ClientUnlockManager.INSTANCE.addUnlockedRecipes(load("unlockedRecipes", ClientUnlockManager.INSTANCE.availableRecipes(), Identifier.CODEC.listOf()));
        this.enabled = load("enabled", this.enabled);
        this.indexShowsUnlockedItems = load("indexShowsUnlockedItems", this.indexShowsUnlockedItems);
    }

    @Override
    protected void saveData() {
        save("unlockedItems", ClientUnlockManager.INSTANCE.availableItems(), ItemStackTemplate.CODEC.listOf());
        save("unlockedRecipes", ClientUnlockManager.INSTANCE.availableRecipes(), Identifier.CODEC.listOf());
        save("enabled", this.enabled);
        save("indexShowsUnlockedItems", this.indexShowsUnlockedItems);
    }


	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    public boolean indexShowsUnlockedItems() {
        return indexShowsUnlockedItems;
    }

    public void setIndexShowsUnlockedItems(boolean enabled) {
        this.indexShowsUnlockedItems = enabled;
    }
}
