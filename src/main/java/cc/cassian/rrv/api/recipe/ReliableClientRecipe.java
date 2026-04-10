package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface ReliableClientRecipe {

    List<ReliableClientRecipe> PLACEHOLDER = List.of(
            new ReliableClientRecipe() {


                @Override
                public ReliableClientRecipeType getViewType() {
                    return CraftingClientRecipeType.INSTANCE;
                }

                @Override
                public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
                }

                @Override
                public List<SlotContent> getIngredients() {
                    return List.of();
                }

                @Override
                public List<SlotContent> getResults() {
                    return List.of();
                }

            }
    );

    /**
     * @return The client recipe type of this recipe
     */
    ReliableClientRecipeType getViewType();

    /**
     * Bind the SlotContents of the recipe to the according slots
     *
     * @param slotFillContext The context the {@link SlotContent}s is bound to
     */
    void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext);

    /**
     * @return A list of {@link SlotContent}s, representing the ingredients of this recipe
     */
    List<SlotContent> getIngredients();

    /**
     * @return A list of {@link SlotContent}s, representing the results of this recipe
     */
    List<SlotContent> getResults();

	/**
     * Whether this recipe should be considered for tools like a craftable filter.
     * This is used to prevent informational recipes like tags or info recipes from affecting the craftable index.
     */
    default boolean isVisualOnly() {
        return false;
    }

    /**
     * @param stack The ItemStack that is checked
     * @return Whether this specific ItemStack can redirect as an ingredient (mainly used for component checks)
     */
    default boolean redirectsAsIngredient(ItemStack stack) {

        boolean potionRedirectCheck = ItemViewRecipes.makePotionRedirectCheck(stack, this.getIngredients());
        boolean enchantmentRedirectCheck = ItemViewRecipes.makeEnchantedRedirectCheck(stack, this.getIngredients());

        return potionRedirectCheck && enchantmentRedirectCheck;
    }

    /**
     * @param stack The ItemStack that is checked
     * @return Whether this specific ItemStack can redirect as a result (mainly used for component checks)
     */
    default boolean redirectsAsResult(ItemStack stack) {
        boolean potionRedirectCheck = ItemViewRecipes.makePotionRedirectCheck(stack, this.getResults());
        boolean enchantmentRedirectCheck = ItemViewRecipes.makeEnchantedRedirectCheck(stack, this.getResults());

        return potionRedirectCheck && enchantmentRedirectCheck;
    }

    /**
     * @return The priority of this recipe (The higher the priority, the earlier a recipe is displayed in the view)
     */
    default int getPriority() {
        return 0;
    }

    /**
     * The identifier of this recipe.
     */
    default Identifier getId() {
        return null;
    }

    /**
     * @return A list of {@link AnimationTicker}s; Useful for rendering animations
     */
    default List<AnimationTicker> getAnimationTickers() {
        return List.of();
    }

    /**
     * @param screen       The current viewScreen
     * @param guiGraphics  The guiGraphics supplied by Minecraft
     * @param mouseX       The current x-position of the mouse <b>relative to the position of the rendered recipe</b>
     * @param mouseY       The current y-position of the mouse <b>relative to the position of the rendered recipe</b>
     * @param partialTicks partialTicks
     */
    default void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }

    /**
     * Called on every gameTick
     */
    default void tick() {

    }

    /**
     * Called when this recipe pop's up in the viewScreen
     * <br>
     * Useful for setting things up like entities for rendering etc...
     */
    default void initRecipe() {

    }

    /**
     * Called when this recipe pop's out of the viewScreen
     * <br>
     * Useful for performance reasons, to remove entities etc...
     */
    default void fadeRecipe() {

    }

    /**
     * @return Whether this recipe should support item-transfer
     */
    default boolean supportsItemTransfer() {
        return false;
    }

    /**
     * Deprecated: Use <b>getTransferClasses();</b>
     *
     * @return A class associated with the recipe to determine whether an item-transfer should be possible or not
     */
    @Deprecated
    default Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return null;
    }

    /**
     * @return A list of classes associated with the recipe to determine whether an item-transfer should be possible
     */
    default List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return this.getTransferClass() == null ? List.of() : List.of(this.getTransferClass());
    }

    /**
     * @param screen The current gui screen the player was in before opening the recipe view
     * @return Whether the screen is compatible with this specific recipe
     * <br>
     * <b>Example</b>: Shaped crafting recipes can only be transferred to the survival inventory when the grid is not larger than 2x2
     */
    default boolean canTransferToScreen(AbstractContainerScreen<?> screen) {
        return true;
    }

    /**
     * Map the recipe's slots to the destination inventory's slots (sometimes they might be different)
     *
     * @param transferMap An empty transferMap
     * @param screen      The current containerScreen, the items should be transferred to
     */
    default void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {
    }


    /**
     * All recipes added after RRV 8.0.0 should not provide null as an ID.
     */
    @Deprecated
    default Identifier entryId() {
        return Objects.requireNonNullElse(getId(), getViewType().getId().withSuffix("/" + getResults().hashCode()));
    }


    /**
     * A representation of a TransferMap
     */
    class RecipeTransferMap {

        private final HashMap<Integer, Integer> map;

        public RecipeTransferMap() {
            map = new HashMap<>();
        }

        public void linkSlots(int recipeSlot, int destSlot) {
            this.map.put(recipeSlot, destSlot);
        }

        public HashMap<Integer, Integer> getTransferMap() {
            return this.map;
        }
    }


    /**
     * A data object containing information about the current render dimensions of the recipe
     * <br><br>
     * <b>Reason</b>: Since minecraft changed things like tooltip and entity rendering,
     * we cannot render these things within a previously moved matrix anymore
     * @param left
     * @param top
     * @param width
     * @param height
     */
    record RecipePosition(int left, int top, int width, int height) {

    }
}
