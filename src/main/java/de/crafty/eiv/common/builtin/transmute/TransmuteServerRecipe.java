package de.crafty.eiv.common.builtin.transmute;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class TransmuteServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<TransmuteServerRecipe> TYPE = EivRecipeType.register(
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

        tag.put("input", EivTagUtil.writeIngredient(this.input));

        tag.put("materials", EivTagUtil.writeIngredient(this.material));

        tag.put("results", EivTagUtil.writeList(this.results, (origin, tag1) -> EivTagUtil.encodeItemStackOnServer(origin)));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = EivTagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.material = EivTagUtil.readIngredient(tag.getCompound("materials").orElseGet(CompoundTag::new));

        this.results = EivTagUtil.readList(tag, "results", EivTagUtil::decodeItemStackOnClient);
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
