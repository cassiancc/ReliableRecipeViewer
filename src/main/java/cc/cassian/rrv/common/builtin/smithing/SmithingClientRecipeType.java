package cc.cassian.rrv.common.builtin.smithing;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmithingClientRecipeType implements ReliableClientRecipeType {

    protected static final SmithingClientRecipeType INSTANCE = new SmithingClientRecipeType();

    private static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/smithing.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.smithing");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("smithing");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.SMITHING_TABLE);
    }

    @Override
    public int getDisplayWidth() {
        return 114;
    }

    @Override
    public int getDisplayHeight() {
        return 24;
    }

    @Override
    public Identifier getGuiTexture() {
        return BACKGROUND;
    }

    @Override
    public int getSlotCount() {
        return 4;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Base
        slotDefinition.addItemSlot(0, 3, 4);

        //Addition
        slotDefinition.addItemSlot(1, 22, 4);

        //Template
        slotDefinition.addItemSlot(2, 40, 4);

        //Result
        slotDefinition.addItemSlot(3, 94, 4);
    }


    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.SMITHING_TABLE));
    }
}
