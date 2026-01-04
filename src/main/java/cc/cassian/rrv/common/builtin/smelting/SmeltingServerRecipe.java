package cc.cassian.rrv.common.builtin.smelting;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class SmeltingServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<SmeltingServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("smelting"),
            () -> new SmeltingServerRecipe(null, ItemStack.EMPTY)
    );

    private Ingredient input;
    private ItemStack result;

    public SmeltingServerRecipe(Ingredient input, ItemStack result) {
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

        tag.put("input", RrvTagUtil.writeIngredient(this.input));
        tag.put("result", RrvTagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.input = RrvTagUtil.readIngredient(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.result = RrvTagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
