package cc.cassian.rrv.common.builtin.smelting;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

public class SmeltingServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<SmeltingServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("smelting"),
            () -> new SmeltingServerRecipe(SlotContent.of(), SlotContent.of())
    );

    private SlotContent input;
    private SlotContent result;

    public SmeltingServerRecipe(Ingredient input, ItemStackTemplate result) {
        this(SlotContent.of(input), SlotContent.of(result));
    }

    public SmeltingServerRecipe(SlotContent input, SlotContent result) {
        this.input = SlotContent.of(input);
        this.result = SlotContent.of(result);
    }

    public SlotContent getInput() {
        return this.input;
    }

    public SlotContent getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("input", TagUtil.writeSlotContent(this.input));
        tag.put("result", TagUtil.writeSlotContent(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.input = TagUtil.readSlotContent(tag.getCompound("input").orElseGet(CompoundTag::new));
        this.result = TagUtil.readSlotContent(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
