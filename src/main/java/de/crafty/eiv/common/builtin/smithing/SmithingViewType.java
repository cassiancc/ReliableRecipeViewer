package de.crafty.eiv.common.builtin.smithing;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SmithingViewType implements IEivRecipeViewType {

    protected static final SmithingViewType INSTANCE = new SmithingViewType();

    private static final Identifier SMITHING_LOCATION = Identifier.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/smithing.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.smithing");
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
        return 108;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public Identifier getGuiTexture() {
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
