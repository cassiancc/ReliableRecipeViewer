package cc.cassian.rrv.common.builtin.smithing;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmithingClientRecipeType implements ReliableClientRecipeType {

    protected static final SmithingClientRecipeType INSTANCE = new SmithingClientRecipeType();

    private static final ResourceLocation SMITHING_LOCATION = ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/smithing.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.smithing");
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.withDefaultNamespace("smithing");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.SMITHING_TABLE);
    }

    @Override
    public int getDisplayWidth() {
        return 108;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return SMITHING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 4;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Base
        slotDefinition.addItemSlot(0, 1, 1);

        //Addition
        slotDefinition.addItemSlot(1, 19, 1);

        //Template
        slotDefinition.addItemSlot(2, 37, 1);

        //Result
        slotDefinition.addItemSlot(3, 91, 1);
    }


    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.SMITHING_TABLE));
    }
}
