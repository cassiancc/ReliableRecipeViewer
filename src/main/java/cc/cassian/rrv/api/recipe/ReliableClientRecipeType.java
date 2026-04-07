package cc.cassian.rrv.api.recipe;

import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Client-Side representation of a recipe type
 */
public interface ReliableClientRecipeType {

    ReliableClientRecipeType NONE = new ReliableClientRecipeType() {

        @Override
        public Component getDisplayName() {
            return Component.empty();
        }
        

        @Override
        public int getDisplayWidth() {
            return 0;
        }

        @Override
        public int getDisplayHeight() {
            return 0;
        }

        @Override
        public Identifier getGuiTexture() {
            return null;
        }

        @Override
        public int getSlotCount() {
            return 0;
        }

        @Override
        public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        }

        @Override
        public Identifier getId() {
            return null;
        }

        @Override
        public ItemStack getIcon() {
            return new ItemStack(Items.BARRIER);
        }
    };

    /**
     *
     * @return The name that is displayed as the recipe type in the view screen
     */
    Component getDisplayName();

    /**
     *
     * @return The display width of the recipe type's gui texture
     */
    int getDisplayWidth();

    /**
     * @return The display height of the recipe type's gui texture
     */
    int getDisplayHeight();

    /**
     *
     * @return The path to the recipe type's gui texture
     */
    @Nullable Identifier getGuiTexture();

    /**
     * @return The default priority of this recipe type (The higher the priority, the earlier a recipe type is displayed in the view)
     */
    default int getPriority() {
        return 0;
    }

    /**
     * @return Whether this recipe type is enabled in the view by default
     */
    default boolean enabled() {
        return true;
    }

    /**
     *
     * @return The number of slots <b>one</b> recipe requires for display
     */
    int getSlotCount();

    /**
     * Place the slots of the recipe's type in the gui
     *
     * @param slotDefinition An empty SlotDefinition
     */
    void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition);

    /**
     *
     * @return A unique id of the recipe's type
     */
    Identifier getId();


    /**
     *
     * @return An icon for the recipe's type, displayed in the type selection above the recipe view.
     * If {@link #renderIcon} is overridden, this will be ignored.
     */
    ItemStack getIcon();


    /**
     * Replaces the rendering of {@link #getIcon()} with custom rendering.
     * @param screen       The current viewScreen
     * @param x            The x-position to render at.
     * @param y            The y-position to render at.
     * @param guiGraphics  The guiGraphics supplied by Minecraft
     * @param mouseX       The current x-position of the mouse
     * @param mouseY       The current y-position of the mouse
     * @param partialTicks partialTicks
     */
    default void renderIcon(RecipeViewScreen screen, int x, int y, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fakeItem(this.getIcon(), x, y);
    }

    /**
     * Craft-References are used to redirect the player to a list of this type's recipes that are associated with the reference-item
     *
     *
     * @return A list of craft references
     */
    default List<ItemStack> getCraftReferences() {
        return List.of();
    }

    /**
     *
     * @return A condition managing the recipes a craft-reference can redirect to<br>
     * <br>
     * <b>Example</b>: The villager's working blocks only redirect if the trading recipe matches to the profession of the villager applied by the working block
     */
    default ReferenceCondition getCraftReferenceCondition() {
        return (stack, viewRecipe) -> true;
    }




    interface ReferenceCondition {

        /**
         *
         * @param craftReference The craft-reference
         * @param viewRecipe The recipe that is checked
         * @return Whether the craft-reference can redirect to the viewRecipe
         */
        boolean matches(ItemStack craftReference, ReliableClientRecipe viewRecipe);

    }
}
