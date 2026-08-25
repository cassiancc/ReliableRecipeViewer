package cc.cassian.rrv.common.overlay.itemlist.panel;

import cc.cassian.rrv.api.overlay.OverlayView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.jei.JeiCompatibilityUtil;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cc.cassian.rrv.common.overlay.ItemSlot.ITEM_ENTRY_SIZE;

/// CLIENT-ONLY
public class SidePanelOverlay extends AbstractRrvItemListOverlay {

    public static final SidePanelOverlay INSTANCE = new SidePanelOverlay();

    private static final int HEADER_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 20;
    private NonNullList<ItemStack> inventory;
    private List<ItemStack> lastAvailableItems = availableItems;
    private Screen currentScreen;

    public SidePanelOverlay() {
        super(-1, -1, -1, -1);

    }

    @Override
    public boolean isEnabled() {
        return
                super.isEnabled() &&
                (
                    Configs.CLIENT_SETTINGS.isShowSidePanel().equals(OverlayDisplay.ENABLED) ||
                    (Configs.CLIENT_SETTINGS.isShowSidePanel().equals(OverlayDisplay.WHEN_SEARCHING) && ItemViewOverlay.INSTANCE.isSearching()) ||
                    (Configs.CLIENT_SETTINGS.isShowSidePanel().equals(OverlayDisplay.WITH_ITEM_VIEW) && ItemViewOverlay.INSTANCE.isEnabled())
                ) &&
                !Configs.CLIENT_SETTINGS.isJeiPanel();
    }

    @Override
    public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
        updateButtons();
    }

    @Override
    public void onScreenChanged(InventoryPositionInfo info) {
        this.initForScreen(info.screen(), info);
        super.onScreenChanged(info);
        this.updateSidePanelIndex(Reason.SCREEN_CHANGE);
        this.createButtons(createTitleText(), checkedX()+16, itemEndX - 24, checkedX()+2, itemEndX - 15);
        this.currentScreen = info.screen();
    }

    public MutableComponent createTitleText() {
        return Component.translatable("rrv." + Configs.CLIENT_SETTINGS.getSidePanel().getSerializedName());
    }

    private void initForScreen(Screen screen, InventoryPositionInfo invInfo) {

        //-14 for cleaner appearance
        this.width = screen.width - ((screen.width - 176) / 2 + 176) - 14 - 2 * ITEM_ENTRY_SIZE;
        this.width -= (this.width - 4) % ITEM_ENTRY_SIZE;
        this.width = Math.max(this.width, Minecraft.getInstance().font.width(Component.translatable("rrv.craftables"))+30);

        this.height = screen.height;

        if (Configs.CLIENT_SETTINGS.isRightIndex()) {
            this.x = 0;
        } else {
            this.x = invInfo.screenWidth() - this.width;
        }

        this.y = 0;

        this.itemStartX = this.x + 2;
        this.itemStartY = HEADER_HEIGHT;

        this.itemEndX = this.x + this.width - 2;
        this.itemEndY = this.y + this.height - FOOTER_HEIGHT;

        if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
            this.itemStartX+=12;
            this.itemStartY+=26;
            this.itemEndY-=10;
        }
    }

    /**
     * Updates the list of item slots
     */
	public void updateSidePanelIndex(Reason reason) {
        var screen = RRVClientUtil.currentScreen();
        if (isRecipeViewScreen(screen) && reason.equals(Reason.SCREEN_CHANGE)) return; // prevent opening the recipe screen from changing the craftables
        if (RRVPlatform.INSTANCE.isDevelopment()) ReliableRecipeViewer.LOGGER.debug("Updating side panel index due to {}", reason);
        this.availableItems.clear();

		Util.backgroundExecutor().execute(() -> {
            var availableItems = new ArrayList<ItemStack>();
            if (showCraftables()) {
                Minecraft client = Minecraft.getInstance();
                LocalPlayer player = client.player;
                if (player == null) {
                    return;
                }
                // when searching, use the last unfiltered list rather than constantly querying the recipe manager
                if (!reason.equals(Reason.SEARCH)) {
                    this.inventory = player.getInventory().getNonEquipmentItems();

                    // search by what craftables the workstation supports
                    if (!(screen instanceof CreativeModeInventoryScreen))
                        inventory.forEach(inventoryItem -> {
                            ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, availableItems, true));
                        });

                    // if the workstation is not supported, search by what craftables exist
                    if (availableItems.isEmpty()) {
                        try {
                            inventory.forEach(inventoryItem -> {
                                ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, availableItems, false));
                            });
                        } catch (ConcurrentModificationException ignored) {}
                    }

                    // save last available items for when searching occurs
                    this.lastAvailableItems = new ArrayList<>(availableItems);
                } else {
                    availableItems.addAll(lastAvailableItems);
                }

                filter(availableItems);
                RrvUtil.sortByName(availableItems);
                if (screen == this.currentScreen && this.availableItems.isEmpty()) {
                    this.availableItems.addAll(availableItems);
                    Minecraft.getInstance().execute(this::updateSlots);
                }
            }
            List<ItemStack> expandedItems = StackGroupManager.expandGroupsInList(availableItems);
            if (screen == this.currentScreen && this.availableItems.isEmpty()) {
                this.availableItems.addAll(expandedItems);
                this.updateSlots();
            }

        });
        if (!showCraftables()) {
            availableItems.addAll(StackGroupManager.expandGroupsInList(BookmarkManager.INSTANCE.displayItems()));
            Minecraft.getInstance().execute(this::updateSlots);
        }
        updateButtons();
    }

    public void updateButtons() {
        this.updateButtons(createTitleText());
    }

    @Override
    public boolean showButtons(Component title) {
        return super.showButtons(title) && !Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.DISABLED);
    }

    private static boolean isRecipeViewScreen(Screen screen) {
        return screen instanceof RecipeViewScreen || (ModCompat.JEI && JeiCompatibilityUtil.isJeiRecipeViewScreen(screen));
    }

    private void filter(List<ItemStack> availableItems) {
        for (String query : ItemViewOverlay.INSTANCE.getCurrentQueries()) {
            String substring = RrvUtil.lowercaseSubstring(query);
            if (!ItemFilters.advancedFilter(availableItems, query)) {
                availableItems.removeIf(stack-> !stack.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(substring));
            }
        }
    }

    void updateRecipes(ReliableClientRecipe recipe, ArrayList<ItemStack> availableItems, boolean b) {
        if (recipe.isVisualOnly() || !Configs.CATEGORIES.enabled(recipe.getType())) return;
        Minecraft client = Minecraft.getInstance();
        if (b && !RRVClientUtil.matchesAnyTransferClass(recipe, RRVClientUtil.currentScreen())) return;
        AtomicInteger foundIngredientCount = new AtomicInteger();
        int requiredIngredientCount = recipe.getIngredients().size();
        recipe.getIngredients().forEach(ingredient -> {
            if (client.player.getInventory().hasAnyMatching(inv->ingredient.hasItem(inv.getItem()))) {
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


    @Override
    protected boolean keyPressed(KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        }

        for (ItemSlot slot : this.itemSlots()) {
            if (slot==null || !slot.isHovered())
                continue;

            if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(event)) {
                if (showCraftables()) {
                    BookmarkManager.INSTANCE.bookmarkItem(slot.getStack());
                } else {
                    BookmarkManager.INSTANCE.removeItem(slot.getStack());
                }
                return true;
            }
        }

        return false;
    }

    @Override
    protected @NonNull Identifier getReportedOverlayId() {
        return switch (Configs.CLIENT_SETTINGS.getSidePanel()) {
            case BOOKMARKS -> OverlayView.BOOKMARKS;
            case CRAFTABLES -> OverlayView.CRAFTABLES;
            default -> OverlayView.UNKNOWN;
        };
    }

    @Override
    protected boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        if (Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.DISABLED)) {
            return false;
        }
        if (isHoveringOverTitle(event.x(), event.y())) {
            if (showBookmarks())
                Configs.CLIENT_SETTINGS.setSidePanel(SidePanel.CRAFTABLES);
            else
                Configs.CLIENT_SETTINGS.setSidePanel(SidePanel.BOOKMARKS);
            updateSidePanelIndex(Reason.BUTTON);
            return true;
        }
        return false;
    }

    private boolean isHoveringOverTitle(double mouseX, double mouseY) {
        if (next.isHovered() || back.isHovered()) {
            return false;
        }
        int left = 0;
        if (!Configs.CLIENT_SETTINGS.isRightIndex()) {
            left = OverlayManager.INSTANCE.currentInfo().screenWidth() - this.width;
        }
        int xMin = left + this.width / 2 - 59;
        int xMax = left + this.width / 2 + 60;
        int verticalPadding = Configs.CLIENT_SETTINGS.isRecipeBookTheme() ? 25 : 0;
        return (mouseX > xMin && mouseX < xMax) && (mouseY >= (1+verticalPadding) && mouseY <= (19+verticalPadding));
    }

    public static boolean showBookmarks() {
        return Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.BOOKMARKS);
    }

    public static boolean showCraftables() {
        return Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.CRAFTABLES);
    }

    @Override
    protected void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.DISABLED))
            return;

        if (Configs.CLIENT_SETTINGS.isRecipeBookTheme())
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of("recipe_book"), checkedX()+6, checkedY()+20, checkedWidth()-6, checkedY()+checkedHeight()-40, -1);
        else
            guiGraphics.fill(checkedX(), checkedY(), checkedX() + checkedWidth(), checkedY() + checkedHeight(), new Color(0, 0, 0, 64).getRGB());
    }

    @Override
    protected void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.DISABLED)) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        var screen = RRVClientUtil.currentScreen();
        Font font = client.font;


        String pageKey = "rrv." + Configs.CLIENT_SETTINGS.getSidePanel().getSerializedName();
        var page = Component.translatable(pageKey);
        int colour = -1;
        if (isHoveringOverTitle(mouseX, mouseY)) {
            colour = -256;
            guiGraphics.requestCursor(CursorTypes.POINTING_HAND);
            guiGraphics.setComponentTooltipForNextFrame(font, List.of(Component.translatable(pageKey + ".hint1"), Component.translatable(pageKey + ".hint2"), Component.translatable("rrv.switch_tabs.hint")), mouseX, mouseY + 10);
        }

        if (this.fittingPerPage() > 0) {
            int titleX = (checkedX() + checkedWidth()) / 2;
            int titleY = checkedY() + 10;
            if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
                titleX += 3;
                titleY += 25;
            }
            this.drawScaledString(font, guiGraphics, page, titleX, titleY, colour);
        }

        try {
            ItemSlot.currentFrameSlots = this.itemSlots();
            for (ItemSlot slot : this.itemSlots()) {
                if (slot == null) return;
                slot.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            }
            ItemSlot.currentFrameSlots = null;
        } catch (ConcurrentModificationException ignored) {
        }

        drawProgressBar(guiGraphics, Configs.CLIENT_SETTINGS.isRightIndex(), true);
    }

    public enum Reason {
        BUTTON, INVENTORY_CHANGE, BOOKMARK, SCREEN_CHANGE, SEARCH, OTHER;
    }
}
