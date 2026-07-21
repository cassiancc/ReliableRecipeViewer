package cc.cassian.rrv.common.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.builtin.burning.BurningClientRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.ShapelessServerRecipe;
import cc.cassian.rrv.common.config.ServerConfigs;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.entity.EntityServerRecipe;
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
import net.minecraft.client.multiplayer.ClientLevel;
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
//? if >26.1 {
/*import net.minecraft.world.entity.EntityTypes;
 *///?}
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static cc.cassian.rrv.common.ReliableRecipeViewer.*;

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
                if (entityType.getDefaultLootTable().isEmpty())
                    return;

                LootTable table = getLootTable(entityType.getDefaultLootTable().get());

                List<SlotContent> loot = new ArrayList<>();

                addLoot(table, loot, null);

                if (entityType.equals(EntityType.CHICKEN)) {
                    addLoot(getLootTable(BuiltInLootTables.CHICKEN_LAY), loot, "view.rrv.type.entity.egg_lay");
                }

                loot.addAll(ItemViewRecipes.MOB_DROPS.get(entityType));

                if (!loot.isEmpty())
                    recipeList.add(new EntityServerRecipe(entityType, loot));
            });

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


        //Trading
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

    private static void addLoot(LootTable lootTable, List<SlotContent> loot, @Nullable String withLore) {
        var accessor = (LootTableAccessor) lootTable;
        for (LootPool pool : accessor.getPools()) {
            LootPoolAccessor lootPoolAccessor = (LootPoolAccessor) pool;

            for (LootPoolEntryContainer container : lootPoolAccessor.entries()) {
                if (container instanceof LootItem lootItem) {
                    LootItemAccessor lootItemAccessor = (LootItemAccessor) lootItem;
                    //? if <26.3
                    LootPoolSingletonContainerAccessor containerAccessor = (LootPoolSingletonContainerAccessor) lootItemAccessor;

                    ItemStack stack = new ItemStack(lootItemAccessor.getItem().value());

                    //FIXME
                    //? if <26.3 {
                    containerAccessor.getFunctions().forEach(function -> {

                        if (function instanceof SetPotionFunction setPotionFunction)
                            stack.set(DataComponents.POTION_CONTENTS, stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).withPotion(((SetPotionFunctionAccessor) setPotionFunction).getPotion()));

                    });
                    //?}

                    List<LootItemCondition> conditions = RrvUtil.getLootItemFunctions(lootPoolAccessor.conditions());
                    for (LootItemCondition condition : conditions) {
                        if (condition instanceof LootItemKilledByPlayerCondition)
                            stack.set(DataComponents.LORE, stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(Component.translatable("view.rrv.type.entity.player_kill").withStyle(ChatFormatting.GRAY)));
                    }

                    loot.add(SlotContent.of(stack));
                }
                if (container instanceof CompositeEntryBase entryBase) {
                    CompositeEntryBaseAccessor entryBaseAccessor = (CompositeEntryBaseAccessor) entryBase;
                    entryBaseAccessor.getChildren().forEach(child -> {
                        if (child instanceof LootItem lootItem) {
                            LootItemAccessor lootItemAccessor = (LootItemAccessor) lootItem;
                            ItemStack stack = new ItemStack(lootItemAccessor.getItem());
                            if (withLore != null)
                                stack.set(DataComponents.LORE, stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(Component.translatable(withLore).withStyle(ChatFormatting.GRAY)));
                            loot.add(SlotContent.of(stack));
                        }
                    });
                }
            }
        }
    }


}
