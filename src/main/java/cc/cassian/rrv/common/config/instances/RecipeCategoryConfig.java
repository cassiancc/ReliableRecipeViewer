package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Optional;

public class RecipeCategoryConfig extends AbstractRrvConfig {

	public final LinkedHashMap<Identifier, ConfiguredRecipeCategory> CATEGORIES = new LinkedHashMap<>();

	public void addNewCategory(Identifier id, Integer priority, boolean enabled) {
		CATEGORIES.putIfAbsent(id, new ConfiguredRecipeCategory(id, priority, enabled));
	}

	public void addNewCategories() {
		// build list of recipe categories
		ArrayList<Identifier> ids = new ArrayList<>();
		for (ReliableClientRecipe reliableClientRecipe : ClientRecipeCache.INSTANCE.getRecipes()) {
			ReliableClientRecipeType recipeTypes = reliableClientRecipe.getViewType();
			Identifier categoryId = recipeTypes.getId();
			if (recipeTypes.getPriority() == 0) {
				if (!ids.contains(categoryId)) {
					ids.add(categoryId);
				}
			} else if (!CATEGORIES.containsKey(categoryId)) {
				addNewCategory(categoryId, recipeTypes.getPriority(), recipeTypes.enabled());
			}
		}
		// sort
		ids.sort(Comparator.comparing(Identifier::toString));
		int i = 0;
        for (Identifier id : ids) {
            addNewCategory(id, i++,true);
        }
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
		var category = Optional.ofNullable(CATEGORIES.get(id));
        return category.map(ConfiguredRecipeCategory::priority).orElse(0);
    }

    public boolean enabled(ReliableClientRecipeType reliableClientRecipeType) {
		if (CATEGORIES.containsKey(reliableClientRecipeType.getId()))
        	return CATEGORIES.get(reliableClientRecipeType.getId()).enabled();
		ReliableRecipeViewer.LOGGER.error("Category %s is not in configuration!".formatted(reliableClientRecipeType.getId()));
	    return true; // this shouldn't ever be the case, but safer to allow it
    }

    public void setPriority(Identifier identifier, Integer newPriority) {
        CATEGORIES.computeIfPresent(identifier, (k, oldCategory) -> new ConfiguredRecipeCategory(oldCategory.id, newPriority, oldCategory.enabled));
    }

    public void setEnabled(Identifier identifier, Boolean newState) {
		CATEGORIES.computeIfPresent(identifier, (k, oldCategory) -> new ConfiguredRecipeCategory(oldCategory.id, oldCategory.priority, newState));
    }

	public record ConfiguredRecipeCategory(Identifier id, int priority, boolean enabled) {

	}

	public RecipeCategoryConfig() {
		super("recipe_categories");
	}

	@Override
	protected void loadData() {

		if (this.data().has("categories")) {
			this.data().getAsJsonObject("categories").asMap().forEach((key, element) -> {
				JsonObject encodedItem = element.getAsJsonObject();
				var priority = encodedItem.get("priority").getAsInt();
				var enabled = encodedItem.get("enabled").getAsBoolean();

				try {
					Identifier id = Identifier.parse(key);
					CATEGORIES.put(id, new ConfiguredRecipeCategory(id, priority, enabled));
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
}
