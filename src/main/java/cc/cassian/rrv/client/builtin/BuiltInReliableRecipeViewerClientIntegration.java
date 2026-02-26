package cc.cassian.rrv.client.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningClientRecipe;
import cc.cassian.rrv.common.builtin.anvil.AnvilCombiningServerRecipe;
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
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.info.InfoServerRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionServerRecipe;
import cc.cassian.rrv.common.builtin.repairing.RepairingClientRecipe;
import cc.cassian.rrv.common.builtin.repairing.RepairingServerRecipe;
import cc.cassian.rrv.common.builtin.shaped.CraftingClientRecipe;
import cc.cassian.rrv.common.builtin.shaped.ShapedServerRecipe;
import cc.cassian.rrv.common.builtin.shapeless.ShapelessServerRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingClientRecipe;
import cc.cassian.rrv.common.builtin.smelting.SmeltingServerRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingClientRecipe;
import cc.cassian.rrv.common.builtin.smithing.SmithingServerRecipe;
import cc.cassian.rrv.common.builtin.smoking.SmokingClientRecipe;
import cc.cassian.rrv.common.builtin.smoking.SmokingServerRecipe;
import cc.cassian.rrv.common.builtin.stonecutting.StonecutterClientRecipe;
import cc.cassian.rrv.common.builtin.stonecutting.StonecutterServerRecipe;
import cc.cassian.rrv.common.builtin.tag.TagClientRecipe;
import cc.cassian.rrv.common.builtin.tag.TagServerRecipe;
import cc.cassian.rrv.common.builtin.tipped_arrow.TippedArrowServerRecipe;
import cc.cassian.rrv.common.builtin.transmute.TransmuteServerRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerClientRecipe;
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.*;
//? neoforge
//import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.io.IOException;
import java.util.*;

import static cc.cassian.rrv.common.ReliableRecipeViewer.LOGGER;

public class BuiltInReliableRecipeViewerClientIntegration implements ReliableRecipeViewerClientPlugin {

    @Override
    public void onIntegrationInitialize() {


        ItemView.addClientReloadCallback(() -> {

            BuiltInRegistries.BLOCK.get(CommonTags.EXCLUDED_BLOCKS).ifPresent(blocks -> blocks.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(block -> ItemView.excludeItem(block.asItem())));
            BuiltInRegistries.ITEM.get(CommonTags.EXCLUDED_ITEMS).ifPresent(items -> items.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(ItemView::excludeItem));
            BuiltInRegistries.FLUID.get(CommonTags.EXCLUDED_FLUIDS).ifPresent(fluids -> fluids.stream().filter(Holder::isBound).filter(Holder::isBound).map(Holder::value).forEach(fluid -> ItemView.excludeItem(fluid.defaultFluidState().createLegacyBlock().getBlock().asItem())));

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
                SlotContent.of(unwrapped.getBase()).getValidContents().forEach(baseStack -> {
                    SlotContent.of(unwrapped.getTemplate()).getValidContents().forEach(templateStack -> {
                        recipes.add(new SmithingClientRecipe(unwrapped.isTrim(), unwrapped.getAddition(), Ingredient.of(baseStack.getItem()), Ingredient.of(templateStack.getItem()), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            } else if (unwrapped.getBase() != null && unwrapped.getAddition() != null) {
                SlotContent.of(unwrapped.getBase()).getValidContents().forEach(baseStack -> {
                    SlotContent.of(unwrapped.getAddition()).getValidContents().forEach(additionStack -> {
                        recipes.add(new SmithingClientRecipe(unwrapped.isTrim(), Ingredient.of(additionStack.getItem()), Ingredient.of(baseStack.getItem()), unwrapped.getTemplate(), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
                    });

                });
            } else if (unwrapped.getAddition() != null && unwrapped.getTemplate() != null) {
                SlotContent.of(unwrapped.getTemplate()).getValidContents().forEach(templateStack -> {
                    SlotContent.of(unwrapped.getAddition()).getValidContents().forEach(additionStack -> {
                        recipes.add(new SmithingClientRecipe(unwrapped.isTrim(), Ingredient.of(additionStack.getItem()), unwrapped.getBase(), Ingredient.of(templateStack.getItem()), unwrapped.getPattern(), unwrapped.getUpgradeResult()));
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
        ItemView.addClientRecipeWrapper(TagServerRecipe.TYPE, unwrapped -> List.of(new TagClientRecipe(unwrapped)));
        // info
        ItemView.addClientRecipeWrapper(InfoServerRecipe.TYPE, modRecipe -> {
            ArrayList<InfoClientRecipe> infoRecipes = new ArrayList<>();
            Map<Identifier, Resource> identifierResourceMap = Minecraft.getInstance().getResourceManager().listResources("rrv_info", (identifier) -> true);
            identifierResourceMap.forEach((identifier, resource) -> {
				try {
                    JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
                    if (parsedRecipe.get("type").getAsString().equals("rrv:info")) {
                        var text = parsedRecipe.get("text").getAsString();
                        infoRecipes.add(new InfoClientRecipe(RrvUtil.readSlotContent("key", "info", identifier, parsedRecipe), text));
                    } else {
                        LOGGER.error("Could not parse info recipe '{}' as it was missing a type!", identifier);
                    }
                } catch (IOException e) {
					LOGGER.error("Could not parse info recipe '{}' due to an exception: ", identifier, e);
				}
			});
            return infoRecipes;
        });
        // world interaction
        ItemView.addClientRecipeWrapper(WorldInteractionServerRecipe.TYPE, modRecipe -> {
            ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes = new ArrayList<>();

            Map<Identifier, Resource> identifierResourceMap = Minecraft.getInstance().getResourceManager().listResources("rrv_world_interaction", (identifier) -> true);
            for (Map.Entry<Identifier, Resource> entry : identifierResourceMap.entrySet()) {
                var slots = getSlotContents("world_interaction", entry);
                if (slots != null)
                    worldInteractionRecipes.add(new WorldInteractionClientRecipe(slots.left, slots.right, slots.result, slots.priority));
            }

            var axes = SlotContent.of(ItemTags.AXES);
            var shovels = SlotContent.of(ItemTags.SHOVELS);
            BuiltInRegistries.BLOCK.stream().forEach((block -> {
                if (block instanceof WeatheringCopper weatheringCopper) {
                    Optional<Block> next = WeatheringCopper.getNext(block);
                    next.ifPresent(value -> worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), SlotContent.of(new ItemStack(Items.CLOCK.builtInRegistryHolder(), 1, DataComponentPatch.builder().set(DataComponents.ITEM_NAME, Component.translatable("view.rrv.type.world_interaction.time")).set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("view.rrv.type.world_interaction.time_passes")))).build())), SlotContent.of(value.asItem()))));

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
            HoneycombItem.WAXABLES.get().forEach(((block, block2) -> {
				worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), SlotContent.of(Items.HONEYCOMB), SlotContent.of(block2.asItem())));
            }));
            HoneycombItem.WAX_OFF_BY_BLOCK.get().forEach(((block, block2) -> {
				worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), axes, SlotContent.of(block2.asItem())));
            }));
            AxeItem.STRIPPABLES.forEach(((block, state) -> {
                worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(block), axes, SlotContent.of(state)));
            }));
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

            worldInteractionRecipes.add(new WorldInteractionClientRecipe(SlotContent.of(Items.GLASS_BOTTLE), SlotContent.of(Blocks.WATER), SlotContent.of(PotionContents.createItemStack(Items.POTION, Potions.WATER))));


            return worldInteractionRecipes;
        });
        // repairing
        ItemView.addClientRecipeWrapper(AnvilCombiningServerRecipe.TYPE, modRecipe -> {
                ArrayList<AnvilCombiningClientRecipe> anvilCombiningRecipes = new ArrayList<>();
                Map<Identifier, Resource> identifierResourceMap = Minecraft.getInstance().getResourceManager().listResources("rrv_anvil_combining", (identifier) -> true);
                for (Map.Entry<Identifier, Resource> entry : identifierResourceMap.entrySet()) {
                    var slots = getSlotContents("anvil_combining", entry);
                    if (slots != null)
                        anvilCombiningRecipes.add(new AnvilCombiningClientRecipe(slots.left, slots.right, slots.result, slots.priority));
                }
                return anvilCombiningRecipes;
        });
        ItemView.addClientRecipeWrapper(RepairingServerRecipe.TYPE, unwrapped -> List.of(new RepairingClientRecipe(unwrapped.getBase(), unwrapped.getTemplate(), unwrapped.getResult())));
    }

    private static ResourceDrivenRecipeResult getSlotContents(String type, Map.Entry<Identifier, Resource> entry) {
        String typeSpaced = type.replace("_", " ");
        Identifier identifier = entry.getKey();
        Resource resource = entry.getValue();
        try {
            JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
            if (parsedRecipe.get("type").getAsString().equals("rrv:" + type)) {

                SlotContent left = RrvUtil.readSlotContent("left", typeSpaced, identifier, parsedRecipe);
                SlotContent right = RrvUtil.readSlotContent("right", typeSpaced, identifier, parsedRecipe);
                SlotContent result = RrvUtil.readSlotContent("result", typeSpaced, identifier, parsedRecipe);
                int priority = 0;
                if (parsedRecipe.has("priority") && parsedRecipe.get("priority").isJsonPrimitive() && parsedRecipe.getAsJsonPrimitive("priority").isNumber())
                    priority = parsedRecipe.getAsJsonPrimitive("priority").getAsInt();
                return new ResourceDrivenRecipeResult(left, right, result, priority);
            } else {
                LOGGER.error("Could not parse {} recipe '{}' as it was missing a type!", typeSpaced, identifier);
            }
        } catch (IOException e) {
            LOGGER.error("Could not parse {} recipe '{}' due to an exception: ", typeSpaced, identifier, e);
        }
        return null;
    }
    
    public record ResourceDrivenRecipeResult(SlotContent left, SlotContent right, SlotContent result, int priority) {}


}
