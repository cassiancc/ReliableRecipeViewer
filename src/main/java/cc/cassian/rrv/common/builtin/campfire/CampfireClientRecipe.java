package cc.cassian.rrv.common.builtin.campfire;

import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CampfireClientRecipe implements ReliableClientRecipe {

    private final SlotContent input, result;
    private final AnimationTicker cookingTicker;

    public CampfireClientRecipe(CampfireServerRecipe campfireCookingRecipe) {
        this.input = SlotContent.of(campfireCookingRecipe.getInput());
        this.result = SlotContent.of(campfireCookingRecipe.getResult());

        this.cookingTicker = AnimationTicker.create(ResourceLocation.withDefaultNamespace("campfire_cooking_ticker"), 300);
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return CampfireClientRecipeType.INSTANCE;
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
        guiGraphics.blit(RenderType::guiTextured, BuiltInReliableRecipeViewerIntegration.WIDGETS, 25, 1, 14, 0, cookingProgress, 16, 128, 128);
    }
}
