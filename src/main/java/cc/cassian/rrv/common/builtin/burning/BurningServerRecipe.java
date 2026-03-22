package cc.cassian.rrv.common.builtin.burning;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class BurningServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<BurningServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("burning"),
            () -> new BurningServerRecipe(SlotContent.of(), 0)
    );

    private SlotContent fuel;
    private int burnTime;

    public BurningServerRecipe(Item fuel, int burnTime) {
        this.fuel = SlotContent.of(fuel);
        this.burnTime = burnTime;
    }

    public BurningServerRecipe(SlotContent fuel, int burnTime) {
        this.fuel = fuel;
        this.burnTime = burnTime;
    }

    public SlotContent getFuel() {
        return this.fuel;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("fuel", TagUtil.writeSlotContent(this.fuel));
        tag.putInt("burnTime", this.burnTime);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.fuel = TagUtil.readSlotContent(tag.getCompoundOrEmpty("fuel"));
        this.burnTime = tag.getIntOr("burnTime", AbstractFurnaceBlockEntity.BURN_TIME_STANDARD);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
