package cc.cassian.rrv.common.builtin.repairing;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class RepairingClientRecipe implements ReliableClientRecipe {

    private final SlotContent base, repairIngredient, result;


    public RepairingClientRecipe(ItemStack base, Ingredient repairIngredient, ItemStack result) {
        this.base = base != null ? SlotContent.of(base) : SlotContent.of(Items.AIR);
        this.repairIngredient = repairIngredient != null ? SlotContent.of(repairIngredient) : SlotContent.of(Items.AIR);

        this.result = SlotContent.of(result);
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return RepairingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.base);
        slotFillContext.bindSlot(1, this.repairIngredient);
        slotFillContext.bindSlot(2, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.base, this.repairIngredient);
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
