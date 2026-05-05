package cc.cassian.rrv.common.builtin.smelting;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmeltingClientRecipeType implements ReliableClientRecipeType {

    public static final SmeltingClientRecipeType INSTANCE = new SmeltingClientRecipeType();

    private static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/smelting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.smelting");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("furnace_smelting");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.FURNACE);
    }

    @Override
    public int getDisplayWidth() {
        return 88;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public Identifier getGuiTexture() {
        return BACKGROUND;
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Input Slot
        slotDefinition.addItemSlot(0, 4, 4);

        //Fuel Slot
        slotDefinition.addItemSlot(1, 4, 40);

        //Result Slot
        slotDefinition.addItemSlot(2, 64, 22);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.FURNACE));
    }
}
