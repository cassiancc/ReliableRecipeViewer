package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public interface EnchantBookForEmeraldsAccessor {

    @Accessor("villagerXp")
    int villagerXp();

    @Accessor("tradeableEnchantments")
    TagKey<Enchantment> tradeableEnchantments();

    @Accessor("minLevel")
    int minLevel();

    @Accessor("maxLevel")
    int maxLevel();

}
