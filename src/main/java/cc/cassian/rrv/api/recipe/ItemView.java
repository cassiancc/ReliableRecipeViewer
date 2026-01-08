package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Main API class used to register RRV compat for other mods
 */
public class ItemView {

    /**
     * A list of client-side excluded items that won't show up in the ItemView overlay
     */
    private static final List<Item> EXCLUDED = new ArrayList<>();

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
     * ServerRecipeProviders offer a recipeList where mods can easily add their own server recipes
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
        Arrays.stream(items).filter(item -> !EXCLUDED.contains(item)).forEach(EXCLUDED::add);
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
    public static List<Item> getExcluded() {
        return EXCLUDED;
    }


    /**
     * Opens a recipe view for the client-player containing all recipes that use the specified stack as an ingredient
     * @param stack The ingredient stack
     */
    public static void openForStackIngredient(ItemStack stack) {
        ItemViewOverlay.INSTANCE.openRecipeView(stack, ItemViewOverlay.ItemViewOpenType.INPUT);
    }

    /**
     * Opens a recipe view for the client-player containing all recipes that own the specified stack as a result
     * @param stack The result stack
     */
    public static void openForStackResult(ItemStack stack) {
        ItemViewOverlay.INSTANCE.openRecipeView(stack, ItemViewOverlay.ItemViewOpenType.RESULT);
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
     * @param callback
     */
    public static void addClientReloadCallback(ReloadCallback callback) {
        CLIENT_RELOAD_CALLBACKS.add(callback);
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
