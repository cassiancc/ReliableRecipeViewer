package cc.cassian.rrv.common.builtin.smithing;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmithingClientRecipe implements ReliableClientRecipe {

    private final SlotContent additionIngredient;
    private final SlotContent base, template;
    private final SlotContent result;

    private final boolean isTrimType;
    private final SlotContent upgradeResult;
    private final Identifier id;


    public SmithingClientRecipe(Identifier id, boolean isTrimType, SlotContent additionIngredient, SlotContent base, SlotContent template, TrimPattern trimPattern, SlotContent upgradeResult) {
        this.isTrimType = isTrimType;
        this.id = id;

        this.template = template;
        this.base = base;
        this.additionIngredient = additionIngredient;
        this.upgradeResult = upgradeResult;

        if (this.isTrimType) {
            List<ItemStack> possibleResults = new ArrayList<>();

            this.additionIngredient.getValidContents().forEach(addition -> {
                possibleResults.add(SmithingTrimRecipe.applyTrim(this.base.next(), addition, Holder.direct(trimPattern)));
            });

            this.result = SlotContent.of(possibleResults);

            return;
        }

        this.result = this.upgradeResult;

    }

    public SmithingClientRecipe(Identifier identifier, boolean isTrim, Ingredient base, Ingredient template, Ingredient addition, TrimPattern pattern, @Nullable ItemStackTemplate upgradeResult) {
        this(identifier, isTrim, SlotContent.of(addition), SlotContent.of(base), SlotContent.of(template), pattern, SlotContent.of(upgradeResult));
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return SmithingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindOptionalSlot(0, this.template, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(1, this.base, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);
        slotFillContext.bindOptionalSlot(2, this.additionIngredient, RecipeViewMenu.OptionalSlotRenderer.DEFAULT);

        slotFillContext.bindDependentSlot(3, this.additionIngredient::index, this.result);
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
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(SmithingScreen.class);
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);
        transferMap.linkSlots(1, 1);
        transferMap.linkSlots(2, 2);

    }
}
