package cc.cassian.rrv.common.recipe;

import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static cc.cassian.rrv.common.ReliableRecipeViewer.LOGGER;

public class ResourceRecipeManager {
	private static @NonNull Map<Identifier, Resource> getIdentifierResourceMap(String path) {
		return Minecraft.getInstance().getResourceManager().listResources(path, (identifier) -> true);
	}

	public static void addInfoRecipes(String path, ArrayList<InfoClientRecipe> infoRecipes, boolean b) {
		addInfoRecipes(getIdentifierResourceMap(path), infoRecipes, b);
	}

	private static void addInfoRecipes(Map<Identifier, Resource> identifierResourceMap, ArrayList<InfoClientRecipe> infoRecipes, boolean b) {
		identifierResourceMap.forEach((identifier, resource) -> {
			try {
				JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
				if (parsedRecipe.get("type").getAsString().equals("rrv:info")) {
					var text = parsedRecipe.get("text").getAsString();
					infoRecipes.add(new InfoClientRecipe(RrvUtil.readSlotContent("key", "info", identifier, parsedRecipe), text));
					if (b)
						LOGGER.info("RRV: Loaded info recipe from legacy path 'rrv_info'. Please move your recipes to 'rrv/recipe' in the future!");
					else
						LOGGER.debug("RRV: Loaded info recipe {}", identifier);
				} else {
					LOGGER.error("RRV: Could not parse info recipe '{}' as it was missing a type!", identifier);
				}
			} catch (IOException e) {
				LOGGER.error("RRV: Could not parse info recipe '{}' due to an exception: ", identifier, e);
			}
		});
	}

	public static void addWorldInteractionRecipes(String path, ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes, boolean b) {
		addWorldInteractionRecipes(getIdentifierResourceMap(path), worldInteractionRecipes, b);
	}

	private static void addWorldInteractionRecipes(Map<Identifier, Resource> identifierResourceMap, ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes, boolean b) {
		for (Map.Entry<Identifier, Resource> entry : identifierResourceMap.entrySet()) {
			var slots = readCombinationRecipe("world_interaction", entry);
			if (slots != null) {
				worldInteractionRecipes.add(new WorldInteractionClientRecipe(slots.left, slots.right, slots.result, slots.priority));
				if (b)
					LOGGER.info("RRV: Loaded world interaction recipe from legacy path 'rrv_world interaction'. Please move your recipes to 'rrv/recipe' in the future!");
				else
					LOGGER.debug("RRV: Loaded world interaction recipe {}", entry.getKey());
			}
		}
	}

	public static ArrayList<AnvilCombiningClientRecipe> addAnvilCombiningRecipes(String path) {
		return addAnvilCombiningRecipes(getIdentifierResourceMap(path));
	}

	private static ArrayList<AnvilCombiningClientRecipe> addAnvilCombiningRecipes(Map<Identifier, Resource> identifierResourceMap) {
		ArrayList<AnvilCombiningClientRecipe> anvilCombiningRecipes = new ArrayList<>();
		for (Map.Entry<Identifier, Resource> entry : identifierResourceMap.entrySet()) {
			var slots = readCombinationRecipe("anvil_combining", entry);
			if (slots != null)
				anvilCombiningRecipes.add(new AnvilCombiningClientRecipe(slots.left, slots.right, slots.result, slots.priority));
		}
		return anvilCombiningRecipes;
	}

	private static CombinationRecipeResult readCombinationRecipe(String type, Map.Entry<Identifier, Resource> entry) {
		String typeSpaced = type.replace("_", " ");
		Identifier identifier = entry.getKey();
		Resource resource = entry.getValue();
		try {
			JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
			if (parsedRecipe.get("type").getAsString().equals("rrv:" + type)) {
				SlotContent left = RrvUtil.readSlotContent("left", typeSpaced, identifier, parsedRecipe);
				SlotContent right = RrvUtil.readSlotContent("right", typeSpaced, identifier, parsedRecipe);
				SlotContent result = RrvUtil.readSlotContent("result", typeSpaced, identifier, parsedRecipe);
				int priority = 0;
				if (parsedRecipe.has("priority") && parsedRecipe.get("priority").isJsonPrimitive() && parsedRecipe.getAsJsonPrimitive("priority").isNumber())
					priority = parsedRecipe.getAsJsonPrimitive("priority").getAsInt();
				return new CombinationRecipeResult(left, right, result, priority);
			}
		} catch (IOException e) {
			LOGGER.error("Could not parse {} recipe '{}' due to an exception: ", typeSpaced, identifier, e);
		}
		return null;
	}

	private record CombinationRecipeResult(SlotContent left, SlotContent right, SlotContent result, int priority) {}
}
