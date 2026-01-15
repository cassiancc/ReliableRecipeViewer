package cc.cassian.rrv.common.builtin.repairing;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class RepairingServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<RepairingServerRecipe> TYPE = ReliableServerRecipeType.register(
            ReliableRecipeViewer.of("repairing"),
            () -> new RepairingServerRecipe( null, null, null)
    );


    private ItemStack base;
    private ItemStack result;
    private Ingredient repairIngredient;

    public RepairingServerRecipe(ItemStack base, Ingredient repairIngredient, ItemStack stack) {
        this.base = base;
        this.repairIngredient = repairIngredient;
        this.result = stack;
    }


    public ItemStack getBase() {
        return this.base;
    }

    public Ingredient getTemplate() {
        return this.repairIngredient;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("base", TagUtil.encodeItemStackOnServer(this.base));
        tag.put("repair_ingredient", TagUtil.writeIngredient(this.repairIngredient));
        tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.base = TagUtil.decodeItemStackOnClient(tag.getCompound("base").orElseGet(CompoundTag::new));
        this.repairIngredient = TagUtil.readIngredient(tag.getCompound("repair_ingredient").orElseGet(CompoundTag::new));
        this.result = TagUtil.decodeItemStackOnClient(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

	public ItemStack getResult() {
		return result;
	}
}
