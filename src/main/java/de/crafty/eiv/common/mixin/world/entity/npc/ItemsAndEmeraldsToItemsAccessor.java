package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.trading.ItemCost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(VillagerTrades.ItemsAndEmeraldsToItems.class)
public interface ItemsAndEmeraldsToItemsAccessor {

    @Accessor("villagerXp")
    int getVillagerXp();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("fromItem")
    ItemCost fromItem();

    @Accessor("emeraldCost")
    int emeraldCost();

    @Accessor("toItem")
    ItemStack toItem();

    @Accessor("enchantmentProvider")
    Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider();


}
