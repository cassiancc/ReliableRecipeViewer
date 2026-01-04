package cc.cassian.rrv.common.builtin.shapeless;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ShapelessServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<ShapelessServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("shapeless_crafting"),
            () -> new ShapelessServerRecipe(List.of(), ItemStack.EMPTY)
    );

    private List<Ingredient> ingredients;
    private ItemStack result;

    public ShapelessServerRecipe(List<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }


    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ItemStack getResult() {
        return this.result;
    }


    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("ingredients", TagUtil.writeList(this.ingredients, (origin, tag1) -> TagUtil.writeIngredient(origin)));
        tag.put("result", TagUtil.writeItemStack(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.ingredients = TagUtil.readList(tag, "ingredients", TagUtil::readIngredient);
        this.result = TagUtil.readItemStack(tag.getCompound("result").orElseGet(CompoundTag::new));

    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
