package cc.cassian.rrv.common.recipe;

import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cc.cassian.rrv.common.ReliableRecipeViewer.LOGGER;

public class ResourceRecipeManager {
	private static Map<Identifier, Resource> getIdentifierResourceMap(String path) {
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
					LOGGER.info("RRV: Loaded world interaction recipe from legacy path 'rrv_world_interaction'. Please move your recipes to 'rrv/recipe' in the future!");
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

	public static void replaceIndex(List<ItemStack> results) {
		getIdentifierResourceMap("rrv/index").forEach((identifier, resource) -> {
			try {
				JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
				// replace - whether to replace the entire index with this modified one
				if (parsedRecipe.has("replace") && parsedRecipe.get("replace").isJsonPrimitive() && parsedRecipe.get("replace").getAsJsonPrimitive().isBoolean() && parsedRecipe.getAsJsonPrimitive("replace").getAsBoolean()) {
					results.clear();
				}
				// values to remove from the index
				if (parsedRecipe.has("remove") && parsedRecipe.get("remove").isJsonArray()) {
					parsedRecipe.getAsJsonArray("remove").forEach(item -> {
						if (item.isJsonObject()) {
							results.remove(RrvUtil.getItemStack(item));
						}
						else if (item.isJsonPrimitive() && item.getAsJsonPrimitive().isString()) {
							Optional<Item> optional = BuiltInRegistries.ITEM.getOptional(Identifier.parse(item.getAsString()));
							optional.ifPresent(value -> results.removeIf((i)-> i.is(value)));
						}
					});
				}
				// new values to insert into the index
				if (parsedRecipe.has("values") && parsedRecipe.get("values").isJsonArray()) {
					parsedRecipe.getAsJsonArray("values").forEach(item -> {
						if (item.isJsonObject() && item.getAsJsonObject().has("after")) {
							findAndAddStack(results, item.getAsJsonObject(), "after", 1);
						} else if (item.isJsonObject() && item.getAsJsonObject().has("before")) {
							findAndAddStack(results, item.getAsJsonObject(), "before", 0);
						}
						else {
							ItemStack itemStack = RrvUtil.getItemStack(item);
							if (itemStack.isEmpty())
								results.add(itemStack);
						}
					});
				}
			} catch (Exception e) {
				LOGGER.error("Could not parse index modification {} due to an exception: ", identifier, e);
			}
		});
	}

	private static void findAndAddStack(List<ItemStack> results, JsonObject itemObject, String key, int offset) {
		Optional<ItemStack> first = results.stream().filter(stack -> ItemStack.isSameItem(stack, RrvUtil.getItemStack(itemObject.get(key)))).findFirst();
		if (first.isPresent()) {
			var indexOf = results.indexOf(first.get());
			results.add(indexOf+offset, RrvUtil.getItemStack(itemObject));
		}
	}

	private record CombinationRecipeResult(SlotContent left, SlotContent right, SlotContent result, int priority) {}
}
