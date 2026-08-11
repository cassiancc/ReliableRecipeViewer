package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
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
        return 55;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.setHighlightWithoutContents(false);

        slotDefinition.addItemSlot(0, 42, 80);

        for (int row = 0; row < 6; row++) {
            for (int i = 0; i < 3; i++) {
                slotDefinition.addItemSlot(row * 9 + i+1, i * 18 + 106, 9 + row * 18);
            }
        }

    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("entity_loot");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.CREEPER_SPAWN_EGG);
    }

    int index = 0;
    long lastChanged = 0;

    @Override
    public void renderIcon(RecipeViewScreen screen, int x, int y, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            long gameTime = level.getGameTime();
            int i = 20; // change every second
            long l = gameTime % i;

            if (l == 0 && (gameTime-lastChanged > i)) {
                lastChanged = gameTime;
                index++;
                if (index>SPAWN_EGGS.size()) {
                    index = 0;
                }
            }
            guiGraphics.fakeItem(SPAWN_EGGS.get(index), x, y);
        } else {
            ReliableClientRecipeType.super.renderIcon(screen, x, y, guiGraphics, mouseX, mouseY, partialTicks);
        }
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
        return new ButtonData(info.guiLeft() + getDisplayWidth() - 14, info.guiTop()+64, true);
    }
}
