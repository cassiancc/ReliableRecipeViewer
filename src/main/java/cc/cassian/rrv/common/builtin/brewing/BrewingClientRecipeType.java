package cc.cassian.rrv.common.builtin.brewing;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BrewingClientRecipeType implements ReliableClientRecipeType {

    protected static final BrewingClientRecipeType INSTANCE = new BrewingClientRecipeType();

    public static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/brewing.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.brewing");
    }

    @Override
    public int getDisplayWidth() {
        return 139;
    }

    @Override
    public int getDisplayHeight() {
        return 67;
    }

    @Override
    public Identifier getGuiTexture() {
        return BACKGROUND;
    }

    @Override
    public int getSlotCount() {
        return 5;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        //Result
        slotDefinition.addItemSlot(0, 4, 5);

        //reagent
        slotDefinition.addItemSlot(1, 61, 6);

        //Ingredient bottles
        slotDefinition.addItemSlot(2, 38, 40);
        slotDefinition.addItemSlot(3, 61, 47);
        slotDefinition.addItemSlot(4, 84, 40);
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
