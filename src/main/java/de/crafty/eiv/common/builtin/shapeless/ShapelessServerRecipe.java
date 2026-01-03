package de.crafty.eiv.common.builtin.shapeless;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ShapelessServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<ShapelessServerRecipe> TYPE = EivRecipeType.register(
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

        tag.put("ingredients", EivTagUtil.writeList(this.ingredients, (origin, tag1) -> EivTagUtil.writeIngredient(origin)));
        tag.put("result", EivTagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.ingredients = EivTagUtil.readList(tag, "ingredients", EivTagUtil::readIngredient);
        this.result = EivTagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));

    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
