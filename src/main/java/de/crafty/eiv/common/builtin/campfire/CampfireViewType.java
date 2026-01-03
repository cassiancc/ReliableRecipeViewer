package de.crafty.eiv.common.builtin.campfire;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CampfireViewType implements IEivRecipeViewType {

    protected static final CampfireViewType INSTANCE = new CampfireViewType();

    private static final Identifier CAMPFIRE_LOCATION = Identifier.fromNamespaceAndPath(CommonEIV.MODID, "textures/gui/type/campfire.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.eiv.type.campfire");
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("campfire_cooking");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.CAMPFIRE);
    }

    @Override
    public int getDisplayWidth() {
        return 74;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public Identifier getGuiTexture() {
        return CAMPFIRE_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Ingredient
        slotDefinition.addItemSlot(0, 1, 1);

        //Cooked result
        slotDefinition.addItemSlot(1, 57, 1);
    }


    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.CAMPFIRE));
    }
}
