package cc.cassian.rrv.client.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
//? if >26.2 {
/*import cc.cassian.rrv.common.builtin.burning.BurningServerRecipe;
import cc.cassian.rrv.common.builtin.composting.CompostingServerRecipe;
*///?} else {
import cc.cassian.rrv.common.mixin.world.item.alchemy.PotionBrewingAccessor;
import net.minecraft.world.entity.npc.villager.Villager;
//?}
import cc.cassian.rrv.common.builtin.composting.CompostingClientRecipe;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStackTemplate;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.builtin.blasting.BlastingClientRecipe;
import cc.cassian.rrv.common.builtin.brewing.BrewingClientRecipe;
import cc.cassian.rrv.common.builtin.burning.BurningClientRecipe;
import cc.cassian.rrv.common.builtin.campfire.CampfireClientRecipe;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.ShapelessServerRecipe;
import cc.cassian.rrv.common.builtin.entity.EntityClientRecipe;
import cc.cassian.rrv.common.builtin.entity.EntityServerRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingClientRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingClientRecipe;
import cc.cassian.rrv.common.builtin.smoking.SmokingClientRecipe;
import cc.cassian.rrv.common.builtin.stonecutting.StonecutterClientRecipe;
import cc.cassian.rrv.common.builtin.tag.item.ItemTagClientRecipe;
import cc.cassian.rrv.common.builtin.tag.block.BlockTagClientRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerClientRecipe;
//~ if >26 'backport' -> 'common.builtin.villager'
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.IndexSource;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.mixin.recipe.ConcretePowderBlockAccessor;
import cc.cassian.rrv.common.mixin.world.item.crafting.*;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.client.recipe.ResourceRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
//? fabric {
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
//? if >26.2 {
/*import cc.cassian.rrv.common.recipe.util.WorldInteractionRecipeUtil;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
*///?}
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
//? if <26.3 {
import net.minecraft.world.item.alchemy.PotionBrewing;
//?}
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
//? neoforge
//import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.*;
import java.util.function.BiPredicate;

import static cc.cassian.rrv.client.recipe.ResourceRecipeManager.*;
import static cc.cassian.rrv.common.recipe.util.RrvUtil.blockName;
import static cc.cassian.rrv.common.recipe.util.RrvUtil.getItemsFromIngredient;

public class BuiltInReliableRecipeViewerClientIntegration implements ReliableRecipeViewerClientPlugin {

	public static final BiPredicate<ItemStack, ItemStack> TRIM_CHECK = ItemViewRecipes.makeTrimCheck();
	public static final BiPredicate<ItemStack, ItemStack> ENCHANTMENT_CHECK = ItemViewRecipes.makeEnchantmentCheck();
	public static final BiPredicate<ItemStack, ItemStack> FIREWORK_ROCKET_CHECK = ItemViewRecipes.makeFireworkRocketCheck();
	public static final BiPredicate<ItemStack, ItemStack> STEW_CHECK = ItemViewRecipes.makeStewCheck();
	public static final BiPredicate<ItemStack, ItemStack> POTION_CHECK = ItemViewRecipes.makePotionCheck();

	static <T> void excludeTag(Registry<T> registry, TagKey<T> tag) {
        registry.get(tag).ifPresent(named -> named.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(t -> {
            switch (t) {
                case Item item -> ItemView.excludeItems(item);
                case Block block -> ItemView.excludeItems(block.asItem());
                case Fluid fluid -> ItemView.excludeItems(new FluidStack(fluid).createItemStack().getItem());
                default -> {}
            }
        }));
        //? fabric
        ItemView.excludeItems(ClientTags.getOrCreateLocalTag(tag));
    }

    @Override
    public void onIntegrationInitialize() {
        ItemView.addClientReloadCallback(() -> {
            //? neoforge
            //ItemView.excludeItems(Items.AIR);
            ItemView.excludeItemStack(new ItemStack(Items.POTION), new ItemStack(Items.SPLASH_POTION), new ItemStack(Items.LINGERING_POTION), new ItemStack(Items.ENCHANTED_BOOK), new ItemStack(Items.TIPPED_ARROW), new ItemStack(Items.SUSPICIOUS_STEW));
            if (Configs.CLIENT_SETTINGS.getIndexSource().containsKey(IndexSource.CREATIVE))
                ItemView.excludeItemStack(new ItemStack(Items.SUSPICIOUS_STEW));
            excludeTag(BuiltInRegistries.BLOCK, CommonTags.EXCLUDED_BLOCKS);
            excludeTag(BuiltInRegistries.ITEM, CommonTags.EXCLUDED_ITEMS);
            excludeTag(BuiltInRegistries.FLUID, CommonTags.EXCLUDED_FLUIDS);
            hideRecipes();
            StackGroupManager.reload();
			ItemView.addItemCheck(DataComponents.ITEM_MODEL);
			ItemView.addItemCheck(DataComponents.PAINTING_VARIANT);
			ItemView.addItemCheck(POTION_CHECK);
			ItemView.addItemCheck(STEW_CHECK);
			ItemView.addItemCheck(FIREWORK_ROCKET_CHECK);
			ItemView.addItemCheck(ENCHANTMENT_CHECK);
			ItemView.addItemCheck(TRIM_CHECK);
			ItemView.addMobFood(EntityType.ALLAY, SlotContent.of(ItemTags.DUPLICATES_ALLAYS), Component.translatable("view.rrv.type.entity.allay_duplication")); // allay duplication isn't food but may as well be
			ItemView.addMobFood(EntityType.ARMADILLO, ItemTags.ARMADILLO_FOOD);
			ItemView.addMobFood(EntityType.AXOLOTL, ItemTags.AXOLOTL_FOOD);
			ItemView.addMobFood(EntityType.BEE, ItemTags.BEE_FOOD);
			ItemView.addMobFood(EntityType.CAMEL, ItemTags.CAMEL_FOOD);
			ItemView.addMobFood(EntityType.CAMEL_HUSK, ItemTags.CAMEL_HUSK_FOOD);
			ItemView.addMobFood(EntityType.CAT, ItemTags.CAT_FOOD);
			ItemView.addMobFood(EntityType.CHICKEN, ItemTags.CHICKEN_FOOD);
			ItemView.addMobFood(EntityType.COW, ItemTags.COW_FOOD);
			ItemView.addMobFood(EntityType.FOX, ItemTags.FOX_FOOD);
			ItemView.addMobFood(EntityType.FROG, ItemTags.FROG_FOOD);
			ItemView.addMobFood(EntityType.GOAT, ItemTags.GOAT_FOOD);
			ItemView.addMobFood(EntityType.HAPPY_GHAST, ItemTags.HAPPY_GHAST_FOOD);
			ItemView.addMobFood(EntityType.HOGLIN, ItemTags.HOGLIN_FOOD);
			ItemView.addMobFood(EntityType.HORSE, ItemTags.HORSE_FOOD);
			ItemView.addMobFood(EntityType.DONKEY, ItemTags.HORSE_FOOD);
			ItemView.addMobFood(EntityType.LLAMA, ItemTags.LLAMA_FOOD);
			var mooshroomFood = new ArrayList<>(SlotContent.of(ItemTags.COW_FOOD).getValidContents());
			mooshroomFood.addAll(SlotContent.of(CommonTags.FLOWERS).getValidContents());
			ItemView.addMobFood(EntityType.MOOSHROOM, SlotContent.of(mooshroomFood)); // mooshrooms can eat suspicious stew foods
			ItemView.addMobFood(EntityType.MULE, ItemTags.HORSE_FOOD);
			ItemView.addMobFood(EntityType.NAUTILUS, ItemTags.NAUTILUS_FOOD);
			ItemView.addMobFood(EntityType.OCELOT, ItemTags.OCELOT_FOOD);
			ItemView.addMobFood(EntityType.PARROT, ItemTags.PARROT_FOOD);
			ItemView.addMobFood(EntityType.PANDA, ItemTags.PANDA_FOOD);
			ItemView.addMobFood(EntityType.PIG, ItemTags.PIG_FOOD);
			ItemView.addMobFood(EntityType.RABBIT, ItemTags.RABBIT_FOOD);
			ItemView.addMobFood(EntityType.SNIFFER, ItemTags.SNIFFER_FOOD);
			ItemView.addMobFood(EntityType.STRIDER, ItemTags.STRIDER_FOOD);
			ItemView.addMobFood(EntityType.TRADER_LLAMA, ItemTags.LLAMA_FOOD);
			//? if >26.2 {
			/*ItemView.addMobFood(EntityType.VILLAGER, SlotContent.ofItemList(BuiltInRegistries.ITEM.stream().filter(p->p.getDefaultInstance().has(DataComponents.VILLAGER_FOOD)).toList()));
			*///?} else {
			ItemView.addMobFood(EntityType.VILLAGER, SlotContent.ofItemList(Villager.FOOD_POINTS.keySet().stream().toList()));
			//?}
			ItemView.addMobFood(EntityType.WOLF, ItemTags.WOLF_FOOD);
			ItemView.addMobFood(EntityType.ZOMBIE_HORSE, ItemTags.ZOMBIE_HORSE_FOOD);
        });

        //Wrapper
        ItemView.addClientRecipeWrapper(VillagerServerRecipe.TYPE, unwrapped -> unwrapped.getClientOffers().stream().map(VillagerClientRecipe::new).toList());
        ItemView.addClientRecipeWrapper(EntityServerRecipe.TYPE, unwrapped -> {
            if (unwrapped.getEntityType() == null) return Collections.emptyList();
			if (unwrapped.getDrops().isEmpty() && !ItemViewRecipes.MOB_FOOD.containsKey(unwrapped.getEntityType())) return Collections.emptyList();
			return List.of(new EntityClientRecipe(unwrapped));
		});
        ItemView.addClientRecipeWrapper(ShapelessServerRecipe.TYPE, unwrapped -> List.of(new CraftingClientRecipe.Builder(null, unwrapped.getIngredients()).setResult(unwrapped.getResult()).build()));
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            // Crafting
            addCraftingRecipes(recipeList, level);
            // Smelting
            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMELTING).forEach(smeltingRecipeRecipeHolder -> recipeList.add(new SmeltingClientRecipe(smeltingRecipeRecipeHolder)));
            // Blasting
            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.BLASTING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new BlastingClientRecipe(smokingRecipeRecipeHolder)));
            // Smoking
            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMOKING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new SmokingClientRecipe(smokingRecipeRecipeHolder)));
            // Campfire
            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.CAMPFIRE_COOKING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new CampfireClientRecipe(smokingRecipeRecipeHolder)));
            // Fuel
            addFuelRecipes(recipeList, level);
			// Composting
			addCompostingRecipes(recipeList);
			// Smithing
            addSmithingRecipes(recipeList);
            // Stonecutting
            ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.STONECUTTING).forEach(stonecutterRecipeRecipeHolder -> recipeList.add(new StonecutterClientRecipe(stonecutterRecipeRecipeHolder)));
            // Anvil Combining
            recipeList.addAll(addAnvilCombiningRecipes());
            // Info
            recipeList.addAll(addInfoRecipes());
            // World Interaction
            recipeList.addAll(addWorldInteractionRecipes());
            // Brewing
            addBrewingRecipes(recipeList, level);
            // Item Tags
            BuiltInRegistries.ITEM.listTagIds().forEach((tag) -> {
                if (!HIDDEN_ITEM_TAGS.contains(tag.location())) {
                    Optional<HolderSet.Named<Item>> tagContents = BuiltInRegistries.ITEM.get(tag);
                    if (tagContents.isPresent() && !tagContents.get().stream().allMatch(item -> ItemView.getExcludedItems().contains(item.value()))) {
                        recipeList.add(new ItemTagClientRecipe(tag));
                    }
                }
            });
            // Block Tags
            BuiltInRegistries.BLOCK.listTagIds().forEach((tag) -> {
                if (!HIDDEN_BLOCK_TAGS.contains(tag.location())) {
                    Optional<HolderSet.Named<Block>> tagContents = BuiltInRegistries.BLOCK.get(tag);
                    if (tagContents.isPresent() && !tagContents.get().stream().allMatch(item -> ItemView.getExcludedItems().contains(item.value().asItem()))) {
                        recipeList.add(new BlockTagClientRecipe(tag));
                    }
                }
            });
        });
    }

	private static void addCraftingRecipes(List<ReliableClientRecipe> recipeList, ClientLevel level) {
        ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.CRAFTING).forEach(craftingRecipeHolder -> {
            var id = craftingRecipeHolder.id().identifier();
            var recipe = craftingRecipeHolder.value();
            try {
				switch (recipe) {
					case ShapelessRecipe shapelessRecipe ->
							recipeList.add(new CraftingClientRecipe.Builder(id, shapelessRecipe.ingredients.stream()
									.map(SlotContent::of).toList()).setResult(shapelessRecipe.result).build());
					case ShapedRecipe shapedRecipe -> {
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
						recipeList.add(new CraftingClientRecipe.Builder(id, ingredients).setSize(shapedRecipe.getWidth(), shapedRecipe.getHeight()).setResult(shapedRecipe.result).build());
					}
					case TransmuteRecipe transmuteRecipe -> {
						TransmuteRecipeAccessor accessor = (TransmuteRecipeAccessor) transmuteRecipe;

						List<ItemStackTemplate> results = new ArrayList<>();

						var ingredients = getItemsFromIngredient(accessor.getInput());

						ingredients.forEach(item -> {
							//? if >26.2 {
							/*var result = new ItemStackTemplate(accessor.getResult().item().orElse(ingredients.getFirst().builtInRegistryHolder()), accessor.getResult().count(), accessor.getResult().components());
							*///?} else if >26 {
							var result = accessor.getResult();
							//?} else {
							/*var result = new ItemStackTemplate(accessor.getResult());
							*///?}
							results.add(result);
						});

						if (!ingredients.isEmpty() && !results.isEmpty())
							recipeList.add(new CraftingClientRecipe.Builder(id, accessor.getInput(), accessor.getMaterial()).setResult(results).build());

					}
					//? if >26 {
					case DyeRecipe dyeRecipe -> {
						DyeRecipeAccessor accessor = (DyeRecipeAccessor) dyeRecipe;

						List<Item> ingredients = getItemsFromIngredient(accessor.getTarget());

						List<ItemStackTemplate> results = new ArrayList<>();
						for (Item ingredient : ingredients) {
							for (DyeColor dyeColor : DyeColor.values()) {
								results.add(ItemStackTemplate.fromNonEmptyStack(DyedItemColor.applyDyes(ingredient.getDefaultInstance(), Collections.singletonList(dyeColor))));
							}
						}

						recipeList.add(new CraftingClientRecipe.Builder(id, accessor.getTarget(), accessor.getDye()).setResult(results).setDependentIndex(1).build());
					}
					case ImbueRecipe imbueRecipe -> {
						ImbueRecipeAccessor accessor = (ImbueRecipeAccessor) imbueRecipe;


						Registry<Potion> potionRegistry = level.registryAccess().lookupOrThrow(Registries.POTION);
						potionRegistry.forEach(potion -> {
							Holder<Potion> potionHolder = potionRegistry.wrapAsHolder(potion);
							var items = getItemsFromIngredient(accessor.getSource()).stream().map(item -> PotionContents.createItemStack(item, potionHolder)).toList();
							HashMap<Integer, SlotContent> ingredients = fillCraftingGrid(SlotContent.of(items), SlotContent.of(accessor.getMaterial()));

							ItemStack result = accessor.getResult().create().copyWithCount(8);
							result.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder));
							recipeList.add(new CraftingClientRecipe.Builder(id, ingredients).setResult(SlotContent.of(result)).setPriority(5).build());
						});
					}
					case DecoratedPotRecipe decoratedPotRecipe -> {
						DecoratedPotRecipeAccessor accessor = (DecoratedPotRecipeAccessor) decoratedPotRecipe;

						HashMap<Integer, SlotContent> ingredients = new HashMap<>();
						ingredients.put(1, SlotContent.of(accessor.getLeftPattern()));
						ingredients.put(3, SlotContent.of(accessor.getRightPattern()));
						ingredients.put(5, SlotContent.of(accessor.getBackPattern()));
						ingredients.put(7, SlotContent.of(accessor.getFrontPattern()));

						List<ItemStack> results = new ArrayList<>();
						for (Item item : getItemsFromIngredient(accessor.getFrontPattern())) {
							//? if >26.2 {
							/*var ingredient = Optional.of(new ItemStackTemplate(item));
							*///?} else {
							var ingredient = item;
							 //?}
							PotDecorations decorations = new PotDecorations(ingredient, ingredient, ingredient, ingredient);
							DataComponentPatch components = DataComponentPatch.builder().set(DataComponents.POT_DECORATIONS, decorations).build();
							results.add(accessor.getResult().apply(components));
						}

						recipeList.add(new CraftingClientRecipe.Builder(id, ingredients).setResult(SlotContent.of(results)).setDependentIndex(7).build());
					}
					case BookCloningRecipe bookCloningRecipe -> {
						BookCloningRecipeAccessor accessor = (BookCloningRecipeAccessor) bookCloningRecipe;
						recipeList.add(new CraftingClientRecipe.Builder(id, accessor.getSource(), accessor.getMaterial()).setResult(accessor.getResult().withCount(2)).build());
					}
					case MapExtendingRecipe mapExtendingRecipe -> {
						MapExtendingRecipeAccessor accessor = (MapExtendingRecipeAccessor) mapExtendingRecipe;
						HashMap<Integer, SlotContent> ingredients = fillCraftingGrid(SlotContent.of(accessor.getMap()), SlotContent.of(accessor.getMaterial()));
						recipeList.add(new CraftingClientRecipe.Builder(id, ingredients).setResult(SlotContent.of(accessor.getResult()
								//? if >26.2
								//, getItemsFromIngredient(accessor.getMap()).getFirst()
						)).build());
					}
					case FireworkRocketRecipe fireworkRocketRecipe -> {
						FireworkRocketRecipeAccessor accessor = (FireworkRocketRecipeAccessor) fireworkRocketRecipe;
						List<SlotContent> ingredients = new ArrayList<>(List.of(
								SlotContent.of(accessor.getFuel()),
								SlotContent.of(accessor.getShell())
								// todo star, firework stars are weird and optional
						));
						recipeList.add(new CraftingClientRecipe.Builder(id, ingredients).setResult(SlotContent.of(accessor.getResult())).setPriority(20).build());
						ingredients.addFirst(SlotContent.of(accessor.getFuel()));
						recipeList.add(new CraftingClientRecipe.Builder(id, ingredients).setResult(SlotContent.of(accessor.getResult().apply(DataComponentPatch.builder().set(DataComponents.FIREWORKS, new Fireworks(2, List.of())).build()))).setPriority(20).build());
						ingredients.addFirst(SlotContent.of(accessor.getFuel()));
						recipeList.add(new CraftingClientRecipe.Builder(id, ingredients).setResult(SlotContent.of(accessor.getResult().apply(DataComponentPatch.builder().set(DataComponents.FIREWORKS, new Fireworks(3, List.of())).build()))).setPriority(20).build());
					}
					case ShieldDecorationRecipe shieldDecorationRecipe -> {
						ShieldDecorationRecipeAccessor accessor = (ShieldDecorationRecipeAccessor) shieldDecorationRecipe;
						ArrayList<ItemStack> results = new ArrayList<>();
						for (Item item : getItemsFromIngredient(accessor.getBanner())) {
							if (item instanceof BannerItem bannerItem) {
								var dyeColor = bannerItem.getColor();
								results.add(accessor.getResult().apply(DataComponentPatch.builder().set(DataComponents.BASE_COLOR, dyeColor).build()));
							}
						}
						recipeList.add(new CraftingClientRecipe.Builder(id, accessor.getTarget(), accessor.getBanner()).setResult(SlotContent.of(results)).setDependentIndex(1).build());
					}
					//?}
					case RepairItemRecipe repairItemRecipe ->
						// Repairing
							addRepairingRecipes(recipeList);
					default -> {
					}
				}
            } catch (Exception e) {
                // Log crafting recipes that throw out an exception on parse
                ReliableRecipeViewer.LOGGER.atError().setCause(e).log(
                        "Exception while parsing crafting recipe \"{}\"", id);
            }
        });
    }

    private static HashMap<Integer, SlotContent> fillCraftingGrid(SlotContent middleItem, SlotContent surroundingItems) {
        HashMap<Integer, SlotContent> ingredients = new HashMap<>();
        for (int i = 0; i < 9; i++){
            if (i == 4)
                ingredients.put(i, middleItem);
            else
                ingredients.put(i, surroundingItems);
        }
        return ingredients;
    }

    private static void addFuelRecipes(List<ReliableClientRecipe> recipeList, ClientLevel level) {
		//? if >26.2 {
		/*ItemView.addClientRecipeWrapper(BurningServerRecipe.TYPE, (unwrapped -> List.of(new BurningClientRecipe(unwrapped.getFuel().getDefaultInstance(), unwrapped.getBurnTime()))));
		*///?} else {
		FuelValues fuelValues = level.fuelValues();
        fuelValues.fuelItems().forEach(item -> {
            //? fabric
            recipeList.add(new BurningClientRecipe(item, fuelValues.burnDuration(new ItemStack(item))));
            //? neoforge
            //recipeList.add(new BurningClientRecipe(item, item.getDefaultInstance().getBurnTime(null, fuelValues)));
        });
		//?}
    }

    private static void addSmithingRecipes(List<ReliableClientRecipe> recipeList) {
        ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.SMITHING).forEach(smithingRecipeRecipeHolder -> {
            var smithingRecipe = smithingRecipeRecipeHolder.value();

            try {
                if (smithingRecipe instanceof SmithingTrimRecipe trimRecipe) {
                    recipeList.add(SmithingClientRecipe.trimRecipe(smithingRecipeRecipeHolder.id().identifier(),
                            trimRecipe.additionIngredient().orElse(null), trimRecipe.baseIngredient(),
                            trimRecipe.templateIngredient().orElse(null), trimRecipe.pattern));
                } else if (smithingRecipe instanceof SmithingTransformRecipe transformRecipe) {
					//? if >26 {
					var result = transformRecipe.result;
					//?} else {
					/*var result = new ItemStackTemplate(transformRecipe.result);
					*///?}
					recipeList.add(SmithingClientRecipe.transformationRecipe(smithingRecipeRecipeHolder.id().identifier(),
                            transformRecipe.additionIngredient().orElse(null), transformRecipe.baseIngredient(),
                            transformRecipe.templateIngredient().orElse(null), result));
                }
            } catch (Exception e) {
                // Log smithing recipes that throw out an exception on parse
                ReliableRecipeViewer.LOGGER.atError().setCause(e).log(
                        "Exception while parsing smithing recipe \"{}\"",
                        smithingRecipeRecipeHolder.id().identifier());
            }
        });
    }

    private static void addBrewingRecipes(List<ReliableClientRecipe> recipeList, ClientLevel level) {
        //? if >26.2 {
        /*ClientRecipeManager.INSTANCE.getRecipesForType(RecipeType.BREWING).forEach(holder -> {
            var recipe = holder.value();
			recipeList.add(new BrewingClientRecipe(holder.id().identifier(),
                    SlotContent.of(recipe.getOutput()),
                    SlotContent.of(recipe.getReagent()),
                    SlotContent.of(recipe.getInput())
            ));
		});
        *///?} else {
        PotionBrewing potionBrewing = level.potionBrewing();
        List<PotionBrewing.Mix<Potion>> potionMixes = ((PotionBrewingAccessor) potionBrewing).getPotionMixes();
        List<PotionBrewing.Mix<Item>> containerMixes = ((PotionBrewingAccessor) potionBrewing).getContainerMixes();

        containerMixes.forEach(itemMix -> recipeList.add(new BrewingClientRecipe(itemMix.to().unwrapKey().map(ResourceKey::identifier).orElse(Identifier.withDefaultNamespace(UUID.randomUUID().toString())).withPrefix("/brewing/").withSuffix(RrvUtil.ingredientSuffix(itemMix.ingredient())), new ItemStack(itemMix.to().value()), itemMix.ingredient(), new ItemStack(itemMix.from().value()))));

        potionMixes.forEach(potionMix -> {
            var id = potionMix.to().unwrapKey().map(ResourceKey::identifier).orElse(Identifier.withDefaultNamespace(UUID.randomUUID().toString())).withPrefix("/brewing/");
            recipeList.add(new BrewingClientRecipe(id.withSuffix("_potion"+RrvUtil.ingredientSuffix(potionMix.ingredient())), PotionContents.createItemStack(Items.POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.POTION, potionMix.from())));
            recipeList.add(new BrewingClientRecipe(id.withSuffix("_awkward_potion"+RrvUtil.ingredientSuffix(potionMix.ingredient())),PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.from())));
            recipeList.add(new BrewingClientRecipe(id.withSuffix("_lingering_potion"+RrvUtil.ingredientSuffix(potionMix.ingredient())), PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.from())));

        });
        //?}
    }

    private static void addRepairingRecipes(List<ReliableClientRecipe> recipeList) {
        BuiltInRegistries.ITEM.entrySet().forEach((entry) -> {
            var item = entry.getValue();
            var stack = item.getDefaultInstance();
            if (stack.has(DataComponents.REPAIRABLE)) {
                Repairable repairable = stack.get(DataComponents.REPAIRABLE);
                var damagedStack = stack.copy();
                damagedStack.setDamageValue(stack.getMaxDamage() / 2);
                assert repairable != null;
                recipeList.add(new AnvilCombiningClientRecipe(entry.getKey().identifier().withPrefix("/anvil_repairing/"), SlotContent.of(damagedStack), SlotContent.of(repairable.items()), SlotContent.of(stack), -10));
                recipeList.add(new CraftingClientRecipe.Builder(entry.getKey().identifier().withPrefix("/repairing/"), SlotContent.of(damagedStack), SlotContent.of(damagedStack)).setResult(SlotContent.of(stack)).setPriority(-10).build());
            }
        });
    }

    private Collection<? extends ReliableClientRecipe> addWorldInteractionRecipes() {
        ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes = new ArrayList<>();
        ResourceRecipeManager.addWorldInteractionRecipes(worldInteractionRecipes);
        ItemViewRecipes.addAllWorldInteractionRecipes(worldInteractionRecipes);

        var axes = SlotContent.of(ItemTags.AXES);
		var shovels = SlotContent.of(ItemTags.SHOVELS);
		var hoes = SlotContent.of(ItemTags.HOES);

        //? if >26.2 {
		/*Registry<BlockTransformer> blockTransformers = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BLOCK_TRANSFORMER);
		for (Map.Entry<ResourceKey<BlockTransformer>, BlockTransformer> blockTransformer : blockTransformers.entrySet()) {
			WorldInteractionRecipeUtil.addTransformerRecipes(blockTransformer.getValue().transforms(), worldInteractionRecipes, SlotContent.ofItemList(BuiltInRegistries.ITEM.stream().filter(c-> {
				ItemStack stack = c.getDefaultInstance();
				if (stack.has(DataComponents.BLOCK_TRANSFORMER)) {
					return stack.get(DataComponents.BLOCK_TRANSFORMER).unwrapKey().get().equals(blockTransformer.getKey());
				}
				return false;
			}).toList()));
		}
        *///?}

        BuiltInRegistries.ITEM.entrySet().forEach(itemEntry -> {
            if (itemEntry.getValue() instanceof FluidItem fluidItem) {
                Item bucket = fluidItem.getFluid().getBucket();
                if (bucket == null || bucket.getDefaultInstance().isEmpty()) return;
				//~ if >26 'ItemStack'->'ItemStackTemplate'
                worldInteractionRecipes.add(new WorldInteractionClientRecipe(itemEntry.getKey().identifier().withPath("/world_interaction/%s_bucketing"::formatted), SlotContent.of(new FluidStack(fluidItem.getFluid())), SlotContent.of(Optional.ofNullable(bucket.getCraftingRemainder()).orElse(new ItemStackTemplate(Items.BUCKET))), SlotContent.of(bucket)));
            }

            if (itemEntry.getValue() instanceof BlockItem blockItem) {
                var block = blockItem.getBlock();
                var id = itemEntry.getKey().identifier();
                if (block instanceof WeatheringCopper) {
                    Optional<Block> next = WeatheringCopper.getNext(block);
                    next.ifPresent(value -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(id.withPrefix("/world_interaction/").withSuffix("_oxidizing"), SlotContent.of(block), WorldInteractionClientRecipe.TIME, SlotContent.of(value.asItem()))));

                    Optional<Block> previous = WeatheringCopper.getPrevious(block);
                    previous.ifPresent(value -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(id.withPrefix("/world_interaction/").withSuffix("_reverse_oxidizing"), SlotContent.of(block), axes, SlotContent.of(value.asItem()))));
                }
                if (block instanceof TallFlowerBlock || block instanceof FlowerBedBlock) {
                    worldInteractionRecipes.add(new WorldInteractionClientRecipe(id.withPrefix("/world_interaction/").withSuffix("_bone_meal"), SlotContent.of(block), SlotContent.of(Items.BONE_MEAL), SlotContent.of(new ItemStack(block, 2))));
                }
                //? neoforge {
                    /*Holder.Reference<Block> blockReference = block.builtInRegistryHolder();
                    if (blockReference.getData(NeoForgeDataMaps.WAXABLES) != null) {
                        SlotContent waxed = SlotContent.of(blockReference.getData(NeoForgeDataMaps.WAXABLES).waxed());
                        worldInteractionRecipes.add(new WorldInteractionClientRecipe(blockName("/world_interaction/wax_off_", block), waxed, axes, SlotContent.of(block)));
                        worldInteractionRecipes.add(new WorldInteractionClientRecipe(blockName("/world_interaction/wax_", block), SlotContent.of(block), SlotContent.of(Items.HONEYCOMB), waxed));
                    }
                    if (blockReference.getData(NeoForgeDataMaps.STRIPPABLES) != null) {
                        worldInteractionRecipes.add(new WorldInteractionClientRecipe(blockName("/world_interaction/strip_", block), SlotContent.of(block), axes, SlotContent.of(blockReference.getData(NeoForgeDataMaps.STRIPPABLES).strippedBlock())));
                    }
                    *///?}

                if (block instanceof ConcretePowderBlock concretePowderBlock) {
                    worldInteractionRecipes.add(new WorldInteractionClientRecipe(id.withPrefix("/world_interaction/").withSuffix("_solidify"), SlotContent.of(block), SlotContent.of(new FluidStack(Fluids.WATER)), SlotContent.of(((ConcretePowderBlockAccessor) concretePowderBlock).getConcrete())));
                }
            }
        });

        // honeycomb
        //? fabric {
        HoneycombItem.WAXABLES.get().forEach(((block, block2) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(blockName("/world_interaction/wax_", block), SlotContent.of(block), SlotContent.of(Items.HONEYCOMB), SlotContent.of(block2)))));
        HoneycombItem.WAX_OFF_BY_BLOCK.get().forEach(((block, block2) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(blockName("/world_interaction/wax_off_",block), SlotContent.of(block), axes, SlotContent.of(block2)))));
        //? if <26.3
        AxeItem.STRIPPABLES.forEach(((block, state) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(blockName("/world_interaction/strip_", block), SlotContent.of(block), axes, SlotContent.of(state)))));
        //?}

        // flattenables
        //? if <26.3
        ShovelItem.FLATTENABLES.forEach(((block, state) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/shovel_path"), SlotContent.of(block), shovels, SlotContent.of(state.getBlock())))));

        // hoes
        //? if <26.3 {
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/hoe_dirt"),SlotContent.of(Blocks.ROOTED_DIRT), hoes, SlotContent.of(Items.DIRT)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/hoe_farmland"), SlotContent.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.DIRT_PATH), hoes, SlotContent.of(Items.FARMLAND)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/hoe_hanging_roots"),SlotContent.of(Blocks.ROOTED_DIRT), hoes, SlotContent.of(Items.HANGING_ROOTS)));
        //?}

        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/shearing_bee_nest"), SlotContent.of(Blocks.BEEHIVE, Blocks.BEE_NEST), SlotContent.of(Items.SHEARS), SlotContent.of(new ItemStack(Items.HONEYCOMB, 3))));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/glass_bottle_bee_nest"), SlotContent.of(Blocks.BEEHIVE, Blocks.BEE_NEST), SlotContent.of(Items.GLASS_BOTTLE), SlotContent.of(Items.HONEY_BOTTLE)));

        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/water_filling"), SlotContent.of(new FluidStack(Fluids.WATER, 333)), SlotContent.of(Items.GLASS_BOTTLE), SlotContent.of(PotionContents.createItemStack(Items.POTION, Potions.WATER))));

        return worldInteractionRecipes;
    }

	private void addCompostingRecipes(List<ReliableClientRecipe> recipeList) {
		//? if >26.2 {
		/*ItemView.addClientRecipeWrapper(CompostingServerRecipe.TYPE, (unwrapped -> List.of(new CompostingClientRecipe(unwrapped.getCompostedItem().getDefaultInstance(), unwrapped.getLayers()))));
		 *///?} else {
		ComposterBlock.COMPOSTABLES.forEach((itemLike, aFloat) -> {
			recipeList.add(new CompostingClientRecipe(itemLike.asItem().getDefaultInstance(), aFloat));
		});
		//?}
	}


}
