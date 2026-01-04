package cc.cassian.rrv.common.builtin.burning;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BurningClientRecipeType implements ReliableClientRecipeType {

    public static final BurningClientRecipeType INSTANCE = new BurningClientRecipeType();


    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.burning");
    }

    @Override
    public int getDisplayWidth() {
        return 72;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public Identifier getGuiTexture() {
        return Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/burning.png");
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 1, 1);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("furnace_burning");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.COAL);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.FURNACE));
    }
}
