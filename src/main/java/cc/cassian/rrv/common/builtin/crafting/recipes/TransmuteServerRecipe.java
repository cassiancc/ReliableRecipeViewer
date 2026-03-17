package cc.cassian.rrv.common.builtin.crafting.recipes;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
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
    private SlotContent input;
    private SlotContent material;
    private SlotContent results;
    private int dependentIndex;

    public TransmuteServerRecipe(Identifier id, Ingredient input, Ingredient material, List<ItemStackTemplate> results) {
        this(id, input, material, results, -1);
    }

    public TransmuteServerRecipe(Identifier id, Ingredient input, Ingredient material, List<ItemStackTemplate> results, int dependentIndex) {
        this.input = SlotContent.of(input);
        this.material = SlotContent.of(material);
        this.results = SlotContent.ofTemplates(results);
        this.dependentIndex = dependentIndex;
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public SlotContent getInput() {
        return this.input;
    }

    public SlotContent getMaterial() {
        return this.material;
    }

    public SlotContent getResults() {
        return this.results;
    }

    public int getDependentIndex() {
        return dependentIndex;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", TagUtil.writeSlotContent(this.input));

        tag.put("materials", TagUtil.writeSlotContent(this.material));

        tag.put("results", TagUtil.writeSlotContent(this.results));

        tag.putInt("dependent_index", this.dependentIndex);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = TagUtil.readSlotContent(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.material = TagUtil.readSlotContent(tag.getCompound("materials").orElseGet(CompoundTag::new));

        this.results = TagUtil.readSlotContent(tag.getCompound("results").orElseGet(CompoundTag::new));
        this.dependentIndex = tag.getIntOr("dependent_index", -1);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
