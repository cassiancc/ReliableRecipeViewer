package cc.cassian.rrv.common.builtin.smoking;

import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.common.api.recipe.IRrvClientRecipe;
import cc.cassian.rrv.common.api.recipe.IRrvClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.List;

public class SmokingClientRecipe implements IRrvClientRecipe {

    private final SlotContent input, result;
    private final AnimationTicker smokingTicker;

    public SmokingClientRecipe(SmokingServerRecipe smokingRecipe) {

        this.input = SlotContent.of(smokingRecipe.getInput());
        this.result = SlotContent.of(smokingRecipe.getResult());

        this.smokingTicker = AnimationTicker.create(Identifier.withDefaultNamespace("smoking_ticker"), 100);
    }


    @Override
    public IRrvClientRecipeType getViewType() {
        return SmokingClientRecipeRecipeType.INSTANCE;
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
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.smokingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.smokingTicker.getProgress() * 14);
        int smeltProgress = Math.round(this.smokingTicker.getProgress() * 24);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 1, 20 + (14 - litProgress), 0, 14 - litProgress, 14, litProgress, 128, 128);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 24, 19, 14, 0, smeltProgress, 16, 128, 128);
    }


    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public Class<? extends AbstractContainerScreen<?>> getTransferClass() {
        return SmokerScreen.class;
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {

        transferMap.linkSlots(0, 0);

    }
}
