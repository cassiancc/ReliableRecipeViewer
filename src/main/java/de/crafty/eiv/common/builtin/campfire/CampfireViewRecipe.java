package de.crafty.eiv.common.builtin.campfire;

import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CampfireViewRecipe implements IEivViewRecipe {

    private final SlotContent input, result;
    private final AnimationTicker cookingTicker;

    public CampfireViewRecipe(CampfireServerRecipe campfireCookingRecipe) {
        this.input = SlotContent.of(campfireCookingRecipe.getInput());
        this.result = SlotContent.of(campfireCookingRecipe.getResult());

        this.cookingTicker = AnimationTicker.create(Identifier.withDefaultNamespace("campfire_cooking_ticker"), 300);
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return CampfireViewType.INSTANCE;
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
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.cookingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        guiGraphics.renderItem(new ItemStack(Items.CAMPFIRE), 1, 20);

        int cookingProgress = Math.round(this.cookingTicker.getProgress() * 24);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 25, 1, 14, 0, cookingProgress, 16, 128, 128);
    }
}
