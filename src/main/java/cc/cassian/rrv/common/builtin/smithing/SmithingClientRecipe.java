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

    private final int priority;
    private final Identifier id;

    public SmithingClientRecipe(Identifier id, SlotContent additionIngredient, SlotContent base, SlotContent template, SlotContent result, int priority) {
        this.priority = priority;
        this.id = id;
        this.template = template;
        this.base = base;
        this.additionIngredient = additionIngredient;
        this.result = result;
    }

    /// Smithing trim recipes
    public static SmithingClientRecipe trimRecipe(Identifier id, SlotContent additionIngredient, SlotContent base, SlotContent template, TrimPattern trimPattern) {
        return new SmithingClientRecipe(id, template, base, additionIngredient, getPossibleResults(additionIngredient, base, trimPattern), 1);
    }

    /// Smithing trim recipes
    public static SmithingClientRecipe trimRecipe(Identifier id, Ingredient additionIngredient, Ingredient base, Ingredient template, TrimPattern trimPattern) {
        return trimRecipe(id, SlotContent.of(additionIngredient), SlotContent.of(base), SlotContent.of(template), trimPattern);
    }

    /// Smithing transformation recipes
    public static SmithingClientRecipe transformationRecipe(Identifier id, SlotContent additionIngredient, SlotContent base, SlotContent template, SlotContent upgradeResult) {
        return new SmithingClientRecipe(id, additionIngredient, base, template, upgradeResult, 0);
    }

    /// Smithing transformation recipes
    public static SmithingClientRecipe transformationRecipe(Identifier id, Ingredient additionIngredient, Ingredient base, Ingredient template, ItemStackTemplate upgradeResult) {
        return transformationRecipe(id, SlotContent.of(additionIngredient), SlotContent.of(base), SlotContent.of(template), SlotContent.of(upgradeResult));
    }

    private static SlotContent getPossibleResults(SlotContent additionIngredient, SlotContent base, TrimPattern trimPattern) {
        List<ItemStack> possibleResults = new ArrayList<>();

        additionIngredient.getValidContents().forEach(addition -> {
            possibleResults.add(SmithingTrimRecipe.applyTrim(base.next(), addition, Holder.direct(trimPattern)));
        });
        return SlotContent.of(possibleResults);
    }

    @Override
    public ReliableClientRecipeType getType() {
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
        return priority;
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
