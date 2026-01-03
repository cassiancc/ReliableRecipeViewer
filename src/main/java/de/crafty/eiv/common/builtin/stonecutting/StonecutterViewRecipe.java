package de.crafty.eiv.common.builtin.stonecutting;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;

import java.util.List;

public class StonecutterViewRecipe implements IEivViewRecipe {


    private final SlotContent input, result;

    public StonecutterViewRecipe(StonecutterServerRecipe stonecutterRecipe) {
        this.input = SlotContent.of(stonecutterRecipe.getInput());
        this.result = SlotContent.of(stonecutterRecipe.getResult());
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return StonecutterViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
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
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return StonecutterScreen.class;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);

    }
}
