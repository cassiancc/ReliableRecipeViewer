package cc.cassian.rrv.common.mixin.world.entity.npc;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
//? if >26 {
/*import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
@Mixin(VillagerTrade.class)
public interface VillagerTradeAccessor {
	@Accessor
	TradeCost getWants();

	@Accessor
	Optional<TradeCost> getAdditionalWants();

	@Accessor("gives")
	ItemStack getGives();
}
*///?} else {
@Mixin(net.minecraft.world.entity.npc.villager.VillagerTrades.class)
public interface VillagerTradeAccessor {
}
//?}



