package cc.cassian.rrv.common.builtin.composting;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CompostingClientRecipe implements ReliableClientRecipe {

    private final SlotContent compost;
    private final int compostingChance;
    private final Identifier id;

    public CompostingClientRecipe(ItemStack item, float i) {
        //~ if >26 'getItemHolder'->'typeHolder'
        this.id = item.typeHolder().unwrapKey().orElseThrow().identifier().withPrefix("/").withSuffix("_composting");
        this.compost = SlotContent.of(item);
        this.compostingChance = Math.round(i * 100);
    }

    @Override
    public ReliableClientRecipeType getType() {
        return CompostingClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.compost);
        slotFillContext.bindSlot(1, SlotContent.of(Items.BONE_MEAL));
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.compost);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(SlotContent.of(Items.BONE_MEAL));
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        // composting text
        Font font = Minecraft.getInstance().font;
        context.guiGraphics().text(font, Component.translatable("view.rrv.type.composting.chance", compostingChance, "%"), 24, 25 / 2 - font.lineHeight / 2, 0xFF808080, false);
    }
}
