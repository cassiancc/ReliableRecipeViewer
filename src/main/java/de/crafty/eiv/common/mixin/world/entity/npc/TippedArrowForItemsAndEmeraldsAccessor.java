package de.crafty.eiv.common.mixin.world.entity.npc;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
//? <26 {
import net.minecraft.world.entity.npc.villager.VillagerTrades;

@Mixin(VillagerTrades.TippedArrowForItemsAndEmeralds.class)
public interface TippedArrowForItemsAndEmeraldsAccessor {

    @Accessor("maxUses")
    int maxUses();

    @Accessor("villagerXp")
    int villagerXp();

    @Accessor("toItem")
    ItemStack toItem();

    @Accessor("toCount")
    int toCount();

    @Accessor("emeraldCost")
    int emeraldCost();

    @Accessor("fromItem")
    Item fromItem();

    @Accessor("fromCount")
    int fromCount();

}
//?} else {
/*@Mixin(ItemStack.class)
public interface TippedArrowForItemsAndEmeraldsAccessor {
}
*///?}