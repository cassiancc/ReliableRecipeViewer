package de.crafty.eiv.common.builtin.shaped;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CraftingViewType implements IEivRecipeViewType {

    public static final CraftingViewType INSTANCE = new CraftingViewType();

    private static final Identifier CRAFTING_LOCATION = Identifier.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/crafting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.crafting");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("crafting_shaped");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.CRAFTING_TABLE);
    }

    @Override
    public int getDisplayWidth() {
        return 116;
    }


    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public Identifier getGuiTexture() {
        return CRAFTING_LOCATION;
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
                slotDefinition.addItemSlot(x + y * 3, 1 + x * 18,  1 + y * 18);
            }
        }

        //Result Slot
        slotDefinition.addItemSlot(9, 95, 19);

    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.CRAFTING_TABLE), new ItemStack(Items.CRAFTER));
    }
}
