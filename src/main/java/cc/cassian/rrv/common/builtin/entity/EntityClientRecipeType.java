package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

import java.util.List;

public class EntityClientRecipeType implements ReliableClientRecipeType {

    public static final EntityClientRecipeType INSTANCE = new EntityClientRecipeType();
    private static final List<ItemStack> SPAWN_EGGS = BuiltInRegistries.ITEM.stream().filter(item -> item instanceof SpawnEggItem).map(ItemStack::new).toList();
    private static final ReferenceCondition REFERENCE_CONDITION = (craftReference, viewRecipe) -> {

        if(!(craftReference.getItem() instanceof SpawnEggItem spawnEggItem) || !(viewRecipe instanceof EntityClientRecipe entityViewRecipe))
            return true;

        return spawnEggItem.getType(craftReference) == entityViewRecipe.getEntityType();

    };
    private static final Identifier BACKGROUND = ReliableRecipeViewer.of("textures/gui/type/entity.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.rrv.type.entity");
    }

    @Override
    public int getDisplayWidth() {
        return 162;
    }

    @Override
    public int getDisplayHeight() {
        return 142;
    }

    @Override
    public Identifier getGuiTexture() {
        return BACKGROUND;
    }

    //Mob loot should not exceed 54 slots
    @Override
    public int getSlotCount() {
        return 54;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.setHighlightWithoutContents(false);

        for (int row = 0; row < 6; row++) {
            for (int i = 0; i < 9; i++) {
                slotDefinition.addItemSlot(row * 9 + i, i * 18 + 1, 45 + row * 18);
            }
        }

    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("entity_loot");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.IRON_SWORD);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return SPAWN_EGGS;
    }

    @Override
    public ReferenceCondition getCraftReferenceCondition() {
        return REFERENCE_CONDITION;
    }

    @Override
    public ButtonData placeRecipeShareButton(RecipeViewMenu.DisplayInfo info) {
        return new ButtonData(info.guiLeft() + getDisplayWidth() - 12, info.guiTop()+30, true);
    }
}
