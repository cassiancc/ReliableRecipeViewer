package cc.cassian.rrv.common.builtin.anvil;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class AnvilCombiningClientRecipe implements ReliableClientRecipe {

    private final SlotContent left, right, result;
    private final int priority;
    private final Identifier id;

    public AnvilCombiningClientRecipe(Identifier id, SlotContent left, SlotContent right, SlotContent result, int priority) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.result = result;
        this.priority = priority;
    }


    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return AnvilCombiningClientRecipeType.INSTANCE;
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

    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(AnvilScreen.class);
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);
        transferMap.linkSlots(1, 1);

    }
}
