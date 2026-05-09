package cc.cassian.rrv.common.builtin.stonecutting;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class StonecutterClientRecipeType implements ReliableClientRecipeType {

    protected static final StonecutterClientRecipeType INSTANCE = new StonecutterClientRecipeType();

    private static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/stonecutter.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.stonecutter");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("stonecutting");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.STONECUTTER);
    }

    @Override
    public int getDisplayWidth() {
        return 80;
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
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 4);
        slotDefinition.addItemSlot(1, 60, 4);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.STONECUTTER));
    }

    public ButtonData placeRecipeTransferButton(int guiLeft, int guiTop) {
        return new ButtonData(guiLeft + getDisplayWidth() + 4, guiTop + getDisplayHeight() / 2 - 14, true);
    }

    public ButtonData placeRecipeShareButton(int guiLeft, int guiTop) {
        return new ButtonData(guiLeft + getDisplayWidth() + 4, guiTop + getDisplayHeight() / 2 + 1, true);
    }
}
