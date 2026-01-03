package de.crafty.eiv.common.builtin.burning;

import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.List;

public class BurningViewRecipe implements IEivViewRecipe {

    private final SlotContent fuel;
    private final int burnTime;

    private final AnimationTicker ticker;

    public BurningViewRecipe(BurningServerRecipe recipe) {
        this.fuel = SlotContent.of(recipe.getFuel());
        this.burnTime = recipe.getBurnTime();

        this.ticker = AnimationTicker.create(Identifier.withDefaultNamespace("burning_tick_" + this.burnTime), this.burnTime);

    }

    @Override
    public IEivRecipeViewType getViewType() {
        return BurningViewType.INSTANCE;
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

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 19, 2 + (14 - burnProgress), 0, 14 - burnProgress, 14, burnProgress, 128, 128);
        guiGraphics.drawString(font, Component.literal(this.burnTime + " ticks"), 38, 18 / 2 - font.lineHeight / 2, 0xFF808080, false);
    }
}
