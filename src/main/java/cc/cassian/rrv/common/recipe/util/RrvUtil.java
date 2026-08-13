package cc.cassian.rrv.common.recipe.util;

import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.mixin.world.item.crafting.IngredientAccessor;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.LootPoolAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.LootTableAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.CompositeEntryBaseAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.LootItemAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.LootPoolSingletonContainerAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.functions.SetComponentsFunctionAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.functions.SetPotionFunctionAccessor;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
//? if >26.2 {
/*import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.level.storage.loot.functions.SequenceFunction;
import cc.cassian.rrv.common.builtin.composting.CompostingServerRecipe;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
*///?}
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.*;

import static cc.cassian.rrv.common.ReliableRecipeViewer.LOGGER;
import static net.minecraft.server.permissions.Permissions.*;

@ApiStatus.Internal
@NullMarked
public class RrvUtil {
    private static final ArrayList<String> INITIALIZED_MODS = new ArrayList<>();

    public static Collection<String> getInitializedMods() {
        return INITIALIZED_MODS;
    }

    public static boolean hasPermission(Player sender) {
        return sender.permissions().hasPermission(COMMANDS_GAMEMASTER);
    }

    public static boolean hasPermission(CommandSourceStack sender) {
        return sender.permissions().hasPermission(COMMANDS_GAMEMASTER);
    }

    public static SlotContent readSlotContent(String key, String type, Identifier identifier, JsonObject parsedRecipe) {
        JsonElement keyElement = parsedRecipe.get(key);
        if (keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString()) {
            var itemText = keyElement.getAsString();
            if (itemText.contains("#")) {
				TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(itemText.replace("#", "")));
                return SlotContent.of(tag);
            } else {
                var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemText));
                return SlotContent.of(item);
            }
        } else if (keyElement.isJsonArray() && keyElement.getAsJsonArray().get(0).isJsonPrimitive()) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            keyElement.getAsJsonArray().forEach(jsonElement->{
                var itemText = jsonElement.getAsString();
                if (itemText.contains("#")) {
                    TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(itemText.replace("#", "")));
                    var items = BuiltInRegistries.ITEM.getTagOrEmpty(tag);
                    items.forEach(holder -> itemStacks.add(holder.value().getDefaultInstance()));
                } else {
                    var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemText));
                    itemStacks.add(item.getDefaultInstance());
                }
            });
           return SlotContent.of(itemStacks);
        } else if (keyElement.isJsonObject()) {
            return SlotContent.of(getItemStack(keyElement));
        } else {
            LOGGER.error("Could not parse {} recipe '{}' as it was missing a key!", type, identifier);
        }
        return SlotContent.of();
    }

    public static ItemStack getItemStack(JsonElement keyElement) {
        if (keyElement.isJsonObject())
            return ItemStack.CODEC.parse(ClientRecipeManager.INSTANCE.createSerializationContext(JsonOps.INSTANCE), keyElement).result().orElseThrow();
        else if (keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString()) {
            return BuiltInRegistries.ITEM.getValue(Identifier.parse(keyElement.getAsString())).getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }

    public static List<Item> getItemsFromIngredient(Ingredient ingredient) {
        var ingredientContent = ((IngredientAccessor) (Object) ingredient).getValues().unwrap();
        List<Item> ingredients = new ArrayList<>();
        if (ingredientContent.left().isPresent()) {
            SlotContent.getItemsFromTag(ingredientContent.left().get()).ifPresent(holders -> {
                holders.forEach(holder -> ingredients.add(holder.value()));
            });
        }

        if (ingredientContent.right().isPresent())
            ingredients.addAll(ingredientContent.right().get().stream().filter(Holder::isBound).map(Holder::value).toList());
        return ingredients;
    }

    public static String ingredientSuffix(Ingredient ingredient) {
        List<Item> itemsFromIngredient = getItemsFromIngredient(ingredient);
        if (itemsFromIngredient.isEmpty()) return "";
        return "_from_" + itemsFromIngredient.getFirst().builtInRegistryHolder().key().identifier().getPath();
    }

    public static String blockName(Block block) {
        return getIdentifier(block).map(Identifier::toString).orElse("").replace(":", "_");
    }

    public static Identifier blockName(String prefix, Block block) {
        return getIdentifier(block).orElseThrow().withPath(path-> "%s%s".formatted(prefix, path.replace(":", "_")));
    }

    private static Optional<Identifier> getIdentifier(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).map(ResourceKey::identifier);
    }

    public static @Nullable Level getLevel() {
        MinecraftServer server = ServerRecipeManager.INSTANCE.getServer();
        if (server != null) {
            return server.overworld();
        } else {
            return RRVClientUtil.level();
        }
    }

    /// Replaces I18n.get
    public static String get(String key) {
        return Language.getInstance().getOrDefault(key);
    }

    /// Replaces I18n.exists - removed in 26.2.
    public static boolean has(String key) {
        return Language.getInstance().has(key);
    }

    public static String lowercaseSubstring(String newQuery) {
        return !newQuery.isEmpty() ? newQuery.substring(1).toLowerCase(Locale.ROOT) : newQuery.toLowerCase(Locale.ROOT);
    }

    /// Creates a recipe map from a collection of recipes - constructor removed in 26.3. Might eventually be swapped out for an abstraction layer.
	public static RecipeMap createRecipeMap(Collection<RecipeHolder<?>> recipes) {
        //? if >26.2 {
        /*ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType = ImmutableMultimap.builder();
        ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey = ImmutableMap.builder();

        for (RecipeHolder<?> recipe : recipes) {
            byType.put(recipe.value().getType(), recipe);
            byKey.put(recipe.id(), recipe);
        }

        return new RecipeMap(byType.build(), byKey.build());
        *///?} else {
        return RecipeMap.create(recipes);
        //?} 
    }
    
    /// Use this to get a TagKey's translation key safely on any side.
    ///
    /// Format for vanilla registry TagKeys is:
    /// `tag.(registry_path).(tag_namespace).(tag_path)`
    ///
    /// Format for modded registry TagKeys is:
    /// `tag.(registry_namespace).(registry_path).(tag_namespace).(tag_path)`
    ///
    /// The registry's path and tag path's slashes will be converted to periods.
    ///
    /// @return the translation key for a TagKey
    public static String getTranslationKey(TagKey<Item> tagKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("tag.");
        Identifier registryIdentifier = tagKey.registry().identifier();
        Identifier tagIdentifier = tagKey.location();

        if (!registryIdentifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
            stringBuilder.append(registryIdentifier.getNamespace())
                    .append(".");
        }

        stringBuilder.append(registryIdentifier.getPath().replace("/", "."))
                .append(".")
                .append(tagIdentifier.getNamespace())
                .append(".")
                .append(tagIdentifier.getPath().replace("/", ".").replace(":", "."));

        return stringBuilder.toString();
	}

    /// Initialize entrypoints from `fabric.mod.json` or `neoforge.mods.toml` files.
    public static void initializeEntrypoint(String modId, ReliableRecipeViewerPlugin plugin) {
        ReliableRecipeViewer.LOGGER.debug("RRV: Loading integration from mod: {}", modId);
        try {
            if (!INITIALIZED_MODS.contains(modId)) {
                plugin.onIntegrationInitialize();
                ReliableRecipeViewer.LOGGER.info("RRV: Integration initialized for mod: {}", modId);
                INITIALIZED_MODS.add(modId);
            } else {
                ReliableRecipeViewer.LOGGER.debug("RRV: Skipped initializing integration for multi-loader mod: {}", modId);
            }
            return;
        } catch (Exception e) {
            ReliableRecipeViewer.LOGGER.error("RRV: Failed to load integration from mod: {} due to {}", modId, e);
        }
    }

    //? if >26.2 {
    /*@SuppressWarnings("all")
    public static <T> List<T> getLootItemFunctions(Optional<Holder<T>> tradeModifiers) {
        List<T> givenItemModifiers = new ArrayList<>();
        tradeModifiers.ifPresent(holder->{
            T modifier = holder.value();
            if (modifier instanceof SequenceFunction sequenceFunction) {
                givenItemModifiers.addAll((Collection<? extends T>) sequenceFunction.functions.stream().map(Holder::value).toList());
            } else {
                givenItemModifiers.add(modifier);
            }
        });
        return givenItemModifiers;
    }
    *///?} else {
    public static <T> List<T> getLootItemFunctions(List<T> tradeModifiers) {
        return tradeModifiers;
    }
	//?}

    //? if >26.2 {
    /*/// Query a resolvable number. Used for fuel values and composting.
    public static @Nullable Float getNumberProvidedFloat(ResolvableNumber number) {
        if (number instanceof ResolvableNumber.Constant(float value)) {
            return value;
        } else if (number instanceof ResolvableNumber.Reference(ResourceKey<NumberProvider> providerResourceKey)) {
			return RrvUtil.getNumberProvidedFloat(providerResourceKey);
        }
        return null;
    }

    /// Query a basic number provider. Used for fuel values and composting.
    public static @Nullable Float getNumberProvidedFloat(ResourceKey<NumberProvider> key) {
        var numberProviderReference = ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.NUMBER_PROVIDER).getOrThrow(key);
        return getNumberProvidedFloat(numberProviderReference);
	}

    public static @Nullable Float getNumberProvidedFloat(Holder<NumberProvider> numberProviderReference) {
        NumberProvider number = numberProviderReference.value();
		return switch (number) {
			case ConstantValue(float value) ->
                    value;
			case ConditionalValue conditionalValue ->
                    getNumberProvidedFloat(conditionalValue.onFalse());
			case NumberDispatcher(List<NumberDispatcher.Case> cases, Holder<NumberProvider> defaultValue) ->
                    getNumberProvidedFloat(defaultValue);
            // Compostables (and frankly number providers in general) are really strangely written. https://github.com/misode/mcmeta/blob/data-json/data/minecraft/number_provider/compostable/low.json
            case WeightedListValue(WeightedList<Holder<NumberProvider>> distribution) -> {
				var unwrapped = distribution.unwrap();
				for (Weighted<Holder<NumberProvider>> holder : unwrapped) {
					if (Objects.equals(getNumberProvidedFloat(holder.value()), 1.0f)) {
						yield (float)(holder.weight() * 0.01);
					}
				}
                yield 0f;
			}
			default -> {
				LOGGER.error("RRV: Failed to load number provider from key: {}, was unrecognized type {}", numberProviderReference.unwrapKey(), number.getClass());
				yield null;
			}
		};
    }
    *///?}

    public static void sortByName(List<ItemStack> availableItems) {
        availableItems.sort(Comparator.comparing(i -> i.getDisplayName().getString()));
    }

    public static List<ItemStack> getLoot(LootTable lootTable, @Nullable String withLore) {
        var stacks = new ArrayList<ItemStack>();
        var accessor = (LootTableAccessor) lootTable;
        for (LootPool pool : accessor.getPools()) {
            LootPoolAccessor lootPoolAccessor = (LootPoolAccessor) pool;

            for (LootPoolEntryContainer container : lootPoolAccessor.entries()) {
                if (container instanceof LootItem lootItem) {
                    LootItemAccessor lootItemAccessor = (LootItemAccessor) lootItem;
                    var containerAccessor = (LootPoolSingletonContainerAccessor) lootItemAccessor;

                    ItemStack stack = new ItemStack(lootItemAccessor.getItem().value());

                    //FIXME
                    //? if <26.3 {
                    containerAccessor.getFunctions().forEach(function -> {

                        if (function instanceof SetPotionFunction setPotionFunction)
                            stack.set(DataComponents.POTION_CONTENTS, stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).withPotion(((SetPotionFunctionAccessor) setPotionFunction).getPotion()));
                        if (function instanceof SetComponentsFunction setComponentsFunction) {
							stack.applyComponents(((SetComponentsFunctionAccessor) setComponentsFunction).getComponents());
						}

                    });
                    //?}

                    List<LootItemCondition> conditions = RrvUtil.getLootItemFunctions(lootPoolAccessor.conditions());
                    for (LootItemCondition condition : conditions) {
                        if (condition instanceof LootItemKilledByPlayerCondition)
                            stack.set(DataComponents.LORE, stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(Component.translatable("view.rrv.type.entity.player_kill").withStyle(ChatFormatting.GRAY)));
                    }

                    stacks.add(stack);
                }
                if (container instanceof CompositeEntryBase entryBase) {
                    CompositeEntryBaseAccessor entryBaseAccessor = (CompositeEntryBaseAccessor) entryBase;
                    entryBaseAccessor.getChildren().forEach(child -> {
                        if (child instanceof LootItem lootItem) {
                            LootItemAccessor lootItemAccessor = (LootItemAccessor) lootItem;
                            ItemStack stack = new ItemStack(lootItemAccessor.getItem());
                            if (withLore != null)
                                stack.set(DataComponents.LORE, stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(Component.translatable(withLore).withStyle(ChatFormatting.GRAY)));
                            stacks.add(stack);
                        }
                    });
                }
            }
        }
        return stacks;
    }


}
