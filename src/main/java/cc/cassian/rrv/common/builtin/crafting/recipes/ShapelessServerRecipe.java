package cc.cassian.rrv.common.builtin.crafting.recipes;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ShapelessServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<ShapelessServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("shapeless_crafting"),
            () -> new ShapelessServerRecipe(null, List.of(), null)
    );

    private final Identifier id;
    private List<SlotContent> ingredients;
    private SlotContent result;

    public ShapelessServerRecipe(Identifier id, List<Ingredient> ingredients, ItemStackTemplate result) {
        this.ingredients = ingredients.stream().map(SlotContent::of).toList();
        this.result = SlotContent.of(result);
        this.id = id;
    }


    public List<SlotContent> getIngredients() {
        return this.ingredients;
    }

    public SlotContent getResult() {
        return this.result;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("ingredients", TagUtil.writeList(this.ingredients, (origin, tag1) -> TagUtil.writeSlotContent(origin)));
        tag.put("result", TagUtil.writeSlotContent(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.ingredients = TagUtil.readList(tag, "ingredients", TagUtil::readSlotContent);
        this.result = TagUtil.readSlotContent(tag.getCompound("result").orElseGet(CompoundTag::new));

    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
