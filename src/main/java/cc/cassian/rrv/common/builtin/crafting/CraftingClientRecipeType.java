package cc.cassian.rrv.common.builtin.crafting;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CraftingClientRecipeType implements ReliableClientRecipeType {

    public static final CraftingClientRecipeType INSTANCE = new CraftingClientRecipeType();

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/crafting_bordered.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.crafting");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("crafting");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.CRAFTING_TABLE);
    }

    @Override
    public int getDisplayWidth() {
        return 122;
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
        return 10;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Input slots
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                slotDefinition.addItemSlot(x + y * 3, 4 + x * 18,  4 + y * 18);
            }
        }

        //Result Slot
        slotDefinition.addItemSlot(9, 98, 22);

    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.CRAFTING_TABLE), new ItemStack(Items.CRAFTER));
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
