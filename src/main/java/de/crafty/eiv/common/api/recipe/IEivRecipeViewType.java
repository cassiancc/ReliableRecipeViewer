package de.crafty.eiv.common.api.recipe;

import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * Client-Side representation of a recipe type
 */
public interface IEivRecipeViewType {

    IEivRecipeViewType NONE = new IEivRecipeViewType() {

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
    Identifier getGuiTexture();

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
     * @return An icon for the recipe's type, displayed in the type selection above the recipe view
     */
    ItemStack getIcon();

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
        boolean matches(ItemStack craftReference, IEivViewRecipe viewRecipe);

    }
}
