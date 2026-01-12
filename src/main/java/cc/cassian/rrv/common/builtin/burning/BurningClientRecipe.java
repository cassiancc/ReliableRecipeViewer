package cc.cassian.rrv.common.builtin.burning;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BurningClientRecipe implements ReliableClientRecipe {

    private final SlotContent fuel;
    private final int burnTime;

    private final AnimationTicker ticker;

    public BurningClientRecipe(BurningServerRecipe recipe) {
        this.fuel = SlotContent.of(recipe.getFuel());
        this.burnTime = recipe.getBurnTime();

        this.ticker = AnimationTicker.create(ResourceLocation.withDefaultNamespace("burning_tick_" + this.burnTime), this.burnTime);

    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return BurningClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.fuel);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.fuel);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of();
    }


    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.ticker);
    }


    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int burnProgress = Math.round(this.ticker.getProgress() * 14);

        Font font = Minecraft.getInstance().font;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 19, 2 + (14 - burnProgress), 0, 14 - burnProgress, 14, burnProgress, 128, 128);
        guiGraphics.drawString(font, Component.translatable("view.rrv.type.burning.ticks", this.burnTime), 38, 18 / 2 - font.lineHeight / 2, 0xFF808080, false);
    }
}
