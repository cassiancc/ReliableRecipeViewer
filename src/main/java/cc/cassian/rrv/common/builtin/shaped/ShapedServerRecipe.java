package cc.cassian.rrv.common.builtin.shaped;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;

public class ShapedServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<ShapedServerRecipe> TYPE = RrvRecipeType.register(
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
            tag.put("ci_" + slotId, RrvTagUtil.writeIngredient(ingredient));
        });
        tag.put("result", RrvTagUtil.encodeItemStackOnServer(this.result));
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
            ingredients.put(slot, RrvTagUtil.readIngredient(tag.getCompound(key).orElseGet(CompoundTag::new)));
        });

        this.ingredients = ingredients;
        this.result = RrvTagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
