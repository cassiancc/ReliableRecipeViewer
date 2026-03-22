package cc.cassian.rrv.common.builtin.villager;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.mixin.world.entity.npc.*;
import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.VillagerTypePredicate;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.FilteredFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class VillagerServerRecipe implements ReliableServerRecipe {

	public static final ReliableServerRecipeType<VillagerServerRecipe> TYPE = ReliableServerRecipeType.register(
			Identifier.withDefaultNamespace("villager_trading"),
			() -> new VillagerServerRecipe(null, 0, null)
	);

	private ResourceKey<VillagerProfession> profession;
	private int professionLevel;
	private List<VillagerTrade> clientTrade;
	private Holder<VillagerTrade> serverTrade;
	private SlotContent cost1, cost2, offerStacks;


	public VillagerServerRecipe(ResourceKey<VillagerProfession> key, int level, Holder<VillagerTrade> trade) {
		this.profession = key;
		this.professionLevel = level;
		this.serverTrade = trade;
	}

	@Override
	public void writeToTag(CompoundTag tag) {
		tag.store("profession", ResourceKey.codec(Registries.VILLAGER_PROFESSION), this.profession);
		tag.putInt("professionLevel", this.professionLevel);
		ListTag trades = new ListTag();
		trades.add(VillagerTrade.CODEC.encodeStart(ServerRecipeManager.INSTANCE.createSerializationContext(), serverTrade.value()).result().orElseThrow());
		tag.put("trades", trades);
		tag.put("offerStacks", TagUtil.writeSlotContent(offerStacks(serverTrade.value())));
		tag.put("cost1", TagUtil.writeSlotContent(cost1(serverTrade.value())));
		tag.put("cost2", TagUtil.writeSlotContent(cost2(serverTrade.value())));
	}

	@Override
	public void loadFromTag(CompoundTag tag) {
		if (tag.contains("profession"))
			this.profession = tag.read("profession", ResourceKey.codec(Registries.VILLAGER_PROFESSION)).orElseThrow();

		this.professionLevel = tag.getIntOr("professionLevel", 0);

		ArrayList<VillagerTrade> trades = new ArrayList<>();
		tag.getListOrEmpty("trades").forEach(trade -> VillagerTrade.CODEC.decode(ClientRecipeManager.INSTANCE.createSerializationContext(), trade).result().ifPresent(decodedTrade -> trades.add(decodedTrade.getFirst())));
		this.clientTrade = trades;

		this.cost1 = TagUtil.readSlotContent(tag.getCompoundOrEmpty("cost1"));
		this.cost2 = TagUtil.readSlotContent(tag.getCompoundOrEmpty("cost2"));
		this.offerStacks = TagUtil.readSlotContent(tag.getCompoundOrEmpty("offerStacks"));
	}

	@Override
	public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
		return TYPE;
	}

	public List<VillagerTrade> getClientTrades() {
		return clientTrade;
	}

	public ResourceKey<VillagerProfession> getProfession() {
		return profession;
	}

	public List<VillagerOffer> getOffers() {
		ArrayList<VillagerOffer> villagerOffers = new ArrayList<>();
		getClientTrades().forEach(trade -> villagerOffers.add(new VillagerOffer(trade, profession, professionLevel, offerStacks, cost1, cost2, requiredType(trade))));
		return villagerOffers;
	}

	public SlotContent offerStacks(VillagerTrade trade) {
		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;
		AtomicReference<ItemStack> stack = new AtomicReference<>(RrvUtil.decodeTemplate(tradeAccessor.getGives()));
		tradeAccessor.getGivenItemModifiers().forEach(modifier -> {
			if (modifier instanceof ExplorationMapFunction || modifier instanceof FilteredFunction) return; // utter bodge - fixes the map item getting entirely voided
			stack.set(modifier.apply(stack.get(), lootContext()));
		});
		return SlotContent.of(stack.get());
	}

	public SlotContent cost1(VillagerTrade trade) {
		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;
		return getItemFromTradeCost(tradeAccessor.getWants());
	}

	public SlotContent cost2(VillagerTrade trade) {
		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;
		if (tradeAccessor.getAdditionalWants().isPresent()) {
			return getItemFromTradeCost(tradeAccessor.getAdditionalWants().get());
		} else return SlotContent.of();
	}

	private SlotContent getItemFromTradeCost(TradeCost wants) {
		return SlotContent.of(new ItemStackTemplate(wants.item(), wants.count().getInt(lootContext()), wants.components().asPatch()));
	}

	public ResourceKey<VillagerType> requiredType(VillagerTrade trade) {
		ResourceKey<VillagerType> villagerTypeHolder = VillagerType.PLAINS;
		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;
		var predicate = tradeAccessor.getMerchantPredicate();
		if (predicate.orElse(null) instanceof LootItemEntityPropertyCondition lootItemEntityPropertyCondition) {
			for (Map.Entry<DataComponentPredicate.Type<?>, DataComponentPredicate> entry : lootItemEntityPropertyCondition.predicate().get().components().partial().entrySet()) {
				DataComponentPredicate.Type<?> type = entry.getKey();
				DataComponentPredicate dataComponentPredicate = entry.getValue();
				if (type.equals(DataComponentPredicates.VILLAGER_VARIANT)) {
					villagerTypeHolder = (((VillagerTypePredicate) dataComponentPredicate).villagerTypes().get(0)).unwrapKey().orElse(VillagerType.PLAINS);
				}
			}
		}
		return villagerTypeHolder;
	}

	private LootContext lootContext() {
		return new LootContext.Builder(new LootParams.Builder(ServerRecipeManager.INSTANCE.getServer().overworld()).create(new ContextKeySet.Builder().build())).create(Optional.empty());
	}


	public record VillagerOffer(VillagerTrade trade, ResourceKey<VillagerProfession> profession, int professionLevel, SlotContent offerStacks, SlotContent cost1, SlotContent cost2, ResourceKey<VillagerType> requiredType) {
	}
}