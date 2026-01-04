package cc.cassian.rrv.common.builtin.smelting;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class SmeltingServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<SmeltingServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("smelting"),
            () -> new SmeltingServerRecipe(null, ItemStack.EMPTY)
    );

    private Ingredient input;
    private ItemStack result;

    public SmeltingServerRecipe(Ingredient input, ItemStack result) {
        this.input = input;
        this.result = result;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", TagUtil.writeIngredient(this.input));
        tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = TagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.result = TagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
