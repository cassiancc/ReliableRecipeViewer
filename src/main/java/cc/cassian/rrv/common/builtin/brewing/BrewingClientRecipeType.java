package cc.cassian.rrv.common.builtin.brewing;

import cc.cassian.rrv.common.CommonRRV;
import cc.cassian.rrv.common.api.recipe.IRrvClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BrewingClientRecipeType implements IRrvClientRecipeType {

    protected static final BrewingClientRecipeType INSTANCE = new BrewingClientRecipeType();
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.brewing");
    }

    @Override
    public int getDisplayWidth() {
        return 133;
    }

    @Override
    public int getDisplayHeight() {
        return 61;
    }

    @Override
    public Identifier getGuiTexture() {
        return Identifier.fromNamespaceAndPath(CommonRRV.MODID, "textures/gui/type/brewing.png");
    }

    @Override
    public int getSlotCount() {
        return 5;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        //Result
        slotDefinition.addItemSlot(0, 1, 2);

        //magic ingredient
        slotDefinition.addItemSlot(1, 58, 3);

        //Ingredient bottles
        slotDefinition.addItemSlot(2, 35, 37);
        slotDefinition.addItemSlot(3, 58, 44);
        slotDefinition.addItemSlot(4, 81, 37);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("brewing");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.BREWING_STAND);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.BREWING_STAND), new ItemStack(Items.POTION));
    }
}
