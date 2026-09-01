package cc.cassian.rrv.common.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.util.MobDropModifyContext;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.ServerConfigs;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.entity.EntityServerRecipe;
//~ if >26 'backport' -> 'common.builtin.villager'
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.LootPoolAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.LootTableAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.CompositeEntryBaseAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.LootItemAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.LootPoolSingletonContainerAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.functions.SetPotionFunctionAccessor;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.entity.EntityType;
//? if >=26.2 {
/*import net.minecraft.world.entity.EntityTypes;
*///?}
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
//? if >1.21.11 {
import net.minecraft.world.item.trading.TradeSet;
//?} else {
/*import net.minecraft.world.entity.npc.villager.VillagerTrades;
*///?}
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
//? if >26.2 {
/*import cc.cassian.rrv.common.builtin.composting.CompostingServerRecipe;
import cc.cassian.rrv.common.builtin.burning.BurningServerRecipe;
import net.minecraft.world.flag.FeatureFlags;
*///?}
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class BuiltInReliableRecipeViewerIntegration implements ReliableRecipeViewerPlugin {

    public static final Identifier WIDGETS = ReliableRecipeViewer.of("textures/gui/rrv_widgets.png");

    //Default slot rendering
    public static final Identifier DEFAULT_SLOT_TEXTURE = ReliableRecipeViewer.of("textures/gui/default_slot.png");

    @Override
    public void onIntegrationInitialize() {

        ItemView.addServerReloadCallback(() -> {
            Registry<Potion> potionRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);

            potionRegistry.forEach(potion -> {
                var potionHolder = potionRegistry.wrapAsHolder(potion);

                if (!ItemView.isExcludedPotion(potionHolder)) {
                    ItemView.addStackSensitive(PotionContents.createItemStack(Items.POTION, potionHolder));
                    ItemView.addStackSensitive(PotionContents.createItemStack(Items.SPLASH_POTION, potionHolder));
                    ItemView.addStackSensitive(PotionContents.createItemStack(Items.LINGERING_POTION, potionHolder));

                    if (isBrewablePotion(potionHolder)) {
                        ItemStack tipped = new ItemStack(Items.TIPPED_ARROW);
                        tipped.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder));
                        ItemView.addStackSensitive(tipped);
                    }
                }
            });


            Registry<Enchantment> enchantmentRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            enchantmentRegistry.entrySet().forEach((entry) -> {
                var key = entry.getKey();
                var enchantment = entry.getValue();
                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {

                    var enchantmentHolder = enchantmentRegistry.wrapAsHolder(enchantment);
                    if (!enchantmentHolder.is(CommonTags.EXCLUDED_ENCHANTMENTS) && !ItemView.getExcludedEnchantments().contains(key)) {
                        ItemStack enchantedBook = EnchantmentHelper.createBook(new EnchantmentInstance(enchantmentHolder, i));
                        ItemView.addStackSensitive(enchantedBook);
                    }
                }
            });

            ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.INSTRUMENT).asHolderIdMap().iterator().forEachRemaining(instrument->{
                if (instrument.is(InstrumentTags.GOAT_HORNS)) {
                    ItemView.addStackSensitive(InstrumentItem.create(Items.GOAT_HORN, instrument));
                    ItemStack stack = InstrumentItem.create(Items.GOAT_HORN, instrument);
                    stack.set(DataComponents.LORE, stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(Component.translatable("view.rrv.type.entity.goat_horn").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY))));
                    ItemView.addMobDrops(EntityType.GOAT, SlotContent.of(stack));
                }
            });
            ItemView.addMobDrops(EntityType.WITHER, SlotContent.of(Items.NETHER_STAR));
            addLoot(EntityType.CHICKEN, getLootTable(BuiltInLootTables.CHICKEN_LAY), "view.rrv.type.entity.egg_lay");

            Registry<PaintingVariant> paintingVariantRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT);
            paintingVariantRegistry.forEach(paintingVariant -> {
                var paintingVariantHolder = paintingVariantRegistry.wrapAsHolder(paintingVariant);
                ItemStack stack = new ItemStack(Items.PAINTING);
                stack.set(DataComponents.PAINTING_VARIANT, paintingVariantHolder);
                ItemView.addStackSensitive(stack);
            });
        });

        //providers


        ItemView.addServerRecipeProvider(recipeList -> {
            BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
                Optional<ResourceKey<LootTable>> defaultLootTable = entityType.getDefaultLootTable();
                if (defaultLootTable.isEmpty())
                    return;

				addLoot(entityType, getLootTable(defaultLootTable.get()), null);

				List<SlotContent> loot = new ArrayList<>(ItemViewRecipes.MOB_DROPS.get(entityType).stream().map(slotContent -> {
                    for (MobDropModifyContext modifiedMobDrop : ItemViewRecipes.MODIFIED_MOB_DROPS.stream().filter((mobDropModifyContext) -> mobDropModifyContext.entityTypePredicate().test(entityType)).toList()) {
                        if (modifiedMobDrop.slotContentPredicate().test(slotContent)) {
                            return modifiedMobDrop.newDrop();
                        }
                    }
                    return slotContent;
                }).filter(content->!content.isEmpty()).toList());

                recipeList.add(new EntityServerRecipe(entityType, loot));
            });
            ItemViewRecipes.MOB_DROPS.clear();
        });
        if (ModCompat.FABRIC_RECIPE_API) {
            for (Identifier serializer : ServerConfigs.SERVER_SETTINGS.getSynchronizedRecipeSerializers()) {
                ServerRecipeManager.INSTANCE.synchronizeRecipeType(BuiltInRegistries.RECIPE_SERIALIZER.getValue(serializer), null);
            }
        }
        //? if neoforge {
        /*for (Identifier type : ServerConfigs.SERVER_SETTINGS.getSynchronizedRecipeTypes()) {
            ServerRecipeManager.INSTANCE.synchronizeRecipeType(null, BuiltInRegistries.RECIPE_TYPE.getValue(type));
        }
        *///?}


        //? <26 {
        /*ItemView.addServerRecipeProvider(recipeList -> {

            VillagerTrades.TRADES.forEach((profession, byProfessionLevel) -> {

                byProfessionLevel.forEach((professionLevel, itemListings) -> {
                    Arrays.stream(itemListings).toList().forEach(listing -> {

                        if (listing instanceof VillagerTrades.EmeraldForItems emeraldForItems)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.EMERALD_FOR_ITEMS, emeraldForItems)));

                        if (listing instanceof VillagerTrades.ItemsForEmeralds itemsForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ITEMS_FOR_EMERALDS, itemsForEmeralds)));

                        if (listing instanceof VillagerTrades.SuspiciousStewForEmerald suspiciousStewForEmerald)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.SUSPICIOUS_STEW, suspiciousStewForEmerald)));

                        if (listing instanceof VillagerTrades.EnchantBookForEmeralds enchantBookForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ENCHANT_BOOK, enchantBookForEmeralds)));

                        if (listing instanceof VillagerTrades.TreasureMapForEmeralds treasureMapForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.TREASURE_MAP, treasureMapForEmeralds)));

                        if (listing instanceof VillagerTrades.TippedArrowForItemsAndEmeralds tippedArrowForItemsAndEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.TIPPED_ARROW, tippedArrowForItemsAndEmeralds)));

                        if (listing instanceof VillagerTrades.EnchantedItemForEmeralds enchantedItemForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ENCHANTED_ITEM_FOR_EMERALDS, enchantedItemForEmeralds)));

                        if (listing instanceof VillagerTrades.DyedArmorForEmeralds dyedArmorForEmeralds)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.DYED_ARMOR, dyedArmorForEmeralds)));

                        if (listing instanceof VillagerTrades.ItemsAndEmeraldsToItems itemsAndEmeraldsToItems)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.ITEMS_AND_EMERALDS_TO_ITEMS, itemsAndEmeraldsToItems)));

                        if (listing instanceof VillagerTrades.EmeraldsForVillagerTypeItem emeraldsForVillagerTypeItem)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.EMERALDS_FOR_VILLAGER_TYPE, emeraldsForVillagerTypeItem)));

                        if (listing instanceof VillagerTrades.TypeSpecificTrade typeSpecificTrade)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(VillagerServerRecipe.VillagerOfferType.TYPE_SPECIFIC, typeSpecificTrade)));

                        //? neoforge {
                        /^if (listing instanceof net.neoforged.neoforge.common.BasicItemListing basicItemListing)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(cc.cassian.rrv.backport.BackportNeoForgeUtil.NEOFORGE_BASIC, basicItemListing)));
                        ^///?}
                    });
                });

            });

        });
        *///?} else {
        VillagerServerRecipe.registerDefaultProcessors();
        ItemView.addServerRecipeProvider(recipeList -> {
            HolderLookup.RegistryLookup<VillagerProfession> villagerProfessionRegistryLookup = ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.VILLAGER_PROFESSION);
            HolderLookup.RegistryLookup<TradeSet> tradeSetRegistryLookup = ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.TRADE_SET);

            villagerProfessionRegistryLookup.listElements().forEach((professionReference) -> {
                professionReference.value().tradeSetsByLevel().forEach((level, tradeSetKey) -> {
                    //~ if >26.2 '.getTrades'->'.trades' {
                    var trades = tradeSetRegistryLookup.getOrThrow(tradeSetKey).value().getTrades();
                    //~}
                    trades.forEach(villagerTradeHolder -> {
                        recipeList.add(new VillagerServerRecipe(professionReference.key(), level, villagerTradeHolder.value(), villagerTradeHolder.unwrapKey().map(ResourceKey::identifier).orElse(null)));
                    });
                });
            });

        });
        //?}

        //? if >26.2 {
        /*ItemView.addServerRecipeProvider(recipeList -> {
            for (Item item : BuiltInRegistries.ITEM) {
                ItemStack stack = item.getDefaultInstance();
                if (stack.has(DataComponents.COOKING_FUEL)) {
                    var burnTime = Objects.requireNonNull(stack.get(DataComponents.COOKING_FUEL)).burnTime();
                    var providedValue = RrvUtil.getNumberProvidedInt(burnTime);
                    if (providedValue != null) {
                        recipeList.add(new BurningServerRecipe(item, providedValue));
                    }
                }
                if (stack.has(DataComponents.COMPOSTABLE)) {
                    var layers = Objects.requireNonNull(stack.get(DataComponents.COMPOSTABLE)).layers();
                    var providedValue = RrvUtil.getNumberProvidedInt(layers);
                    if (providedValue != null) {
                        recipeList.add(new CompostingServerRecipe(item, providedValue));
                    }
                }
            }
        });
        *///?}
    }

	private boolean isBrewablePotion(Holder<Potion> potionHolder) {
        //? if >26.2 {
        /*return ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION).listElements().anyMatch((potion) -> potion.value().isEnabled(FeatureFlags.VANILLA_SET) && potion.value().equals(potionHolder.value()));
        *///?} else {
        return ServerRecipeManager.INSTANCE.getServer().potionBrewing().isBrewablePotion(potionHolder);
        //?}
	}

    private static LootTable getLootTable(ResourceKey<LootTable> key) {
        return ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().getLootTable(key);
    }

    private static void addLoot(EntityType<?> entityType, LootTable lootTable, @Nullable String withLore) {
        for (ItemStack itemStack : RrvUtil.getLoot(lootTable, withLore)) {
            ItemView.addMobDrops(entityType, SlotContent.of(itemStack));
        }
    }

}
