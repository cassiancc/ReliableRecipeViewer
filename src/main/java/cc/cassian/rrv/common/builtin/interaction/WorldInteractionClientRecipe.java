package cc.cassian.rrv.common.builtin.interaction;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

public class WorldInteractionClientRecipe implements ReliableClientRecipe {

    private final SlotContent left, right, result;
    private final int priority;
    public static final SlotContent TIME = SlotContent.of(new ItemStack(Items.CLOCK.builtInRegistryHolder(), 1, DataComponentPatch.builder().set(DataComponents.ITEM_NAME, Component.translatable("view.rrv.type.world_interaction.time")).set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("view.rrv.type.world_interaction.time_passes")))).build()));
    private Identifier id;


    @Deprecated
    public WorldInteractionClientRecipe(SlotContent left, SlotContent right, SlotContent result, int priority) {
        this(null, left, right, result, 0);

    }

    @Deprecated
    public WorldInteractionClientRecipe(SlotContent left, SlotContent right, SlotContent result) {
        this(null, left, right, result, 0);
    }

    public WorldInteractionClientRecipe(Identifier id, SlotContent left, SlotContent right, SlotContent result, int priority) {
        this.left = left;
        this.right = right;
        this.result = result;
        this.priority = priority;
        this.id = id;
    }

    public WorldInteractionClientRecipe(Identifier id, SlotContent left, SlotContent right, SlotContent result) {
        this(id, left, right, result, 0);

    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return WorldInteractionClientRecipeType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.left);
        slotFillContext.bindSlot(1, this.right);
        slotFillContext.bindSlot(2, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        ArrayList<SlotContent> ingredients = new ArrayList<>();
        ingredients.add(left);
        if (!TIME.equals(this.right))
            ingredients.add(this.right);
        return ingredients;
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }
}
