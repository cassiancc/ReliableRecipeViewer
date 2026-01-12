package cc.cassian.rrv.common.overlay.itemlist.bookmark;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class ItemBookmarkOverlay extends AbstractRrvItemListOverlay {

    public static final ItemBookmarkOverlay INSTANCE = new ItemBookmarkOverlay();

    private static final int HEADER_HEIGHT = 20;
    private static final int FOOTER_HEIGHT = 40;


    private ItemBookmarkOverlay() {
        super(-1, -1, -1, -1);
    }


    public void bookmarkItem(ItemStack stack) {
        if (!this.availableItems().contains(stack)) {
            this.availableItems().add(stack);
            this.updateSlots();
        }
    }

    @Override
    public void onScreenChanged(InventoryPositionInfo info) {
        this.initForScreen(info.screen(), info);
        super.onScreenChanged(info);
        this.updateSlots();
    }

    private boolean shouldHide() {
        return !Configs.CLIENT_SETTINGS.isShowBookmarks() || this.itemSlots().isEmpty() || (this.fittingPerPage() == 0);
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (shouldHide())
            return;

        if (Configs.CLIENT_SETTINGS.isItemWrapMode())
            guiGraphics.fill(this.x, this.y, this.width, this.height, new Color(0, 0, 0, 64).getRGB());
        else
            guiGraphics.fill(this.effectiveX, this.effectiveY, this.effectiveWidth, this.effectiveHeight, new Color(0, 0, 0, 64).getRGB());

    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (shouldHide())
            return;

        Minecraft client = Minecraft.getInstance();

        Font font = client.font;

        if (Configs.CLIENT_SETTINGS.isItemWrapMode())
            this.drawScaledString(font, guiGraphics, Component.translatable("rrv.bookmarks"), this.x + this.width / 2, 6, -1);
        else
            this.drawScaledString(font, guiGraphics, Component.translatable("rrv.bookmarks"), this.effectiveX + this.effectiveWidth / 2, 6, -1);


        String pageString = (this.getPage() + 1) + "/" + (this.getMaxPageIndex() + 1);

        if (Configs.CLIENT_SETTINGS.isItemWrapMode())
            guiGraphics.drawCenteredString(font, pageString, Math.max(this.width / 2, font.width(pageString) / 2 + 2), this.y + this.height - 2 - 20 - 10, -1);
        else
            guiGraphics.drawCenteredString(font, pageString, Math.max(this.effectiveWidth / 2, font.width(pageString) / 2 + 2), this.effectiveY + this.effectiveHeight - 2 - 20 - 10, -1);


        for (ItemSlot slot : this.itemSlots()) {
            slot.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }


    @Override
    protected boolean keyPressed(int keyEvent, int scanCode) {
        super.keyPressed(keyEvent, scanCode);

        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(keyEvent, scanCode)) {
                this.availableItems.remove(slot.getStack());
                this.updateSlots();
                if (this.itemSlots().isEmpty() && !this.availableItems.isEmpty()) {
                    this.startIndex = Math.max(0, this.startIndex - this.fittingPerPage());
                    this.updateSlots();
                }
                return true;
            }

            break;
        }

        return false;
    }


    public void initForScreen(AbstractContainerScreen<? extends AbstractContainerMenu> screen, InventoryPositionInfo invInfo) {


        if (Configs.CLIENT_SETTINGS.isRightIndex()) {
            this.x = 0;
        } else {
            this.x = invInfo.screenWidth() - this.width;
        }
        this.y = 0;

        this.width = screen.width - ((screen.width - 176) / 2 + 176) - 14 - 2 * ITEM_ENTRY_SIZE;
        this.width -= (this.width - 4) % ITEM_ENTRY_SIZE;
        this.height = screen.height;

        this.itemStartX = 2;
        this.itemStartY = HEADER_HEIGHT;

        this.itemEndX = this.x + this.width - 2;
        this.itemEndY = this.y + this.height - FOOTER_HEIGHT;

    }

}
