package cc.cassian.rrv.common.builtin.smelting;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;

public class SmeltingClientRecipe implements ReliableClientRecipe {

    private final SlotContent input, result;
    private final AnimationTicker smeltingTicker;
    private final Identifier id;

    public SmeltingClientRecipe(RecipeHolder<SmeltingRecipe> recipe) {

        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().input());
        this.result = SlotContent.of(recipe.value().result);

        this.smeltingTicker = AnimationTicker.create(Identifier.withDefaultNamespace("smelting_tick"), 200);
    }

    @Override
    public SmeltingClientRecipeType getType() {
        return SmeltingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(2, this.result);
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
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.smeltingTicker);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {

        int litProgress = Math.round(this.smeltingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smeltingTicker.getProgress() * 24);

        context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 4, 23 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 26, 21, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(FurnaceScreen.class);
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);
    }
}
