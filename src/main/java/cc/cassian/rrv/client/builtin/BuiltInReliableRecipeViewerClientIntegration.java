package cc.cassian.rrv.client.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningServerRecipe;
import cc.cassian.rrv.common.builtin.anvil.ResourceDrivenAnvilCombiningServerRecipe;
import cc.cassian.rrv.common.builtin.blasting.BlastingClientRecipe;
import cc.cassian.rrv.common.builtin.blasting.BlastingServerRecipe;
import cc.cassian.rrv.common.builtin.brewing.BrewingClientRecipe;
import cc.cassian.rrv.common.builtin.brewing.BrewingServerRecipe;
import cc.cassian.rrv.common.builtin.burning.BurningClientRecipe;
import cc.cassian.rrv.common.builtin.burning.BurningServerRecipe;
import cc.cassian.rrv.common.builtin.campfire.CampfireClientRecipe;
import cc.cassian.rrv.common.builtin.campfire.CampfireServerRecipe;
import cc.cassian.rrv.common.builtin.entity.EntityClientRecipe;
import cc.cassian.rrv.common.builtin.entity.EntityServerRecipe;
import cc.cassian.rrv.common.builtin.info.InfoServerRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionServerRecipe;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.crafting.CraftingClientRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.ShapedServerRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.ShapelessServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingClientRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingClientRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingServerRecipe;
import cc.cassian.rrv.common.builtin.smoking.SmokingClientRecipe;
import cc.cassian.rrv.common.builtin.smoking.SmokingServerRecipe;
import cc.cassian.rrv.common.builtin.stonecutting.StonecutterClientRecipe;
import cc.cassian.rrv.common.builtin.stonecutting.StonecutterServerRecipe;
import cc.cassian.rrv.common.builtin.tag.item.ItemTagClientRecipe;
import cc.cassian.rrv.common.builtin.tag.item.ItemTagServerRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.TippedArrowServerRecipe;
import cc.cassian.rrv.common.builtin.crafting.recipes.TransmuteServerRecipe;
import cc.cassian.rrv.common.builtin.tag.block.BlockTagClientRecipe;
import cc.cassian.rrv.common.builtin.tag.block.BlockTagServerRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerClientRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.*;
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
        ItemView.addClientRecipeWrapper(BurningServerRecipe.TYPE, unwrapped -> List.of(new BurningClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(SmeltingServerRecipe.TYPE, unwrapped -> List.of(new SmeltingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(BlastingServerRecipe.TYPE, unwrapped -> List.of(new BlastingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(SmokingServerRecipe.TYPE, unwrapped -> List.of(new SmokingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(ShapelessServerRecipe.TYPE, unwrapped -> List.of(new CraftingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(ShapedServerRecipe.TYPE, unwrapped -> List.of(new CraftingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(TransmuteServerRecipe.TYPE, unwrapped -> List.of(new CraftingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(TippedArrowServerRecipe.TYPE, unwrapped -> List.of(new CraftingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(CampfireServerRecipe.TYPE, unwrapped -> List.of(new CampfireClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(StonecutterServerRecipe.TYPE, unwrapped -> List.of(new StonecutterClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(SmithingServerRecipe.TYPE, unwrapped -> {
            List<SmithingClientRecipe> recipes = new ArrayList<>();

            if (unwrapped.getBase() != null && unwrapped.getTemplate() != null) {
                unwrapped.getBase().getValidContents().forEach(baseStack -> {
                    unwrapped.getTemplate().getValidContents().forEach(templateStack -> {
                        recipes.add(new SmithingClientRecipe(unwrapped.isTrim(), unwrapped.getAddition(), SlotContent.of(baseStack), SlotContent.of(templateStack), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            } else if (unwrapped.getBase() != null && unwrapped.getAddition() != null) {
                unwrapped.getBase().getValidContents().forEach(baseStack -> {
                    unwrapped.getAddition().getValidContents().forEach(additionStack -> {
                        recipes.add(new SmithingClientRecipe(unwrapped.isTrim(), SlotContent.of(additionStack), SlotContent.of(baseStack), unwrapped.getTemplate(), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            } else if (unwrapped.getAddition() != null && unwrapped.getTemplate() != null) {
                unwrapped.getTemplate().getValidContents().forEach(templateStack -> {
                    unwrapped.getAddition().getValidContents().forEach(additionStack -> {
                        recipes.add(new SmithingClientRecipe(unwrapped.isTrim(), SlotContent.of(additionStack), unwrapped.getBase(), SlotContent.of(templateStack), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            }


            return recipes;
        });
        ItemView.addClientRecipeWrapper(BrewingServerRecipe.TYPE, unwrapped -> List.of(new BrewingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(VillagerServerRecipe.TYPE, unwrapped -> {
            return unwrapped.getOffers().stream().map(VillagerClientRecipe::new).toList();
        });
        ItemView.addClientRecipeWrapper(EntityServerRecipe.TYPE, unwrapped -> List.of(new EntityClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(ItemTagServerRecipe.TYPE, unwrapped -> {
            TagKey<Item> tagKey = unwrapped.getTagKey();
            if (HIDDEN_ITEM_TAGS.contains(tagKey.location())) {
                return List.of();
            }
            return List.of(new ItemTagClientRecipe(tagKey));
		});
        ItemView.addClientRecipeWrapper(BlockTagServerRecipe.TYPE, unwrapped -> {
            TagKey<Block> tagKey = unwrapped.getTagKey();
            if (HIDDEN_BLOCK_TAGS.contains(tagKey.location())) {
                return List.of();
            }
            return List.of(new BlockTagClientRecipe(tagKey));
		});
        // info
        ItemView.addClientRecipeWrapper(InfoServerRecipe.TYPE, modRecipe -> addInfoRecipes());
        // world interaction
        ItemView.addClientRecipeWrapper(WorldInteractionServerRecipe.TYPE, modRecipe -> {
            ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes = new ArrayList<>();
            addResourceDrivenWorldInteractionRecipes(worldInteractionRecipes);
            ItemView.addAllWorldInteractionRecipes(worldInteractionRecipes);

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
        });
        // repairing
        ItemView.addClientRecipeWrapper(AnvilCombiningServerRecipe.TYPE, modRecipe -> List.of(new AnvilCombiningClientRecipe(modRecipe.getLeft(), modRecipe.getRight(), modRecipe.getResult())));
        ItemView.addClientRecipeWrapper(ResourceDrivenAnvilCombiningServerRecipe.TYPE, modRecipe -> addAnvilCombiningRecipes());
    }


}
