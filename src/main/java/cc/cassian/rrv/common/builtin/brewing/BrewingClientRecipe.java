package cc.cassian.rrv.common.builtin.brewing;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public class BrewingClientRecipe implements ReliableClientRecipe {

    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};


    private final SlotContent bottle1, bottle2, bottle3;
    private final SlotContent result, magicIngredient;

    private final AnimationTicker brewProgressTicker;
    private final Identifier id = null;

    public BrewingClientRecipe(ItemStack result, Ingredient magicIngredient, ItemStack bottleIngredient) {
//        FIXME
//        ResourceKey<Potion> potion = result.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion().map(Holder::unwrapKey).orElseThrow().orElseThrow();
//        this.id = potion.identifier().withPrefix("/").withSuffix("_brewing_"+result.getItem().builtInRegistryHolder().key().identifier().getPath());
        this.result = SlotContent.of(result);
        this.magicIngredient = SlotContent.of(magicIngredient);
        this.bottle1 = SlotContent.of(bottleIngredient);
        this.bottle2 = SlotContent.of(bottleIngredient);
        this.bottle3 = SlotContent.of(bottleIngredient);

        this.brewProgressTicker = AnimationTicker.create(Identifier.withDefaultNamespace("brew_progress_tick"), 400);
    }

    @Override
    public ReliableClientRecipeType getType() {
        return BrewingClientRecipeType.INSTANCE;
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
    public Identifier getId() {
        return id;
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.brewProgressTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 39, 30, 38, 0, 18, 4, 128, 128);

        int brewProgress = Math.round(this.brewProgressTicker.getProgress() * 28);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 76, 2, 56, 0, 9, brewProgress, 128, 128);

        int bubbleProgress = 29 - BUBBLELENGTHS[this.brewProgressTicker.getTick() / 2 % 7];
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 42, 29 - bubbleProgress, 64, 29 - bubbleProgress, 13, bubbleProgress, 128, 128);
    }
}
