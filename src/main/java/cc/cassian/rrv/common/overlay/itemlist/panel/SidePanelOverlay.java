package cc.cassian.rrv.common.overlay.itemlist.panel;

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
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.List;

import static cc.cassian.rrv.common.overlay.ItemSlot.ITEM_ENTRY_SIZE;

/// CLIENT-ONLY
public class SidePanelOverlay extends AbstractRrvItemListOverlay {

    public static final SidePanelOverlay INSTANCE = new SidePanelOverlay();

    private static final int HEADER_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 20;
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
		Util.backgroundExecutor().execute(() -> {
            var screen = RRVClientUtil.currentScreen();
            if (isRecipeViewScreen(screen) && reason.equals(Reason.SCREEN_CHANGE)) return; // prevent opening the recipe screen from changing the craftables
            if (RRVPlatform.INSTANCE.isDevelopment()) ReliableRecipeViewer.LOGGER.debug("Updating side panel index due to {}", reason);
            this.availableItems.clear();
            List<ItemStack> expandedItems = StackGroupManager.expandGroupsInList(Configs.CLIENT_SETTINGS.getSidePanel().getStacks(new SidePanelContents(reason, Minecraft.getInstance().player, screen instanceof CreativeModeInventoryScreen)));
            if (screen == this.currentScreen && this.availableItems.isEmpty()) {
                this.availableItems.addAll(expandedItems);
                this.updateSlots();
            }
        });

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
        return Configs.CLIENT_SETTINGS.getSidePanel().getId();
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

        extractSlots(guiGraphics, mouseX, mouseY, partialTicks);

        drawProgressBar(guiGraphics, Configs.CLIENT_SETTINGS.isRightIndex(), true);
    }

    public enum Reason {
        BUTTON, INVENTORY_CHANGE, BOOKMARK, SCREEN_CHANGE, SEARCH, OTHER;
    }
}
