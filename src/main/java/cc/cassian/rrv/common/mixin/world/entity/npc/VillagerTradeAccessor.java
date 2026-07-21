package cc.cassian.rrv.common.mixin.world.entity.npc;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

@Mixin(VillagerTrade.class)
public interface VillagerTradeAccessor {
	@Accessor
	TradeCost getWants();

	//? if >26.2 {
	/*@Accessor("givenItemModifier")
	Optional<Holder<LootItemFunction>> getGivenItemModifiers();
	*///?} else {
	@Accessor("givenItemModifiers")
	List<LootItemFunction> getGivenItemModifiers();
	//?}

	@Accessor("doubleTradePriceEnchantments")
	Optional<HolderSet<Enchantment>> getDoubleTradePriceEnchantments();

	//~ if >26.2 'NumberProvider'->'Holder<NumberProvider>' {
	@Accessor("maxUses")
	NumberProvider getMaxUses();

	@Accessor("xp")
	NumberProvider getXp();
	//~}

	@Accessor
	Optional<TradeCost> getAdditionalWants();

	@Accessor("gives")
	ItemStackTemplate getGives();

	@Accessor("merchantPredicate")
	Optional<LootItemCondition> getMerchantPredicate();
}



