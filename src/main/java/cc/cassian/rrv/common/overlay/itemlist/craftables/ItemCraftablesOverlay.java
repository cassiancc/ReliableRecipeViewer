package cc.cassian.rrv.common.overlay.itemlist.craftables;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.ItemBookmarkOverlay;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCraftablesOverlay extends AbstractRrvItemListOverlay {

    public static final ItemCraftablesOverlay INSTANCE = new ItemCraftablesOverlay();

    public SpriteIconButton next = null;
    public SpriteIconButton back = null;

    private static final int HEADER_HEIGHT = 30;
    private static int FOOTER_HEIGHT = 20;

    public ItemCraftablesOverlay() {
        super(-1, -1, -1, -1);

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
        this.updateQuery();
        this.createButtons(OverlayManager.INSTANCE.currentInfo());
    }


    @Override
    protected void placeWidgets(ScreenContext ctx) {
        ctx.addRenderable(this.next);
        ctx.addRenderable(this.back);
    }

    private void initForScreen(AbstractContainerScreen<? extends AbstractContainerMenu> screen, InventoryPositionInfo invInfo) {

        //-14 for cleaner appearance
        this.width = invInfo.screenWidth() - ((invInfo.screenWidth() - 176) / 2 + 176) - 14;
        this.width -= (this.width - 4) % ITEM_ENTRY_SIZE;

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
    }


    /**
     * Updates the list of item slots
     */
    private void updateQuery() {
        this.availableItems.clear();
        var inventory = Minecraft.getInstance().player.getInventory();
        inventory.forEach(inventoryItem -> {
            ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> {
                if (recipe.isVisualOnly()) return;
                AtomicInteger foundIngredientCount = new AtomicInteger();
                int requiredIngredientCount = recipe.getIngredients().size();
                recipe.getIngredients().forEach(ingredient -> {
                    if (inventory.hasAnyMatching(inv->ingredient.hasItem(inv.getItem()))) {
                        foundIngredientCount.getAndIncrement();
                    }
                });
                if (foundIngredientCount.get() == requiredIngredientCount) {
                    recipe.getResults().forEach(result -> {
                        result.getValidContents().forEach(ingredient -> {
                            if (!this.availableItems.contains(ingredient)) {
                                this.availableItems.add(ingredient);
                            }
                        });
                    });
                }
            });
        });

        this.updateSlots();
    }


    @Override
    protected boolean keyPressed(KeyEvent event) {
        super.keyPressed(event);


        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(event))
                ItemBookmarkOverlay.INSTANCE.bookmarkItem(slot.getStack());
        }
        updateQuery();
        return false;
    }

    @Override
    protected boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        var s = super.mouseClicked(event, doubleClick);
        updateQuery();
        return s;
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.fittingPerPage() == 0)
            return;

        if (Configs.CLIENT_SETTINGS.isItemWrapMode())
            guiGraphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0, 0, 0, 64).getRGB());
        else
            guiGraphics.fill(this.effectiveX, this.effectiveY, this.effectiveX + this.effectiveWidth, this.effectiveY + this.effectiveHeight, new Color(0, 0, 0, 64).getRGB());
    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;


        var page = Component.literal((this.getPage() + 1) + "/" + (this.getMaxPageIndex() + 1));


        if (this.fittingPerPage() > 0) {
            if (Configs.CLIENT_SETTINGS.isItemWrapMode())
                this.drawScaledString(font, guiGraphics, page, this.x + this.width / 2, this.y + 10, -1);
            else
                this.drawScaledString(font, guiGraphics, page, this.effectiveX + this.effectiveWidth / 2, this.effectiveY + 10, -1);
        }


        for (ItemSlot slot : this.itemSlots()) {
            slot.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

           /*
        double scrollPage = this.getPage();
        if (scrollPage == 0) {
            scrollPage = .5;
        }


        if (Configs.CLIENT_SETTINGS.isItemWrapMode()) {
            guiGraphics.fill(this.x, this.y + 24, this.x + this.width, this.y + 28, new Color(255, 255, 255, 32).getRGB());
            guiGraphics.fill(this.x, this.y + 24, (int) (this.x + (((double) this.width / getMaxPageIndex()) * (scrollPage))), this.y + 28, new Color(255, 255, 255, 255).getRGB());
        } else {
            guiGraphics.fill(this.effectiveX, this.effectiveY + 24, this.effectiveX + this.effectiveWidth, this.y + 28, new Color(255, 255, 255, 32).getRGB());
            guiGraphics.fill(this.effectiveX, this.effectiveY + 24, (int) (this.effectiveX + (((double) this.effectiveWidth / getMaxPageIndex()) * (scrollPage))), this.y + 28, new Color(255, 255, 255, 255).getRGB());
        }

         */

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
        back.setPosition(ItemCraftablesOverlay.INSTANCE.itemStartX+2, 3);
        next.setPosition(ItemCraftablesOverlay.INSTANCE.itemEndX-16, 3);


        next.visible = ItemCraftablesOverlay.INSTANCE.isEnabled();
        back.visible = ItemCraftablesOverlay.INSTANCE.isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && Configs.CLIENT_SETTINGS.isShowCraftables();
    }


}
