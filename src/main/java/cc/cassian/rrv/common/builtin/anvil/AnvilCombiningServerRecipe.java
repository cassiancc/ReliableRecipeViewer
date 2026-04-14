package cc.cassian.rrv.common.builtin.anvil;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Server recipes for vanilla recipe types are unneeded post RRV 8.0.0.
 */
@Deprecated
public class AnvilCombiningServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<AnvilCombiningServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.fromNamespaceAndPath("rrv", "anvil_combining"),
            () -> new AnvilCombiningServerRecipe(null,null,null)
    );


    private SlotContent left;
    private SlotContent right;
    private SlotContent result;

    public AnvilCombiningServerRecipe(ItemStack left, Ingredient right, ItemStack result
    ) {
        this.left = SlotContent.of(left);
        this.right = SlotContent.of(right);
        this.result = SlotContent.of(result);
    }

    public SlotContent getLeft() {
        return this.left;
    }

    public SlotContent getRight() {
        return right;
    }

    public SlotContent getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        if (this.left != null) {
            tag.put("left", TagUtil.writeSlotContent(this.left));
            tag.put("right", TagUtil.writeSlotContent(this.right));
            tag.put("result", TagUtil.writeSlotContent(this.result));
        }
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.left = TagUtil.readSlotContent(tag.getCompound("left").orElseGet(CompoundTag::new));
        this.right = TagUtil.readSlotContent(tag.getCompound("right").orElseGet(CompoundTag::new));
        this.result = TagUtil.readSlotContent(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}