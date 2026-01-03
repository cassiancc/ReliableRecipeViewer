package de.crafty.eiv.common.builtin.stonecutting;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class StonecutterViewType implements IEivRecipeViewType {

    protected static final StonecutterViewType INSTANCE = new StonecutterViewType();

    private static final Identifier STONECUTTER_LOCATION = Identifier.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/stonecutter.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.stonecutter");
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
        return 74;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public Identifier getGuiTexture() {
        return STONECUTTER_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        //Input
        slotDefinition.addItemSlot(0, 1, 1);

        //Result
        slotDefinition.addItemSlot(1, 57, 1);
    }


    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.STONECUTTER));
    }
}
