package cc.cassian.rrv.common.overlay.itemlist;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.ItemSlot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static cc.cassian.rrv.common.config.options.WrapScrolling.shouldWrapScroll;

public abstract class AbstractRrvItemListOverlay extends AbstractRrvOverlay {


    protected static final int ITEM_ENTRY_SIZE = 19;

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

        if (ReliableRecipeViewerClient.isCheatmodeActive()) {
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

        if (scrolledY != 0)
            this.updateSlots();


        return true;
    }

    protected void prevPage(Button button) {
        int fittingPerPage = this.fittingPerPage();
        if (fittingPerPage == 0) return;
        if (this.startIndex == 0 && shouldWrapScroll(button)) {
            int size = this.availableItems.size();
            this.startIndex = size - (size - (size / fittingPerPage) * fittingPerPage);
        } else {
            this.startIndex = Math.max(0, this.startIndex - fittingPerPage);
        }

        this.updateSlots();
    }

    protected void nextPage(Button button) {
        var currentIndex = this.startIndex;
        int fittingPerPage = this.fittingPerPage();
        if (fittingPerPage == 0) return;
        int size = this.availableItems.size();
        this.startIndex = Math.min(this.startIndex + fittingPerPage, size - (size - (size / fittingPerPage) * fittingPerPage));
        if (currentIndex == this.startIndex && shouldWrapScroll(button)) {
            this.startIndex = 0;
        }
        this.updateSlots();
    }

    @Override
    protected boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (ItemSlot itemSlot : this.itemSlots()) {
            if (itemSlot.isHovered()) {
                itemSlot.onClicked(event);
                return true;
            }
        }

        return false;
    }


    /**
     * Responsible for adding the item entries to the overlay
     */
    public void updateSlots() {
        this.itemSlots().clear();

        int currentStackPos = this.startIndex;

        for (int y = this.itemStartY; y <= this.itemEndY - ITEM_ENTRY_SIZE; y += ITEM_ENTRY_SIZE) {
            for (int x = this.itemStartX; x <= this.itemEndX - ITEM_ENTRY_SIZE; x += ITEM_ENTRY_SIZE) {

                if (Configs.CLIENT_SETTINGS.isItemWrapMode()) {
                    if (this.isPositionBlocked(x, y, ITEM_ENTRY_SIZE, ITEM_ENTRY_SIZE))
                        continue;

                    if (currentStackPos < this.availableItems().size())
                        this.itemSlots().add(new ItemSlot(this.availableItems().get(currentStackPos), x, y));

                    currentStackPos++;
                    continue;
                }

                if (x >= this.effectiveX && x <= this.effectiveX + this.effectiveWidth - ITEM_ENTRY_SIZE && y >= this.effectiveY && y <= this.effectiveY + this.effectiveHeight - ITEM_ENTRY_SIZE) {

                    if (currentStackPos < this.availableItems().size())
                        this.itemSlots().add(new ItemSlot(this.availableItems().get(currentStackPos), x, y));

                    currentStackPos++;
                }

            }
        }

        this.fittingPerPage = currentStackPos - this.startIndex;

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
                y+=4;
                maxWidth-=24;
                if (sidePanel) {
                    x+=5;
                }
            }

            guiGraphics.fill(x, y, x + maxWidth, y+4, new Color(255, 255, 255, 32).getRGB());

            guiGraphics.fill(x, y, getWidth(x, maxWidth, scrollPage, rightIndex), y+4, new Color(255, 255, 255, 255).getRGB());
        }
    }

    protected int getWidth(double x, int width, double scrollPage, boolean rightIndex) {
        int i = (int) (x + (((double) width / getMaxPageIndex()) * scrollPage));
        if (i > width && rightIndex) return (int) (x+width);
        return i;
    }

    public List<ItemStack> availableItems() {
        return this.availableItems;
    }


}
