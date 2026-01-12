package cc.cassian.rrv.common.builtin.brewing;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BrewingServerRecipe implements ReliableServerRecipe {


    public static final ReliableServerRecipeType<BrewingServerRecipe> TYPE = ReliableServerRecipeType.register(
            ResourceLocation.withDefaultNamespace("brewing"),
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

        tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
        tag.put("magicIngredient", TagUtil.writeIngredient(this.magicIngredient));
        tag.put("bottleIngredient", TagUtil.encodeItemStackOnServer(this.bottleIngredient));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.result = TagUtil.decodeItemStackOnClient(tag.getCompoundOrEmpty("result"));
        this.magicIngredient = TagUtil.readIngredient(tag.getCompoundOrEmpty("magicIngredient"));
        this.bottleIngredient = TagUtil.decodeItemStackOnClient(tag.getCompoundOrEmpty("bottleIngredient"));

    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
