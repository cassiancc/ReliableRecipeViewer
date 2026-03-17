package cc.cassian.rrv.common.builtin.interaction;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class WorldInteractionServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<WorldInteractionServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.fromNamespaceAndPath("rrv", "world_interaction"),
            () -> new WorldInteractionServerRecipe()
    );


    private SlotContent left, right, result;

    public WorldInteractionServerRecipe(SlotContent left, SlotContent right, SlotContent result
    ) {
        this.left = left;
        this.right = right;
        this.result = result;
    }

    public WorldInteractionServerRecipe() {
        this.left = null;
        this.right = null;
        this.result = null;
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
            tag.put("result", TagUtil.writeSlotContent(this.right));
        }
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.left = TagUtil.readSlotContent(tag.getCompound("left").orElseGet(CompoundTag::new));
        this.right = TagUtil.readSlotContent(tag.getCompound("right").orElseGet(CompoundTag::new));
        this.right = TagUtil.readSlotContent(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
