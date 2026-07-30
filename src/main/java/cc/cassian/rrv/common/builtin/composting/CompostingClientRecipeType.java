package cc.cassian.rrv.common.builtin.composting;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CompostingClientRecipeType implements ReliableClientRecipeType {

    public static final CompostingClientRecipeType INSTANCE = new CompostingClientRecipeType();
    public static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/composting.png");


    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.composting");
    }

    @Override
    public int getDisplayWidth() {
        return 110;
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
        slotDefinition.addItemSlot(1, 90, 4);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("composting");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.COMPOSTER);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.COMPOSTER));
    }

    @Override
    public int getPriority() {
        return 50;
    }
}
