package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(VillagerTrades.ItemsForEmeralds.class)
public interface ItemsForEmeraldsAccessor {

    @Accessor("maxUses")
    int maxUses();

    @Accessor("villagerXp")
    int villagerXp();

    @Accessor("itemStack")
    ItemStack itemStack();

    @Accessor("emeraldCost")
    int emeraldCost();

    @Accessor("enchantmentProvider")
    Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider();

}
