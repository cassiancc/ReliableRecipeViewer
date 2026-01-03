package de.crafty.eiv.common.builtin.brewing;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.builtin.BuiltInEivIntegration;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.common.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class BrewingViewRecipe implements IEivViewRecipe {

    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};


    private final SlotContent bottle1, bottle2, bottle3;
    private final SlotContent result, magicIngredient;

    private final AnimationTicker brewProgressTicker;

    public BrewingViewRecipe(BrewingServerRecipe brewingServerRecipe) {

        this.bottle1 = SlotContent.of(brewingServerRecipe.getBottleIngredient().copy());
        this.bottle2 = SlotContent.of(brewingServerRecipe.getBottleIngredient().copy());
        this.bottle3 = SlotContent.of(brewingServerRecipe.getBottleIngredient().copy());

        this.result = SlotContent.of(brewingServerRecipe.getResult());
        this.magicIngredient = SlotContent.of(brewingServerRecipe.getMagicIngredient());

        this.brewProgressTicker = AnimationTicker.create(Identifier.withDefaultNamespace("brew_progress_tick"), 400);
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return BrewingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.result);
        slotFillContext.bindSlot(1, this.magicIngredient);

        slotFillContext.bindSlot(2, this.bottle1);
        slotFillContext.bindSlot(3, this.bottle2);
        slotFillContext.bindSlot(4, this.bottle3);

    }



    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.bottle1, this.bottle2, this.bottle3, this.magicIngredient);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }


    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.brewProgressTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 39, 30, 38, 0, 18, 4, 128, 128);

        int brewProgress = Math.round(this.brewProgressTicker.getProgress() * 28);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 76, 2, 56, 0, 9, brewProgress, 128, 128);

        int bubbleProgress = 29 - BUBBLELENGTHS[this.brewProgressTicker.getTick() / 2 % 7];
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInEivIntegration.WIDGETS, 42, 29 - bubbleProgress, 64, 29 - bubbleProgress, 13, bubbleProgress, 128, 128);
    }
}
