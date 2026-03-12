package cc.cassian.rrv.common.builtin.crafting.recipes;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class TransmuteServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<TransmuteServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("transmutation_crafting"),
            () -> new TransmuteServerRecipe(null, null, null, List.of())

    );

    private final Identifier id;
    private Ingredient input;
    private Ingredient material;
    private List<ItemStackTemplate> results;
    private int dependentIndex;

    public TransmuteServerRecipe(Identifier id, Ingredient input, Ingredient material, List<ItemStackTemplate> results) {
        this(id, input, material, results, -1);
    }

    public TransmuteServerRecipe(Identifier id, Ingredient input, Ingredient material, List<ItemStackTemplate> results, int dependentIndex) {
        this.input = input;
        this.material = material;
        this.results = results;
        this.dependentIndex = dependentIndex;
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Ingredient getMaterial() {
        return this.material;
    }

    public List<ItemStackTemplate> getResults() {
        return this.results;
    }

    public int getDependentIndex() {
        return dependentIndex;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", TagUtil.writeIngredient(this.input));

        tag.put("materials", TagUtil.writeIngredient(this.material));

        tag.put("results", TagUtil.writeList(this.results, (origin, tag1) -> TagUtil.encodeItemStackOnServer(origin)));

        tag.putInt("dependent_index", this.dependentIndex);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = TagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.material = TagUtil.readIngredient(tag.getCompound("materials").orElseGet(CompoundTag::new));

        this.results = TagUtil.readList(tag, "results", TagUtil::decodeItemStackTemplateOnClient);
        this.dependentIndex = tag.getIntOr("dependent_index", -1);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
