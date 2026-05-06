package cc.cassian.rrv.common.builtin.campfire;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CampfireClientRecipeType implements ReliableClientRecipeType {

    protected static final CampfireClientRecipeType INSTANCE = new CampfireClientRecipeType();

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/campfire.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.campfire");
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
        return 80;
    }

    @Override
    public int getDisplayHeight() {
        return 42;
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

        //Ingredient
        slotDefinition.addItemSlot(0, 3, 4);

        //Cooked result
        slotDefinition.addItemSlot(1, 60, 4);
    }


    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.CAMPFIRE));
    }

    public ButtonData placeRecipeShareButton(int guiLeft, int guiTop) {
        return new ButtonData(guiLeft + getDisplayWidth() - 16, guiTop + getDisplayHeight() - 16, true);
    }
}
