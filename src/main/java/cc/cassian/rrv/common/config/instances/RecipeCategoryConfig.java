package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

public class RecipeCategoryConfig extends AbstractRrvConfig {

	public final LinkedHashMap<String, RecipeCategory> CATEGORIES = new LinkedHashMap<>();
	boolean newCategories = false;

	public void addNewCategory(Identifier id, Integer priority) {
		CATEGORIES.putIfAbsent(id.toString(), new RecipeCategory(id, priority, true));
		newCategories = true;
	}

	public void addNewCategories() {
		// build list of recipe categories
		ArrayList<Identifier> ids = new ArrayList<>();
		for (ReliableClientRecipe reliableClientRecipe : ClientRecipeCache.INSTANCE.getRecipes()) {
			Identifier id = reliableClientRecipe.getViewType().getId();
			if (!ids.contains(id)) {
				ids.add(id);
			}
		}
		// sort
		ids.sort(Identifier::compareTo);
		int i = 0;
        for (Identifier id : ids) {
            addNewCategory(id, i++*10);
        }
		// save
        saveCategories();

	}

    public void saveCategories() {
		save();

    }

    public int compareTo(Identifier id1, Identifier id2) {
        var priority1 = getPriority(id1);
		var priority2 = getPriority(id2);
		if (priority1 == 0 && priority2 == 0) {
			return id1.compareTo(id2);
		} else {
			return Integer.compare(priority1, priority2);
		}
    }

	public int getPriority(Identifier id) {
		var category = Optional.ofNullable(CATEGORIES.get(id.toString()));
        return category.map(RecipeCategory::priority).orElse(0);
    }

	public record RecipeCategory(Identifier id, int priority, boolean enabled) {

	}

	public RecipeCategoryConfig() {
		super("recipe_categories");
	}

	@Override
	protected void loadData() {

		if (this.data().has("categories")) {
			this.data().getAsJsonObject("recipeCategories").asMap().forEach((key, element) -> {
				JsonObject encodedItem = element.getAsJsonObject();
				var priority = encodedItem.get("priority").getAsInt();
				var enabled = encodedItem.get("enabled").getAsBoolean();

				try {
					CATEGORIES.put(key, new RecipeCategory(Identifier.parse(key), priority, enabled));
				} catch (Exception e) {
					ReliableRecipeViewer.LOGGER.error("Failed to load recipe category from json: {}", encodedItem);
				}
			});
		}

	}

	@Override
	protected void saveData() {

		JsonObject itemList = new JsonObject();

		CATEGORIES.forEach((key, category) -> {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("enabled", category.enabled);
			jsonObject.addProperty("priority", category.priority);
			itemList.add(category.id.toString(), jsonObject);
		});

		this.data().add("categories", itemList);

	}


	public int getPriority(String string) {
		if (CATEGORIES.containsKey(string)) {
			return CATEGORIES.get(string).priority();
		}
		return 0;
	}
}
