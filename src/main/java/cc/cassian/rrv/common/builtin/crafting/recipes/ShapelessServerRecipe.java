package cc.cassian.rrv.common.builtin.crafting.recipes;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ShapelessServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<ShapelessServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("shapeless_crafting"),
            () -> new ShapelessServerRecipe(null, List.of(), null)
    );

    private final Identifier id;
    private List<Ingredient> ingredients;
    private ItemStackTemplate result;

    public ShapelessServerRecipe(Identifier id, List<Ingredient> ingredients, ItemStackTemplate result) {
        this.ingredients = ingredients;
        this.result = result;
        this.id = id;
    }


    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ItemStackTemplate getResult() {
        return this.result;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("ingredients", TagUtil.writeList(this.ingredients, (origin, tag1) -> TagUtil.writeIngredient(origin)));
        tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.ingredients = TagUtil.readList(tag, "ingredients", TagUtil::readIngredient);
        this.result = TagUtil.decodeItemStackTemplateOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));

    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
