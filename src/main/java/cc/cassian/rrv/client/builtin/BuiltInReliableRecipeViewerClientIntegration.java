package cc.cassian.rrv.client.builtin;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
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
import cc.cassian.rrv.common.builtin.shaped.CraftingClientRecipe;
import cc.cassian.rrv.common.builtin.shaped.ShapedServerRecipe;
import cc.cassian.rrv.common.builtin.shapeless.ShapelessClientRecipe;
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
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.item.crafting.*;

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
        ItemView.addClientRecipeWrapper(ShapelessServerRecipe.TYPE, unwrapped -> List.of(new ShapelessClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(ShapedServerRecipe.TYPE, unwrapped -> List.of(new CraftingClientRecipe(unwrapped)));
        ItemView.addClientRecipeWrapper(TransmuteServerRecipe.TYPE, unwrapped -> List.of(new ShapelessClientRecipe(unwrapped)));
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
        ItemView.addClientRecipeWrapper(InfoServerRecipe.TYPE, modRecipe -> {
            ArrayList<InfoClientRecipe> infoRecipes = new ArrayList<>();
            Map<Identifier, Resource> identifierResourceMap = Minecraft.getInstance().getResourceManager().listResources("rrv_info", (identifier) -> true);
            identifierResourceMap.forEach((identifier, resource) -> {
				try {
                    JsonObject parsedRecipe = StrictJsonParser.parse(resource.openAsReader()).getAsJsonObject();
                    if (parsedRecipe.get("type").getAsString().equals("rrv:info")) {
                        var text = parsedRecipe.get("text").getAsString();
                        if (parsedRecipe.get("key").isJsonPrimitive() && parsedRecipe.get("key").getAsJsonPrimitive().isString()) {
                            var itemText = parsedRecipe.get("key").getAsString();
                            if (itemText.contains("#")) {
                                var item = TagKey.create(Registries.ITEM, Identifier.parse(itemText.replace("#", "")));
                                infoRecipes.add(new InfoClientRecipe(SlotContent.of(item), text));
                            } else {
                                var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemText));
                                infoRecipes.add(new InfoClientRecipe(SlotContent.of(item), text));
                            }
                        } else {
                            LOGGER.error("Could not parse info recipe '{}' as it was missing a key!", identifier);
                        }
                    } else {
                        LOGGER.error("Could not parse info recipe '{}' as it was missing a type!", identifier);
                    }
                } catch (IOException e) {
					LOGGER.error("Could not parse info recipe '{}' due to an exception: ", identifier, e);
				}
			});
            return infoRecipes;
        });
    }


}
