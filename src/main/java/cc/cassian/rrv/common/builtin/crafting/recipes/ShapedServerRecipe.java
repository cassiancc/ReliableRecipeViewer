package cc.cassian.rrv.common.builtin.crafting.recipes;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;

public class ShapedServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<ShapedServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("shaped_crafting"),
            () -> new ShapedServerRecipe(null, 0, 0, new HashMap<>(), null)
    );

    private final Identifier id;
    private HashMap<Integer, SlotContent> ingredients;
    private SlotContent result;
    private int width, height;

    public ShapedServerRecipe(Identifier id, int width, int height, HashMap<Integer, SlotContent> ingredients, SlotContent result) {
        this.ingredients = ingredients;
        this.result = result;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public HashMap<Integer, SlotContent> getIngredients() {
        return this.ingredients;
    }

    public SlotContent getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putInt("width", this.width);
        tag.putInt("height", this.height);

        this.ingredients.forEach((slotId, ingredient) -> {
            tag.put("ci_" + slotId, TagUtil.writeSlotContent(ingredient));
        });
        tag.put("result", TagUtil.writeSlotContent(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.width = tag.getIntOr("width", 0);
        this.height = tag.getIntOr("height", 0);

        HashMap<Integer, SlotContent> ingredients = new HashMap<>();

        tag.keySet().forEach(key -> {
            if (!key.startsWith("ci_"))
                return;

            int slot = Integer.parseInt(key.replace("ci_", ""));
            ingredients.put(slot, TagUtil.readSlotContent(tag.getCompound(key).orElseGet(CompoundTag::new)));
        });

        this.ingredients = ingredients;
        this.result = TagUtil.readSlotContent(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
