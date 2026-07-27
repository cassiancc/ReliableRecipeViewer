package cc.cassian.rrv.common.mixin.world.entity.npc;

import net.minecraft.resources.ResourceKey;

import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.ItemCost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

//? <26 {
/*import net.minecraft.world.entity.npc.villager.VillagerTrades;

@Mixin(VillagerTrades.EmeraldsForVillagerTypeItem.class)
public interface EmeraldsForVillagerTypeItemAccessor {

    @Accessor("villagerXp")
    int getVillagerXp();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("trades")
    Map<ResourceKey<VillagerType>, Item> getTrades();

    @Accessor("cost")
    int getCost();

}
*///?} else {
@Mixin(VillagerType.class)
public interface EmeraldsForVillagerTypeItemAccessor {
}
//?}