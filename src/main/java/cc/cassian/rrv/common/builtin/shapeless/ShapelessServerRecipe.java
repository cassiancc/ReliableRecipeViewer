package cc.cassian.rrv.common.builtin.shapeless;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ShapelessServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<ShapelessServerRecipe> TYPE = RrvRecipeType.register(
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

        tag.put("ingredients", RrvTagUtil.writeList(this.ingredients, (origin, tag1) -> RrvTagUtil.writeIngredient(origin)));
        tag.put("result", RrvTagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.ingredients = RrvTagUtil.readList(tag, "ingredients", RrvTagUtil::readIngredient);
        this.result = RrvTagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));

    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
