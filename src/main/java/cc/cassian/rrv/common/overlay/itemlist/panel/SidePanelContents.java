package cc.cassian.rrv.common.overlay.itemlist.panel;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/// The contents of a side panel.
public class SidePanelContents {

	/// Why this set of contents was created.
	private final SidePanelOverlay.Reason reason;
	/// The current local player.
	private final Player player;
	/// Whether the currently open screen is the creative inventory screen - has some special casing as it will never have valid craftables.
	private final boolean creativeScreen;
	/// Cached set of available items.
	private static List<ItemStack> lastAvailableItems = new ArrayList<>();

	public SidePanelContents(SidePanelOverlay.Reason reason, Player player, boolean creativeScreen) {
		this.reason = reason;
		this.player = player;
		this.creativeScreen = creativeScreen;
	}

	public static List<ItemStack> bookmarks(SidePanelContents contents) {
		return BookmarkManager.INSTANCE.displayItems();
	}

	public static List<ItemStack> craftables(SidePanelContents contents) {
		ArrayList<ItemStack> availableItems = new ArrayList<>();
		var player = contents.player;
		if (player == null) {
			return availableItems;
		}
		// when searching, use the last unfiltered list rather than constantly querying the recipe manager
		if (!contents.reason().equals(SidePanelOverlay.Reason.SEARCH)) {
			var inventory = player.getInventory().getNonEquipmentItems();

			// search by what craftables the workstation supports
			if (!(contents.creativeScreen))
				inventory.forEach(inventoryItem -> {
					ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, availableItems, player, true));
				});

			// if the workstation is not supported, search by what craftables exist
			if (availableItems.isEmpty()) {
				try {
					inventory.forEach(inventoryItem -> {
						ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, availableItems, player, false));
					});
				} catch (ConcurrentModificationException ignored) {}
			}

			// save last available items for when searching occurs
			lastAvailableItems = new ArrayList<>(availableItems);
		} else {
			availableItems.addAll(lastAvailableItems);
		}

		filter(availableItems);
		RrvUtil.sortByName(availableItems);
		return availableItems;
	}

	public static void filter(List<ItemStack> availableItems) {
		for (String query : ItemViewOverlay.INSTANCE.getCurrentQueries()) {
			String substring = RrvUtil.lowercaseSubstring(query);
			if (!ItemFilters.advancedFilter(availableItems, query)) {
				availableItems.removeIf(stack-> !stack.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(substring));
			}
		}
	}

	public static List<ItemStack> disabled(SidePanelContents contents) {
		return Collections.emptyList();
	}

	static void updateRecipes(ReliableClientRecipe recipe, List<ItemStack> availableItems, Player player, boolean b) {
		if (recipe.isVisualOnly() || !Configs.CATEGORIES.enabled(recipe.getType())) return;
		if (b && !RRVClientUtil.matchesAnyTransferClass(recipe, RRVClientUtil.currentScreen())) return;
		AtomicInteger foundIngredientCount = new AtomicInteger();
		int requiredIngredientCount = recipe.getIngredients().size();
		recipe.getIngredients().forEach(ingredient -> {
			if (player.getInventory().hasAnyMatching(inv->ingredient.hasItem(inv.getItem()))) {
				foundIngredientCount.getAndIncrement();
			}
		});
		if (foundIngredientCount.get() == requiredIngredientCount) {
			recipe.getResults().forEach(result -> {
				result.getValidContents().forEach(ingredient -> {
					CompoundTag compoundTag = new CompoundTag();
					String recipeId = recipe.entryId().toString();
					compoundTag.putString("rrv_result", recipeId);
					ingredient.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
					Optional<ItemStack> first = availableItems.stream().filter(i-> {
						if (i.has(DataComponents.CUSTOM_DATA)) {
							var data = i.get(DataComponents.CUSTOM_DATA).copyTag();
							if (data.contains("rrv_result")) {
								return data.getString("rrv_result").orElseThrow().equals(recipeId);
							}
						}
						return false;
					}).findFirst();
					if (first.isEmpty()) {
						availableItems.add(ingredient);
					}
				});
			});
		}
	}

	public SidePanelOverlay.Reason reason() {
		return reason;
	}
}
