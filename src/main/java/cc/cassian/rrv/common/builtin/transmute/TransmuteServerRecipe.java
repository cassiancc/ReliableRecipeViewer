package cc.cassian.rrv.common.builtin.transmute;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class TransmuteServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<TransmuteServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("transmutation_crafting"),
            () -> new TransmuteServerRecipe(null, null, List.of())

    );

    private Ingredient input;
    private Ingredient material;
    private List<ItemStack> results;

    public TransmuteServerRecipe(Ingredient input, Ingredient material, List<ItemStack> results) {
        this.input = input;
        this.material = material;
        this.results = results;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Ingredient getMaterial() {
        return this.material;
    }

    public List<ItemStack> getResults() {
        return this.results;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", TagUtil.writeIngredient(this.input));

        tag.put("materials", TagUtil.writeIngredient(this.material));

        tag.put("results", TagUtil.writeList(this.results, (origin, tag1) -> TagUtil.writeItemStack(origin)));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = TagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.material = TagUtil.readIngredient(tag.getCompound("materials").orElseGet(CompoundTag::new));

        this.results = TagUtil.readList(tag, "results", TagUtil::readItemStack);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
