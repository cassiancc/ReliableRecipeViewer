package de.crafty.eiv.common.overlay.itemlist;

import de.crafty.eiv.common.CommonEIVClient;
import de.crafty.eiv.common.config.Configs;
import de.crafty.eiv.common.overlay.AbstractEivOverlay;
import de.crafty.eiv.common.overlay.ItemSlot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEivItemListOverlay extends AbstractEivOverlay {


    protected static final int ITEM_ENTRY_SIZE = 20;

    protected int itemStartX, itemStartY, itemEndX, itemEndY;
    protected int startIndex;
    private int fittingPerPage;

    protected List<ItemStack> availableItems;


    protected AbstractEivItemListOverlay(int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
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

        if (CommonEIVClient.isCheatmodeActive()) {
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

        if (scrolledY < 0 && this.startIndex + fittingPerPage < this.availableItems().size())
            this.startIndex = this.startIndex + fittingPerPage;

        if (scrolledY > 0)
            this.startIndex = Math.max(0, this.startIndex - fittingPerPage);

        if (scrolledY != 0)
            this.updateSlots();


        return true;
    }

    @Override
    protected boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (ItemSlot itemSlot : this.itemSlots()) {
            if (itemSlot.isHovered()) {
                itemSlot.onClicked((int) event.x(), (int) event.y(), event.button());
                return true;
            }
        }

        return true;
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


    protected void drawScaledString(Font font, GuiGraphics guiGraphics, Component comp, int x, int y, int color) {

        float scaleFactor = Math.min(1.0F, 1.0F / (font.width(comp) / ((Configs.CLIENT_SETTINGS.isItemWrapMode() ? this.width : this.effectiveWidth) - 4.0F)));

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scaleFactor, scaleFactor);
        guiGraphics.drawCenteredString(font, comp, 0, 0, color);
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
        int maxPageIndex = ((this.availableItems().size() - 1) / (this.fittingPerPage()));
        if (this.startIndex % this.fittingPerPage() != 0 && this.startIndex % this.fittingPerPage() < this.availableItems().size() % this.fittingPerPage())
            maxPageIndex++;

        return maxPageIndex;
    }

    public List<ItemStack> availableItems() {
        return this.availableItems;
    }


}
