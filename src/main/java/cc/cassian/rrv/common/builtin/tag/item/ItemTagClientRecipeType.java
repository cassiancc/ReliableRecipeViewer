package cc.cassian.rrv.common.builtin.tag.item;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemTagClientRecipeType implements ReliableClientRecipeType {

    public static final ItemTagClientRecipeType INSTANCE = new ItemTagClientRecipeType();
    public static final Identifier GUI_BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/entity.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.item_tag");
    }

    @Override
    public int getDisplayWidth() {
        return 162;
    }

    @Override
    public int getDisplayHeight() {
        return 142;
    }

    @Override
    public Identifier getGuiTexture() {
        return GUI_BACKGROUND;
    }

    // Tags should not exceed 54 slots
    @Override
    public int getSlotCount() {
        return 55;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.setHighlightWithoutContents(false);

        slotDefinition.addItemSlot(0,73,8);
        for (int row = 0; row < 6; row++) {
            for (int i = 0; i < 9; i++) {
                slotDefinition.addItemSlot(row * 9 + i+1, i * 18 + 1, 45 + row * 18);
            }
        }

    }

    @Override
    public Identifier getId() {
        return ReliableRecipeViewer.of("item_tag");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.NAME_TAG);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public ButtonData placeRecipeShareButton(int guiLeft, int guiTop) {
        return new ButtonData(guiLeft + getDisplayWidth() - 12, guiTop+30, true);
    }
}
