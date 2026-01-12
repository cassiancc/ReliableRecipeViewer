package cc.cassian.rrv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? <26 {
import net.minecraft.world.entity.npc.VillagerTrades;

@Mixin(VillagerTrades.EnchantedItemForEmeralds.class)
public interface EnchantedItemForEmeraldsAccessor {

    @Accessor("villagerXp")
    int villagerXp();

    @Accessor("maxUses")
    int maxUses();

    @Accessor("itemStack")
    ItemStack itemStack();

    @Accessor("baseEmeraldCost")
    int baseEmeraldCost();

    @Accessor("priceMultiplier")
    float priceMultiplier();

}
//?} else {
/*@Mixin(VillagerType.class)
public interface EnchantedItemForEmeraldsAccessor {
}
*///?}