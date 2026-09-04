package cc.cassian.rrv.common.builtin.burning;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.rendering.AnimationTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BurningClientRecipe implements ReliableClientRecipe {

    private final SlotContent fuel;
    private final float burnTime;

    private final AnimationTicker ticker;
    private final Identifier id;

    public BurningClientRecipe(Item item, int i) {
        this.id = BuiltInRegistries.ITEM.getKey(item).withPrefix("/").withSuffix("_burning");
        this.fuel = SlotContent.of(item);
        this.burnTime = i;

        this.ticker = AnimationTicker.create(Identifier.withDefaultNamespace("burning_tick_" + (int) this.burnTime), (int) this.burnTime);
    }

    //? if >26.2 {
    /*public BurningClientRecipe(ItemStack item, float i) {
        this.id = item.typeHolder().unwrapKey().get().identifier().withPrefix("/").withSuffix("_burning");
        this.fuel = SlotContent.of(item);
        this.burnTime = i;

        this.ticker = AnimationTicker.create(Identifier.withDefaultNamespace("burning_tick_" + (int) this.burnTime), (int) this.burnTime);
    }
    *///?}

    @Override
    public ReliableClientRecipeType getType() {
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
    public Identifier getId() {
        return id;
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.ticker);
    }


    @Override
    public void renderRecipe(RecipeScreenContext context) {
        // burn sprite
        int burnProgress = Math.round(this.ticker.getProgress() * 14);
        context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, BuiltInReliableRecipeViewerIntegration.WIDGETS, 22, 5 + (14 - burnProgress), 0, 14 - burnProgress, 14, burnProgress, 128, 128);
        // burn text
        Font font = Minecraft.getInstance().font;
        context.guiGraphics().text(font, Component.translatable("view.rrv.type.burning.ticks", this.burnTime), 40, 25 / 2 - font.lineHeight / 2, 0xFF808080, false);
    }
}
