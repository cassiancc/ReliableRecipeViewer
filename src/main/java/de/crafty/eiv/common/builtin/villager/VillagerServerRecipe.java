package de.crafty.eiv.common.builtin.villager;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.mixin.world.entity.npc.*;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCost;
import net.minecraft.world.item.enchantment.providers.EnchantmentsByCostWithDifficulty;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VillagerServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<VillagerServerRecipe> TYPE = EivRecipeType.register(
            Identifier.withDefaultNamespace("villager_trading"),
            () -> new VillagerServerRecipe(null, 0, null)
    );

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

        tag.putString("profession", this.profession.identifier().toString());
        tag.putInt("professionLevel", this.professionLevel);

        tag.putString("type", this.dataObject.type().id().toString());

        CompoundTag data = new CompoundTag();
        this.dataObject.type().encoder().encode(VillagerServerRecipe.castListing(this.dataObject.listing), data);

        tag.put("data", data);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        if (tag.contains("profession"))
            this.profession = BuiltInRegistries.VILLAGER_PROFESSION.get(Identifier.parse(tag.getString("profession").orElseThrow())).orElseThrow().key();

        this.professionLevel = tag.getIntOr("professionLevel", 0);

        VillagerOfferType<?> type = VillagerOfferType.byId(Identifier.parse(tag.getString("type").orElseThrow()));
        this.clientSideVillagerOffers = type.decoder().decode(this.profession, this.professionLevel, tag.getCompoundOrEmpty("data"));

    }

    //:D
    public static  <T extends VillagerTrades.ItemListing> T castListing(VillagerTrades.ItemListing listing) {
        return (T) listing;
    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }


    public record VillagerDataObject<T extends VillagerTrades.ItemListing>(VillagerOfferType<T> type, T listing) {
    }

    public record VillagerOffer(ResourceKey<VillagerProfession> profession, int professionLevel,
                                @Nullable ResourceKey<VillagerType> requiredtype, List<ItemStack> offerStacks,
                                List<ItemStack> cost1, List<ItemStack> cost2, int villagerXp, int maxUses) {

    }


    public record VillagerOfferType<T extends VillagerTrades.ItemListing>(Identifier id, Encoder<T> encoder,
                                                                          Decoder decoder) {

        private static final HashMap<Identifier, VillagerOfferType<?>> TYPES = new HashMap<>();
        private static final HashMap<Class<?>, Identifier> ID_BY_CLASS = new HashMap<>();

        public static final VillagerOfferType<VillagerTrades.EmeraldForItems> EMERALD_FOR_ITEMS = register(
                Identifier.withDefaultNamespace("emerald_for_items"),
                VillagerTrades.EmeraldForItems.class,
                (listing, out) -> {
                    EmeraldForItemsAccessor accessor = (EmeraldForItemsAccessor) listing;

                    out.put("cost", EivTagUtil.encodeItemStackOnServer(accessor.getItemStack().itemStack()));
                    out.putInt("emeraldCount", accessor.getEmeraldAmount());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                },
                (profession, professionLevel, in) -> {

                    ItemStack cost = EivTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("cost"));
                    int emeraldCount = in.getIntOr("emeraldCount", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, List.of(new ItemStack(Items.EMERALD, emeraldCount)), List.of(cost), List.of(), villagerXp, maxUses));
                }
        );

        public static final VillagerOfferType<VillagerTrades.ItemsForEmeralds> ITEMS_FOR_EMERALDS = register(
                Identifier.withDefaultNamespace("items_for_emeralds"),
                VillagerTrades.ItemsForEmeralds.class,
                (listing, out) -> {

                    ItemsForEmeraldsAccessor accessor = (ItemsForEmeraldsAccessor) listing;

                    List<ItemStack> offerStacks = new ArrayList<>();

                    if (accessor.enchantmentProvider().isPresent()) {
                        EnchantmentProvider provider = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT_PROVIDER).get(accessor.enchantmentProvider().get()).orElseThrow().value();
                        offerStacks.addAll(VillagerServerRecipe.createOfferStacksFromEnchantmentProvider(provider, accessor.itemStack()));
                    } else
                        offerStacks.add(accessor.itemStack().copy());


                    out.put("offers", EivTagUtil.writeList(offerStacks, (origin, tag) -> EivTagUtil.encodeItemStackOnServer(origin)));
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());
                },
                (profession, professionLevel, in) -> {


                    List<ItemStack> offers = EivTagUtil.readList(in, "offers", EivTagUtil::decodeItemStackOnClient);
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offers, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(), villagerXp, maxUses));
                }
        );

        public static final VillagerOfferType<VillagerTrades.SuspiciousStewForEmerald> SUSPICIOUS_STEW = register(
                Identifier.withDefaultNamespace("suspicious_stew"),
                VillagerTrades.SuspiciousStewForEmerald.class,
                (listing, out) -> {
                    SuspiciousStewForEmeraldAccessor accessor = (SuspiciousStewForEmeraldAccessor) listing;
                    ItemStack stewStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
                    stewStack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, accessor.effects());

                    out.put("stew", EivTagUtil.encodeItemStackOnServer(stewStack));
                    out.putInt("emeraldCost", 1);
                    out.putInt("villagerXp", accessor.xp());
                    out.putInt("maxUses", 12);

                },
                (profession, professionLevel, in) -> {

                    ItemStack stew = EivTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("stew"));
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, List.of(stew), List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(), villagerXp, maxUses));
                }
        );

        public static final VillagerOfferType<VillagerTrades.EnchantBookForEmeralds> ENCHANT_BOOK = register(
                Identifier.withDefaultNamespace("enchant_book"),
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
                        offerTag.put("offerStacks", EivTagUtil.writeList(stacks, (origin, tag) -> EivTagUtil.encodeItemStackOnServer(origin)));
                        offerTag.put("costStacks", EivTagUtil.writeList(costStacks, (origin, tag) -> EivTagUtil.encodeItemStackOnServer(origin)));
                        offersTag.put(enchantment.identifier().toDebugFileName(), offerTag);
                    });

                    out.put("offers", offersTag);
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", 12);

                },
                (profession, professionLevel, in) -> {

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    List<VillagerOffer> villagerOffers = new ArrayList<>();

                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    CompoundTag offersTag = in.getCompoundOrEmpty("offers");
                    offersTag.values().stream().map(tag -> tag.asCompound().orElseGet(CompoundTag::new)).forEach(offerTag -> {

                        List<ItemStack> offerStacks = EivTagUtil.readList(offerTag, "offerStacks", EivTagUtil::decodeItemStackOnClient);
                        List<ItemStack> costStacks = EivTagUtil.readList(offerTag, "costStacks", EivTagUtil::decodeItemStackOnClient);

                        VillagerOffer offer = new VillagerOffer(profession, professionLevel, villagerType, offerStacks, costStacks, List.of(new ItemStack(Items.BOOK)), villagerXp, maxUses);
                        villagerOffers.add(offer);
                    });

                    return villagerOffers;
                }
        );


        public static final VillagerOfferType<VillagerTrades.TreasureMapForEmeralds> TREASURE_MAP = register(
                Identifier.withDefaultNamespace("treasure_map"),
                VillagerTrades.TreasureMapForEmeralds.class,
                (listing, out) -> {

                    TreasureMapForEmeraldsAccessor accessor = (TreasureMapForEmeraldsAccessor) listing;

                    out.putString("decoration", accessor.destinationType().unwrapKey().orElseThrow().identifier().toString());
                    out.putString("displayName", accessor.displayName());
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());

                },
                (profession, professionLevel, in) -> {

                    MapDecorationType decorationType = BuiltInRegistries.MAP_DECORATION_TYPE.getOptional(Identifier.parse(in.getString("decoration").orElseThrow())).orElseThrow();
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

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, List.of(offerStack), List.of(costStack1), List.of(costStack2), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.TippedArrowForItemsAndEmeralds> TIPPED_ARROW = register(
                Identifier.withDefaultNamespace("tipped_arrow"),
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

                    out.put("offers", EivTagUtil.writeList(offerStacks, (origin, tag) -> EivTagUtil.encodeItemStackOnServer(origin)));
                    out.putString("fromItem", EivTagUtil.itemToString(accessor.fromItem()));
                    out.putInt("fromCount", accessor.fromCount());
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());

                },
                (profession, professionLevel, in) -> {

                    List<ItemStack> offers = EivTagUtil.readList(in, "offers", EivTagUtil::decodeItemStackOnClient);
                    Item fromItem = EivTagUtil.itemFromString(in.getStringOr("fromItem", ""));
                    int fromCount = in.getIntOr("fromCount", 0);
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offers, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(new ItemStack(fromItem, fromCount)), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.EnchantedItemForEmeralds> ENCHANTED_ITEM_FOR_EMERALDS = register(
                Identifier.withDefaultNamespace("enchanted_item_for_emeralds"),
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


                    out.put("offers", EivTagUtil.writeList(offerStacks, (origin, tag) -> EivTagUtil.encodeItemStackOnServer(origin)));
                    out.put("costStack", EivTagUtil.encodeItemStackOnServer(costStack));
                    out.putInt("villagerXp", accessor.villagerXp());
                    out.putInt("maxUses", accessor.maxUses());
                },
                (profession, professionLevel, in) -> {


                    List<ItemStack> offerStacks = EivTagUtil.readList(in, "offers", EivTagUtil::decodeItemStackOnClient);
                    ItemStack costStack = EivTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("costStack"));
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();


                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offerStacks, List.of(costStack), List.of(), villagerXp, maxUses));

                }
        );


        public static final VillagerOfferType<VillagerTrades.DyedArmorForEmeralds> DYED_ARMOR = register(
                Identifier.withDefaultNamespace("dyed_armor"),
                VillagerTrades.DyedArmorForEmeralds.class,
                (listing, out) -> {
                    DyedArmorForEmeraldsAccessor accessor = (DyedArmorForEmeraldsAccessor) listing;


                    ItemStack offerStack = new ItemStack(accessor.getItem());

                    out.put("offerStack", EivTagUtil.encodeItemStackOnServer(offerStack));

                    out.putInt("emeraldCost", accessor.getValue());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                },
                (profession, professionLevel, in) -> {

                    ItemStack offerStack = EivTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("offerStack"));
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    List<ItemStack> offerStacks = new ArrayList<>();

                    if (offerStack.is(ItemTags.DYEABLE)) {
                        for (DyeColor color1 : DyeColor.values()) {
                            offerStacks.add(DyedItemColor.applyDyes(offerStack.copy(), List.of(DyeItem.byColor(color1))));

                            /*for (DyeColor color2 : DyeColor.values()) {
                                offerStacks.add(DyedItemColor.applyDyes(offerStack.copy(), List.of(DyeItem.byColor(color1), DyeItem.byColor(color2))));


                                for (DyeColor color3 : DyeColor.values()) {
                                    offerStacks.add(DyedItemColor.applyDyes(offerStack.copy(), List.of(DyeItem.byColor(color1), DyeItem.byColor(color2), DyeItem.byColor(color3))));
                                }
                            }*/
                        }
                    } else
                        offerStacks.add(offerStack);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offerStacks, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.ItemsAndEmeraldsToItems> ITEMS_AND_EMERALDS_TO_ITEMS = register(
                Identifier.withDefaultNamespace("items_and_emeralds_to_items"),
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

                    out.put("offerStacks", EivTagUtil.writeList(offerStacks, (origin, tag) -> EivTagUtil.encodeItemStackOnServer(origin)));
                    out.put("costStack", EivTagUtil.encodeItemStackOnServer(costStack));
                    out.putInt("emeraldCost", accessor.emeraldCost());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                },
                (profession, professionLevel, in) -> {

                    List<ItemStack> offerStacks = EivTagUtil.readList(in, "offerStacks", EivTagUtil::decodeItemStackOnClient);
                    ItemStack costStack = EivTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("costStack"));
                    int emeraldCost = in.getIntOr("emeraldCost", 0);
                    int villagerXp = in.getIntOr("villagerXp", 0);
                    int maxUses = in.getIntOr("maxUses", 0);

                    ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                    return List.of(new VillagerOffer(profession, professionLevel, villagerType, offerStacks, List.of(new ItemStack(Items.EMERALD, emeraldCost)), List.of(costStack), villagerXp, maxUses));
                }
        );


        public static final VillagerOfferType<VillagerTrades.EmeraldsForVillagerTypeItem> EMERALDS_FOR_VILLAGER_TYPE = register(
                Identifier.withDefaultNamespace("emeralds_for_villager_type"),
                VillagerTrades.EmeraldsForVillagerTypeItem.class,
                (listing, out) -> {
                    EmeraldsForVillagerTypeItemAccessor accessor = (EmeraldsForVillagerTypeItemAccessor) listing;


                    out.putInt("cost", accessor.getCost());
                    out.putInt("villagerXp", accessor.getVillagerXp());
                    out.putInt("maxUses", accessor.getMaxUses());

                    CompoundTag tradesTag = new CompoundTag();

                    accessor.getTrades().forEach((villagerType, item) -> {
                        tradesTag.putString(villagerType.identifier().toString(), EivTagUtil.itemToString(item));
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
                        ResourceKey<VillagerType> villagerType = BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(s)).orElseThrow().key();
                        Item item = EivTagUtil.itemFromString(tag.asString().orElseThrow());
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
                Identifier.withDefaultNamespace("type_specific"),
                VillagerTrades.TypeSpecificTrade.class,
                (listing, out) -> {

                    listing.trades().forEach((villagerType, itemListing) -> {

                        VillagerOfferType<?> offerType = byClass(itemListing.getClass());
                        if(offerType == null)
                            return;

                        CompoundTag encodedListing = new CompoundTag();
                        offerType.encoder().encode(VillagerServerRecipe.castListing(itemListing), encodedListing);

                        encodedListing.putString("requiredType", villagerType.identifier().toString());
                        encodedListing.putString("listingType", offerType.id().toString());

                        out.put(villagerType.identifier().toString(), encodedListing);
                    });

                },
                (profession, professionLevel, in) -> {

                    List<VillagerOffer> villagerOffers = new ArrayList<>();

                    in.forEach((villagerType, tag) -> {

                        CompoundTag listingTag = tag.asCompound().orElseThrow();

                        Identifier listingId = Identifier.parse(listingTag.getString("listingType").orElseThrow());
                        VillagerOfferType<?> offerType = byId(listingId);

                        villagerOffers.addAll(offerType.decoder().decode(profession, professionLevel, listingTag));
                    });

                    return villagerOffers;
                }
        );

        public static <T extends VillagerTrades.ItemListing> VillagerOfferType<T> register(Identifier id, Class<T> clazz, Encoder<T> encoder, Decoder decoder) {
            VillagerOfferType<T> type = new VillagerOfferType<>(id, encoder, decoder);
            TYPES.put(id, type);
            ID_BY_CLASS.put(clazz, id);
            return type;
        }

        public static <T extends VillagerTrades.ItemListing> VillagerOfferType<T> byId(Identifier id) {
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

}
