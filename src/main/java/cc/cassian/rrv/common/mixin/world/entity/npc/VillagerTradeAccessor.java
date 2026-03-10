package cc.cassian.rrv.common.mixin.world.entity.npc;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

@Mixin(VillagerTrade.class)
public interface VillagerTradeAccessor {
	@Accessor
	TradeCost getWants();

	@Accessor("givenItemModifiers")
	List<LootItemFunction> getGivenItemModifiers();

	@Accessor
	Optional<TradeCost> getAdditionalWants();

	@Accessor("gives")
	ItemStackTemplate getGives();

	@Accessor("merchantPredicate")
	Optional<LootItemCondition> getMerchantPredicate();
}



