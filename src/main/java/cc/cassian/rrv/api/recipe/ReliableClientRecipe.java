package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public interface ReliableClientRecipe {

    List<ReliableClientRecipe> PLACEHOLDER = List.of(
            new ReliableClientRecipe() {

                @Override
                public ReliableClientRecipeType getType() {
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

    /// Renamed to [ReliableClientRecipe#getType()] in 8.0.0
    /// @return The client recipe type of this recipe
    @Deprecated(since = "8.0.0")
    default ReliableClientRecipeType getViewType() {
        return getType();
    }

    /// Provides the [ReliableClientRecipeType] of this client recipe.
    /// @return The client recipe type of this recipe
    default ReliableClientRecipeType getType() {
        return getViewType();
    }

    /// Bind the SlotContents of the recipe to the according slots
    ///
    /// @param slotFillContext The context the [SlotContent]s is bound to
    void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext);

    /// @return A list of [SlotContent]s, representing the ingredients of this recipe
    List<SlotContent> getIngredients();

    /// @return A list of [SlotContent]s, representing the results of this recipe
    List<SlotContent> getResults();

	/**
     * Whether this recipe should be considered for tools like a craftable filter.
     * This is used to prevent informational recipes like tags or info recipes from affecting the craftable index.
     */
    default boolean isVisualOnly() {
        return false;
    }

    /// @param stack The ItemStack that is checked
    /// @return Whether this specific ItemStack can redirect as an ingredient (mainly used for component checks)
    default boolean redirectsAsIngredient(ItemStack stack) {
        return ItemViewRecipes.makeDefaultChecks(stack, this.getIngredients());
    }

    /// @param stack The ItemStack that is checked
    /// @return Whether this specific ItemStack can redirect as a result (mainly used for component checks)
    default boolean redirectsAsResult(ItemStack stack) {
        return ItemViewRecipes.makeDefaultChecks(stack, this.getResults());
    }

    /// @return The priority of this recipe (The higher the priority, the earlier a recipe is displayed in the view)
    default int getPriority() {
        return 0;
    }

    /// The identifier of this recipe, used for clientside operations like recipe hiding and favoriting.
    ///
    /// If this recipe does not have a file associated with it, prefix the path with `/` e.g. `minecraft:/code_driven_recipe` so that users do not look for a nonexistent file.
    default Identifier getId() {
        return null;
    }

    /// @return A list of [AnimationTicker]s; Useful for rendering animations
    default List<AnimationTicker> getAnimationTickers() {
        return List.of();
    }

    /// Deprecated: use [ReliableClientRecipe#renderRecipe(cc.cassian.rrv.api.client.RecipeScreenContext)], which provides the same functionality while allowing for recipes to be rendered on screens other than a [RecipeViewScreen].
    /// @param screen       The current recipe screen
    /// @param guiGraphics  The [GuiGraphicsExtractor] supplied by Minecraft
    /// @param mouseX       The current x-position of the mouse **relative to the position of the rendered recipe**
    /// @param mouseY       The current y-position of the mouse **relative to the position of the rendered recipe**
    /// @param partialTicks partialTicks
    @Deprecated(since = "8.7.0")
    default void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }

    /// Allows rendering custom content to the recipe screen.
    default void renderRecipe(RecipeScreenContext context) {
        if (context.screen() instanceof RecipeViewScreen recipeViewScreen) {
            renderRecipe(recipeViewScreen, context.recipePosition(), context.guiGraphics(), context.mouseX(), context.mouseY(), context.partialTicks());
        } else {
            try {
                renderRecipe(null, context.recipePosition(), context.guiGraphics(), context.mouseX(), context.mouseY(), context.partialTicks());
            } catch (Exception e) {
                context.guiGraphics().textWithWordWrap(Minecraft.getInstance().font, FormattedText.of("Failed to render recipe."), 20, 20, 160, -16777216, false);
                ReliableRecipeViewer.LOGGER.error("Failed to render recipe", e);
            }
        }
    }

    /// Called on every game tick
    default void tick() {

    }

    /// Called when this recipe pops up in the recipe screen
    ///
    /// Useful for setting things up like entities for rendering etc...
    default void initRecipe() {

    }

    /// Called when this recipe pops out of the recipe screen
    ///
    /// Useful for performance reasons, to remove entities etc...
    default void fadeRecipe() {

    }

    /**
     * @return Whether this recipe should support item transfer
     */
    default boolean supportsItemTransfer() {
        return false;
    }

    /// Deprecated: Use **getTransferClasses();**
    ///
    /// @return A class associated with the recipe to determine whether an item transfer should be possible or not
    @Deprecated
    default Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return null;
    }

    /// @return A list of classes associated with the recipe to determine whether an item transfer should be possible
    default List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return this.getTransferClass() == null ? List.of() : List.of(this.getTransferClass());
    }

    /// @param screen The current gui screen the player was in before opening the recipe view
    /// @return Whether the screen is compatible with this specific recipe
    ///
    /// **Example**: Shaped crafting recipes can only be transferred to the survival inventory when the grid is not larger than 2x2
    default boolean canTransferToScreen(AbstractContainerScreen<?> screen) {
        return true;
    }

    /// Map the recipe's slots to the destination inventory's slots (sometimes they might be different)
    ///
    /// @param transferMap An empty transferMap
    /// @param screen      The current containerScreen, the items should be transferred to
    default void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {
    }


    /// Provides a recipe ID that is guaranteed to not be null.
    ///
    /// Added for backwards compatibility: All client recipes added after RRV 8.0.0 should not provide null as an ID.
    @Deprecated
    default Identifier entryId() {
        return Objects.requireNonNullElse(getId(), getType().getId().withSuffix("/" + getResults().hashCode()));
    }


    /// A representation of a TransferMap
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


    /// A data object containing information about the current render dimensions of the recipe
    ///
    /// **Reason**: Since Minecraft changed things like tooltip and entity rendering,
    /// we cannot render these things within a previously moved matrix anymore
    /// @param left
    /// @param top
    /// @param width
    /// @param height
    record RecipePosition(int left, int top, int width, int height) {

    }
}
