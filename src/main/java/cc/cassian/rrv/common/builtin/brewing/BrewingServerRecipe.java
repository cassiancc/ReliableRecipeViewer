package cc.cassian.rrv.common.builtin.brewing;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BrewingServerRecipe implements ReliableServerRecipe {


    public static final ReliableServerRecipeType<BrewingServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("brewing"),
            () -> new BrewingServerRecipe(ItemStack.EMPTY, null, ItemStack.EMPTY)
    );

    private SlotContent result, bottleIngredient, magicIngredient;

    public BrewingServerRecipe(ItemStack result, Ingredient magicIngredient, ItemStack bottleIngredient) {
       this(SlotContent.of(magicIngredient), SlotContent.of(bottleIngredient), SlotContent.of(result));
    }

    public BrewingServerRecipe(SlotContent reagent, SlotContent bottle, SlotContent result) {
        this.result = result;
        this.magicIngredient = reagent;
        this.bottleIngredient = bottle;
    }

    public SlotContent getResult() {
        return this.result;
    }

    public SlotContent getMagicIngredient() {
        return this.magicIngredient;
    }

    public SlotContent getBottleIngredient() {
        return this.bottleIngredient;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.put("result", TagUtil.writeSlotContent(this.result));
        tag.put("magicIngredient", TagUtil.writeSlotContent(this.magicIngredient));
        tag.put("bottleIngredient", TagUtil.writeSlotContent(this.bottleIngredient));

    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.result = TagUtil.readSlotContent(tag.getCompoundOrEmpty("result"));
        this.magicIngredient = TagUtil.readSlotContent(tag.getCompoundOrEmpty("magicIngredient"));
        this.bottleIngredient = TagUtil.readSlotContent(tag.getCompoundOrEmpty("bottleIngredient"));

    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
