package cc.cassian.rrv.common.overlay.itemlist.panel;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.PolymerHelpers;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SidePanelOverlay extends AbstractRrvItemListOverlay {

    public static final SidePanelOverlay INSTANCE = new SidePanelOverlay();

    public SpriteIconButton next = null;
    public SpriteIconButton back = null;

    private static final int HEADER_HEIGHT = 30;
    private static int FOOTER_HEIGHT = 20;
    private NonNullList<ItemStack> inventory;

    public SidePanelOverlay() {
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
        this.updateSidePanelIndex("a screen change!" + info.screen());
        this.createButtons(OverlayManager.INSTANCE.currentInfo());
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
	public void updateSidePanelIndex(String reason) {
        if (Platform.INSTANCE.isDevelopment())
            ReliableRecipeViewer.LOGGER.debug("Updating side panel index due to %s".formatted(reason));
        this.availableItems.clear();
        if (showCraftables()) {
            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;
            if (player == null || (ModCompat.POLYDEX && PolymerHelpers.isPolymerScreenOpen(player))) {
                return;
            }
			this.inventory = player.getInventory().getNonEquipmentItems();
            var screen = client.screen;
            if (!(screen instanceof CreativeModeInventoryScreen))
                inventory.forEach(inventoryItem -> {
                ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, inventory, true));
            });
            if (this.availableItems.isEmpty()) {
                inventory.forEach(inventoryItem -> {
                    ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(inventoryItem).forEach(recipe -> updateRecipes(recipe, inventory, false));
                });
            }
        } else {
            this.availableItems.addAll(BookmarkManager.INSTANCE.availableItems());
        }

        this.updateSlots();
    }

    void updateRecipes(ReliableClientRecipe recipe, NonNullList<ItemStack> inventory, boolean b) {
        if (recipe.isVisualOnly()) return;
        Minecraft client = Minecraft.getInstance();
        if (b && !RrvUtil.matchesAnyTransferClass(recipe, client.screen)) return;
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
                    if (!this.availableItems.contains(ingredient)) {
                        this.availableItems.add(ingredient);
                    }
                });
            });
        }
    }


    @Override
    protected boolean keyPressed(KeyEvent event) {
        super.keyPressed(event);

        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(event) && showCraftables()) {
                BookmarkManager.INSTANCE.bookmarkItem(slot.getStack());
            } else {
                BookmarkManager.INSTANCE.removeItem(slot.getStack());
            }

        }

        return false;
    }

    @Override
    protected boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean b = super.mouseClicked(event, doubleClick);
        if (b) return true;
        if (isHoveringOverTitle(event.x(), event.y())) {
            if (showBookmarks())
                Configs.CLIENT_SETTINGS.setSidePanel(SidePanel.CRAFTABLES);
            else
                Configs.CLIENT_SETTINGS.setSidePanel(SidePanel.BOOKMARKS);
            updateSidePanelIndex("a mouse click on the title!");
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
        return (mouseX > xMin && mouseX < xMax) && (mouseY >= 1 && mouseY <= 20);
    }

    public static boolean showBookmarks() {
        return Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.BOOKMARKS);
    }

    public static boolean showCraftables() {
        return Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.CRAFTABLES);
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.fittingPerPage() == 0 || Configs.CLIENT_SETTINGS.getSidePanel().equals(SidePanel.DISABLED))
            return;

        guiGraphics.fill(checkedX(), checkedY(), checkedX() + checkedWidth(), checkedY() + checkedHeight(), new Color(0, 0, 0, 64).getRGB());
    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
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
            guiGraphics.setComponentTooltipForNextFrame(font, List.of(Component.translatable(pageKey + ".hint1"), Component.translatable(pageKey + ".hint2"), Component.translatable("rrv.switch_tabs.hint")), mouseX, mouseY+10);
        }

        if (this.fittingPerPage() > 0) {
			this.drawScaledString(font, guiGraphics, page, checkedX() + checkedWidth() / 2, checkedY() + 10, colour);
		}


        for (ItemSlot slot : this.itemSlots()) {
            slot.render(guiGraphics, mouseX, mouseY, partialTicks);
        }


        double scrollPage = this.getPage();
        if (scrollPage == 0) {
            scrollPage = .5;
        }




        guiGraphics.fill(checkedX(), checkedY() + 24, checkedX() + checkedWidth(), checkedY() + 28, new Color(255, 255, 255, 32).getRGB());
        guiGraphics.fill(checkedX(), checkedY() + 24, getWidth(checkedX(), checkedWidth(), scrollPage), checkedY() + 28, new Color(255, 255, 255, 255).getRGB());

    }

    private int getWidth(double x, int width, double scrollPage) {
        int i = (int) (x + (((double) width / getMaxPageIndex()) * scrollPage));
        if (i > width && Configs.CLIENT_SETTINGS.isRightIndex()) return width;
        return i;
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
        back.setPosition(SidePanelOverlay.INSTANCE.itemStartX+2, 3);
        next.setPosition(SidePanelOverlay.INSTANCE.itemEndX-16, 3);


        next.visible = false;
        back.visible = false;
    }
}
