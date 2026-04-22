package cc.cassian.rrv.common.recipe;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
//? fabric {
import net.fabricmc.loader.api.FabricLoader;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static cc.cassian.rrv.common.ReliableRecipeViewer.GSON;
import static cc.cassian.rrv.common.ReliableRecipeViewer.LOGGER;

public class ResourceRecipeManager {
	public static final ArrayList<Identifier> HIDDEN_ITEM_TAGS = new ArrayList<>();
	public static final ArrayList<Identifier> HIDDEN_BLOCK_TAGS = new ArrayList<>();

	private static Map<Identifier, Resource> getIdentifierResourceMap(String path) {
		return Minecraft.getInstance().getResourceManager().listResources(path, (identifier) -> true);
	}

	/// Hides recipes from the recipe screen.
	public static void hideRecipes() {
		getIdentifierResourceMap("rrv/exclusions").forEach((identifier, resource) -> {
			try {
				JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
				if (parsedRecipe.get("type").getAsString().equals("rrv:exclusions")) {
					parsedRecipe.entrySet().forEach(entry -> {
						if (entry.getKey().contains(":")) {
							var key = Identifier.parse(entry.getKey());
							if (Configs.CATEGORIES.CATEGORIES.containsKey(key)) {
								entry.getValue().getAsJsonArray().forEach(jsonElement -> ItemView.excludeRecipe(key, Identifier.parse(jsonElement.getAsString())));
							}
						}
					});

					parsedRecipe.get("item").getAsJsonArray().forEach(item -> HIDDEN_ITEM_TAGS.add(Identifier.parse(item.getAsString())));
					parsedRecipe.get("block").getAsJsonArray().forEach(item -> HIDDEN_BLOCK_TAGS.add(Identifier.parse(item.getAsString())));
					LOGGER.debug("RRV: Loaded exclusion list {}", identifier);
				}
			} catch (IOException e) {
				LOGGER.error("RRV: Could not parse exclusion list '{}' due to an exception: ", identifier, e);
			}
		});
	}

	/// Adds info recipes from resource packs and the API ([ItemView#addInfoRecipe]).
	public static ArrayList<InfoClientRecipe> addInfoRecipes() {
		ArrayList<InfoClientRecipe> infoRecipes = new ArrayList<>();
		getIdentifierResourceMap("rrv/recipe").forEach((identifier, resource) -> {
			try {
				JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
				if (parsedRecipe.get("type").getAsString().equals("rrv:info")) {
					var text = parsedRecipe.get("text").getAsString();
					infoRecipes.add(new InfoClientRecipe(identifier.withPath((path)->path.replace(".json", "")), RrvUtil.readSlotContent("key", "info", identifier, parsedRecipe), text));
					LOGGER.debug("RRV: Loaded info recipe {}", identifier);
				}
			} catch (IOException e) {
				LOGGER.error("RRV: Could not parse info recipe '{}' due to an exception: ", identifier, e);
			}
		});
		ItemViewRecipes.addAllInfoRecipes(infoRecipes);
		return infoRecipes;
	}

	/// Adds world interaction recipes from resource packs.
	public static void addWorldInteractionRecipes(ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes) {
		for (Map.Entry<Identifier, Resource> entry : getIdentifierResourceMap("rrv/recipe").entrySet()) {
			var slots = readCombinationRecipe("world_interaction", entry);
			if (slots != null) {
				worldInteractionRecipes.add(new WorldInteractionClientRecipe(entry.getKey().withPath(path->path.replace(".json", "")), slots.left, slots.right, slots.result, slots.priority));
				LOGGER.debug("RRV: Loaded world interaction recipe {}", entry.getKey());
			}
		}
	}

	/// Adds anvil combining recipes from resource packs.
	public static ArrayList<AnvilCombiningClientRecipe> addAnvilCombiningRecipes() {
		ArrayList<AnvilCombiningClientRecipe> anvilCombiningRecipes = new ArrayList<>();
		for (Map.Entry<Identifier, Resource> entry : getIdentifierResourceMap("rrv/recipe").entrySet()) {
			var slots = readCombinationRecipe("anvil_combining", entry);
			if (slots != null)
				anvilCombiningRecipes.add(new AnvilCombiningClientRecipe(entry.getKey(), slots.left, slots.right, slots.result, slots.priority));
		}
		return anvilCombiningRecipes;
	}

	/// Reads data from combination recipes - world interaction, anvil combining, etc.
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

	/// Replaces or adds to the index based on data from a resource pack.
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

	/// Client fallback for when a server did not provide recipes.
	/// Fabric-exclusive, as Neo does not provide NIO paths for mods.
	public static void getLocalRecipes() {
		ArrayList<RecipeHolder<?>> objects = new ArrayList<>();
		//? fabric {
		FabricLoader.getInstance().getAllMods().forEach(mod->{
			var rootPath = mod.getRootPaths().getFirst();
			String modId = mod.getMetadata().getId();
			getCachedRecipesFromMod(rootPath, modId, objects);
		});
		//?}
		ReliableRecipeViewerClient.LOCAL_RECIPES = RecipeMap.create(objects);
	}

	private static void getCachedRecipesFromMod(Path rootPath, String modId, ArrayList<RecipeHolder<?>> objects) {
		var path = rootPath.resolve("data/%s/recipe".formatted(modId));
		if (Files.exists(path)) {
			try (Stream<Path> files = Files.walk(path)) {
				files.forEach(recipePath->{
					if (Files.isDirectory(recipePath)) return;
					try (BufferedReader tagReader = Files.newBufferedReader(recipePath)) {
						String id = recipePath.getFileName().toString().replace(".json", "");
						JsonElement jsonElement = GSON.fromJson(tagReader, JsonElement.class);
						JsonObject recipeObject = jsonElement.getAsJsonObject();
						if (!recipeObject.has("type")) return;
						Recipe<?> recipe = Recipe.CODEC.parse(ClientRecipeManager.INSTANCE.createSerializationContext(JsonOps.INSTANCE), recipeObject).getOrThrow(JsonParseException::new);
						objects.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(modId, id)), recipe));
					} catch (Exception e) {
						LOGGER.error("Error loading local recipe: {}", recipePath);
					}
				});
			} catch (Exception e) {
				LOGGER.error("Error loading local recipes from mod: {}", modId);
			}
		}
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
