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
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cc.cassian.rrv.common.overlay.ItemSlot.ITEM_ENTRY_SIZE;

public class SidePanelOverlay extends AbstractRrvItemListOverlay {

    public static final SidePanelOverlay INSTANCE = new SidePanelOverlay();

    public SpriteIconButton next = null;
    public SpriteIconButton back = null;

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
                );
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean prev = this.isEnabled();
        super.setEnabled(enabled);

        if (prev != enabled && enabled) {
            this.next.visible = true;
            this.back.visible = true;
        }

        if (prev != enabled && !enabled) {
            this.next.visible = false;
            this.back.visible = false;
        }
    }


    @Override
    public void onScreenChanged(InventoryPositionInfo info) {
        this.initForScreen(info.screen(), info);
        super.onScreenChanged(info);
        this.updateSidePanelIndex(Reason.SCREEN_CHANGE);
        this.createButtons(OverlayManager.INSTANCE.currentInfo());
        this.currentScreen = info.screen();
    }


    @Override
    protected void placeWidgets(ScreenContext ctx) {
        ctx.addRenderable(this.next);
        ctx.addRenderable(this.back);
    }

    private void initForScreen(AbstractContainerScreen<? extends AbstractContainerMenu> screen, InventoryPositionInfo invInfo) {

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
		Util.backgroundExecutor().execute(() -> {
            var screen = RRVClientUtil.currentScreen();
            if (screen instanceof RecipeViewScreen && reason.equals(Reason.SCREEN_CHANGE)) return; // prevent opening the recipe screen from changing the craftables
            if (RRVPlatform.INSTANCE.isDevelopment()) ReliableRecipeViewer.LOGGER.debug("Updating side panel index due to {}", reason);
            this.availableItems.clear();
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
                availableItems.sort(Comparator.comparing(i -> i.getDisplayName().getString()));
            } else {
                availableItems.addAll(BookmarkManager.INSTANCE.displayItems());
            }
            if (screen == this.currentScreen && this.availableItems.isEmpty()) {
                this.availableItems.addAll(availableItems);
                this.updateSlots();
            }

        });
    }



    private void filter(List<ItemStack> availableItems) {
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
            if (!slot.isHovered())
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
        int left = 0;
        if (!Configs.CLIENT_SETTINGS.isRightIndex()) {
            left = OverlayManager.INSTANCE.currentInfo().screenWidth() - this.width;
        }
        int xMin = left + this.width / 2 - 59;
        int xMax = left + this.width / 2 + 60;
        int verticalPadding = Configs.CLIENT_SETTINGS.isRecipeBookTheme() ? 25 : 0;
        return (mouseX > xMin && mouseX < xMax) && (mouseY >= (1+verticalPadding) && mouseY <= (21+verticalPadding));
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
            guiGraphics.setComponentTooltipForNextFrame(font, List.of(Component.translatable(pageKey + ".hint1"), Component.translatable(pageKey + ".hint2"), Component.translatable("rrv.switch_tabs.hint")), mouseX, mouseY+10);
        }

        if (this.fittingPerPage() > 0) {
            int titleX = (checkedX() + checkedWidth()) / 2;
            int titleY = checkedY() + 10;
            if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
                titleX+=3;
                titleY+=25;
            }
            this.drawScaledString(font, guiGraphics, page, titleX, titleY, colour);
		}

        try {
            for (ItemSlot slot : this.itemSlots()) {
                slot.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            }
        } catch (ConcurrentModificationException ignored) {}



        drawProgressBar(guiGraphics, Configs.CLIENT_SETTINGS.isRightIndex(), true);

    }

    public void createButtons(InventoryPositionInfo info){

        back = SpriteIconButton.builder(Component.literal("<"), (button)-> {
            int fittingPerPage = this.fittingPerPage();
            this.startIndex = Math.max(0, this.startIndex - fittingPerPage);
            this.updateSlots();
        }, true).sprite(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "back"), 10, 10).width(16).build();
        next = SpriteIconButton.builder(Component.literal(">"), (button)->{
            int fittingPerPage = this.fittingPerPage();
            this.startIndex = Math.min(this.startIndex + fittingPerPage, this.availableItems.size() - (this.availableItems.size() - (this.availableItems.size() / fittingPerPage) * fittingPerPage));
            this.updateSlots();
        }, true).sprite(Identifier.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "next"), 10, 10).width(16).build();

        int buttonY = 5;
        int buttonEnd = itemEndX - 16;
        if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
            buttonY+=25;
            buttonEnd-=13;
        }

        back.setPosition(itemStartX+2, buttonY);
        next.setPosition(buttonEnd, buttonY);


        next.visible = false;
        back.visible = false;
    }

    public enum Reason {
        BUTTON, INVENTORY_CHANGE, BOOKMARK, SCREEN_CHANGE, SEARCH, OTHER;
    }
}
