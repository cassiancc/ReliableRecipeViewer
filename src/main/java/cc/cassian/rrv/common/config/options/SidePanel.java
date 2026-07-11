package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.recipe.ClientUnlockManager;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum SidePanel implements StringRepresentable {
	BOOKMARKS(1),
	CRAFTABLES(2),
	UNLOCKED(3),
	DISABLED(0);

	private final int id;

	SidePanel(int id) {
		this.id = id;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static final Codec<SidePanel> CODEC = StringRepresentable.fromEnum(SidePanel::values);

	private static synchronized SidePanel get(int id) {
		SidePanel[] values = values();
		for (SidePanel type : values) {
			if (type.getId() == id) {
				return type;
			}
		}
		return SidePanel.DISABLED;
	}

	public int getId() {
		return id;
	}

    public static void next(boolean skipDisabled) {
		int id1 = Configs.CLIENT_SETTINGS.getSidePanel().id + 1;
		if (id1 > 3) id1 = 0;
		if (id1 == 0 && skipDisabled) id1 = 1;
		Configs.CLIENT_SETTINGS.setSidePanel(get(id1));
    }

	public static ArrayList<ItemStack> lastAvailableItems = new ArrayList<>();
	public static @Nullable AbstractContainerScreen<? extends AbstractContainerMenu> currentScreen = null;


	public static List<ItemStack> populateSlots(SidePanelOverlay.Reason reason, Screen screen) {
		ArrayList<ItemStack> availableItems = new ArrayList<>();
		switch (Configs.CLIENT_SETTINGS.getSidePanel()) {
			case BOOKMARKS -> {
				availableItems.addAll(BookmarkManager.INSTANCE.displayItems());
			}
			case CRAFTABLES -> {
				Util.backgroundExecutor().execute(() -> {
					Minecraft client = Minecraft.getInstance();
					LocalPlayer player = client.player;
					if (player == null) {
						return;
					}
					// when searching, use the last unfiltered list rather than constantly querying the recipe manager
					if (!reason.equals(SidePanelOverlay.Reason.SEARCH)) {
						var inventory = player.getInventory().getNonEquipmentItems();

						// search by what craftables the workstation supports
						if (!(screen instanceof CreativeModeInventoryScreen))
							inventory.forEach(inventoryItem -> {
								ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, true, availableItems));
							});

						// if the workstation is not supported, search by what craftables exist
						if (availableItems.isEmpty()) {
							try {
								inventory.forEach(inventoryItem -> {
									ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, false, availableItems));
								});
							} catch (ConcurrentModificationException ignored) {}
						}

						// save last available items for when searching occurs
						SidePanel.lastAvailableItems = new ArrayList<>(availableItems);
					} else {
						availableItems.addAll(SidePanel.lastAvailableItems);
					}

					filter(availableItems);
					availableItems.sort(Comparator.comparing(i -> i.getDisplayName().getString()));
					if (screen == SidePanel.currentScreen && SidePanelOverlay.INSTANCE.availableItems().isEmpty()) {
						SidePanelOverlay.INSTANCE.availableItems().addAll(availableItems);
						Minecraft.getInstance().execute(SidePanelOverlay.INSTANCE::updateSlots);
					}
					List<ItemStack> expandedItems = StackGroupManager.expandGroupsInList(availableItems);
					if (screen == SidePanel.currentScreen && SidePanelOverlay.INSTANCE.availableItems().isEmpty()) {
						SidePanelOverlay.INSTANCE.availableItems().addAll(expandedItems);
						SidePanelOverlay.INSTANCE.updateSlots();
					}

				});
			}
			case UNLOCKED -> {
				if (!(screen instanceof CreativeModeInventoryScreen))
					ClientRecipeCache.INSTANCE.getRecipes().stream().filter((recipe)-> RRVClientUtil.matchesAnyTransferClass(recipe, RRVClientUtil.currentScreen())).sorted(Comparator.comparing(ReliableClientRecipe::entryId)).forEach(recipe->{
						ItemStack stack = recipe.getResults().getFirst().getValidContents().getFirst();
						ClientUnlockManager.INSTANCE.unlockItem(stack);
						setResultAndAdd(recipe, stack, availableItems);
					});
				if (availableItems.isEmpty()) {
					ClientRecipeCache.INSTANCE.getRecipes().stream().sorted(Comparator.comparing(ReliableClientRecipe::entryId)).forEach(recipe->{
						ItemStack stack = recipe.getResults().getFirst().getValidContents().getFirst();
						ClientUnlockManager.INSTANCE.unlockItem(stack);
						setResultAndAdd(recipe, stack, availableItems);
					});
				}
			}
			case DISABLED -> {}
		}
		return availableItems;
	}

	private static void filter(List<ItemStack> availableItems) {
		for (String query : ItemViewOverlay.INSTANCE.getCurrentQueries()) {
			if (query.startsWith("@")) {
				availableItems.removeIf(stack-> !ItemFilters.modNamespace(stack, query.substring(1)));
			}
			else if (query.startsWith(":")) {
				availableItems.removeIf(stack-> !ItemFilters.id(stack, query.substring(1)));
			}
			else if (query.startsWith("#")) {
				availableItems.removeIf(stack-> !ItemFilters.tag(stack, query.substring(1)));
			}
			else {
				availableItems.removeIf(stack-> !stack.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
			}
		}
	}

	static void updateRecipes(ReliableClientRecipe recipe, boolean requireWorkstationScreenOpen, List<ItemStack> availableItems) {
		if (recipe.isVisualOnly() || !Configs.CATEGORIES.enabled(recipe.getType())) return;
		Minecraft client = Minecraft.getInstance();
		if (requireWorkstationScreenOpen && !RRVClientUtil.matchesAnyTransferClass(recipe, RRVClientUtil.currentScreen())) return;
		AtomicInteger foundIngredientCount = new AtomicInteger();
		int requiredIngredientCount = recipe.getIngredients().size();
		recipe.getIngredients().forEach(ingredient -> {
			if (client.player.getInventory().hasAnyMatching(inv->ingredient.hasItem(inv.getItem()))) {
				foundIngredientCount.getAndIncrement();
			}
		});
		if (foundIngredientCount.get() == requiredIngredientCount) {
			recipe.getResults().forEach(result -> {
				result.getValidContents().forEach(ingredient -> setResultAndAdd(recipe, ingredient, availableItems));
			});
		}
	}

	private static void setResultAndAdd(ReliableClientRecipe recipe, ItemStack ingredient, List<ItemStack> availableItems) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("rrv_result", recipe.entryId().toString());
		ingredient.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));
		availableItems.stream().filter(ingredient1-> ItemStack.isSameItem(ingredient1, ingredient)).findFirst().ifPresentOrElse(stack->{
			CompoundTag compoundTag1 = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			compoundTag1.remove("rrv_result");
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag1));
		}, ()-> availableItems.add(ingredient));

	}

	public static void back() {
		int id1 = Configs.CLIENT_SETTINGS.getSidePanel().id - 1;
		if (id1 < 0) id1 = 3;
		Configs.CLIENT_SETTINGS.setSidePanel(get(id1));
	}
}
