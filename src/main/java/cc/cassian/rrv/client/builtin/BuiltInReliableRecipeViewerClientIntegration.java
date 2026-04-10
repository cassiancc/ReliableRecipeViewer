package cc.cassian.rrv.client.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.builtin.SynchronizedServerRecipeStub;
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
import cc.cassian.rrv.common.mixin.world.item.crafting.DyeRecipeAccessor;
import cc.cassian.rrv.common.mixin.world.item.crafting.IngredientAccessor;
import cc.cassian.rrv.common.mixin.world.item.crafting.TransmuteRecipeAccessor;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.SynchronizedRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
//? neoforge
//import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.*;

import static cc.cassian.rrv.common.recipe.ResourceRecipeManager.*;

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
        //FIXME - proper API
        ItemView.addClientRecipeWrapper(SynchronizedServerRecipeStub.TYPE, modRecipe -> {

            ArrayList<ReliableClientRecipe> recipeList = new ArrayList<>();

            // Crafting
            addCraftingRecipes(recipeList);

            //Tipped arrows
            addTippedArrowRecipes(recipeList);
            // Smelting
            SynchronizedRecipeManager.getAllOfType(RecipeType.SMELTING).forEach(smeltingRecipeRecipeHolder -> recipeList.add(new SmeltingClientRecipe(smeltingRecipeRecipeHolder)));
            // Blasting
            SynchronizedRecipeManager.getAllOfType(RecipeType.BLASTING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new BlastingClientRecipe(smokingRecipeRecipeHolder)));
            // Smoking
            SynchronizedRecipeManager.getAllOfType(RecipeType.SMOKING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new SmokingClientRecipe(smokingRecipeRecipeHolder)));
            // Campfire
            SynchronizedRecipeManager.getAllOfType(RecipeType.CAMPFIRE_COOKING).forEach(smokingRecipeRecipeHolder -> recipeList.add(new CampfireClientRecipe(smokingRecipeRecipeHolder)));
            // Fuel
            addFuelRecipes(recipeList);

            // Smithing
            addSmithingRecipes(recipeList);

            SynchronizedRecipeManager.getAllOfType(RecipeType.STONECUTTING).forEach(stonecutterRecipeRecipeHolder -> recipeList.add(new StonecutterClientRecipe(stonecutterRecipeRecipeHolder)));

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

            return recipeList;
        });
    }

    private static void addCraftingRecipes(ArrayList<ReliableClientRecipe> recipeList) {
        SynchronizedRecipeManager.getAllOfType(RecipeType.CRAFTING).forEach(craftingRecipeHolder -> {
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
                    recipeList.add(new CraftingClientRecipe(id, accessor.getInput(), accessor.getMaterial(), results));

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
                recipeList.add(new CraftingClientRecipe(id, accessor.getTarget(), accessor.getDye(), results, 1));
            }
        });
    }

    private static void addTippedArrowRecipes(ArrayList<ReliableClientRecipe> recipeList) {
        Registry<Potion> potionRegistry = ServerRecipeManager.INSTANCE.getServer().registryAccess().lookupOrThrow(Registries.POTION);
        potionRegistry.forEach(potion -> {
            Holder<Potion> potionHolder = potionRegistry.wrapAsHolder(potion);
            ItemStack potionStack = PotionContents.createItemStack(Items.LINGERING_POTION, potionHolder);
            HashMap<Integer, SlotContent> ingredients = new HashMap<>();
            for (int i = 0; i < 9; i++){
                if (i == 4)
                    ingredients.put(i, SlotContent.of(potionStack));
                else
                    ingredients.put(i, SlotContent.of(Items.ARROW));
            }

            ItemStack result = new ItemStack(Items.TIPPED_ARROW, 8);
            result.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder));
//            var id = potionHolder.unwrapKey().orElseThrow().identifier().withSuffix("_tipped_arrow_crafting");
            recipeList.add(new CraftingClientRecipe(null, 3, 3, ingredients, SlotContent.of(result)));
        });
    }

    private static void addFuelRecipes(ArrayList<ReliableClientRecipe> recipeList) {
        FuelValues fuelValues = Minecraft.getInstance().level.fuelValues();
        fuelValues.fuelItems().forEach(item -> {
            //? fabric
            recipeList.add(new BurningClientRecipe(item, fuelValues.burnDuration(new ItemStack(item))));
            //? neoforge
            //recipeList.add(new BurningClientRecipe(item, item.getDefaultInstance().getBurnTime(null, fuelValues)));
        });
    }

    private static void addSmithingRecipes(ArrayList<ReliableClientRecipe> recipeList) {
        SynchronizedRecipeManager.getAllOfType(RecipeType.SMITHING).forEach(smithingRecipeRecipeHolder -> {
            var smithingRecipe = smithingRecipeRecipeHolder.value();

            if (smithingRecipe instanceof SmithingTrimRecipe trimRecipe)
                recipeList.add(new SmithingClientRecipe(smithingRecipeRecipeHolder.id().identifier(), true, trimRecipe.baseIngredient(), trimRecipe.templateIngredient().orElse(null), trimRecipe.additionIngredient().orElse(null), trimRecipe.pattern.value(), null));

            if (smithingRecipe instanceof SmithingTransformRecipe transformRecipe) {
                recipeList.add(new SmithingClientRecipe(smithingRecipeRecipeHolder.id().identifier(), false, transformRecipe.baseIngredient(), transformRecipe.templateIngredient().orElse(null), transformRecipe.additionIngredient().orElse(null), null, transformRecipe.result));
            }

        });
    }

    private static void addBrewingRecipes(ArrayList<ReliableClientRecipe> recipeList) {
        PotionBrewing potionBrewing = ServerRecipeManager.INSTANCE.getServer().potionBrewing();
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

    private static void addRepairingRecipes(ArrayList<ReliableClientRecipe> recipeList) {
        BuiltInRegistries.ITEM.forEach(item -> {
            var stack = item.getDefaultInstance();
            if (stack.has(DataComponents.REPAIRABLE)) {
                Repairable repairable = stack.get(DataComponents.REPAIRABLE);
                var damagedStack = stack.copy();
                damagedStack.setDamageValue(stack.getMaxDamage() / 2);
                assert repairable != null;
                recipeList.add(new AnvilCombiningClientRecipe(SlotContent.of(damagedStack), SlotContent.of(repairable.items()), SlotContent.of(stack)));
            }
        });
    }

    private Collection<? extends ReliableClientRecipe> addWorldInteractionRecipes() {
        ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes = new ArrayList<>();
        addResourceDrivenWorldInteractionRecipes(worldInteractionRecipes);
        ItemViewRecipes.addAllWorldInteractionRecipes(worldInteractionRecipes);

        var axes = SlotContent.of(ItemTags.AXES);
        var shovels = SlotContent.of(ItemTags.SHOVELS);
        BuiltInRegistries.BLOCK.stream().forEach((block -> {
            if (block instanceof WeatheringCopper weatheringCopper) {
                Optional<Block> next = WeatheringCopper.getNext(block);
                next.ifPresent(value -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), WorldInteractionClientRecipe.TIME, SlotContent.of(value.asItem()))));

                Optional<Block> previous = WeatheringCopper.getPrevious(block);
                previous.ifPresent(value -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), axes, SlotContent.of(value.asItem()))));
            }
            if (block instanceof TallFlowerBlock || block instanceof FlowerBedBlock) {
                worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), SlotContent.of(Items.BONE_MEAL), SlotContent.of(new ItemStack(block, 2))));
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
        HoneycombItem.WAXABLES.get().forEach(((block, block2) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), SlotContent.of(Items.HONEYCOMB), SlotContent.of(block2.asItem())))));
        HoneycombItem.WAX_OFF_BY_BLOCK.get().forEach(((block, block2) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), axes, SlotContent.of(block2.asItem())))));
        AxeItem.STRIPPABLES.forEach(((block, state) -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), axes, SlotContent.of(state)))));
        //?}

        // flattenables
        ShovelItem.FLATTENABLES.forEach(((block, state) -> {
            worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), shovels, SlotContent.of(state.getBlock())));
        }));

        // hoes
        var hoes = SlotContent.of(ItemTags.HOES);
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(Ingredient.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.DIRT_PATH)), hoes, SlotContent.of(Items.FARMLAND)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(Blocks.ROOTED_DIRT), hoes, SlotContent.of(Items.DIRT)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(Blocks.ROOTED_DIRT), hoes, SlotContent.of(Items.HANGING_ROOTS)));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(Ingredient.of(Blocks.BEEHIVE, Blocks.BEE_NEST)), SlotContent.of(Items.SHEARS), SlotContent.of(new ItemStack(Items.HONEYCOMB, 3))));
        worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(Ingredient.of(Blocks.BEEHIVE, Blocks.BEE_NEST)), SlotContent.of(Items.GLASS_BOTTLE), SlotContent.of(Items.HONEY_BOTTLE)));

        worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(Blocks.WATER), SlotContent.of(Items.GLASS_BOTTLE), SlotContent.of(PotionContents.createItemStack(Items.POTION, Potions.WATER))));


        return worldInteractionRecipes;
    }


}
