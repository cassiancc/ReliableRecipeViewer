package cc.cassian.rrv.common.builtin.interaction;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WorldInteractionClientRecipe implements ReliableClientRecipe {

    private final SlotContent left, right, result;


    public WorldInteractionClientRecipe(SlotContent left, SlotContent right, SlotContent result) {
        this.left = left;
        this.right = right;

        this.result = result;
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return WorldInteractionClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.left);
        slotFillContext.bindSlot(1, this.right);
        slotFillContext.bindSlot(2, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.left, this.right);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }
}
