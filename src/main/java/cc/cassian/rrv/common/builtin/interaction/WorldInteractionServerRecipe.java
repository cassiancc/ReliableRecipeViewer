package cc.cassian.rrv.common.builtin.interaction;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class WorldInteractionServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<WorldInteractionServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.fromNamespaceAndPath("rrv", "world_interaction"),
            () -> new WorldInteractionServerRecipe( null, null, null)
    );


    private ItemStack left;
    private ItemStack right;
    private ItemStack result;

    public WorldInteractionServerRecipe(ItemStack left, ItemStack right, ItemStack result
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


    public ItemStack getLeft() {
        return this.left;
    }

    public ItemStack getRight() {
        return right;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("left", TagUtil.encodeItemStackOnServer(this.left));
        tag.put("right", TagUtil.encodeItemStackOnServer(this.right));
        tag.put("result", TagUtil.encodeItemStackOnServer(this.right));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.left = TagUtil.decodeItemStackOnClient(tag.getCompound("left").orElseGet(CompoundTag::new));
        this.right = TagUtil.decodeItemStackOnClient(tag.getCompound("right").orElseGet(CompoundTag::new));
        this.right = TagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

}
