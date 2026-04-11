/*
MIT License

Copyright (c) 2026 Lius Hohmann

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package cc.cassian.rrv.common.builtin.villager;

import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.mixin.world.entity.npc.VillagerTradeAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.functions.*;
import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.VillagerTypePredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class VillagerServerRecipe implements ReliableServerRecipe {

	public static final ReliableServerRecipeType<VillagerServerRecipe> TYPE = ReliableServerRecipeType.register(
			Identifier.withDefaultNamespace("villager_trading"),
			() -> new VillagerServerRecipe(null, 0, null, null)
	);

	private static final HashMap<Class<? extends LootItemFunction>, GivenItemFunctionProcessor<?>> FUNCTION_PROCESSORS = new HashMap<>();
	private static final HashMap<Class<? extends LootItemFunction>, SubTradePostProcessor<?>> POST_PROCESSORS = new HashMap<>();
	private Identifier id;


	public static <T extends LootItemFunction> void registerFunctionProcessor(Class<T> clazz, GivenItemFunctionProcessor<T> processor) {
		FUNCTION_PROCESSORS.put(clazz, processor);
	}

	public static <T extends LootItemFunction> void registerFunctionPostProcessor(Class<T> clazz, SubTradePostProcessor<T> processor) {
		POST_PROCESSORS.put(clazz, processor);
	}

	private ResourceKey<VillagerProfession> profession;
	private int professionLevel;
	private ResourceKey<VillagerType> requiredType;

    private int villagerXp, maxUses;

	private List<SubTradeGroup> subTradeGroups;
	private List<LootItemFunction> modifiers;

	private HolderSet<Enchantment> doubleTradePriceEnchantments;

	public VillagerServerRecipe(ResourceKey<VillagerProfession> profession, int professionLevel, VillagerTrade trade, Identifier identifier) {
		this.profession = profession;
		this.professionLevel = professionLevel;

		this.subTradeGroups = new ArrayList<>();
		this.modifiers = new ArrayList<>();
		this.id = identifier;


		//If Client side
		if (trade == null)
			return;


		VillagerTradeAccessor tradeAccessor = (VillagerTradeAccessor) trade;

		this.requiredType = requiredType(tradeAccessor);

		if (tradeAccessor.getDoubleTradePriceEnchantments().isPresent())
			this.doubleTradePriceEnchantments = tradeAccessor.getDoubleTradePriceEnchantments().get();

        ItemStack wants = this.costAsStack(tradeAccessor.getWants());
        ItemStack additionalWants = tradeAccessor.getAdditionalWants().map(this::costAsStack).orElse(ItemStack.EMPTY);

		this.villagerXp = getMinMax(tradeAccessor.getXp()).max();
		this.maxUses = getMinMax(tradeAccessor.getMaxUses()).max();

		ItemStack offerStack = tradeAccessor.getGives().create();

		this.subTradeGroups.add(new SubTradeGroup(List.of(wants), additionalWants.isEmpty() ? List.of() : List.of(additionalWants), List.of(offerStack), new CompoundTag()));

		for (LootItemFunction modifier : tradeAccessor.getGivenItemModifiers()) {
			GivenItemFunctionProcessor<?> processor = FUNCTION_PROCESSORS.getOrDefault(modifier.getClass(), null);

			if (processor != null) {
				List<SubTradeGroup> newGroups = new ArrayList<>();
				for (SubTradeGroup group : this.subTradeGroups) {
					newGroups.addAll(processor.process(this.cast(modifier), group.cost1().stream().map(ItemStack::copy).toList(), group.cost2().stream().map(ItemStack::copy).toList(), group.offerStacks().stream().map(ItemStack::copy).toList(), this.doubleTradePriceEnchantments, group.extraData().copy()));
				}

				this.subTradeGroups = newGroups;
			}

		}

		this.modifiers = tradeAccessor.getGivenItemModifiers();
	}

	private <T extends LootItemFunction> T cast(LootItemFunction function) {
		return (T) function;
	}

	private ItemStack costAsStack(TradeCost cost) {
		ItemStack stack = new ItemStack(cost.item());
		MinMaxValue minMax = getMinMax(cost.count());

		stack.setCount(Math.max(minMax.min() + (minMax.max() - minMax.min()) / 2, 1));

		if (minMax.max() > minMax.min())
			stack.update(DataComponents.LORE, ItemLore.EMPTY, itemLore -> itemLore.withLineAdded(Component.literal(minMax.min() + " - " + minMax.max()).withStyle(ChatFormatting.DARK_GRAY)));

		return stack;
	}

	public ResourceKey<VillagerType> requiredType(VillagerTradeAccessor tradeAccessor) {
		ResourceKey<VillagerType> villagerTypeHolder = VillagerType.PLAINS;
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


	@Override
	public void writeToTag(CompoundTag tag) {

		tag.store("profession", ResourceKey.codec(Registries.VILLAGER_PROFESSION), this.profession);
		tag.putInt("professionLevel", this.professionLevel);

		if(this.requiredType != null)
			tag.putString("requiredType", this.requiredType.identifier().toString());

		ListTag subTrades = new ListTag();
		this.subTradeGroups.forEach(group -> {
			subTrades.add(SubTradeGroup.CODEC.encode(group, ServerRecipeManager.INSTANCE.createSerializationContext(), new CompoundTag()).getOrThrow());
		});

		tag.put("subTrades", subTrades);

		ListTag modifiersTag = new ListTag();
		this.modifiers.forEach(modifier -> {
			modifiersTag.add(LootItemFunctions.ROOT_CODEC.encode(modifier, ServerRecipeManager.INSTANCE.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).getOrThrow());
		});
		if (this.id != null) {
			tag.store("id", Identifier.CODEC, id);
		}

		tag.put("modifiers", modifiersTag);

	}


	@Override
	public void loadFromTag(CompoundTag tag) {
		if (tag.contains("profession"))
			this.profession = tag.read("profession", ResourceKey.codec(Registries.VILLAGER_PROFESSION)).orElseThrow();

		this.professionLevel = tag.getIntOr("professionLevel", 0);
		if (tag.contains("requiredType"))
			this.requiredType = BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(tag.getString("requiredType").orElseThrow())).orElseThrow().key();

		this.subTradeGroups.clear();
		tag.getListOrEmpty("subTrades").forEach(subTrade -> {
			this.subTradeGroups.add(SubTradeGroup.CODEC.parse(ClientRecipeManager.INSTANCE.createSerializationContext(), subTrade).getOrThrow());
		});

		this.modifiers.clear();
		tag.getListOrEmpty("modifiers").forEach(modifier -> {
			this.modifiers.add(LootItemFunctions.ROOT_CODEC.parse(ClientRecipeManager.INSTANCE.createSerializationContext(), modifier).getOrThrow());
		});


		for (LootItemFunction modifier : this.modifiers) {

			SubTradePostProcessor<?> processor = POST_PROCESSORS.getOrDefault(modifier.getClass(), null);
			if (processor == null)
				continue;

			List<SubTradeGroup> newGroups = new ArrayList<>();
			for (SubTradeGroup group : this.subTradeGroups) {
				newGroups.addAll(processor.postProcess(this.cast(modifier), group));
			}
			this.subTradeGroups = newGroups;

		}

		this.id = tag.read("id", Identifier.CODEC).orElse(null);
	}

	public List<VillagerOffer> getClientOffers() {
		List<VillagerOffer> offers = new ArrayList<>();

		this.subTradeGroups.forEach(group -> {
			offers.add(new VillagerOffer(
					this.id,
					this.profession,
					this.professionLevel,
					this.requiredType,
					group.offerStacks(),
					group.cost1(),
					group.cost2(),
					this.villagerXp,
					this.maxUses));
		});

		return offers;
	}

	@Override
	public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
		return TYPE;
	}


	public static void registerDefaultProcessors() {

		VillagerServerRecipe.registerFunctionProcessor(SetNameFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {
			SetNameFunctionAccessor accessor = (SetNameFunctionAccessor) function;

			if (accessor.getName().isPresent())
				offerStacks.forEach(stack -> {
					stack.set(DataComponents.ITEM_NAME, accessor.getName().get());
				});

			return List.of(new SubTradeGroup(cost1, cost2, offerStacks, extraData));
		});

		VillagerServerRecipe.registerFunctionProcessor(ExplorationMapFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {
			ExplorationMapFunctionAccessor accessor = (ExplorationMapFunctionAccessor) function;

			ItemStack stack = new ItemStack(Items.FILLED_MAP);
			MapItemSavedData.addTargetDecoration(stack, BlockPos.ZERO, "+", accessor.getDecorationType());

			return List.of(new SubTradeGroup(cost1, cost2, List.of(stack), extraData));
		});

		VillagerServerRecipe.registerFunctionProcessor(SetPotionFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {
			SetPotionFunctionAccessor accessor = (SetPotionFunctionAccessor) function;

			offerStacks.forEach(stack -> {
				stack.update(DataComponents.POTION_CONTENTS, PotionContents.EMPTY, accessor.getPotion(), PotionContents::withPotion);
			});

			return List.of(new SubTradeGroup(cost1, cost2, offerStacks, extraData));
		});

		VillagerServerRecipe.registerFunctionProcessor(SetRandomPotionFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {

			SetRandomPotionFunctionAccessor accessor = (SetRandomPotionFunctionAccessor) function;

			if (accessor.getOptions().isEmpty())
				return List.of(new SubTradeGroup(cost1, cost2, offerStacks, extraData));


			List<ItemStack> newOfferStacks = new ArrayList<>();

			offerStacks.forEach(stack -> {
				accessor.getOptions().get().forEach(potion -> {

							ItemStack newStack = stack.copy();
							newStack.update(DataComponents.POTION_CONTENTS, PotionContents.EMPTY, potion, PotionContents::withPotion);
							newOfferStacks.add(newStack);

						}
				);
			});

			offerStacks = newOfferStacks;

			return List.of(new SubTradeGroup(cost1, cost2, offerStacks, extraData));
		});

		VillagerServerRecipe.registerFunctionProcessor(SetStewEffectFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {

			CompoundTag stewTag = LootItemFunctions.ROOT_CODEC.encode(function, ServerRecipeManager.INSTANCE.getServer().registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).getOrThrow().asCompound().orElseGet(CompoundTag::new);
			ListTag effects = stewTag.getListOrEmpty("effects");

			List<ItemStack> newOfferStacks = new ArrayList<>();

			offerStacks.forEach(stack -> {
				effects.forEach(tag -> {

					CompoundTag effectTag = tag.asCompound().orElseGet(CompoundTag::new);

					ItemStack copied = stack.copy();
					SuspiciousStewEffects.Entry suspiciousStewEffects = new SuspiciousStewEffects.Entry(ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.MOB_EFFECT).get(Identifier.parse(effectTag.getStringOr("type", ""))).orElseThrow(), effectTag.getIntOr("duration", 1));
					copied.update(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY, suspiciousStewEffects, SuspiciousStewEffects::withEffectAdded);

					newOfferStacks.add(copied);
				});
			});

			offerStacks = newOfferStacks;

			return List.of(new SubTradeGroup(cost1, cost2, offerStacks, extraData));
		});

		VillagerServerRecipe.registerFunctionProcessor(EnchantWithLevelsFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {
			EnchantWithLevelsFunctionAccessor accessor = (EnchantWithLevelsFunctionAccessor) function;
			if (accessor.options().isEmpty())
				return List.of(new SubTradeGroup(cost1, cost2, offerStacks, extraData));

			MinMaxValue minMaxLevel = getMinMax(accessor.getLevels());
			int level = minMaxLevel.getAverage();

			List<ItemStack> newOfferStacks = new ArrayList<>();
			List<ItemStack> newCost1 = new ArrayList<>();

			offerStacks.forEach(stack -> {
				List<EnchantmentInstance> enchantments = EnchantmentHelper.getAvailableEnchantmentResults(level, stack, accessor.options().get().stream());

				ItemStack cost1Stack = offerStacks.indexOf(stack) < cost1.size() ? cost1.get(offerStacks.indexOf(stack)) : cost1.getFirst();

				enchantments.forEach(enchantmentInstance -> {
					ItemStack copied = stack.copy();
					ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
					mutable.set(enchantmentInstance.enchantment(), enchantmentInstance.level());
					EnchantmentHelper.setEnchantments(copied, mutable.toImmutable());
					newOfferStacks.add(copied);

				});

				if (accessor.includeAdditionalCostComponent()) {

					if (doubleTradePriceEnchantments != null && enchantments.stream().anyMatch(enchantmentInstance -> doubleTradePriceEnchantments.contains(enchantmentInstance.enchantment())))
						cost1Stack.setCount(cost1Stack.getCount() + level * 2);
					else
						cost1Stack.setCount(cost1Stack.getCount() + level);

					newCost1.add(cost1Stack);
				}

			});


			return List.of(new SubTradeGroup(newCost1, cost2, newOfferStacks, extraData));

		});

		VillagerServerRecipe.registerFunctionProcessor(SetEnchantmentsFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {

			SetEnchantmentsFunctionAccessor accessor = (SetEnchantmentsFunctionAccessor) function;

			offerStacks.forEach(stack -> {
				EnchantmentHelper.updateEnchantments(
						stack,
						enchantments -> {
							if (accessor.add()) {
								accessor.enchantments()
										.forEach(
												(enchantment, levelProvider) -> enchantments.set(enchantment, Mth.clamp(enchantments.getLevel(enchantment) + getMinMax(levelProvider).getAverage(), 0, 255))
										);
							} else {
								accessor.enchantments().forEach((enchantment, levelProvider) -> enchantments.set(enchantment, Mth.clamp(getMinMax(levelProvider).min(), 0, 255)));
							}
						}
				);
			});

			return List.of(new SubTradeGroup(cost1, cost2, offerStacks, extraData));
		});

		VillagerServerRecipe.registerFunctionProcessor(EnchantRandomlyFunction.class, (function, cost1, cost2, offerStacks, doubleTradePriceEnchantments, extraData) -> {
			EnchantRandomlyFunctionAccessor accessor = (EnchantRandomlyFunctionAccessor) function;


			List<SubTradeGroup> subGroups = new ArrayList<>();

			offerStacks.forEach(stack -> {

				ItemStack cost1Stack = offerStacks.indexOf(stack) < cost1.size() ? cost1.get(offerStacks.indexOf(stack)) : cost1.getFirst();

				boolean targetIsBook = stack.is(Items.BOOK);
				boolean shouldCheckCompatibility = !targetIsBook && accessor.onlyCompatible();
				Stream<Holder<Enchantment>> compatibleEnchantmentsStream = (accessor.options()
						.map(HolderSet::stream)
						.orElseGet(() -> ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Function.identity())))
						.filter(candidate -> !shouldCheckCompatibility || candidate.value().canEnchant(stack));
				List<Holder<Enchantment>> compatibleEnchantments = compatibleEnchantmentsStream.toList();


				compatibleEnchantments.forEach(enchantment -> {

					List<ItemStack> enchantmentStacks = new ArrayList<>();
					List<ItemStack> enchantmentCostStacks = new ArrayList<>();

					for (int i = 1; i <= enchantment.value().getMaxLevel(); i++) {
						ItemStack enchantmentStack = stack.copy();
						ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
						mutable.set(enchantment, i);
						EnchantmentHelper.setEnchantments(enchantmentStack, mutable.toImmutable());
						enchantmentStacks.add(enchantmentStack);

						int maxCost = 6 + 13 * i;
						int minCost = 2 + 3 * i;

						if (doubleTradePriceEnchantments != null && doubleTradePriceEnchantments.contains(enchantment)) {
							maxCost *= 2;
							minCost *= 2;
						}

						Component costComp = Component.literal(minCost + " - " + maxCost).withStyle(ChatFormatting.DARK_GRAY);

						int averageCost = minCost + (maxCost - minCost) / 2;

						if (accessor.includeAdditionalCostComponent()) {
							ItemStack costStack = cost1Stack.copy();

							costStack.setCount(costStack.getCount() + averageCost);
							costStack.update(DataComponents.LORE, ItemLore.EMPTY, itemLore -> itemLore.withLineAdded(costComp));
							enchantmentCostStacks.add(costStack);
						}
					}

					subGroups.add(new SubTradeGroup(enchantmentCostStacks, cost2, enchantmentStacks, extraData));
				});
			});

			return subGroups;
		});

		VillagerServerRecipe.registerFunctionPostProcessor(SetRandomDyesFunction.class, (function, group) -> {


			List<ItemStack> newOfferStacks = new ArrayList<>();
			group.offerStacks().forEach(stack -> {

				DyeColor.VALUES.forEach(dyeColor -> {
					newOfferStacks.add(DyedItemColor.applyDyes(stack.copy(), List.of(dyeColor)));
				});

			});

			return List.of(new SubTradeGroup(group.cost1(), group.cost2(), newOfferStacks, group.extraData()));
		});

	}


	public record VillagerOffer(Identifier id, ResourceKey<VillagerProfession> profession, int professionLevel,
	                            @Nullable ResourceKey<VillagerType> requiredType, List<ItemStack> offerStacks,
	                            List<ItemStack> cost1, List<ItemStack> cost2, int villagerXp, int maxUses) {

	}


	public static MinMaxValue getMinMax(NumberProvider provider) {

		if (provider instanceof ConstantValue(float value))
			return new MinMaxValue((int) value, (int) value);

		if (provider instanceof BinomialDistributionGenerator(NumberProvider n, NumberProvider p))
			return new MinMaxValue(0, getMinMax(n).max());

		if (provider instanceof UniformGenerator(NumberProvider min, NumberProvider max))
			return new MinMaxValue(getMinMax(min).min(), getMinMax(max).max());

		if (provider instanceof Sum(List<NumberProvider> summands)) {
			int min = 0;
			int max = 0;
			for (NumberProvider numberProvider : summands) {
				MinMaxValue minMaxValue = getMinMax(numberProvider);
				min += minMaxValue.min();
				max += minMaxValue.max();
			}

			return new MinMaxValue(min, max);

		}

		return new MinMaxValue(0, 0);
	}


	public record MinMaxValue(int min, int max) {

		public int getAverage() {
			return this.min + (this.min + this.max) / 2;
		}

	}

	public interface GivenItemFunctionProcessor<T extends LootItemFunction> {

		List<SubTradeGroup> process(T function, List<ItemStack> cost1, List<ItemStack> cost2, List<ItemStack> offerStacks, HolderSet<Enchantment> doubleTradePriceEnchantments, CompoundTag extraData);

	}

	public interface SubTradePostProcessor<T extends LootItemFunction> {

		List<SubTradeGroup> postProcess(T function, SubTradeGroup group);
	}

	public record SubTradeGroup(List<ItemStack> cost1, List<ItemStack> cost2, List<ItemStack> offerStacks,
	                            CompoundTag extraData) {


		public static final Codec<SubTradeGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStack.CODEC.listOf().fieldOf("cost1").forGetter(SubTradeGroup::cost1),
				ItemStack.CODEC.listOf().fieldOf("cost2").forGetter(SubTradeGroup::cost2),
				ItemStack.CODEC.listOf().fieldOf("offerStacks").forGetter(SubTradeGroup::offerStacks),
				CompoundTag.CODEC.fieldOf("extraData").forGetter(SubTradeGroup::extraData)
		).apply(instance, SubTradeGroup::new));
	}

}