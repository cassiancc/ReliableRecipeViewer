package de.crafty.eiv.common.builtin.stonecutting;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class StonecutterServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<StonecutterServerRecipe> TYPE = EivRecipeType.register(
            Identifier.withDefaultNamespace("stonecutting"),
            () -> new StonecutterServerRecipe(null, ItemStack.EMPTY)
    );

    private Ingredient input;
    private ItemStack result;

    public StonecutterServerRecipe(Ingredient input, ItemStack result) {
        this.input = input;
        this.result = result;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("input", EivTagUtil.writeIngredient(this.input));
        tag.put("result", EivTagUtil.encodeItemStackOnServer(this.result));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = EivTagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.result = EivTagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));

    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }
}
