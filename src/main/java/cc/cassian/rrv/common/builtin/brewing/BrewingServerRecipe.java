package cc.cassian.rrv.common.builtin.brewing;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BrewingServerRecipe implements IRrvServerRecipe {


    public static final RrvRecipeType<BrewingServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("brewing"),
            () -> new BrewingServerRecipe(ItemStack.EMPTY, null, ItemStack.EMPTY)
    );

    private ItemStack result, bottleIngredient;
    private Ingredient magicIngredient;

    public BrewingServerRecipe(ItemStack result, Ingredient magicIngredient, ItemStack bottleIngredient) {
        this.result = result;
        this.magicIngredient = magicIngredient;
        this.bottleIngredient = bottleIngredient;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public Ingredient getMagicIngredient() {
        return this.magicIngredient;
    }

    public ItemStack getBottleIngredient() {
        return this.bottleIngredient;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("result", RrvTagUtil.encodeItemStackOnServer(this.result));
        tag.put("magicIngredient", RrvTagUtil.writeIngredient(this.magicIngredient));
        tag.put("bottleIngredient", RrvTagUtil.encodeItemStackOnServer(this.bottleIngredient));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.result = RrvTagUtil.decodeItemStackOnClient(tag.getCompoundOrEmpty("result"));
        this.magicIngredient = RrvTagUtil.readIngredient(tag.getCompoundOrEmpty("magicIngredient"));
        this.bottleIngredient = RrvTagUtil.decodeItemStackOnClient(tag.getCompoundOrEmpty("bottleIngredient"));

    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }
}
