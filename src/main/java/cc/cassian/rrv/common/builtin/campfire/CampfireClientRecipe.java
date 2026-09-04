package cc.cassian.rrv.common.builtin.campfire;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class CampfireClientRecipe implements ReliableClientRecipe {

    private final SlotContent input, result;
    private final AnimationTicker cookingTicker;
    private final Identifier id;

    public CampfireClientRecipe(RecipeHolder<CampfireCookingRecipe> campfireCookingRecipe) {
        this.id = campfireCookingRecipe.id().identifier();
        this.input = SlotContent.of(campfireCookingRecipe.value().input());
        this.result = SlotContent.of(campfireCookingRecipe.value().result);

        this.cookingTicker = AnimationTicker.create(Identifier.withDefaultNamespace("campfire_cooking_ticker"), 300);
    }

    @Override
    public ReliableClientRecipeType getType() {
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
    public Identifier getId() {
        return id;
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.cookingTicker);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        context.guiGraphics().fakeItem(new ItemStack(Items.CAMPFIRE), 4, 23);
        int cookingProgress = Math.round(this.cookingTicker.getProgress() * 24);
        context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 28, 4, 14, 0, cookingProgress, 16, 128, 128);
    }
}
