package cc.cassian.rrv.common.builtin.campfire;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class CampfireClientRecipeType implements ReliableClientRecipeType {

    protected static final CampfireClientRecipeType INSTANCE = new CampfireClientRecipeType();

    private static final ResourceLocation CAMPFIRE_LOCATION = ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "textures/gui/type/campfire.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.campfire");
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.withDefaultNamespace("campfire_cooking");
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
    public ResourceLocation getGuiTexture() {
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
