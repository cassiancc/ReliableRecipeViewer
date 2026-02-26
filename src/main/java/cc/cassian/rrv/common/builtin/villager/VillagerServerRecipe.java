package cc.cassian.rrv.common.builtin.villager;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.mixin.world.entity.npc.*;
import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
//? <26 {
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCost;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.entity.npc.VillagerType;
import org.jetbrains.annotations.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
 //?} else {
/*import net.minecraft.world.entity.npc.villager.VillagerType;
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
*///?}
import net.minecraft.world.item.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class VillagerServerRecipe implements ReliableServerRecipe {

	public static final ReliableServerRecipeType<VillagerServerRecipe> TYPE = ReliableServerRecipeType.register(
			ResourceLocation.withDefaultNamespace("villager_trading"),
			() -> new VillagerServerRecipe(null, 0, null)
	);

	//? >26 {
	/*private ResourceKey<VillagerProfession> profession;
	private int professionLevel;
	private List<VillagerTrade> clientTrade;
	private Holder<VillagerTrade> serverTrade;
	private List<ItemStack> cost1;
	private List<ItemStack> cost2;
	private List<ItemStack> offerStacks;


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
		tag.put("offerStacks", TagUtil.writeList(offerStacks(serverTrade.value()), (origin, tag1) -> TagUtil.encodeItemStackOnServer(origin)));
		tag.put("cost1", TagUtil.writeList(cost1(serverTrade.value()), (origin, tag1) -> TagUtil.encodeItemStackOnServer(origin)));
		tag.put("cost2", TagUtil.writeList(cost2(serverTrade.value()), (origin, tag1) -> TagUtil.encodeItemStackOnServer(origin)));
	}

	@Override
	public void loadFromTag(CompoundTag tag) {
		if (tag.contains("profession"))
			this.profession = tag.read("profession", ResourceKey.codec(Registries.VILLAGER_PROFESSION)).orElseThrow();

		this.professionLevel = tag.getIntOr("professionLevel", 0);

		ArrayList<VillagerTrade> trades = new ArrayList<>();
		tag.getListOrEmpty("trades").forEach(trade -> VillagerTrade.CODEC.decode(ClientRecipeManager.INSTANCE.createSerializationContext(), trade).result().ifPresent(decodedTrade -> trades.add(decodedTrade.getFirst())));
		this.clientTrade = trades;

		this.cost1 = TagUtil.readList(tag, "cost1", TagUtil::decodeItemStackOnClient);
		this.cost2 = TagUtil.readList(tag, "cost2", TagUtil::decodeItemStackOnClient);
		this.offerStacks = TagUtil.readList(tag, "offerStacks", TagUtil::decodeItemStackOnClient);
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

	public List<ItemStack> offerStacks(VillagerTrade trade) {
		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;
		AtomicReference<ItemStack> stack = new AtomicReference<>(RrvUtil.decodeTemplate(tradeAccessor.getGives()));
		tradeAccessor.getGivenItemModifiers().forEach(modifier -> {
			if (modifier instanceof ExplorationMapFunction || modifier instanceof FilteredFunction) return; // utter bodge - fixes the map item getting entirely voided
			stack.set(modifier.apply(stack.get(), lootContext()));
		});
		return List.of(stack.get());
	}

	public List<ItemStack> cost1(VillagerTrade trade) {
		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;
		return getItemFromTradeCost(tradeAccessor.getWants());
	}

	public List<ItemStack> cost2(VillagerTrade trade) {
		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;
		if (tradeAccessor.getAdditionalWants().isPresent()) {
			return getItemFromTradeCost(tradeAccessor.getAdditionalWants().get());
		} else return List.of();
	}

	private List<ItemStack> getItemFromTradeCost(TradeCost wants) {
		return List.of(new ItemStack(wants.item(), wants.count().getInt(lootContext()), wants.components().asPatch()));
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


	public record VillagerOffer(VillagerTrade trade, ResourceKey<VillagerProfession> profession, int professionLevel, List<ItemStack> offerStacks, List<ItemStack> cost1, List<ItemStack> cost2, ResourceKey<VillagerType> requiredType) {
	}

	*///?} else {

    private ResourceKey<VillagerProfession> profession;
    private int professionLevel;
    private final VillagerDataObject<?> dataObject;

    private List<VillagerOffer> clientSideVillagerOffers = new ArrayList<>();

    public VillagerServerRecipe(ResourceKey<VillagerProfession> profession, int professionLevel, VillagerDataObject<?> dataObject) {
        this.profession = profession;
        this.professionLevel = professionLevel;
        this.dataObject = dataObject;

    }

    public List<VillagerOffer> getOffers() {
        return this.clientSideVillagerOffers;
    }

    public ResourceKey<VillagerProfession> getProfession() {
        return this.profession;
    }

    public int getProfessionLevel() {
        return this.professionLevel;
    }

    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putString("profession", this.profession.location().toString());
        tag.putInt("professionLevel", this.professionLevel);

        tag.putString("type", this.dataObject.type().id().toString());

        CompoundTag data = new CompoundTag();
        this.dataObject.type().encoder().encode(VillagerServerRecipe.castListing(this.dataObject.listing), data);

        tag.put("data", data);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        if (tag.contains("profession"))
            this.profession = BuiltInRegistries.VILLAGER_PROFESSION.get(ResourceLocation.parse(tag.getString("profession").orElseThrow())).orElseThrow().key();

        this.professionLevel = tag.getIntOr("professionLevel", 0);

        VillagerOfferType<?> type = VillagerOfferType.byId(ResourceLocation.parse(tag.getString("type").orElseThrow()));
        this.clientSideVillagerOffers = type.decoder().decode(this.profession, this.professionLevel, tag.getCompoundOrEmpty("data"));

    }

    //:D
    public static  <T extends VillagerTrades.ItemListing> T castListing(VillagerTrades.ItemListing listing) {
        return (T) listing;
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }


    public record VillagerDataObject<T extends VillagerTrades.ItemListing>(VillagerOfferType<T> type, T listing) {
    }

    public record VillagerOffer(ResourceKey<VillagerProfession> profession, int professionLevel,
                                @Nullable ResourceKey<VillagerType> requiredType, List<ItemStack> offerStacks,
                                List<ItemStack> cost1, List<ItemStack> cost2, int villagerXp, int maxUses) {

    }


    public record VillagerOfferType<T extends VillagerTrades.ItemListing>(ResourceLocation id, Encoder<T> encoder,
                                                                          Decoder decoder) {

        private static final HashMap<ResourceLocation, VillagerOfferType<?>> TYPES = new HashMap<>();
        private static final HashMap<Class<?>, ResourceLocation> ID_BY_CLASS = new HashMap<>();

        public static final VillagerOfferType<VillagerTrades.EmeraldForItems> EMERALD_FOR_ITEMS = register(
                ResourceLocation.withDefaultNamespace("emerald_for_items"),
                VillagerTrades.EmeraldForItems.class,
                (listing, out) -> {
                    EmeraldForItemsAccessor accessor = (EmeraldForItemsAccessor) listing;

                    out.put("cost", TagUtil.writeItemStack(accessor.getItemStack().itemStack()));
                    out.putInt("emeraldCount", accessor.getEmeraldAmount());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                },
                (profession, professionLevel, in) -> {

                    ItemStack cost = TagUtil.readItemStack(in.getCompoundOrEmpty("cost"));
                    int emeraldCount = in.getIntOr("emeraldCount", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, List.of(new ItemStack(Items.EMERALD, emeraldCount)), List.of(cost), List.of(), villagerXp, maxUses));
                }
        );

        public static final VillagerOfferType<VillagerTrades.ItemsForEmeralds> ITEMS_FOR_EMERALDS = register(
                ResourceLocation.withDefaultNamespace("items_for_emeralds"),
                VillagerTrades.ItemsForEmeralds.class,
                (listing, out) -> {

                    ItemsForEmeraldsAccessor accessor = (ItemsForEmeraldsAccessor) listing;

                    List<ItemStack> offerStacks = new ArrayList<>();

                    if (accessor.enchantmentProvider().isPresent()) {
                        EnchantmentProvider provider = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT_PROVIDER).get(accessor.enchantmentProvider().get()).orElseThrow().value();
                        offerStacks.addAll(VillagerServerRecipe.createOfferStacksFromEnchantmentProvider(provider, accessor.itemStack()));
                    } else
                        offerStacks.add(accessor.itemStack().copy());


                    out.put("offers", TagUtil.writeList(offerStacks, (origin, tag) -> TagUtil.writeItemStack(origin)));
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());
                },
                (profession, professionLevel, in) -> {


                    List<ItemStack> offers = TagUtil.readList(in, "offers", TagUtil::readItemStack);
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offers, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(), villagerXp, maxUses));
                }
        );

        public static final VillagerOfferType<VillagerTrades.SuspiciousStewForEmerald> SUSPICIOUS_STEW = register(
                ResourceLocation.withDefaultNamespace("suspicious_stew"),
                VillagerTrades.SuspiciousStewForEmerald.class,
                (listing, out) -> {
                    SuspiciousStewForEmeraldAccessor accessor = (SuspiciousStewForEmeraldAccessor) listing;
                    ItemStack stewStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
                    stewStack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, accessor.effects());

                    out.put("stew", TagUtil.writeItemStack(stewStack));
                    out.putInt("emeraldCost", 1);
                    out.putInt("villagerXp", accessor.xp());
                    out.putInt("maxUses", 12);

                },
                (profession, professionLevel, in) -> {

                    ItemStack stew = TagUtil.readItemStack(in.getCompoundOrEmpty("stew"));
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, List.of(stew), List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(), villagerXp, maxUses));
                }
        );

        public static final VillagerOfferType<VillagerTrades.EnchantBookForEmeralds> ENCHANT_BOOK = register(
                ResourceLocation.withDefaultNamespace("enchant_book"),
                VillagerTrades.EnchantBookForEmeralds.class,
                (listing, out) -> {
                    EnchantBookForEmeraldsAccessor accessor = (EnchantBookForEmeraldsAccessor) listing;

                    //offerStacks
                    HashMap<ResourceKey<Enchantment>, List<ItemStack>> offers = new HashMap<>();
                    HashMap<ResourceKey<Enchantment>, List<ItemStack>> costs = new HashMap<>();

                    Registry<Enchantment> enchantmentRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

                    enchantmentRegistry.get(accessor.tradeableEnchantments()).ifPresent(holders -> {
                        holders.stream().forEach(enchantment -> {

                            List<ItemStack> offerStacks = new ArrayList<>();
                            List<ItemStack> costStacks = new ArrayList<>();

                            for (int i = Math.max(accessor.minLevel(), enchantment.value().getMinLevel()); i <= accessor.maxLevel() && i <= enchantment.value().getMaxLevel(); i++) {
                                ItemStack enchantedBook = EnchantmentHelper.createBook(new EnchantmentInstance(enchantment, i));
                                offerStacks.add(enchantedBook);

                                int emeraldCostsMin = 2 + 3 * i;
                                int emeraldCostsMax = (5 + i * 10 - 1) + 3 * i;

                                if (enchantment.is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
                                    emeraldCostsMin *= 2;
                                    emeraldCostsMax *= 2;
                                }

                                if (emeraldCostsMin > 64)
                                    emeraldCostsMin = 64;

                                if (emeraldCostsMax > 64)
                                    emeraldCostsMax = 64;

                                ItemStack costStack = new ItemStack(Items.EMERALD, emeraldCostsMin + (emeraldCostsMax - emeraldCostsMin) / 2);
                                ItemLore lore = costStack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
                                lore = lore.withLineAdded(Component.literal(emeraldCostsMin + " - " + emeraldCostsMax).withStyle(ChatFormatting.GRAY));
                                costStack.set(DataComponents.LORE, lore);
                                costStacks.add(costStack);
                            }

                            offers.put(enchantment.unwrapKey().get(), offerStacks);
                            costs.put(enchantment.unwrapKey().get(), costStacks);
                        });
                    });

                    CompoundTag offersTag = new CompoundTag();
                    offers.forEach((enchantment, stacks) -> {
                        List<ItemStack> costStacks = costs.get(enchantment);

                        CompoundTag offerTag = new CompoundTag();
                        offerTag.put("offerStacks", TagUtil.writeList(stacks, (origin, tag) -> TagUtil.writeItemStack(origin)));
                        offerTag.put("costStacks", TagUtil.writeList(costStacks, (origin, tag) -> TagUtil.writeItemStack(origin)));
                        offersTag.put(enchantment.location().toDebugFileName(), offerTag);
                    });

                    out.put("offers", offersTag);
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", 12);

                },
                (profession, professionLevel, in) -> {

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    List<VillagerOffer> villagerOffers = new ArrayList<>();

                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    CompoundTag offersTag = in.getCompoundOrEmpty("offers");
                    offersTag.values().stream().map(tag -> tag.asCompound().orElseGet(CompoundTag::new)).forEach(offerTag -> {

                        List<ItemStack> offerStacks = TagUtil.readList(offerTag, "offerStacks", TagUtil::readItemStack);
                        List<ItemStack> costStacks = TagUtil.readList(offerTag, "costStacks", TagUtil::readItemStack);

                        VillagerOffer offer = new VillagerOffer(profession, professionLevel, villagerType, offerStacks, costStacks, List.of(new ItemStack(Items.BOOK)), villagerXp, maxUses);
                        villagerOffers.add(offer);
                    });

                    return villagerOffers;
                }
        );


        public static final VillagerOfferType<VillagerTrades.TreasureMapForEmeralds> TREASURE_MAP = register(
                ResourceLocation.withDefaultNamespace("treasure_map"),
                VillagerTrades.TreasureMapForEmeralds.class,
                (listing, out) -> {

                    TreasureMapForEmeraldsAccessor accessor = (TreasureMapForEmeraldsAccessor) listing;

                    out.putString("decoration", accessor.destinationType().unwrapKey().orElseThrow().location().toString());
                    out.putString("displayName", accessor.displayName());
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());

                },
                (profession, professionLevel, in) -> {

                    MapDecorationType decorationType = BuiltInRegistries.MAP_DECORATION_TYPE.getOptional(ResourceLocation.parse(in.getString("decoration").orElseThrow())).orElseThrow();
                    String displayName = in.getStringOr("displayName", "");
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ItemStack offerStack = new ItemStack(Items.FILLED_MAP);
                    offerStack.set(DataComponents.ITEM_NAME, Component.translatable(displayName));
                    if (decorationType.hasMapColor())
                        offerStack.set(DataComponents.MAP_COLOR, new MapItemColor(decorationType.mapColor()));

                    ItemStack costStack1 = new ItemStack(Items.EMERALD, emeraldCost);
                    ItemStack costStack2 = new ItemStack(Items.COMPASS);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, List.of(offerStack), List.of(costStack1), List.of(costStack2), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.TippedArrowForItemsAndEmeralds> TIPPED_ARROW = register(
                ResourceLocation.withDefaultNamespace("tipped_arrow"),
                VillagerTrades.TippedArrowForItemsAndEmeralds.class,
                (listing, out) -> {
                    TippedArrowForItemsAndEmeraldsAccessor accessor = (TippedArrowForItemsAndEmeraldsAccessor) listing;

                    List<ItemStack> offerStacks = new ArrayList<>();

                    List<Holder<Potion>> potions = BuiltInRegistries.POTION.listElements().filter((potionReference) -> !potionReference.value().getEffects().isEmpty() && ServerRecipeManager.INSTANCE.getServer().potionBrewing().isBrewablePotion(potionReference)).collect(Collectors.toList());
                    potions.forEach(holder -> {

                        ItemStack offerStack = new ItemStack(accessor.toItem().getItem(), accessor.toCount());
                        offerStack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
                        offerStacks.add(offerStack);
                    });

                    out.put("offers", TagUtil.writeList(offerStacks, (origin, tag) -> TagUtil.writeItemStack(origin)));
                    out.putString("fromItem", TagUtil.itemToString(accessor.fromItem()));
                    out.putInt("fromCount", accessor.fromCount());
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());

                },
                (profession, professionLevel, in) -> {

                    List<ItemStack> offers = TagUtil.readList(in, "offers", TagUtil::readItemStack);
                    Item fromItem = TagUtil.itemFromString(in.getStringOr("fromItem", ""));
                    int fromCount = in.getIntOr("fromCount", 0);
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offers, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(new ItemStack(fromItem, fromCount)), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.EnchantedItemForEmeralds> ENCHANTED_ITEM_FOR_EMERALDS = register(
                ResourceLocation.withDefaultNamespace("enchanted_item_for_emeralds"),
                VillagerTrades.EnchantedItemForEmeralds.class,
                (listing, out) -> {

                    EnchantedItemForEmeraldsAccessor accessor = (EnchantedItemForEmeraldsAccessor) listing;

                    List<ItemStack> offerStacks = new ArrayList<>();

                    int bonusCostMin = 0;
                    int bonusCostMax = 5 + 14;

                    int totalCostsMin = Math.min(accessor.baseEmeraldCost() + bonusCostMin, 64);
                    int totalCostsMax = Math.min(accessor.baseEmeraldCost() + bonusCostMax, 64);

                    RegistryAccess registryAccess = ServerRecipeManager.INSTANCE.getServer().registryAccess();
                    Optional<HolderSet.Named<Enchantment>> optional = registryAccess.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.ON_TRADED_EQUIPMENT);

                    Enchantable enchantable = accessor.itemStack().get(DataComponents.ENCHANTABLE);

                    if (optional.isEmpty())
                        return;

                    if (enchantable == null)
                        return;

                    float f = 0.15F;
                    float f1 = -0.15F;

                    bonusCostMin += 1;
                    bonusCostMin = Mth.clamp(Math.round(bonusCostMin + bonusCostMin * f1), 1, Integer.MAX_VALUE);

                    bonusCostMax += 1 + enchantable.value() / 4 + enchantable.value() / 4;
                    bonusCostMax = Mth.clamp(Math.round(bonusCostMax + bonusCostMax * f), 1, Integer.MAX_VALUE);


                    for (int i = bonusCostMin; i <= bonusCostMax; i++) {
                        List<EnchantmentInstance> list = EnchantmentHelper.getAvailableEnchantmentResults(i, accessor.itemStack(), optional.get().stream());
                        list.forEach(enchantmentInstance -> {
                            ItemStack stack = accessor.itemStack().copy();
                            stack.enchant(enchantmentInstance.enchantment(), enchantmentInstance.level());

                            if (offerStacks.stream().noneMatch(stack1 -> stack1.getEnchantments().equals(stack.getEnchantments())))
                                offerStacks.add(stack);
                        });
                    }

                    ItemStack costStack = new ItemStack(Items.EMERALD, totalCostsMin + (totalCostsMax - totalCostsMin) / 2);
                    ItemLore lore = costStack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
                    lore.withLineAdded(Component.literal(totalCostsMin + " - " + totalCostsMax).withStyle(ChatFormatting.GRAY));
                    costStack.set(DataComponents.LORE, lore);


                    out.put("offers", TagUtil.writeList(offerStacks, (origin, tag) -> TagUtil.writeItemStack(origin)));
                    out.put("costStack", TagUtil.writeItemStack(costStack));
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());
                },
                (profession, professionLevel, in) -> {


                    List<ItemStack> offerStacks = TagUtil.readList(in, "offers", TagUtil::readItemStack);
                    ItemStack costStack = TagUtil.readItemStack(in.getCompoundOrEmpty("costStack"));
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();


                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offerStacks, List.of(costStack), List.of(), villagerXp, maxUses));

                }
        );


        public static final VillagerOfferType<VillagerTrades.DyedArmorForEmeralds> DYED_ARMOR = register(
                ResourceLocation.withDefaultNamespace("dyed_armor"),
                VillagerTrades.DyedArmorForEmeralds.class,
                (listing, out) -> {
                    DyedArmorForEmeraldsAccessor accessor = (DyedArmorForEmeraldsAccessor) listing;


                    ItemStack offerStack = new ItemStack(accessor.getItem());

                    out.put("offerStack", TagUtil.writeItemStack(offerStack));

                    out.putInt("emeraldCost", accessor.getValue());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                },
                (profession, professionLevel, in) -> {

                    ItemStack offerStack = TagUtil.readItemStack(in.getCompoundOrEmpty("offerStack"));
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    List<ItemStack> offerStacks = new ArrayList<>();

                    if (offerStack.is(ItemTags.DYEABLE)) {
                        for (DyeColor color1 : DyeColor.values()) {
                            offerStacks.add(DyedItemColor.applyDyes(offerStack.copy(), List.of(DyeItem.byColor(color1))));

                            for (DyeColor color2 : DyeColor.values()) {
                                offerStacks.add(DyedItemColor.applyDyes(offerStack.copy(), List.of(DyeItem.byColor(color1), DyeItem.byColor(color2))));


                                for (DyeColor color3 : DyeColor.values()) {
                                    offerStacks.add(DyedItemColor.applyDyes(offerStack.copy(), List.of(DyeItem.byColor(color1), DyeItem.byColor(color2), DyeItem.byColor(color3))));
                                }
                            }
                        }
                    } else
                        offerStacks.add(offerStack);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offerStacks, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.ItemsAndEmeraldsToItems> ITEMS_AND_EMERALDS_TO_ITEMS = register(
                ResourceLocation.withDefaultNamespace("items_and_emeralds_to_items"),
                VillagerTrades.ItemsAndEmeraldsToItems.class,
                (listing, out) -> {
                    ItemsAndEmeraldsToItemsAccessor accessor = (ItemsAndEmeraldsToItemsAccessor) listing;

                    ItemStack costStack = accessor.fromItem().itemStack().copy();

                    List<ItemStack> offerStacks = new ArrayList<>();

                    if (accessor.enchantmentProvider().isPresent()) {
                        EnchantmentProvider provider = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT_PROVIDER).get(accessor.enchantmentProvider().get()).orElseThrow().value();
                        offerStacks.addAll(createOfferStacksFromEnchantmentProvider(provider, accessor.toItem()));
                    } else
                        offerStacks.add(accessor.toItem().copy());

                    out.put("offerStacks", TagUtil.writeList(offerStacks, (origin, tag) -> TagUtil.writeItemStack(origin)));
                    out.put("costStack", TagUtil.writeItemStack(costStack));
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                },
                (profession, professionLevel, in) -> {

                    List<ItemStack> offerStacks = TagUtil.readList(in, "offerStacks", TagUtil::readItemStack);
                    ItemStack costStack = TagUtil.readItemStack(in.getCompoundOrEmpty("costStack"));
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offerStacks, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(costStack), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.EmeraldsForVillagerTypeItem> EMERALDS_FOR_VILLAGER_TYPE = register(
                ResourceLocation.withDefaultNamespace("emeralds_for_villager_type"),
                VillagerTrades.EmeraldsForVillagerTypeItem.class,
                (listing, out) -> {
                    EmeraldsForVillagerTypeItemAccessor accessor = (EmeraldsForVillagerTypeItemAccessor) listing;


                    out.putInt("cost", accessor.getCost());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                    CompoundTag tradesTag = new CompoundTag();

                    accessor.getTrades().forEach((villagerType, item) -> {
                        tradesTag.putString(villagerType.location().toString(), TagUtil.itemToString(item));
                    });

                    out.put("trades", tradesTag);
                },
                (profession, professionLevel, in) -> {

                    int cost = in.getIntOr("cost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    HashMap<ResourceKey<VillagerType>, Item> trades = new HashMap<>();
                    CompoundTag tradesTag = in.getCompoundOrEmpty("trades");

                    tradesTag.forEach((s, tag) -> {
                        ResourceKey<VillagerType> villagerType = BuiltInRegistries.VILLAGER_TYPE.get(ResourceLocation.parse(s)).orElseThrow().key();
                        Item item = TagUtil.itemFromString(tag.asString().orElseThrow());
                        trades.put(villagerType, item);
                    });

                    List<VillagerOffer> villagerOffers = new ArrayList<>();

                    trades.forEach((villagerTypeResourceKey, item) -> {
                        villagerOffers.add(new VillagerOffer(profession, professionLevel, villagerTypeResourceKey, List.of(new ItemStack(Items.EMERALD)), List.of(new ItemStack(item, cost)), List.of(), villagerXp, maxUses));
                    });

                    return villagerOffers;
                }
        );

        public static final VillagerOfferType<VillagerTrades.TypeSpecificTrade> TYPE_SPECIFIC = register(
                ResourceLocation.withDefaultNamespace("type_specific"),
                VillagerTrades.TypeSpecificTrade.class,
                (listing, out) -> {

                    listing.trades().forEach((villagerType, itemListing) -> {

                        VillagerOfferType<?> offerType = byClass(itemListing.getClass());
                        if(offerType == null)
                            return;

                        CompoundTag encodedListing = new CompoundTag();
                        offerType.encoder().encode(VillagerServerRecipe.castListing(itemListing), encodedListing);

                        encodedListing.putString("requiredType", villagerType.location().toString());
                        encodedListing.putString("listingType", offerType.id().toString());

                        out.put(villagerType.location().toString(), encodedListing);
                    });

                },
                (profession, professionLevel, in) -> {

                    List<VillagerOffer> villagerOffers = new ArrayList<>();

                    in.forEach((villagerType, tag) -> {

                        CompoundTag listingTag = tag.asCompound().orElseThrow();

                        ResourceLocation listingId = ResourceLocation.parse(listingTag.getString("listingType").orElseThrow());
                        VillagerOfferType<?> offerType = byId(listingId);

                        villagerOffers.addAll(offerType.decoder().decode(profession, professionLevel, listingTag));
                    });

                    return villagerOffers;
                }
        );

        public static <T extends VillagerTrades.ItemListing> VillagerOfferType<T> register(ResourceLocation id, Class<T> clazz, Encoder<T> encoder, Decoder decoder) {
            VillagerOfferType<T> type = new VillagerOfferType<>(id, encoder, decoder);
            TYPES.put(id, type);
            ID_BY_CLASS.put(clazz, id);
            return type;
        }

        public static <T extends VillagerTrades.ItemListing> VillagerOfferType<T> byId(ResourceLocation id) {
            return (VillagerOfferType<T>) TYPES.get(id);
        }

        public static <T extends VillagerTrades.ItemListing> VillagerOfferType<T> byClass(Class<T> clazz){
            if(ID_BY_CLASS.containsKey(clazz))
                return byId(ID_BY_CLASS.get(clazz));

            return null;
        }

        public interface Encoder<T extends VillagerTrades.ItemListing> {
            void encode(T listing, CompoundTag out);
        }

        public interface Decoder {

            List<VillagerOffer> decode(ResourceKey<VillagerProfession> profession, int professionLevel, CompoundTag in);
        }

    }


    private static List<ItemStack> createOfferStacksFromEnchantmentProvider(EnchantmentProvider provider, ItemStack stack) {
        List<ItemStack> offerStacks = new ArrayList<>();

        Enchantable enchantable = stack.get(DataComponents.ENCHANTABLE);

        switch (provider) {

            case SingleEnchantment singleEnchantment -> {
                for (int i = singleEnchantment.level().getMinValue(); i <= singleEnchantment.level().getMaxValue(); i++) {
                    ItemStack offerStack = stack.copy();

                    offerStack.enchant(singleEnchantment.enchantment(), i);
                    offerStacks.add(offerStack);
                }
            }
            case EnchantmentsByCost byCost -> {

                if (enchantable == null)
                    return List.of();

                float f = 0.15F;
                float f1 = -0.15F;

                int byCostMin = byCost.cost().getMinValue();
                int byCostMax = byCost.cost().getMaxValue();

                byCostMin += 1;
                byCostMin = Mth.clamp(Math.round(byCostMin + byCostMin * f1), 1, Integer.MAX_VALUE);

                byCostMax += 1 + enchantable.value() / 4 + enchantable.value() / 4;
                byCostMax = Mth.clamp(Math.round(byCostMax + byCostMax * f), 1, Integer.MAX_VALUE);

                for (int i = byCostMin; i <= byCostMax; i++) {

                    EnchantmentHelper.getAvailableEnchantmentResults(i, stack.copy(), byCost.enchantments().stream()).forEach(enchantmentInstance -> {
                        ItemStack offerStack = stack.copy();
                        offerStack.enchant(enchantmentInstance.enchantment(), enchantmentInstance.level());
                        offerStacks.add(offerStack);
                    });
                }

            }
            case EnchantmentsByCostWithDifficulty byCostWithDifficulty -> {

                if (enchantable == null)
                    return List.of();

                float f = 0.15F;
                float f1 = -0.15F;

                int byCostMin = byCostWithDifficulty.minCost();
                int byCostMax = byCostWithDifficulty.minCost() + byCostWithDifficulty.maxCostSpan();

                byCostMin += 1;
                byCostMin = Mth.clamp(Math.round(byCostMin + byCostMin * f1), 1, Integer.MAX_VALUE);

                byCostMax += 1 + enchantable.value() / 4 + enchantable.value() / 4;
                byCostMax = Mth.clamp(Math.round(byCostMax + byCostMax * f), 1, Integer.MAX_VALUE);

                for (int i = byCostMin; i < byCostMax; i++) {
                    EnchantmentHelper.getAvailableEnchantmentResults(i, stack.copy(), byCostWithDifficulty.enchantments().stream()).forEach(enchantmentInstance -> {
                        ItemStack offerStack = stack.copy();
                        offerStack.enchant(enchantmentInstance.enchantment(), enchantmentInstance.level());
                        offerStacks.add(offerStack);
                    });
                }
            }
            default -> offerStacks.add(stack.copy());

        }

        return offerStacks;
    }
    //?}
}