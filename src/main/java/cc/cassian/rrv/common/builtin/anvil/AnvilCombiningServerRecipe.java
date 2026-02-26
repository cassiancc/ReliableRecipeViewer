package cc.cassian.rrv.common.builtin.anvil;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class AnvilCombiningServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<AnvilCombiningServerRecipe> TYPE = ReliableServerRecipeType.register(
            ResourceLocation.fromNamespaceAndPath("rrv", "anvil_combining"),
            () -> new AnvilCombiningServerRecipe(null,null,null)
    );


    private ItemStack left;
    private Ingredient right;
    private ItemStack result;

    public AnvilCombiningServerRecipe(ItemStack left, Ingredient right, ItemStack result
    ) {
        this.left = left;
        this.right = right;
        this.result = result;
    }

    public ItemStack getLeft() {
        return this.left;
    }

    public Ingredient getRight() {
        return right;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        if (this.left != null) {
            tag.put("left", TagUtil.encodeItemStackOnServer(this.left));
            tag.put("right", TagUtil.writeIngredient(this.right));
            tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
        }
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.left = TagUtil.decodeItemStackOnClient(tag.getCompound("left").orElseGet(CompoundTag::new));
        this.right = TagUtil.readIngredient(tag.getCompound("right").orElseGet(CompoundTag::new));
        this.result = TagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
