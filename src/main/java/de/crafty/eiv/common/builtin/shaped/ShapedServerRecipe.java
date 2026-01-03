package de.crafty.eiv.common.builtin.shaped;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;

public class ShapedServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<ShapedServerRecipe> TYPE = EivRecipeType.register(
            Identifier.withDefaultNamespace("shaped_crafting"),
            () -> new ShapedServerRecipe(0, 0, new HashMap<>(), ItemStack.EMPTY)
    );


    private HashMap<Integer, Ingredient> ingredients;
    private ItemStack result;
    private int width, height;

    public ShapedServerRecipe(int width, int height, HashMap<Integer, Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public HashMap<Integer, Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putInt("width", this.width);
        tag.putInt("height", this.height);

        this.ingredients.forEach((slotId, ingredient) -> {
            tag.put("ci_" + slotId, EivTagUtil.writeIngredient(ingredient));
        });
        tag.put("result", EivTagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.width = tag.getIntOr("width", 0);
        this.height = tag.getIntOr("height", 0);

        HashMap<Integer, Ingredient> ingredients = new HashMap<>();

        tag.keySet().forEach(key -> {
            if (!key.startsWith("ci_"))
                return;

            int slot = Integer.parseInt(key.replace("ci_", ""));
            ingredients.put(slot, EivTagUtil.readIngredient(tag.getCompound(key).orElseGet(CompoundTag::new)));
        });

        this.ingredients = ingredients;
        this.result = EivTagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
