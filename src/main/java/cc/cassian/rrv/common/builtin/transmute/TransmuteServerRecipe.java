package cc.cassian.rrv.common.builtin.transmute;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class TransmuteServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<TransmuteServerRecipe> TYPE = RrvRecipeType.register(
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

        tag.put("input", RrvTagUtil.writeIngredient(this.input));

        tag.put("materials", RrvTagUtil.writeIngredient(this.material));

        tag.put("results", RrvTagUtil.writeList(this.results, (origin, tag1) -> RrvTagUtil.encodeItemStackOnServer(origin)));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = RrvTagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.material = RrvTagUtil.readIngredient(tag.getCompound("materials").orElseGet(CompoundTag::new));

        this.results = RrvTagUtil.readList(tag, "results", RrvTagUtil::decodeItemStackOnClient);
    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
