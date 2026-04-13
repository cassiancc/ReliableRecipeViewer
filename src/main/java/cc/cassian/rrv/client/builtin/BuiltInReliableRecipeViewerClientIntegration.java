package cc.cassian.rrv.client.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.builtin.blasting.BlastingClientRecipe;
import cc.cassian.rrv.common.builtin.brewing.BrewingClientRecipe;
import cc.cassian.rrv.common.builtin.burning.BurningClientRecipe;
import cc.cassian.rrv.common.builtin.campfire.CampfireClientRecipe;
import cc.cassian.rrv.common.builtin.entity.EntityClientRecipe;
import cc.cassian.rrv.common.builtin.entity.EntityServerRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingClientRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingClientRecipe;
import cc.cassian.rrv.common.builtin.smoking.SmokingClientRecipe;
import cc.cassian.rrv.common.builtin.stonecutting.StonecutterClientRecipe;
import cc.cassian.rrv.common.builtin.tag.item.ItemTagClientRecipe;
import cc.cassian.rrv.common.builtin.tag.block.BlockTagClientRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerClientRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.common.mixin.world.item.alchemy.PotionBrewingAccessor;
import cc.cassian.rrv.common.mixin.world.item.crafting.*;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.PotDecorations;
//? neoforge
//import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.*;

import static cc.cassian.rrv.common.recipe.ResourceRecipeManager.*;
import static cc.cassian.rrv.common.recipe.util.RrvUtil.getItemsFromIngredient;

public class BuiltInReliableRecipeViewerClientIntegration implements ReliableRecipeViewerClientPlugin {

    @Override
    public void onIntegrationInitialize() {


        ItemView.addClientReloadCallback(() -> {

            BuiltInRegistries.BLOCK.get(CommonTags.EXCLUDED_BLOCKS).ifPresent(blocks -> blocks.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(block -> ItemView.excludeItem(block.asItem())));
            BuiltInRegistries.ITEM.get(CommonTags.EXCLUDED_ITEMS).ifPresent(items -> items.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(ItemView::excludeItem));
            BuiltInRegistries.FLUID.get(CommonTags.EXCLUDED_FLUIDS).ifPresent(fluids -> fluids.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(fluid -> ItemView.excludeItem(fluid.defaultFluidState().createLegacyBlock().getBlock().asItem())));
            ItemFilters.buildTagCache();
            getHiddenTags();
        });

        //Wrapper
        ItemView.addClientRecipeWrapper(VillagerServerRecipe.TYPE, unwrapped -> unwrapped.getClientOffers().stream().map(VillagerClientRecipe::new).toList());
        ItemView.addClientRecipeWrapper(EntityServerRecipe.TYPE, unwrapped -> List.of(new EntityClientRecipe(unwrapped)));
        ItemView.addClientRecipeProvider(recipeList -> {

            // Crafting
            addCraftingRecipes(recipeList);

            //Tipped arrows
//            addTippedArrowRecipes(recipeList);
            // Smelting
            ClientRecipeManager.getRecipesForType(RecipeType.SMELTING).forEach(smeltingRecipeRecipeHolder -> recipeList.add(new SmeltingClientRecipe(smeltingRecipeRecipeHolder)));
            // Blasting
            ClientRecipeManager.getRecipesForType(RecipeType.BLASTING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new BlastingClientRecipe(smokingRecipeRecipeHolder)));
            // Smoking
            ClientRecipeManager.getRecipesForType(RecipeType.SMOKING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new SmokingClientRecipe(smokingRecipeRecipeHolder)));
            // Campfire
            ClientRecipeManager.getRecipesForType(RecipeType.CAMPFIRE_COOKING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new CampfireClientRecipe(smokingRecipeRecipeHolder)));
            // Fuel
            addFuelRecipes(recipeList);

            // Smithing
            addSmithingRecipes(recipeList);

            ClientRecipeManager.getRecipesForType(RecipeType.STONECUTTING).forEach(stonecutterRecipeRecipeHolder -> recipeList.add(new StonecutterClientRecipe(stonecutterRecipeRecipeHolder)));

            // Anvil Combining
            recipeList.addAll(addAnvilCombiningRecipes());
            recipeList.addAll(addInfoRecipes());
            recipeList.addAll(addWorldInteractionRecipes());

            //Repairing
            addRepairingRecipes(recipeList);

            //Brewing
            addBrewingRecipes(recipeList);

            //Tags
            BuiltInRegistries.ITEM.listTagIds().forEach((tag) -> {
                if (!HIDDEN_ITEM_TAGS.contains(tag.location())) {
                    Optional<HolderSet.Named<Item>> tagContents = BuiltInRegistries.ITEM.get(tag);
                    if (tagContents.isPresent() && !tagContents.get().stream().allMatch(item -> ItemView.getExcludedItems().contains(item.value()))) {
                        recipeList.add(new ItemTagClientRecipe(tag));
                    }
                }
            });

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

    private static void addCraftingRecipes(List<ReliableClientRecipe> recipeList) {
        ClientRecipeManager.getRecipesForType(RecipeType.CRAFTING).forEach(craftingRecipeHolder -> {
            var id = craftingRecipeHolder.id().identifier();
            var recipe = craftingRecipeHolder.value();
            if (recipe instanceof ShapelessRecipe shapelessRecipe)
                recipeList.add(new CraftingClientRecipe(id, shapelessRecipe.ingredients, shapelessRecipe.result));


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

                recipeList.add(new CraftingClientRecipe(id, shapedRecipe.getWidth(), shapedRecipe.getHeight(), ingredients, SlotContent.of(shapedRecipe.result)));
            }

            else if (recipe instanceof TransmuteRecipe) {
                TransmuteRecipeAccessor accessor = (TransmuteRecipeAccessor) recipe;

                List<ItemStackTemplate> results = new ArrayList<>();

                var ingredients = getItemsFromIngredient(accessor.getInput());

                ingredients.forEach(ingredient -> results.add(accessor.getResult()));

                if (!ingredients.isEmpty() && !results.isEmpty())
                    recipeList.add(new CraftingClientRecipe(id, accessor.getInput(), accessor.getMaterial(), results));

            }
            else if (recipe instanceof DyeRecipe) {
                DyeRecipeAccessor accessor = (DyeRecipeAccessor) recipe;

                List<Item> ingredients = getItemsFromIngredient(accessor.getTarget());

                List<ItemStackTemplate> results = new ArrayList<>();
                for (Item ingredient : ingredients) {
                    for (DyeColor dyeColor : DyeColor.values()) {
                        results.add(ItemStackTemplate.fromNonEmptyStack(DyedItemColor.applyDyes(ingredient.getDefaultInstance(), Collections.singletonList(dyeColor))));
                    }
                }

                recipeList.add(new CraftingClientRecipe(id, accessor.getTarget(), accessor.getDye(), results, 1));
            }
            else if (recipe instanceof ImbueRecipe) {
                ImbueRecipeAccessor accessor = (ImbueRecipeAccessor) recipe;

                Registry<Potion> potionRegistry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.POTION);
                potionRegistry.forEach(potion -> {
                    Holder<Potion> potionHolder = potionRegistry.wrapAsHolder(potion);
                    var items = getItemsFromIngredient(accessor.getSource()).stream().map(item->PotionContents.createItemStack(item, potionHolder)).toList();
                    HashMap<Integer, SlotContent> ingredients = new HashMap<>();
                    for (int i = 0; i < 9; i++){
                        if (i == 4)
                            ingredients.put(i, SlotContent.of(items));
                        else
                            ingredients.put(i, SlotContent.of(accessor.getMaterial()));
                    }

                    ItemStack result = accessor.getResult().create().copyWithCount(8);
                    result.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder));
                    recipeList.add(new CraftingClientRecipe(id, 3, 3, ingredients, SlotContent.of(result)));
                });
            }
            else if (recipe instanceof DecoratedPotRecipe) {
                DecoratedPotRecipeAccessor accessor = (DecoratedPotRecipeAccessor) recipe;

                HashMap<Integer, SlotContent> ingredients = new HashMap<>();
                ingredients.put(1, SlotContent.of(accessor.getLeftPattern()));
                ingredients.put(3, SlotContent.of(accessor.getRightPattern()));
                ingredients.put(5, SlotContent.of(accessor.getBackPattern()));
                ingredients.put(7, SlotContent.of(accessor.getFrontPattern()));

                List<ItemStack> results = new ArrayList<>();
                for (Item ingredient : getItemsFromIngredient(accessor.getFrontPattern())) {
                    PotDecorations decorations = new PotDecorations(ingredient, ingredient, ingredient, ingredient);
                    DataComponentPatch components = DataComponentPatch.builder().set(DataComponents.POT_DECORATIONS, decorations).build();
                    results.add(accessor.getResult().apply(components));
                }

                recipeList.add(new CraftingClientRecipe(id, 3, 3, ingredients, SlotContent.of(results), 7));
            }
            else if (recipe instanceof BookCloningRecipe) {
                BookCloningRecipeAccessor accessor = (BookCloningRecipeAccessor) recipe;
                recipeList.add(new CraftingClientRecipe(id, accessor.getSource(), accessor.getMaterial(), accessor.getResult().withCount(2)));
            }
        });
    }

    private static void addFuelRecipes(List<ReliableClientRecipe> recipeList) {
        FuelValues fuelValues = Minecraft.getInstance().level.fuelValues();
        fuelValues.fuelItems().forEach(item -> {
            //? fabric
            recipeList.add(new BurningClientRecipe(item, fuelValues.burnDuration(new ItemStack(item))));
            //? neoforge
            //recipeList.add(new BurningClientRecipe(item, item.getDefaultInstance().getBurnTime(null, fuelValues)));
        });
    }

    private static void addSmithingRecipes(List<ReliableClientRecipe> recipeList) {
        ClientRecipeManager.getRecipesForType(RecipeType.SMITHING).forEach(smithingRecipeRecipeHolder -> {
            var smithingRecipe = smithingRecipeRecipeHolder.value();

            if (smithingRecipe instanceof SmithingTrimRecipe trimRecipe)
                recipeList.add(new SmithingClientRecipe(smithingRecipeRecipeHolder.id().identifier(), true, trimRecipe.baseIngredient(), trimRecipe.templateIngredient().orElse(null), trimRecipe.additionIngredient().orElse(null), trimRecipe.pattern.value(), null));

            if (smithingRecipe instanceof SmithingTransformRecipe transformRecipe) {
                recipeList.add(new SmithingClientRecipe(smithingRecipeRecipeHolder.id().identifier(), false, transformRecipe.baseIngredient(), transformRecipe.templateIngredient().orElse(null), transformRecipe.additionIngredient().orElse(null), null, transformRecipe.result));
            }

        });
    }

    private static void addBrewingRecipes(List<ReliableClientRecipe> recipeList) {
        PotionBrewing potionBrewing = Minecraft.getInstance().level.potionBrewing();
        List<PotionBrewing.Mix<Potion>> potionMixes = ((PotionBrewingAccessor) potionBrewing).getPotionMixes();
        List<PotionBrewing.Mix<Item>> containerMixes = ((PotionBrewingAccessor) potionBrewing).getContainerMixes();

        containerMixes.forEach(itemMix -> {
            recipeList.add(new BrewingClientRecipe(new ItemStack(itemMix.to().value()), itemMix.ingredient(), new ItemStack(itemMix.from().value())));
        });

        potionMixes.forEach(potionMix -> {
            recipeList.add(new BrewingClientRecipe(PotionContents.createItemStack(Items.POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.POTION, potionMix.from())));
            recipeList.add(new BrewingClientRecipe(PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.SPLASH_POTION, potionMix.from())));
            recipeList.add(new BrewingClientRecipe(PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.to()), potionMix.ingredient(), PotionContents.createItemStack(Items.LINGERING_POTION, potionMix.from())));

        });
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
                recipeList.add(new AnvilCombiningClientRecipe(entry.getKey().identifier().withPrefix("/repairing/"), SlotContent.of(damagedStack), SlotContent.of(repairable.items()), SlotContent.of(stack), -10));
            }
        });
    }

    private Collection<? extends ReliableClientRecipe> addWorldInteractionRecipes() {
        ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes = new ArrayList<>();
        addResourceDrivenWorldInteractionRecipes(worldInteractionRecipes);
        ItemViewRecipes.addAllWorldInteractionRecipes(worldInteractionRecipes);

        var axes = SlotContent.of(ItemTags.AXES);
        var shovels = SlotContent.of(ItemTags.SHOVELS);
        BuiltInRegistries.BLOCK.entrySet().forEach((entry -> {
            var block = entry.getValue();
            var id = entry.getKey().identifier();
            if (block instanceof WeatheringCopper weatheringCopper) {
                Optional<Block> next = WeatheringCopper.getNext(block);
                next.ifPresent(value -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(id.withPrefix("/world_interaction/").withSuffix("_oxidizing"), SlotContent.of(block), WorldInteractionClientRecipe.TIME, SlotContent.of(value.asItem()))));

                Optional<Block> previous = WeatheringCopper.getPrevious(block);
                previous.ifPresent(value -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(id.withPrefix("/world_interaction/").withSuffix("_reverse_oxidizing"), SlotContent.of(block), axes, SlotContent.of(value.asItem()))));
            }
            if (block instanceof TallFlowerBlock || block instanceof FlowerBedBlock) {
                worldInteractionRecipes.add(new WorldInteractionClientRecipe(id.withPrefix("/world_interaction/").withSuffix("_bone_meal"), SlotContent.of(block), SlotContent.of(Items.BONE_MEAL), SlotContent.of(new ItemStack(block, 2))));
            }
            //? neoforge {
                /*if (block.builtInRegistryHolder().getData(NeoForgeDataMaps.WAXABLES) != null) {
                    worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block.builtInRegistryHolder().getData(NeoForgeDataMaps.WAXABLES).waxed()), axes, SlotContent.of(block)));
                    worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), SlotContent.of(Items.HONEYCOMB), SlotContent.of(block.builtInRegistryHolder().getData(NeoForgeDataMaps.WAXABLES).waxed())));
                }
                if (block.builtInRegistryHolder().getData(NeoForgeDataMaps.STRIPPABLES) != null) {
                    worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), axes, SlotContent.of(block.builtInRegistryHolder().getData(NeoForgeDataMaps.STRIPPABLES).strippedBlock())));
                }
                *///?}
        }));

        // honeycomb
        //? fabric {
        HoneycombItem.WAXABLES.get().forEach(((block, block2) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/waxable_"+block.builtInRegistryHolder().key().identifier().toString().replace(":", "_")), SlotContent.of(block), SlotContent.of(Items.HONEYCOMB), SlotContent.of(block2.asItem())))));
        HoneycombItem.WAX_OFF_BY_BLOCK.get().forEach(((block, block2) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/wax_off_"+block.builtInRegistryHolder().key().identifier().toString().replace(":", "_")), SlotContent.of(block), axes, SlotContent.of(block2.asItem())))));
        AxeItem.STRIPPABLES.forEach(((block, state) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/strippable_"+block.builtInRegistryHolder().key().identifier().toString().replace(":", "_")), SlotContent.of(block), axes, SlotContent.of(state)))));
        //?}

        // flattenables
        ShovelItem.FLATTENABLES.forEach(((block, state) -> {
            worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/shovel_path"), SlotContent.of(block), shovels, SlotContent.of(state.getBlock())));
        }));

        // hoes
        var hoes = SlotContent.of(ItemTags.HOES);
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/hoe_farmland"), SlotContent.of(Ingredient.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.DIRT_PATH)), hoes, SlotContent.of(Items.FARMLAND)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/hoe_dirt"),SlotContent.of(Blocks.ROOTED_DIRT), hoes, SlotContent.of(Items.DIRT)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/hoe_hanging_roots"),SlotContent.of(Blocks.ROOTED_DIRT), hoes, SlotContent.of(Items.HANGING_ROOTS)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/shearing_bee_nest"), SlotContent.of(Ingredient.of(Blocks.BEEHIVE, Blocks.BEE_NEST)), SlotContent.of(Items.SHEARS), SlotContent.of(new ItemStack(Items.HONEYCOMB, 3))));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/glass_bottle_bee_nest"), SlotContent.of(Ingredient.of(Blocks.BEEHIVE, Blocks.BEE_NEST)), SlotContent.of(Items.GLASS_BOTTLE), SlotContent.of(Items.HONEY_BOTTLE)));

        worldInteractionRecipes.add(new WorldInteractionClientRecipe(Identifier.withDefaultNamespace("/world_interaction/water_filling"), SlotContent.of(Blocks.WATER), SlotContent.of(Items.GLASS_BOTTLE), SlotContent.of(PotionContents.createItemStack(Items.POTION, Potions.WATER))));


        return worldInteractionRecipes;
    }


}
