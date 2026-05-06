package cc.cassian.rrv.common.builtin.tag.block;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlockTagClientRecipeType implements ReliableClientRecipeType {

    public static final BlockTagClientRecipeType INSTANCE = new BlockTagClientRecipeType();
    public static final Identifier GUI_BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/entity.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.block_tag");
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
    public int getPriority() {
        return 101;
    }

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public Identifier getId() {
        return ReliableRecipeViewer.of("block_tag");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.NAME_TAG);
    }
}
