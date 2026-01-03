package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

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
