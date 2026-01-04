package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(VillagerTrade.class)
public interface VillagerTradeAccessor {
	@Accessor
	TradeCost getWants();

	@Accessor
	Optional<TradeCost> getAdditionalWants();

	@Accessor("gives")
	ItemStack getGives();
}
