package cc.cassian.rrv.common.builtin.crafting.recipes;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class TippedArrowServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<TippedArrowServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("tipped_arrow_crafting"),
            () -> new TippedArrowServerRecipe(ItemStack.EMPTY)
    );

    private SlotContent potionStack;

    public TippedArrowServerRecipe(ItemStack potionStack) {
        this.potionStack = SlotContent.of(potionStack);
    }

    public SlotContent getPotion() {
        return this.potionStack;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("potionStack", TagUtil.writeSlotContent(this.potionStack));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.potionStack = TagUtil.readSlotContent(tag.getCompound("potionStack").orElseGet(CompoundTag::new));

    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
