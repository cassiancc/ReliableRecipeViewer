package cc.cassian.rrv.common.builtin.stonecutting;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class StonecutterClientRecipe implements ReliableClientRecipe {


    private final SlotContent input, result;
    private final Identifier id;

    public StonecutterClientRecipe(RecipeHolder<StonecutterRecipe> stonecutterRecipe) {
        this.id = stonecutterRecipe.id().identifier();
        this.input = SlotContent.of(stonecutterRecipe.value().input());
        this.result = SlotContent.of(stonecutterRecipe.value().result);
    }

    @Override
    public ReliableClientRecipeType getType() {
        return StonecutterClientRecipeType.INSTANCE;
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
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(StonecutterScreen.class);
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);

    }
}
