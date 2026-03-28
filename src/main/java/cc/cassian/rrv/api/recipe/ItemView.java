package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static cc.cassian.rrv.common.recipe.ItemViewRecipes.INFO_RECIPES;
import static cc.cassian.rrv.common.recipe.ItemViewRecipes.WORLD_INTERACTION_RECIPES;

/**
 * Main API class used to register RRV compat for other mods
 */
@SuppressWarnings("unused")
public class ItemView {

    /**
     * A list of client-side excluded items that won't show up in the ItemView overlay
     */
    private static final List<Item> EXCLUDED_ITEMS = new ArrayList<>();
    private static final List<ResourceKey<Enchantment>> EXCLUDED_ENCHANTMENTS = new ArrayList<>();
    private static final List<Holder<Potion>> EXCLUDED_POTIONS = new ArrayList<>();

    /**
     * Server-Side map of "item-variants", the client gets informed about on every server reload
     */
    private static final HashMap<Item, List<StackSensitive>> STACK_SENSITIVE = new HashMap<>();

    /**
     * A list of Callbacks used for mods to hook into a server reload
     * <br>
     * <br>
     * Stack-Sensitives should also be registered here
     */
    private static final List<ReloadCallback> RELOAD_CALLBACKS = new ArrayList<>();

    /**
     * A list of Callbacks used for mods to hook into a server reload (from the client side)
     * <br>
     * <br>
     * Client side functionality depending on tags should be handled here
     */
    private static final List<ReloadCallback> CLIENT_RELOAD_CALLBACKS = new ArrayList<>();

    /**
     * ServerRecipeProviders offer a recipe list where mods can easily add their own server recipes
     *
     * @param provider The recipe provider
     */
    public static void addServerRecipeProvider(ItemViewRecipes.ServerRecipeProvider provider) {
        ItemViewRecipes.INSTANCE.addRecipeProvider(provider);
    }

    /**
     * Deprecated in favor of {@link ItemView#addServerRecipeProvider(ItemViewRecipes.ServerRecipeProvider)}
     */
    @Deprecated(since = "6.0.0")
    public static void addRecipeProvider(ItemViewRecipes.ServerRecipeProvider provider) {
        addServerRecipeProvider(provider);
    }

    /**
     * ClientRecipeWrappers convert an incoming server recipe into a displayable client recipe later shown in the recipe view.
     * <br>
     * <br>
     * They can also split a server recipe up into multiple client recipes if desired, since they require a list to be returned
     *
     * @param recipeType The server recipe type
     * @param wrapper    The wrapper
     * @param <T>        The class of the server recipe
     */
    public static <T extends ReliableServerRecipe> void addClientRecipeWrapper(ReliableServerRecipeType<T> recipeType, ItemViewRecipes.ClientRecipeWrapper<T> wrapper) {
        ItemViewRecipes.INSTANCE.registerRecipeWrapper(recipeType, wrapper);
    }

    /**
     * Deprecated in favor of {@link ItemView#addClientRecipeWrapper(ReliableServerRecipeType, ItemViewRecipes.ClientRecipeWrapper)}
     */
    @Deprecated(since = "6.1.0")
    public static <T extends ReliableServerRecipe> void registerClientRecipeWrapper(ReliableServerRecipeType<T> recipeType, ItemViewRecipes.ClientRecipeWrapper<T> wrapper) {
        addClientRecipeWrapper(recipeType, wrapper);
    }

    /**
     * Deprecated in favor of {@link ItemView#addClientRecipeWrapper(ReliableServerRecipeType, ItemViewRecipes.ClientRecipeWrapper)}
     */
    @Deprecated(since = "6.0.0")
    public static <T extends ReliableServerRecipe> void registerRecipeWrapper(ReliableServerRecipeType<T> recipeType, ItemViewRecipes.ClientRecipeWrapper<T> wrapper) {
        addClientRecipeWrapper(recipeType, wrapper);
    }


    /**
     * A method used to exclude an item from the ItemView index. Note that RRV also supports the standardized <code>c:hidden_from_recipe_viewers</code> tag.
     * <br>
     * <br>
     * NOTE: This does not hide the item from recipes, only the index.
     * <br>
     * <br>
     * <b>Example</b>: minecraft:air
     *
     * @param item The excluded item
     */
    public static void excludeItem(Item item) {
        excludeItems(item);
    }

    /**
     * Register multiple items to exclude at once. Note that RRV also supports the standardized <code>c:hidden_from_recipe_viewers</code> tag.
     *
     * @param items An array of items to exclude
     */
    public static void excludeItems(Item... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED_ITEMS.contains(item)).forEach(EXCLUDED_ITEMS::add);
    }

    /**
     * A method used to exclude an enchantment from the ItemView index. Note that RRV also supports the standardized <code>c:hidden_from_recipe_viewers</code> tag.
     * <br>
     * <br>
     * NOTE: This does not hide the enchantment from recipes, only the index.
     * <br>
     * <br>
     * <b>Example</b>: minecraft:mending
     *
     * @param item The excluded item
     */
    public static void excludeEnchantment(ResourceKey<Enchantment> item) {
        excludeEnchantments(item);
    }

    /**
     * Register multiple enchantments to exclude at once. Note that RRV also supports the standardized <code>c:hidden_from_recipe_viewers</code> tag.
     *
     * @param items An array of enchantments to exclude
     */
    @SafeVarargs
	public static void excludeEnchantments(ResourceKey<Enchantment>... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED_ENCHANTMENTS.contains(item)).forEach(EXCLUDED_ENCHANTMENTS::add);
    }

    /**
     * Register multiple enchantments to exclude at once. Note that RRV also supports the standardized <code>c:hidden_from_recipe_viewers</code> tag.
     *
     * @param items A collection of enchantments to exclude
     */
    public static void excludeEnchantments(Collection<ResourceKey<Enchantment>> items) {
        items.stream().filter(item -> !EXCLUDED_ENCHANTMENTS.contains(item)).forEach(EXCLUDED_ENCHANTMENTS::add);
    }

    /**
     * A method used to exclude a potion from the ItemView index. Note that RRV also supports the standardized <code>c:hidden_from_recipe_viewers</code> tag.
     * <br>
     * <br>
     * NOTE: This does not hide the enchantment from recipes, only the index.
     * <br>
     * <br>
     * <b>Example</b>: minecraft:mundane
     *
     * @param item The excluded item
     */
    public static void excludePotion(Holder<Potion> item) {
        excludePotions(item);
    }

    /**
     * Register multiple potions to exclude at once. Note that RRV also supports the standardized <code>c:hidden_from_recipe_viewers</code> tag.
     *
     * @param items An array of enchantments to exclude
     */
    @SafeVarargs
    public static void excludePotions(Holder<Potion>... items) {
        Arrays.stream(items).filter(item -> !EXCLUDED_POTIONS.contains(item)).forEach(EXCLUDED_POTIONS::add);
    }


    /**
     * Add "item-variants", called stack-sensitives to the overlay
     * <br>
     * <br>
     * These sensitives are also used to make proper ingredient/result redirections
     *
     * @param stack The stack-sensitive
     */
    public static void addStackSensitive(ItemStack stack) {
        List<StackSensitive> present = STACK_SENSITIVE.getOrDefault(stack.getItem(), new ArrayList<>());
        present.add(new StackSensitive(stack));
        STACK_SENSITIVE.put(stack.getItem(), present);
    }


    /**
     * @return The list of currently present stack-sensitives (server-side)
     */
    public static HashMap<Item, List<StackSensitive>> getStackSensitive() {
        return STACK_SENSITIVE;
    }

    /**
     * @return The list of currently excluded items (client-side)
     */
    public static List<Item> getExcludedItems() {
        return EXCLUDED_ITEMS;
    }

    /**
     * @return The list of currently excluded enchantments (client-side)
     */
    public static List<ResourceKey<Enchantment>> getExcludedEnchantments() {
        return EXCLUDED_ENCHANTMENTS;
    }


    /**
     * @return The list of currently excluded potions (client-side)
     */
    public static List<Holder<Potion>> getExcludedPotions() {
        return EXCLUDED_POTIONS;
    }



    /**
     * Opens a recipe view for the client-player containing all recipes that use the specified stack as an ingredient
     * @param stack The ingredient stack
     */
    public static void openForStackIngredient(ItemStack stack) {
        ItemViewOverlay.INSTANCE.openRecipeView(stack, ActionType.INPUT);
    }

    /**
     * Opens a recipe view for the client-player containing all recipes that own the specified stack as a result
     * @param stack The result stack
     */
    public static void openForStackResult(ItemStack stack) {
        ItemViewOverlay.INSTANCE.openRecipeView(stack, ActionType.RESULT);
    }

    /**
     * Deprecated in favor of the more specific {@link ItemView#addServerReloadCallback(ReloadCallback)}
     */
    @Deprecated(since = "6.1.0")
    public static void addReloadCallback(ReloadCallback callback) {
        addServerReloadCallback(callback);
    }

    /**
     * Mods can add a ReloadCallback to hook into a server reload.
     * <br>
     * <br>
     * They should register their stack-sensitives here, because the list of stack-sensitives is cleared before every reload
     *
     * @param callback The reload callback
     */
    public static void addServerReloadCallback(ReloadCallback callback) {
        RELOAD_CALLBACKS.add(callback);
    }

    /**
     * Mods can add a ReloadCallback to hook into a server reload (from the client side)
     * <br>
     * <br>
     * They should register their excluded items here
     * @param callback: A functional interface that runs on the client when the server reloads.
     */
    public static void addClientReloadCallback(ReloadCallback callback) {
        CLIENT_RELOAD_CALLBACKS.add(callback);
    }

    /**
     * Mods can add a world interaction recipe via the API rather than via a resource pack.
     * Run this via {@link ItemView#addClientReloadCallback} to ensure it's registered on time.
     */
    public static void addWorldInteractionRecipe(WorldInteractionClientRecipe recipe) {
        WORLD_INTERACTION_RECIPES.add(recipe);
    }

    /**
     * Mods can add an info recipe via the API rather than via a resource pack.
     * Run this via {@link ItemView#addClientReloadCallback} to ensure it's registered on time.
     */
    public static void addInfoRecipe(InfoClientRecipe recipe) {
        INFO_RECIPES.add(recipe);
    }

    /**
     * @return A list of currently present reload callbacks
     */
    public static List<ReloadCallback> getReloadCallbacks() {
        return RELOAD_CALLBACKS;
    }

    /**
     *
     * @return A list of currently present client reload callbacks
     */
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

    public static boolean isExcludedItem(ItemStack stack) {
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




    public interface ReloadCallback {

        void onReload();
    }

    /**
     * Representation of a stack-sensitive
     *
     * @param stack The itemStack used as an item-variant
     */
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


    }
}
