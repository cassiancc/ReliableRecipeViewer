package de.crafty.eiv.common.builtin.smithing;

import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//TODO split Smithing recipes in upgrade and trim recipes
public class SmithingViewRecipe implements IEivViewRecipe {

    private final SlotContent additionIngredient;
    private final SlotContent base, template;
    private final SlotContent result;

    private final boolean isTrimType;
    private final TransmuteResult upgradeResult;

    public SmithingViewRecipe(boolean isTrimType, Ingredient additionIngredient, Ingredient base, Ingredient template, TrimPattern trimPattern, @Nullable TransmuteResult upgradeResult) {
        this.isTrimType = isTrimType;

        this.template = template != null ? SlotContent.of(template) : SlotContent.of(Items.AIR);
        this.base = base != null ? SlotContent.of(base) : SlotContent.of(Items.AIR);
        this.additionIngredient = additionIngredient != null ? SlotContent.of(additionIngredient) : SlotContent.of(Items.AIR);
        this.upgradeResult = upgradeResult;

        if (Minecraft.getInstance().player == null) {
            this.result = SlotContent.of(Items.AIR);
            return;
        }

        HolderLookup.Provider provider = Minecraft.getInstance().player.level().registryAccess();

        if (this.isTrimType) {
            List<ItemStack> possibleResults = new ArrayList<>();

            this.additionIngredient.getValidContents().forEach(addition -> {
                possibleResults.add(SmithingTrimRecipe.applyTrim(provider, this.base.next(), addition, Holder.direct(trimPattern)));
            });

            this.result = SlotContent.of(possibleResults);

            return;
        }

        this.result = SlotContent.of(this.upgradeResult == null ? ItemStack.EMPTY : this.upgradeResult.apply(this.base.next()));
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return SmithingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.template);
        slotFillContext.bindSlot(1, this.base);
        slotFillContext.bindSlot(2, this.additionIngredient);

        slotFillContext.bindDepedantSlot(3, this.additionIngredient::index, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.template, this.base, this.additionIngredient);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public int getPriority() {
        return this.isTrimType ? 1 : 0;
    }


    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return SmithingScreen.class;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);
        transferMap.linkSlots(1, 1);
        transferMap.linkSlots(2, 2);

    }
}
