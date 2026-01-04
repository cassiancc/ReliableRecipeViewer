package cc.cassian.rrv.common.builtin.tipped_arrow;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class TippedArrowServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<TippedArrowServerRecipe> TYPE = RrvRecipeType.register(
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

        tag.put("potionStack", RrvTagUtil.encodeItemStackOnServer(this.potionStack));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.potionStack = RrvTagUtil.decodeItemStackOnClient(tag.getCompound("potionStack").orElseGet(CompoundTag::new));

    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
