package cc.cassian.rrv.common.overlay.itemlist;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.overlay.itemlist.view.ReliableSpriteIconButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static cc.cassian.rrv.common.config.options.WrapScrolling.shouldWrapScroll;
import static cc.cassian.rrv.common.overlay.ItemSlot.ITEM_ENTRY_SIZE;

/// CLIENT-ONLY
public abstract class AbstractRrvItemListOverlay extends AbstractRrvOverlay {

    public ReliableSpriteIconButton next = null;
    public ReliableSpriteIconButton back = null;
    protected int itemStartX, itemStartY, itemEndX, itemEndY;
    protected int startIndex;
    private int fittingPerPage;

    protected List<ItemStack> availableItems;


    protected AbstractRrvItemListOverlay(int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        super(defaultX, defaultY, defaultWidth, defaultHeight);


        this.fittingPerPage = 0;
        this.startIndex = 0;

        this.availableItems = new ArrayList<>();
    }

    @Override
    public void updateEffectiveDimensions(InventoryPositionInfo info) {
        super.updateEffectiveDimensions(info);

        this.effectiveWidth -= (this.effectiveWidth - 4) % ITEM_ENTRY_SIZE;
        this.effectiveX = this.effectiveX <= info.screenWidth() / 2 ? this.effectiveX : info.screenWidth() - this.effectiveWidth;
    }

    @Override
    protected boolean scrollMouse(double mouseX, double mouseY, double scrolledX, double scrolledY) {

        if (ReliableRecipeViewerClient.isCheatmodeActive() && !slotsBeingUpdated) {
            for (ItemSlot slot : this.itemSlots()) {
                if (!slot.isHovered())
                    continue;

                slot.changeCheatmodeCount((scrolledY < 0 ? -1 : 1));
                return true;
            }
        }

        int fittingPerPage = this.fittingPerPage();

        if (fittingPerPage == 0)
            return true;

        if (scrolledY < 0)
            nextPage(null);

        if (scrolledY > 0)
            prevPage(null);

        return true;
    }

    protected void prevPage(Button button) {
        int fittingPerPage = this.fittingPerPage();
        if (fittingPerPage == 0) return;
        if (this.startIndex == 0 && shouldWrapScroll(button)) {
            lastPage();
        } else {
            this.startIndex = Math.max(0, this.startIndex - fittingPerPage);
        }
        if (getPage()>getMaxPageIndex()) {
            firstPage();
        }

        this.updateSlots();
    }

    private void lastPage() {
        int size = this.availableItems.size();
        this.startIndex = size - (size - (size / this.fittingPerPage()) * this.fittingPerPage());
    }

    protected void nextPage(Button button) {
        var currentIndex = this.startIndex;
        int fittingPerPage = this.fittingPerPage();
        if (fittingPerPage == 0) return;
        int size = this.availableItems.size();
        this.startIndex = Math.min(this.startIndex + fittingPerPage, size - (size - (size / fittingPerPage) * fittingPerPage));
        if (currentIndex == this.startIndex && shouldWrapScroll(button)) {
            firstPage();
        }
        if (getPage()>getMaxPageIndex()) {
            this.startIndex = currentIndex;
        }
        this.updateSlots();
    }

    public void firstPage() {
        this.startIndex = 0;
    }

    @Override
    protected boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (slotsBeingUpdated) return false;
        for (ItemSlot itemSlot : this.itemSlots()) {
            if (itemSlot.isHovered()) {
                itemSlot.onClicked(event);
                return true;
            }
        }

        return false;
    }

    protected boolean slotsBeingUpdated = false;

    /**
     * Responsible for adding the item entries to the overlay
     */
    public void updateSlots() {
        slotsBeingUpdated = true;
        Util.backgroundExecutor().execute(()->{
            this.itemSlots().clear();

            int currentStackPos = this.startIndex;

            for (int y = this.itemStartY; y <= this.itemEndY - ITEM_ENTRY_SIZE; y += ITEM_ENTRY_SIZE) {
                for (int x = this.itemStartX; x <= this.itemEndX - ITEM_ENTRY_SIZE; x += ITEM_ENTRY_SIZE) {

                    if (Configs.CLIENT_SETTINGS.isItemWrapMode()) {
                        if (this.isPositionBlocked(x, y, ITEM_ENTRY_SIZE, ITEM_ENTRY_SIZE))
                            continue;

                        if (currentStackPos < this.availableItems().size())
                            this.itemSlots().add(new ItemSlot(this.availableItems().get(currentStackPos), x, y, this instanceof SidePanelOverlay));

                        currentStackPos++;
                        continue;
                    }

                    if (x >= this.effectiveX && x <= this.effectiveX + this.effectiveWidth - ITEM_ENTRY_SIZE && y >= this.effectiveY && y <= this.effectiveY + this.effectiveHeight - ITEM_ENTRY_SIZE) {

                        if (currentStackPos < this.availableItems().size())
                            this.itemSlots().add(new ItemSlot(this.availableItems().get(currentStackPos), x, y, this instanceof SidePanelOverlay));

                        currentStackPos++;
                    }

                }
            }

            this.fittingPerPage = currentStackPos - this.startIndex;
            slotsBeingUpdated = false;
        });

    }


    public int fittingPerPage() {
        return this.fittingPerPage;
    }


    protected void drawScaledString(Font font, GuiGraphicsExtractor guiGraphics, Component comp, int x, int y, int color) {

        float scaleFactor = Math.min(1.0F, 1.0F / (font.width(comp) / ((Configs.CLIENT_SETTINGS.isItemWrapMode() ? this.width : this.effectiveWidth) - 4.0F)));

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scaleFactor, scaleFactor);
        guiGraphics.centeredText(font, comp, 0, 0, color);
        guiGraphics.pose().popMatrix();

    }

    protected int getPage() {
        int fittingPerPage = this.fittingPerPage();

        int page = fittingPerPage > 0 ? this.startIndex / fittingPerPage : 0;
        if (page * fittingPerPage < this.startIndex)
            page++;

        return page;
    }

    protected int getMaxPageIndex() {
        if (this.fittingPerPage() < 1) return 0;
        int maxPageIndex = ((this.availableItems().size() - 1) / (this.fittingPerPage()));
        if (this.startIndex % this.fittingPerPage() != 0 && this.startIndex % this.fittingPerPage() < this.availableItems().size() % this.fittingPerPage())
            maxPageIndex++;

        return maxPageIndex;
    }

    protected Component getPageCountText() {
        return Component.literal((this.getPage() + 1) + "/" + (this.getMaxPageIndex() + 1));
    }

    protected void drawProgressBar(GuiGraphicsExtractor guiGraphics, boolean rightIndex, boolean sidePanel) {
        if (Configs.CLIENT_SETTINGS.isShowProgressBar()) {
            double scrollPage = this.getPage();
            if (scrollPage == 0) {
                scrollPage = .5;
            }
            int x = checkedX();
            int y = checkedY() + 24;
            int maxWidth = checkedWidth();
            if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
                x+=10;
                y+=24;
                maxWidth-=24;
                if (sidePanel) {
                    x+=5;
                }
            }

            int white = 255;
            guiGraphics.fill(x, y, x + maxWidth, y+4, new Color(white, white, white, 32).getRGB());
            guiGraphics.fill(x, y, getWidth(x, maxWidth, scrollPage, rightIndex), y+4, new Color(white, white, white, 255).getRGB());
        }
    }

    @Override
    protected void placeWidgets(ScreenContext ctx) {
        super.placeWidgets(ctx);
        ctx.addRenderable(this.back);
        ctx.addRenderable(this.next);
    }

    /// @param buttonStart - x position of the back page button in the recipe book theme.
    /// @param buttonEnd - x position of the next page button in the recipe book theme.
    /// @param classicButtonStart - x position of the back page button in the classic theme.
    /// @param classicButtonEnd - x position of the next page button in the classic theme.
    public void createButtons(Component title, int buttonStart, int buttonEnd, int classicButtonStart, int classicButtonEnd) {

        back = new ReliableSpriteIconButton(16, Component.translatable("rrv.previous_page"), 10, ReliableRecipeViewer.of("back"), ReliableRecipeViewer.of("back"), ReliableRecipeViewer.of("back_disabled"), this::prevPage);
        next = new ReliableSpriteIconButton(16, Component.translatable("rrv.next_page"), 10, ReliableRecipeViewer.of("next"), ReliableRecipeViewer.of("next"), ReliableRecipeViewer.of("next_disabled"), this::nextPage);

        int buttonY = 5;
        if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
            buttonY+=25;
        } else {
            buttonStart = classicButtonStart;
            buttonEnd = classicButtonEnd;
        }

        back.setPosition(buttonStart, buttonY);
        next.setPosition(buttonEnd, buttonY);

        updateButtons(title);
    }

    protected void updateButtons(Component title) {
        if (back != null) {
            boolean enabled = showButtons(title);
            back.visible = enabled;
            next.visible = enabled;
            if (enabled && getMaxPageIndex() > 0) {
                next.active = true;
                back.active = true;
            } else {
                next.active = false;
                back.active = false;
            }
        }
    }

    public boolean showButtons(Component title) {
		return this.isEnabled() && Configs.CLIENT_SETTINGS.isShowButtons() && (getWidth() - 16) > Minecraft.getInstance().font.width(title) + (back.getWidth() + next.getWidth());
    }

    protected int getWidth(double x, int width, double scrollPage, boolean rightIndex) {
        int i = (int) (x + (((double) width / getMaxPageIndex()) * scrollPage));
        if (i > width && rightIndex) return (int) (x+width);
        return (int) Math.min(i, x+width);
    }

    public List<ItemStack> availableItems() {
        return this.availableItems;
    }


}
