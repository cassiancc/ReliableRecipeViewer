package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;

public class RecipeCategoryConfig extends AbstractRrvConfig {

	public final LinkedHashMap<String, RecipeCategory> CATEGORIES = new LinkedHashMap<>();

	public void addNewCategory(Identifier id, Integer priority) {
		CATEGORIES.putIfAbsent(id.toString(), new RecipeCategory(id, priority, true));
	}

	public void addNewCategories() {
		int i = 0;
		for (ReliableClientRecipe reliableClientRecipe : ClientRecipeCache.INSTANCE.getRecipes()) {
			Identifier id = reliableClientRecipe.getViewType().getId();
			addNewCategory(id, i++);
		}

		save();

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
