package cc.cassian.rrv.common.builtin.crafting.recipes;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;

public class ShapedServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<ShapedServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("shaped_crafting"),
            () -> new ShapedServerRecipe(0, 0, new HashMap<>(), null)
    );

    private HashMap<Integer, Ingredient> ingredients;
    private ItemStackTemplate result;
    private int width, height;

    public ShapedServerRecipe(int width, int height, HashMap<Integer, Ingredient> ingredients, ItemStackTemplate result) {
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

    public ItemStackTemplate getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putInt("width", this.width);
        tag.putInt("height", this.height);

        this.ingredients.forEach((slotId, ingredient) -> {
            tag.put("ci_" + slotId, TagUtil.writeIngredient(ingredient));
        });
        tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
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
            ingredients.put(slot, TagUtil.readIngredient(tag.getCompound(key).orElseGet(CompoundTag::new)));
        });

        this.ingredients = ingredients;
        this.result = TagUtil.decodeItemStackTemplateOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
