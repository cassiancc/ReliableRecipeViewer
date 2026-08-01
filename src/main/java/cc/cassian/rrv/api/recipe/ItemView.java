package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.util.MobDropModifyContext;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.google.common.collect.HashMultimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
//? if >26.1
//import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static cc.cassian.rrv.common.recipe.ItemViewRecipes.*;

/// Main API class used to register RRV compat for other mods
@SuppressWarnings("unused")
public class ItemView {

    /// A list of client-side excluded items that won't show up in the ItemView overlay
    private static final List<Item> EXCLUDED_ITEMS = new ArrayList<>();

    /// A list of client-side excluded item stacks that won't show up in the ItemView overlay
    private static final List<ItemStackTemplate> EXCLUDED_ITEM_STACKS = new ArrayList<>();

    /// A list of client-side excluded enchantments that won't show up in the ItemView overlay
    private static final List<ResourceKey<Enchantment>> EXCLUDED_ENCHANTMENTS = new ArrayList<>();

    /// A list of client-side excluded potions that won't show up in the ItemView overlay
    private static final List<Holder<Potion>> EXCLUDED_POTIONS = new ArrayList<>();

    /// A list of client-side excluded recipes that won't show up in the ItemView overlay
    private static final HashMultimap<Identifier, Identifier> EXCLUDED_RECIPES = HashMultimap.create();

    /// A list of client-side excluded recipe types that won't show up in the ItemView overlay
    private static final List<Identifier> EXCLUDED_RECIPE_CATEGORIES = new ArrayList<>();

    /// Server-Side map of "item-variants", the client gets informed about on every server reload
    private static final HashMap<Item, List<StackSensitive>> STACK_SENSITIVE = new HashMap<>();

    /// A list of Callbacks used for mods to hook into a server reload
    ///
    /// Stack-Sensitives should also be registered here
    private static final List<ReloadCallback> RELOAD_CALLBACKS = new ArrayList<>();

    /// A list of Callbacks used for mods to hook into a server reload (from the client side)
    ///
    /// Client side functionality depending on tags should be handled here
    private static final List<ReloadCallback> CLIENT_RELOAD_CALLBACKS = new ArrayList<>();

    /// ServerRecipeProviders offer a recipe list where mods can easily add their own server recipes
    ///
    /// If you are using the Fabric/NeoForge recipe synchronization API, you can skip server recipes and recipe wrappers entirely and just use [ItemView#addClientRecipeProvider].
    ///
    /// @param provider The recipe provider
    public static void addServerRecipeProvider(ItemViewRecipes.ServerRecipeProvider provider) {
        ItemViewRecipes.INSTANCE.addServerRecipeProvider(provider);
    }

    /// ClientRecipeProviders offer a recipe list where mods can easily add their own client recipes.
    ///
    /// This is best paired with the Fabric/NeoForge Recipe Synchronization API. Use [ServerRecipeManager#synchronizeRecipeType] on your recipe type and recipe serializer in the common plugin, and recipes will be accessible via [ClientRecipeManager#getRecipesForType] in your client plugin.
    ///
    ///
    /// @param provider The recipe provider
    public static void addClientRecipeProvider(ItemViewRecipes.ClientRecipeProvider provider) {
        ItemViewRecipes.INSTANCE.addClientRecipeProvider(provider);
    }

    /// ClientRecipeWrappers convert an incoming server recipe into a displayable client recipe later shown in the recipe view.
    ///
    /// If you are using the Fabric/NeoForge recipe synchronization API, you can skip server recipes and recipe wrappers entirely and just use [ItemView#addClientRecipeProvider].
    ///
    /// They can also split a server recipe up into multiple client recipes if desired, since they require a list to be returned
    ///
    /// @param recipeType The server recipe type
    /// @param wrapper    The wrapper
    /// @param <T>        The class of the server recipe
    public static <T extends ReliableServerRecipe> void addClientRecipeWrapper(ReliableServerRecipeType<T> recipeType, ItemViewRecipes.ClientRecipeWrapper<T> wrapper) {
        ItemViewRecipes.INSTANCE.registerRecipeWrapper(recipeType, wrapper);
    }

    /**
     * Replace with {@link ItemView#addClientRecipeWrapper}.
     */
    @Deprecated(forRemoval = true, since = "6.1.0")
    public static <T extends ReliableServerRecipe> void registerClientRecipeWrapper(ReliableServerRecipeType<T> recipeType, ItemViewRecipes.ClientRecipeWrapper<T> wrapper) {
        ItemViewRecipes.INSTANCE.registerRecipeWrapper(recipeType, wrapper);
    }

    /// A method used to exclude an item from the ItemView index. Note that RRV also supports the standardized `c:hidden\_from\_recipe\_viewers` tag.
    ///
    /// **Note**: This does not hide the item from recipes, only the index.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeItem(Items.AIR))
    /// ```
    ///
    /// @param item The excluded item
    public static void excludeItem(Item item) {
        excludeItems(item);
    }


    /// A method used to exclude an item stack from the ItemView index. To hide all item stacks of a certain item, use [ItemView#excludeItem] or the convention tag.
    ///
    /// **Note**: This does not hide the item from recipes, only the index.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeItemStack(Items.STONE.getDefaultInstance))
    /// ```
    ///
    /// @param item The excluded item
    public static void excludeItemStack(ItemStack... item) {
        for (ItemStack itemStack : item) {
            if (!itemStack.isEmpty())
                EXCLUDED_ITEM_STACKS.add(ItemStackTemplate.fromNonEmptyStack(itemStack));
        }
    }


    /// A method used to exclude an item stack from the ItemView index. To hide all item stacks of a certain item, use [ItemView#excludeItem] or the convention tag.
    ///
    /// **Note**: This does not hide the item from recipes, only the index.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeItemStack(Items.STONE.getDefaultInstance))
    /// ```
    ///
    /// @param item The excluded item
    public static void excludeItemStack(ItemStackTemplate... item) {
		EXCLUDED_ITEM_STACKS.addAll(Arrays.asList(item));
    }

    /// A method used to exclude a recipe from the recipe screen.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeRecipe(Identifier.fromNamespaceAndPath("minecraft", "furnace_smelting"), Identifier.fromNamespaceAndPath("minecraft", "stone"))
    /// ```
    ///
    /// @param recipeType The recipe type to exclude recipes from.
    /// @param recipe The recipe to exclude.
    public static void excludeRecipe(Identifier recipeType, Identifier recipe) {
        EXCLUDED_RECIPES.put(recipeType, recipe);
    }


    /// A method used to exclude recipe types from the recipe screen.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeRecipes(Identifier.fromNamespaceAndPath("minecraft", "furnace_smelting"),
    ///    Identifier.fromNamespaceAndPath("minecraft", "stone"),
    ///    Identifier.fromNamespaceAndPath("minecraft", "deepslate")
    /// )
    /// ```
    ///
    /// @param recipeType The recipe type to exclude recipes from.
    /// @param recipes The recipes to exclude.
    public static void excludeRecipes(Identifier recipeType, Identifier... recipes) {
        EXCLUDED_RECIPES.putAll(recipeType, Arrays.stream(recipes).toList());
    }

    /// A method used to exclude a recipe from the recipe screen.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeRecipeCategory(Identifier.fromNamespaceAndPath("minecraft", "furnace_smelting"))
    /// ```
    ///
    /// @param recipeType The recipe type to exclude recipes from.
    public static void excludeRecipeCategory(Identifier recipeType) {
        if (!EXCLUDED_RECIPE_CATEGORIES.contains(recipeType))
            EXCLUDED_RECIPE_CATEGORIES.add(recipeType);
    }


    /// A method used to exclude recipe types from the recipe screen.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeRecipes(
    ///    Identifier.fromNamespaceAndPath("minecraft", "furnace_smelting"),
    ///    Identifier.fromNamespaceAndPath("minecraft", "furnace_blasting"),
    /// )
    /// ```
    ///
    /// @param recipeTypes The recipe type to exclude recipes from.
    public static void excludeRecipeTypes(Identifier... recipeTypes) {
        for (Identifier recipeType : recipeTypes) {
            excludeRecipeCategory(recipeType);
        }
    }

    /// Register multiple items to exclude at once. Note that RRV also supports the standardized `c:hidden\_from\_recipe\_viewers` tag.
    ///
    /// @param items An array of items to exclude
    public static void excludeItems(Item... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED_ITEMS.contains(item)).forEach(EXCLUDED_ITEMS::add);
    }

    /// A method used to exclude an enchantment from the ItemView index. Note that RRV also supports the standardized `c:hidden\_from\_recipe\_viewers` tag.
    ///
    /// **Note**: This does not hide the item from recipes, only the index.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludeEnchantment(Enchantments.MENDING))
    /// ```
    ///
    /// @param item The excluded item
    public static void excludeEnchantment(ResourceKey<Enchantment> item) {
        excludeEnchantments(item);
    }

    /// Register multiple enchantments to exclude at once. Note that RRV also supports the standardized `c:hidden\_from\_recipe\_viewers` tag.
    ///
    /// @param items An array of enchantments to exclude
    @SafeVarargs
	public static void excludeEnchantments(ResourceKey<Enchantment>... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED_ENCHANTMENTS.contains(item)).forEach(EXCLUDED_ENCHANTMENTS::add);
    }

    /// Register multiple enchantments to exclude at once. Note that RRV also supports the standardized `c:hidden\_from\_recipe\_viewers` tag.
    ///
    /// @param items A collection of enchantments to exclude
    public static void excludeEnchantments(Collection<ResourceKey<Enchantment>> items) {
        items.stream().filter(item -> !EXCLUDED_ENCHANTMENTS.contains(item)).forEach(EXCLUDED_ENCHANTMENTS::add);
    }

    /// A method used to exclude a potion from the ItemView index. Note that RRV also supports the standardized `c:hidden\_from\_recipe\_viewers` tag.
    ///
    /// **Note**: This does not hide the enchantment from recipes, only the index.
    ///
    /// **Example**:
    /// ```
    /// ItemView.excludePotion(Potions.MUNDANE))
    /// ```
    ///
    /// @param item The excluded item
    public static void excludePotion(Holder<Potion> item) {
        excludePotions(item);
    }

    /// Register multiple potions to exclude at once. Note that RRV also supports the standardized `c:hidden\_from\_recipe\_viewers` tag.
    ///
    /// @param items An array of enchantments to exclude
    @SafeVarargs
    public static void excludePotions(Holder<Potion>... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED_POTIONS.contains(item)).forEach(EXCLUDED_POTIONS::add);
    }

    /// Add aliases for items, e.g. enchanting table -> enchantment table.
    /// This is used in the Item View's search.
    /// @param item The item
    /// @param alias The alias
    public static void addAlias(Item item, String alias) {
        if (!ItemFilters.ALIASES.get(item).contains(alias))
            ItemFilters.ALIASES.put(item, alias);
    }

    /// Add aliases for items, e.g. enchanting table -> enchantment table.
    /// This is used in the Item View's search.
    /// @param item The item
    /// @param aliases The aliases to add for the item.
    public static void addAliases(Item item, Collection<String> aliases) {
        if (!ItemFilters.ALIASES.get(item).containsAll(aliases))
            ItemFilters.ALIASES.putAll(item, aliases);
    }


    /// Add "item-variants", called stack-sensitives to the overlay
    ///
    /// These sensitives are also used to make proper ingredient/result redirections
    ///
    /// @param stack The stack-sensitive
    public static void addStackSensitive(ItemStack stack) {
        List<StackSensitive> present = STACK_SENSITIVE.getOrDefault(stack.getItem(), new ArrayList<>());
        present.add(new StackSensitive(stack));
        STACK_SENSITIVE.put(stack.getItem(), present);
    }


    /// @return The list of currently present stack-sensitives (server-side)
    public static HashMap<Item, List<StackSensitive>> getStackSensitive() {
        return STACK_SENSITIVE;
    }

    /// @return The list of currently excluded items (client-side)
    public static List<Item> getExcludedItems() {
        return EXCLUDED_ITEMS;
    }

    /// @return The list of currently excluded recipes (client-side)
    public static HashMultimap<Identifier, Identifier> getExcludedRecipes() {
        return EXCLUDED_RECIPES;
    }

    /// @return The list of currently excluded recipe types (client-side)
    public static List<Identifier> getExcludedRecipeTypes() {
        return EXCLUDED_RECIPE_CATEGORIES;
    }

    /// @return The list of currently excluded enchantments (client-side)
    public static List<ResourceKey<Enchantment>> getExcludedEnchantments() {
        return EXCLUDED_ENCHANTMENTS;
    }

    /// @return The list of currently excluded potions (client-side)
    public static List<Holder<Potion>> getExcludedPotions() {
        return EXCLUDED_POTIONS;
    }

    /// Opens a recipe view for the client player containing all recipes that use the specified stack as an ingredient ([ReliableClientRecipe#getIngredients], ([ReliableClientRecipeType#getCraftReferences])).
    /// @param stack The ingredient [ItemStack]
    public static void openForStackIngredient(ItemStack stack) {
        ItemViewOverlay.INSTANCE.openRecipeView(stack, ActionType.INPUT);
    }

    /// Opens a recipe view for the client player containing all recipes that use the specified stack as an ingredient ([ReliableClientRecipe#getIngredients], ([ReliableClientRecipeType#getCraftReferences])).
    /// @param stack The ingredient [ItemStack]
    public static void openForStackIngredient(ItemStack stack, ReliableClientRecipeType type) {
        ItemViewOverlay.INSTANCE.openRecipeView(stack, ActionType.INPUT, type);
    }

    /// Opens a recipe view for the client player containing all recipes that own the specified stack as a result ([ReliableClientRecipe#getResults]).
    /// @param stack The result [ItemStack]
    public static void openForStackResult(ItemStack stack, ReliableClientRecipeType type) {
        ItemViewOverlay.INSTANCE.openRecipeView(stack, ActionType.RESULT, type);
    }

    public ReliableClientRecipe getRecipe(final Identifier recipeId) {
        return getRecipes(recipeId).getFirst();
    }

    public List<ReliableClientRecipe> getRecipes(final Identifier recipeId) {
        return ClientRecipeCache.INSTANCE.getRecipes(recipeId);
    }

    /// Mods can add a ReloadCallback to hook into a server reload.
    ///
    /// This should be used to register stack sensitives, as they are cleared when the server reloads..
    ///
    /// @param callback The reload callback
    public static void addServerReloadCallback(ReloadCallback callback) {
        RELOAD_CALLBACKS.add(callback);
    }

    /// Mods can add a ReloadCallback to hook into a server reload (from the client side)
    ///
    /// This should be used to register excluded items, enchantments, potions, recipes, etc. to guarantee correct ordering.
    /// @param callback: A functional interface that runs on the client when the server reloads.
    public static void addClientReloadCallback(ReloadCallback callback) {
        CLIENT_RELOAD_CALLBACKS.add(callback);
    }

    /// Mods can add a world interaction recipe via the API rather than via a resource pack.
    /// Run this via [ItemView#addClientReloadCallback] to ensure it's registered on time.
    /// @param recipe A new [WorldInteractionClientRecipe].
    public static void addWorldInteractionRecipe(WorldInteractionClientRecipe recipe) {
        WORLD_INTERACTION_RECIPES.add(recipe);
    }

    /// Mods can add hardcoded mob drops (e.g. Nether Stars, Goat Horns) to an entity type's Mob Drops page via the API.
    /// Run this via [ItemView#addServerReloadCallback(ReloadCallback)] to ensure it's registered on time.
    public static void addMobDrops(EntityType<?> type, SlotContent drop) {
        MOB_DROPS.put(type, drop);
    }

    /// Mods can change mob drops on an entity type's Mob Drops page via the API.
    /// Run this via [ItemView#addServerReloadCallback(ReloadCallback)] to ensure it's registered on time.
    public static void modifyMobDrops(Predicate<EntityType<?>> type, Predicate<SlotContent> drop, SlotContent newDrop) {
        MODIFIED_MOB_DROPS.add(new MobDropModifyContext(type, drop, newDrop));
    }

    /// Mods may add components that makes the item view treat it as a unique item with its own associated recipe, rather than just a variation on a normal item.
    /// Vanilla examples include potions, enchanted books, and suspicious stew.
    /// Run this via [ItemView#addServerReloadCallback(ReloadCallback)] to ensure it's registered on time.
    public static void addItemCheck(BiPredicate<ItemStack, ItemStack> predicate) {
        CHECKS.add(predicate);
    }

    /// Mods may add components that makes the item view treat it as a unique item with its own associated recipe, rather than just a variation on a normal item.
    /// Vanilla examples include potions, enchanted books, and suspicious stew.
    public static void addItemCheck(DataComponentType<?> type) {
        CHECKS.add((stack1, stack2) -> {
			if (!(stack1.has(type) && stack2.has(type)))
				return true;
			return stack1.get(type).equals(stack2.get(type));
		});
    }

    /// Mods can add an info recipe via the API rather than via a resource pack.
    /// Run this via [ItemView#addClientReloadCallback] to ensure it's registered on time.
    ///
    /// @param recipe A new [InfoClientRecipe].
    public static void addInfoRecipe(InfoClientRecipe recipe) {
        INFO_RECIPES.add(recipe);
    }

    /// @return A list of currently present reload callbacks
    public static List<ReloadCallback> getReloadCallbacks() {
        return RELOAD_CALLBACKS;
    }

    /// @return A list of currently present client reload callbacks
    public static List<ReloadCallback> getClientReloadCallbacks() {
        return CLIENT_RELOAD_CALLBACKS;
    }

	public static boolean isExcludedItem(Holder<Item> itemHolder) {
        if (!itemHolder.isBound()) return true;
		return isExcludedItem(itemHolder.value());
	}

    public static boolean isExcludedItem(Item item) {
        return EXCLUDED_ITEMS.contains(item);
    }

    public static boolean isExcludedRecipe(Identifier recipeType, Identifier recipe) {
        if (EXCLUDED_RECIPES.containsKey(recipeType))
            return EXCLUDED_RECIPES.get(recipeType).contains(recipe);
        return false;
    }

    public static boolean isExcludedItem(ItemStack stack) {
        if (stack.isEmpty() || EXCLUDED_ITEM_STACKS.contains(ItemStackTemplate.fromNonEmptyStack(stack))) return true;
        if (stack.has(DataComponents.POTION_CONTENTS)) {
            Optional<Holder<Potion>> potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
            if (potion.isPresent()) {
                if (isExcludedPotion(potion.get())) {
                    return true;
                }
            }
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        if (stack.has(DataComponents.STORED_ENCHANTMENTS)) {
            var enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
			assert enchantments != null;
			enchantments.keySet().forEach(enchantment -> {
                if (isExcludedEnchantment(enchantment)) {
                    atomicBoolean.set(true);
                }
            });
        }
        if (atomicBoolean.get()) {
            return true;
        }
        return isExcludedItem(stack.getItem());
    }

    public static boolean isExcludedPotion(Holder<Potion> potion) {
        return potion.is(CommonTags.EXCLUDED_POTIONS) || EXCLUDED_POTIONS.contains(potion);
    }

    public static boolean isExcludedEnchantment(Holder<Enchantment> enchantmentHolder) {
        return enchantmentHolder.is(CommonTags.EXCLUDED_ENCHANTMENTS) || EXCLUDED_ENCHANTMENTS.contains(enchantmentHolder.unwrapKey().orElseThrow());
    }

    public static void excludeItems(Collection<Identifier> localTags) {
        localTags.stream().map(BuiltInRegistries.ITEM::get).filter(Optional::isPresent).map(Optional::get).forEach(value -> ItemView.excludeItems(value.value()));
    }


    public interface ReloadCallback {

        void onReload();
    }

    /// Representation of a stack-sensitive
    ///
    /// @param stack The [ItemStack] used as an item-variant
    public record StackSensitive(ItemStack stack) {

        public static final StreamCodec<RegistryFriendlyByteBuf, StackSensitive> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.COMPOUND_TAG,
                stackSensitive -> TagUtil.encodeItemStackOnServer(stackSensitive.stack()),
                (compoundTag) -> new StackSensitive(TagUtil.decodeItemStackOnClient(compoundTag))
        );

        @Override
        public ItemStack stack() {
            return stack.copy();
        }

        public ItemStackTemplate template() {
            return ItemStackTemplate.fromNonEmptyStack(stack);
        }


    }
}
