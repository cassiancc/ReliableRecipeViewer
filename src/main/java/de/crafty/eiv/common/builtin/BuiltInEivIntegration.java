package de.crafty.eiv.common.builtin;

import com.mojang.datafixers.util.Either;
import de.crafty.eiv.common.api.IExtendedItemViewIntegration;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.builtin.blasting.BlastingServerRecipe;
import de.crafty.eiv.common.builtin.brewing.BrewingServerRecipe;
import de.crafty.eiv.common.builtin.brewing.BrewingViewRecipe;
import de.crafty.eiv.common.builtin.burning.BurningServerRecipe;
import de.crafty.eiv.common.builtin.burning.BurningViewRecipe;
import de.crafty.eiv.common.builtin.campfire.CampfireServerRecipe;
import de.crafty.eiv.common.builtin.entity.EntityServerRecipe;
import de.crafty.eiv.common.builtin.entity.EntityViewRecipe;
import de.crafty.eiv.common.builtin.shaped.ShapedServerRecipe;
import de.crafty.eiv.common.builtin.shapeless.ShapelessServerRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingServerRecipe;
import de.crafty.eiv.common.builtin.smithing.SmithingServerRecipe;
import de.crafty.eiv.common.builtin.smoking.SmokingServerRecipe;
import de.crafty.eiv.common.builtin.stonecutting.StonecutterServerRecipe;
import de.crafty.eiv.common.builtin.tipped_arrow.TippedArrowServerRecipe;
import de.crafty.eiv.common.builtin.transmute.TransmuteServerRecipe;
import de.crafty.eiv.common.builtin.villager.VillagerServerRecipe;
import de.crafty.eiv.common.builtin.villager.VillagerViewRecipe;
import de.crafty.eiv.common.mixin.world.item.alchemy.PotionBrewingAccessor;
import de.crafty.eiv.common.mixin.world.item.crafting.IngredientAccessor;
import de.crafty.eiv.common.mixin.world.item.crafting.TransmuteRecipeAccessor;
import de.crafty.eiv.common.mixin.world.level.storage.loot.LootPoolAccessor;
import de.crafty.eiv.common.mixin.world.level.storage.loot.LootTableAccessor;
import de.crafty.eiv.common.mixin.world.level.storage.loot.entries.CompositeEntryBaseAccessor;
import de.crafty.eiv.common.mixin.world.level.storage.loot.entries.LootItemAccessor;
import de.crafty.eiv.common.mixin.world.level.storage.loot.entries.LootPoolSingletonContainerAccessor;
import de.crafty.eiv.common.mixin.world.level.storage.loot.functions.SetPotionFunctionAccessor;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import de.crafty.eiv.common.builtin.blasting.BlastingViewRecipe;
import de.crafty.eiv.common.builtin.campfire.CampfireViewRecipe;
import de.crafty.eiv.common.builtin.shaped.CraftingViewRecipe;
import de.crafty.eiv.common.builtin.shapeless.ShapelessViewRecipe;
import de.crafty.eiv.common.builtin.smelting.SmeltingViewRecipe;
import de.crafty.eiv.common.builtin.smithing.SmithingViewRecipe;
import de.crafty.eiv.common.builtin.smoking.SmokingViewRecipe;
import de.crafty.eiv.common.builtin.stonecutting.StonecutterViewRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
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
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;

import java.util.*;

import static de.crafty.eiv.common.CommonEIV.*;

public class BuiltInEivIntegration implements IExtendedItemViewIntegration {

    public static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(MODID, "textures/gui/eiv_widgets.png");

    //Default slot rendering
    public static final Identifier DEFAULT_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(MODID, "textures/gui/default_slot.png");

    private static final TagKey<Item> EXCLUDED_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
    private static final TagKey<Block> EXCLUDED_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));

    @Override
    public void onIntegrationInitialize() {


        ItemView.addClientReloadCallback(() -> {

            BuiltInRegistries.BLOCK.get(EXCLUDED_BLOCKS).ifPresent(blocks -> blocks.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(block -> ItemView.excludeItem(block.asItem())));
            BuiltInRegistries.ITEM.get(EXCLUDED_ITEMS).ifPresent(items -> items.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(ItemView::excludeItem));

        });

        ItemView.addReloadCallback(() -> {

            Registry<Potion> potionRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);

            potionRegistry.forEach(potion -> {
                ItemView.addStackSensitive(PotionContents.createItemStack(Items.POTION, potionRegistry.wrapAsHolder(potion)));
                ItemView.addStackSensitive(PotionContents.createItemStack(Items.SPLASH_POTION, potionRegistry.wrapAsHolder(potion)));
                ItemView.addStackSensitive(PotionContents.createItemStack(Items.LINGERING_POTION, potionRegistry.wrapAsHolder(potion)));

                if (ServerRecipeManager.INSTANCE.getServer().potionBrewing().isBrewablePotion(potionRegistry.wrapAsHolder(potion))) {
                    ItemStack tipped = new ItemStack(Items.TIPPED_ARROW);
                    tipped.set(DataComponents.POTION_CONTENTS, new PotionContents(potionRegistry.wrapAsHolder(potion)));
                    ItemView.addStackSensitive(tipped);
                }
            });


            Registry<Enchantment> enchantmentRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            enchantmentRegistry.forEach(enchantment -> {
                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {

                    ItemStack enchantedBook = EnchantmentHelper.createBook(new EnchantmentInstance(enchantmentRegistry.wrapAsHolder(enchantment), i));
                    ItemView.addStackSensitive(enchantedBook);

                }
            });

        });

        //providers

        ItemView.addRecipeProvider(recipeList -> {

            BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
                if (entityType.getDefaultLootTable().isEmpty())
                    return;

                LootTable table = ServerRecipeManager.INSTANCE.getServer().reloadableRegistries().getLootTable(entityType.getDefaultLootTable().get());
                LootTableAccessor accessor = (LootTableAccessor) table;

                List<ItemStack> loot = new ArrayList<>();

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
                                    stack.set(DataComponents.LORE, stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).withLineAdded(Component.translatable("view.eiv.type.entity.playerKill").withStyle(ChatFormatting.RED)));
                            }

                            loot.add(stack);
                        }
                        if (container instanceof CompositeEntryBase entryBase) {
                            CompositeEntryBaseAccessor entryBaseAccessor = (CompositeEntryBaseAccessor) entryBase;
                            entryBaseAccessor.getChildren().forEach(child -> {
                                if (child instanceof LootItem lootItem) {
                                    LootItemAccessor lootItemAccessor = (LootItemAccessor) lootItem;
                                    loot.add(new ItemStack(lootItemAccessor.getItem()));
                                }
                            });
                        }
                    }
                }

                if (entityType == EntityType.WITHER)
                    loot.add(new ItemStack(Items.NETHER_STAR));

                if (!loot.isEmpty())
                    recipeList.add(new EntityServerRecipe(entityType, loot));
            });

        });

        //Burning
        ItemView.addRecipeProvider(recipeList -> {
            FuelValues fuelValues = ServerRecipeManager.INSTANCE.getServer().fuelValues();
            fuelValues.fuelItems().forEach(item -> {
                recipeList.add(new BurningServerRecipe(item, fuelValues.burnDuration(new ItemStack(item))));
            });

        });

        //Smelting
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMELTING).forEach(recipe -> {
                recipeList.add(new SmeltingServerRecipe(recipe.input(), recipe.result));
            });
        });

        //Blasting
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.BLASTING).forEach(recipe -> {
                recipeList.add(new BlastingServerRecipe(recipe.input(), recipe.result));
            });
        });

        //Smoking
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMOKING).forEach(recipe -> {
                recipeList.add(new SmokingServerRecipe(recipe.input(), recipe.result));
            });
        });

        //Crafting
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.CRAFTING).forEach(recipe -> {
                if (recipe instanceof ShapelessRecipe shapelessRecipe)
                    recipeList.add(new ShapelessServerRecipe(shapelessRecipe.ingredients, shapelessRecipe.result));


                if (recipe instanceof ShapedRecipe shapedRecipe) {

                    HashMap<Integer, Ingredient> ingredients = new HashMap<>();

                    int i = 0;
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {

                            if (x >= shapedRecipe.getWidth() || y >= shapedRecipe.getHeight()) {
                                continue;
                            }

                            if (shapedRecipe.getIngredients().get(i).isPresent())
                                ingredients.put(x + y * 3, shapedRecipe.getIngredients().get(i).get());

                            i++;
                        }
                    }

                    recipeList.add(new ShapedServerRecipe(shapedRecipe.getWidth(), shapedRecipe.getHeight(), ingredients, shapedRecipe.result));
                }

                if (recipe instanceof TransmuteRecipe) {
                    TransmuteRecipeAccessor accessor = (TransmuteRecipeAccessor) recipe;

                    List<ItemStack> results = new ArrayList<>();

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
                        results.add(accessor.getResult().apply(new ItemStack(ingredient)));
                    });

                    if (!ingredients.isEmpty() && !results.isEmpty())
                        recipeList.add(new TransmuteServerRecipe(accessor.getInput(), accessor.getMaterial(), results));

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
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.CAMPFIRE_COOKING).forEach(campfireCookingRecipe -> {
                recipeList.add(new CampfireServerRecipe(campfireCookingRecipe.input(), campfireCookingRecipe.result));
            });
        });

        //Stonecutting
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.STONECUTTING).forEach(stonecutterRecipe -> {
                recipeList.add(new StonecutterServerRecipe(stonecutterRecipe.input(), stonecutterRecipe.result));
            });
        });

        //Smithing
        ItemView.addRecipeProvider(recipeList -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMITHING).forEach(smithingRecipe -> {

                if (smithingRecipe instanceof SmithingTrimRecipe trimRecipe)
                    recipeList.add(new SmithingServerRecipe(true, trimRecipe.baseIngredient(), trimRecipe.templateIngredient().orElse(null), trimRecipe.additionIngredient().orElse(null), trimRecipe.pattern.value(), null));

                if (smithingRecipe instanceof SmithingTransformRecipe transformRecipe) {
                    recipeList.add(new SmithingServerRecipe(false, transformRecipe.baseIngredient(), transformRecipe.templateIngredient().orElse(null), transformRecipe.additionIngredient().orElse(null), null, transformRecipe.result));
                }

            });
        });

        //Brewing
        ItemView.addRecipeProvider(recipeList -> {

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
        ItemView.addRecipeProvider(recipeList -> {

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
                    });
                });

            });

        });

        //Wrapper
        ItemView.registerRecipeWrapper(BurningServerRecipe.TYPE, unwrapped -> List.of(new BurningViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmeltingServerRecipe.TYPE, unwrapped -> List.of(new SmeltingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(BlastingServerRecipe.TYPE, unwrapped -> List.of(new BlastingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmokingServerRecipe.TYPE, unwrapped -> List.of(new SmokingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(ShapelessServerRecipe.TYPE, unwrapped -> List.of(new ShapelessViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(ShapedServerRecipe.TYPE, unwrapped -> List.of(new CraftingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(TransmuteServerRecipe.TYPE, unwrapped -> List.of(new ShapelessViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(TippedArrowServerRecipe.TYPE, unwrapped -> List.of(new CraftingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(CampfireServerRecipe.TYPE, unwrapped -> List.of(new CampfireViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(StonecutterServerRecipe.TYPE, unwrapped -> List.of(new StonecutterViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(SmithingServerRecipe.TYPE, unwrapped -> {
            List<SmithingViewRecipe> recipes = new ArrayList<>();

            if (unwrapped.getBase() != null && unwrapped.getTemplate() != null) {
                SlotContent.of(unwrapped.getBase()).getValidContents().forEach(baseStack -> {
                    SlotContent.of(unwrapped.getTemplate()).getValidContents().forEach(templateStack -> {
                        recipes.add(new SmithingViewRecipe(unwrapped.isTrim(), unwrapped.getAddition(), Ingredient.of(baseStack.getItem()), Ingredient.of(templateStack.getItem()), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            } else if (unwrapped.getBase() != null && unwrapped.getAddition() != null) {
                SlotContent.of(unwrapped.getBase()).getValidContents().forEach(baseStack -> {
                    SlotContent.of(unwrapped.getAddition()).getValidContents().forEach(additionStack -> {
                        recipes.add(new SmithingViewRecipe(unwrapped.isTrim(), Ingredient.of(additionStack.getItem()), Ingredient.of(baseStack.getItem()), unwrapped.getTemplate(), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            } else if (unwrapped.getAddition() != null && unwrapped.getTemplate() != null) {
                SlotContent.of(unwrapped.getTemplate()).getValidContents().forEach(templateStack -> {
                    SlotContent.of(unwrapped.getAddition()).getValidContents().forEach(additionStack -> {
                        recipes.add(new SmithingViewRecipe(unwrapped.isTrim(), Ingredient.of(additionStack.getItem()), unwrapped.getBase(), Ingredient.of(templateStack.getItem()), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            }


            return recipes;
        });
        ItemView.registerRecipeWrapper(BrewingServerRecipe.TYPE, unwrapped -> List.of(new BrewingViewRecipe(unwrapped)));
        ItemView.registerRecipeWrapper(VillagerServerRecipe.TYPE, unwrapped -> {
            return unwrapped.getOffers().stream().map(VillagerViewRecipe::new).toList();
        });
        ItemView.registerRecipeWrapper(EntityServerRecipe.TYPE, unwrapped -> List.of(new EntityViewRecipe(unwrapped)));
    }


}
