package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.trading.ItemCost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.EmeraldForItems.class)
public interface EmeraldForItemsAccessor {

    @Accessor("villagerXp")
    int getVillagerXp();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("emeraldAmount")
    int getEmeraldAmount();

    @Accessor("itemStack")
    ItemCost getItemStack();

}
