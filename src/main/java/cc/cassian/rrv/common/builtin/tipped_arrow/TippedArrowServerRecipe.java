package cc.cassian.rrv.common.builtin.tipped_arrow;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class TippedArrowServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<TippedArrowServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("tipped_arrow_crafting"),
            () -> new TippedArrowServerRecipe(ItemStack.EMPTY)
    );

    private ItemStack potionStack;

    public TippedArrowServerRecipe(ItemStack potionStack) {
        this.potionStack = potionStack;
    }

    public ItemStack getPotion() {
        return this.potionStack;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("potionStack", TagUtil.writeItemStack(this.potionStack));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.potionStack = TagUtil.readItemStack(tag.getCompound("potionStack").orElseGet(CompoundTag::new));

    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
