package cc.cassian.rrv.common.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningServerRecipe;
import cc.cassian.rrv.common.builtin.anvil.ResourceDrivenAnvilCombiningServerRecipe;
import cc.cassian.rrv.common.builtin.info.InfoServerRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionServerRecipe;
import cc.cassian.rrv.common.builtin.tag.item.ItemTagServerRecipe;
import cc.cassian.rrv.common.builtin.tag.block.BlockTagServerRecipe;
import cc.cassian.rrv.common.mixin.world.item.crafting.DyeRecipeAccessor;
import com.mojang.datafixers.util.Either;
import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.blasting.BlastingServerRecipe;
import cc.cassian.rrv.common.builtin.brewing.BrewingServerRecipe;
import cc.cassian.rrv.common.builtin.burning.BurningServerRecipe;
import cc.cassian.rrv.common.builtin.campfire.CampfireServerRecipe;
import cc.cassian.rrv.common.builtin.entity.EntityServerRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.ShapedServerRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.ShapelessServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingServerRecipe;
import cc.cassian.rrv.common.builtin.smoking.SmokingServerRecipe;
import cc.cassian.rrv.common.builtin.stonecutting.StonecutterServerRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.TippedArrowServerRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.TransmuteServerRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.common.mixin.world.item.alchemy.PotionBrewingAccessor;
import cc.cassian.rrv.common.mixin.world.item.crafting.IngredientAccessor;
import cc.cassian.rrv.common.mixin.world.item.crafting.TransmuteRecipeAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.LootPoolAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.LootTableAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.CompositeEntryBaseAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.LootItemAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.entries.LootPoolSingletonContainerAccessor;
import cc.cassian.rrv.common.mixin.world.level.storage.loot.functions.SetPotionFunctionAccessor;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;

import java.util.*;

import static cc.cassian.rrv.common.ReliableRecipeViewer.*;

public class BuiltInReliableRecipeViewerIntegration implements ReliableRecipeViewerPlugin {

    public static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/rrv_widgets.png");

    //Default slot rendering
    public static final Identifier DEFAULT_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/default_slot.png");


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

                    if (ServerRecipeManager.INSTANCE.getServer().potionBrewing().isBrewablePotion(potionHolder)) {
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
        });

        //providers


        ItemView.addServerRecipeProvider(recipeList -> {
            recipeList.add(new InfoServerRecipe());
            recipeList.add(new WorldInteractionServerRecipe());
            recipeList.add(new ResourceDrivenAnvilCombiningServerRecipe());

            BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
                if (entityType.getDefaultLootTable().isEmpty())
                    return;

                LootTable table = ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().getLootTable(entityType.getDefaultLootTable().get());
                LootTableAccessor accessor = (LootTableAccessor) table;

                List<SlotContent> loot = new ArrayList<>();

                for (LootPool pool : accessor.getPools()) {
                    LootPoolAccessor lootPoolAccessor = (LootPoolAccessor) pool;

                    for (LootPoolEntryContainer container : lootPoolAccessor.entries()) {
                        if (container instanceof LootItem lootItem) {
                            LootItemAccessor lootItemAccessor = (LootItemAccessor) lootItem;
                            LootPoolSingletonContainerAccessor containerAccessor = (LootPoolSingletonContainerAccessor) lootItemAccessor;

                            ItemStack stack = new ItemStack(lootItemAccessor.getItem().value());

                            containerAccessor.getFunctions().forEach(function -> {

                                if (function instanceof SetPotionFunction setPotionFunction)
                                    stack.set(DataComponents.POTION_CONTENTS, stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).withPotion(((SetPotionFunctionAccessor) setPotionFunction).getPotion()));

                            });

                            for (LootItemCondition condition : lootPoolAccessor.conditions()) {
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
                                    loot.add(SlotContent.of(new ItemStack(lootItemAccessor.getItem())));
                                }
                            });
                        }
                    }
                }

                if (entityType == EntityType.WITHER)
                    loot.add(SlotContent.of(Items.NETHER_STAR));

                if (entityType == EntityType.GOAT) {
                    ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.INSTRUMENT).asHolderIdMap().iterator().forEachRemaining(instrument->{
                        if (instrument.is(InstrumentTags.GOAT_HORNS)) {
                            ItemStack stack = InstrumentItem.create(Items.GOAT_HORN, instrument);
                            stack.set(DataComponents.LORE, stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(Component.translatable("view.rrv.type.entity.goat_horn").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY))));
                            loot.add(SlotContent.of(stack));
                        }
                    });
                }

                if (!loot.isEmpty())
                    recipeList.add(new EntityServerRecipe(entityType, loot));
            });

            BuiltInRegistries.ITEM.listTagIds().forEach((tag) -> {
                Optional<HolderSet.Named<Item>> tagContents = BuiltInRegistries.ITEM.get(tag);
                if (tagContents.isPresent() && !tagContents.get().stream().allMatch(item -> ItemView.getExcludedItems().contains(item.value()))) {
                    recipeList.add(new ItemTagServerRecipe(tag));
                }
            });

            BuiltInRegistries.BLOCK.listTagIds().forEach((tag) -> {
                Optional<HolderSet.Named<Block>> tagContents = BuiltInRegistries.BLOCK.get(tag);
                if (tagContents.isPresent() && !tagContents.get().stream().allMatch(item -> ItemView.getExcludedItems().contains(item.value().asItem()))) {
                    recipeList.add(new BlockTagServerRecipe(tag));
                }
            });

        });

        //Burning
        ItemView.addServerRecipeProvider(recipeList -> {
            FuelValues fuelValues = ServerRecipeManager.INSTANCE.getServer().fuelValues();
            fuelValues.fuelItems().forEach(item -> {
                //? fabric
                recipeList.add(new BurningServerRecipe(item, fuelValues.burnDuration(new ItemStack(item))));
                //? neoforge
                //recipeList.add(new BurningServerRecipe(item, item.getDefaultInstance().getBurnTime(null, fuelValues)));
            });

        });

        //Smelting
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMELTING).forEach(recipe -> {
                recipeList.add(new SmeltingServerRecipe(recipe.input(), recipe.result));
            });
        });

        //Blasting
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.BLASTING).forEach(recipe -> {
                recipeList.add(new BlastingServerRecipe(recipe.input(), recipe.result));
            });
        });

        //Smoking
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMOKING).forEach(recipe -> {
                recipeList.add(new SmokingServerRecipe(recipe.input(), recipe.result));
            });
        });

        //Crafting
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.CRAFTING).forEach(recipe -> {
                if (recipe instanceof ShapelessRecipe shapelessRecipe)
                    recipeList.add(new ShapelessServerRecipe(shapelessRecipe.ingredients, shapelessRecipe.result));


                if (recipe instanceof ShapedRecipe shapedRecipe) {

                    HashMap<Integer, SlotContent> ingredients = new HashMap<>();

                    int i = 0;
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {

                            if (x >= shapedRecipe.getWidth() || y >= shapedRecipe.getHeight()) {
                                continue;
                            }

                            if (shapedRecipe.getIngredients().get(i).isPresent())
                                ingredients.put(x + y * 3, SlotContent.of(shapedRecipe.getIngredients().get(i).get()));

                            i++;
                        }
                    }

                    recipeList.add(new ShapedServerRecipe(shapedRecipe.getWidth(), shapedRecipe.getHeight(), ingredients, SlotContent.of(shapedRecipe.result)));
                }

                if (recipe instanceof TransmuteRecipe) {
                    TransmuteRecipeAccessor accessor = (TransmuteRecipeAccessor) recipe;

                    List<ItemStackTemplate> results = new ArrayList<>();

                    Either<TagKey<Item>, List<Holder<Item>>> ingredientContent = ((IngredientAccessor) (Object) accessor.getInput()).getValues().unwrap();

                    List<Item> ingredients = new ArrayList<>();

                    if (ingredientContent.left().isPresent()) {
                        SlotContent.getItemsFromTag(ingredientContent.left().get()).ifPresent(holders -> {
                            holders.forEach(holder -> ingredients.add(holder.value()));
                        });
                    }
                    if (ingredientContent.right().isPresent())
                        ingredients.addAll(ingredientContent.right().get().stream().map(Holder::value).toList());


                    ingredients.forEach(ingredient -> {
                        results.add(accessor.getResult());
                    });

                    if (!ingredients.isEmpty() && !results.isEmpty())
                        recipeList.add(new TransmuteServerRecipe(accessor.getInput(), accessor.getMaterial(), results));

                }
                if (recipe instanceof DyeRecipe) {
                    DyeRecipeAccessor accessor = (DyeRecipeAccessor) recipe;
                    List<ItemStackTemplate> results = new ArrayList<>();

                    Either<TagKey<Item>, List<Holder<Item>>> ingredientContent = ((IngredientAccessor) (Object) accessor.getTarget()).getValues().unwrap();

                    List<Item> ingredients = new ArrayList<>();
                    if (ingredientContent.left().isPresent()) {
                        SlotContent.getItemsFromTag(ingredientContent.left().get()).ifPresent(holders -> {
                            holders.forEach(holder -> ingredients.add(holder.value()));
                        });
                    }

                    if (ingredientContent.right().isPresent())
                        ingredients.addAll(ingredientContent.right().get().stream().map(Holder::value).toList());
					for (Item ingredient : ingredients) {
						for (DyeColor dyeColor : DyeColor.values()) {
							results.add(ItemStackTemplate.fromNonEmptyStack(DyedItemColor.applyDyes(ingredient.getDefaultInstance(), Collections.singletonList(dyeColor))));
						}
					}
                    recipeList.add(new TransmuteServerRecipe(accessor.getTarget(), accessor.getDye(), results, 1));
                }
            });

            //Tipped arrows
            Registry<Potion> potionRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);
            potionRegistry.forEach(potion -> {
                ItemStack potionStack = PotionContents.createItemStack(Items.LINGERING_POTION, potionRegistry.wrapAsHolder(potion));
                recipeList.add(new TippedArrowServerRecipe(potionStack));
            });
        });

        //Campfire
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.CAMPFIRE_COOKING).forEach(campfireCookingRecipe -> {
                recipeList.add(new CampfireServerRecipe(SlotContent.of(campfireCookingRecipe.input()), SlotContent.of(campfireCookingRecipe.result)));
            });
        });

        //Stonecutting
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.STONECUTTING).forEach(stonecutterRecipe -> {
                recipeList.add(new StonecutterServerRecipe(stonecutterRecipe.input(), stonecutterRecipe.result));
            });
        });

        //Smithing
        ItemView.addServerRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMITHING).forEach(smithingRecipe -> {

                if (smithingRecipe instanceof SmithingTrimRecipe trimRecipe)
                    recipeList.add(new SmithingServerRecipe(true, trimRecipe.baseIngredient(), trimRecipe.templateIngredient().orElse(null), trimRecipe.additionIngredient().orElse(null), trimRecipe.pattern.value(), null));

                if (smithingRecipe instanceof SmithingTransformRecipe transformRecipe) {
                    recipeList.add(new SmithingServerRecipe(false, transformRecipe.baseIngredient(), transformRecipe.templateIngredient().orElse(null), transformRecipe.additionIngredient().orElse(null), null, transformRecipe.result));
                }

            });
        });

        //Brewing
        ItemView.addServerRecipeProvider(recipeList -> {

            PotionBrewing potionBrewing = ServerRecipeManager.INSTANCE.getServer().potionBrewing();
            List<PotionBrewing.Mix<Potion>> potionMixes = ((PotionBrewingAccessor) potionBrewing).getPotionMixes();
            List<PotionBrewing.Mix<Item>> containerMixes = ((PotionBrewingAccessor) potionBrewing).getContainerMixes();

            containerMixes.forEach(itemMix -> {
                recipeList.add(new BrewingServerRecipe(new ItemStack(itemMix.to().value()), itemMix.ingredient(), new ItemStack(itemMix.from().value())));
            });

            potionMixes.forEach(potionMix -> {
                recipeList.add(new BrewingServerRecipe(PotionContents.createItemStack(Items.POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.POTION, potionMix.from())));
                recipeList.add(new BrewingServerRecipe(PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.from())));
                recipeList.add(new BrewingServerRecipe(PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.from())));

            });

        });

        //Trading
        VillagerServerRecipe.registerDefaultProcessors();

        ItemView.addServerRecipeProvider(recipeList -> {
            HolderLookup.RegistryLookup<VillagerProfession> villagerProfessionRegistryLookup = ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.VILLAGER_PROFESSION);
            HolderLookup.RegistryLookup<TradeSet> tradeSetRegistryLookup = ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.TRADE_SET);

            villagerProfessionRegistryLookup.listElements().forEach((professionReference) -> {
                professionReference.value().tradeSetsByLevel().forEach((level, tradeSetKey) -> {
                    var trades = tradeSetRegistryLookup.getOrThrow(tradeSetKey).value().getTrades();
                    trades.forEach(villagerTradeHolder -> {
                        recipeList.add(new VillagerServerRecipe(professionReference.key(), level, villagerTradeHolder.value()));
                    });
                });
            });

        });

        ItemView.addServerRecipeProvider(BuiltInReliableRecipeViewerIntegration::addRepairRecipes);
    }

    private static void addRepairRecipes(List<ReliableServerRecipe> recipeList) {
        BuiltInRegistries.ITEM.forEach(item -> {
            var stack = item.getDefaultInstance();
            if (stack.has(DataComponents.REPAIRABLE)) {
                Repairable repairable = stack.get(DataComponents.REPAIRABLE);
                var damagedStack = stack.copy();
                damagedStack.setDamageValue(stack.getMaxDamage() / 2);
                recipeList.add(new AnvilCombiningServerRecipe(damagedStack, Ingredient.of(repairable.items()), stack));
            }
        });
    }


}
