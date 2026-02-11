package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.gui.RrvClientSettingsScreen;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.PolymerHelpers;
import cc.cassian.rrv.common.integration.polymer.network.StackActionPayload;
import cc.cassian.rrv.common.integration.polymer.recipe.PolydexClientRecipeType;
import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
//? fabric && <26.1 {
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemViewOverlay extends AbstractRrvItemListOverlay {

    public static final ItemViewOverlay INSTANCE = new ItemViewOverlay();
    private static final ResourceLocation SETTINGS_WHEEL = ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "settings_wheel");

    private SearchBar searchbar = null;

    public SpriteIconButton next = null;
    public SpriteIconButton back = null;

    private static final int HEADER_HEIGHT = 30;
    private static int FOOTER_HEIGHT = 20;

    private String currentQuery;
    boolean itemFilterMode;

    public ItemViewOverlay() {
        super(-1, -1, -1, -1);
        this.currentQuery = "";
        this.itemFilterMode = false;

    }


    @Override
    public void setEnabled(boolean enabled) {
        boolean prev = this.isEnabled();
        super.setEnabled(enabled);

        if (prev != enabled && enabled) {
            this.searchbar.visible = true;
            this.next.visible = true;
            this.back.visible = true;
        }

        if (prev != enabled && !enabled) {
            this.searchbar.visible = false;
            this.next.visible = false;
            this.back.visible = false;
        }
    }


    @Override
    public void onScreenChanged(InventoryPositionInfo info) {
        this.initForScreen(info.screen(), info);
        super.onScreenChanged(info);
        this.updateQuery(this.getCurrentQuery());
        this.createSearchbarElement(OverlayManager.INSTANCE.currentInfo());
        this.createButtons(OverlayManager.INSTANCE.currentInfo());
    }


    @Override
    protected void placeWidgets(ScreenContext ctx) {

        ctx.addRenderable(this.searchbar);
        ctx.addRenderable(this.next);
        ctx.addRenderable(this.back);

        InventoryPositionInfo info = OverlayManager.INSTANCE.currentInfo();


        //---- Client Settings Button ----
        SpriteIconButton btn = SpriteIconButton.builder(
                        Component.translatable("rrv.client_settings.btn"),
                        button -> Minecraft.getInstance().setScreen(new RrvClientSettingsScreen(info.screen())),
                        true
                )
                .size(18, 18)
                .sprite(SETTINGS_WHEEL, 14, 14)
                .build();

        int position = 0;
        if (!Configs.CLIENT_SETTINGS.isRightIndex()) {
            position = info.screenWidth() - 18;
        }

        btn.setPosition(position, info.screenHeight() - 18);

        ctx.addRenderable(btn);
    }

    private void initForScreen(AbstractContainerScreen<? extends AbstractContainerMenu> screen, InventoryPositionInfo invInfo) {

        //-14 for cleaner appearance
        this.width = invInfo.screenWidth() - ((invInfo.screenWidth() - 176) / 2 + 176) - 14;
        this.width -= (this.width - 4) % ITEM_ENTRY_SIZE;

        this.height = screen.height;

        if (!Configs.CLIENT_SETTINGS.isRightIndex()) {
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
     * Handles searchbar changes => responsible for custom prefixes
     *
     * @param newQuery
     */
    private void updateQuery(String newQuery) {
        if (!newQuery.equals(this.currentQuery))
            this.startIndex = 0;

        this.currentQuery = newQuery;

        // advanced filtering
        if (newQuery.contains(" ")) {

            ArrayList<String> objects = new ArrayList<>();

            for (String query : newQuery.split(" ")) {
                if (!query.startsWith("@") && !query.startsWith(":") && !query.startsWith("#")) {
                    objects.add(query);
                }
            }

            this.availableItems = ItemFilters.defaultFilter(String.join(" ", objects));

            for (String query : newQuery.split(" ")) {
                if (query.startsWith("@")) {
                    this.availableItems.removeIf(stack-> !ItemFilters.modName(stack, query.substring(1)));
                }
                else if (query.startsWith(":")) {
                    this.availableItems.removeIf(stack-> !ItemFilters.id(stack, query.substring(1)));
                }
                else if (query.startsWith("#")) {
                    this.availableItems.removeIf(stack-> !ItemFilters.tag(stack, query.substring(1)));
                }
            }
        // standard filtering
        } else {
            if (newQuery.startsWith("@"))
                this.availableItems = ItemFilters.modName(newQuery.substring(1));
            else if (newQuery.startsWith(":"))
                this.availableItems = ItemFilters.id(newQuery.substring(1));
            else if (newQuery.startsWith("#"))
                this.availableItems = ItemFilters.tag(newQuery.substring(1));
            else
                this.availableItems = ItemFilters.defaultFilter(newQuery);
        }

        this.availableItems().removeIf(stack -> ItemView.isExcludedItem(stack));

        this.updateSlots();
    }


    @Override
    protected boolean keyPressed(int event, int scanCode) {
        super.keyPressed(event, scanCode);


        for (ItemSlot slot : this.itemSlots()) {
            if (!slot.isHovered())
                continue;

            if (ReliableRecipeViewerClient.ADD_BOOKMARK_KEYBIND.matches(event))
                BookmarkManager.INSTANCE.bookmarkItem(slot.getStack());
        }

        return false;
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.fittingPerPage() == 0)
            return;

        guiGraphics.fill(checkedX(), checkedY(), checkedX() + checkedWidth(), checkedY() + checkedHeight(), new Color(0, 0, 0, 64).getRGB());
    }

    @Override
    protected void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;


        var page = Component.literal((this.getPage() + 1) + "/" + (this.getMaxPageIndex() + 1));


        if (this.fittingPerPage() > 0) {
            this.drawScaledString(font, guiGraphics, page, checkedX() + checkedWidth() / 2, checkedY() + 10, -1);
        }


        for (ItemSlot slot : this.itemSlots()) {
            slot.render(guiGraphics, mouseX, mouseY, partialTicks);
        }


        this.renderItemHighlighting(OverlayManager.INSTANCE.currentInfo().screen(), guiGraphics, mouseX, mouseY, partialTicks);

        double scrollPage = this.getPage();
        if (scrollPage == 0) {
            scrollPage = .5;
        }

        guiGraphics.fill(checkedX(), checkedY() + 24, checkedX() + checkedWidth(), checkedY() + 28, new Color(255, 255, 255, 32).getRGB());
        guiGraphics.fill(checkedX(), checkedY() + 24, getWidth(checkedX(), checkedWidth(), scrollPage), checkedY() + 28, new Color(255, 255, 255, 255).getRGB());

    }

    private int getWidth(double x, int width, double scrollPage) {
        int i = (int) (x + (((double) width / getMaxPageIndex()) * scrollPage));
        if (i > width && !Configs.CLIENT_SETTINGS.isRightIndex()) return width;
        return i;
    }


    public void renderItemHighlighting(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.itemFilterMode)
            return;


        screen.getMenu().slots.forEach(slot -> {

            if (!slot.isActive() || !slot.isHighlightable())
                return;

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(OverlayManager.INSTANCE.currentInfo().leftPos() - 1, OverlayManager.INSTANCE.currentInfo().topPos() - 1);
            if (!slot.hasItem() || this.availableItems.stream().noneMatch(stack -> stack.getItem() == slot.getItem().getItem())) {
                guiGraphics.fill(slot.x, slot.y, slot.x + 18, slot.y + 18, new Color(0, 0, 0, 128).getRGB());
            }
            guiGraphics.pose().popMatrix();

        });
    }


    public void createSearchbarElement(InventoryPositionInfo info) {
        boolean wrapMode = Configs.CLIENT_SETTINGS.isItemWrapMode();



        int boxWidth;
        int x;
        if (Configs.CLIENT_SETTINGS.isCenterSearch()) {
           boxWidth = Math.min(info.imageWidth(), Minecraft.getInstance().getWindow().getGuiScaledWidth()/2);
           x = (info.screenWidth()/2)-(boxWidth/2);
        } else {
           boxWidth = Math.min(100, (wrapMode ? this.width : this.effectiveWidth) - 4);
           x = wrapMode ? (this.x + this.width / 2 - boxWidth / 2) : (this.effectiveX + this.effectiveWidth / 2 - boxWidth / 2);
        }

        int y = info.screenHeight() - 22;


        if(this.searchbar != null && info.screen().getFocused() instanceof SearchBar) {
			this.searchbar.setFocused(false);
            this.searchbar.setSuggestion(null);
		}

        if (this.searchbar != null && boxWidth == this.searchbar.getWidth() && x == this.searchbar.getX() && y == this.searchbar.getY())
            return;


        SearchBar newSearchbar = new SearchBar(Minecraft.getInstance().font, x, y, boxWidth, 20, Component.literal("rrv:searchbar"), this);
        newSearchbar.setMaxLength(32);
        newSearchbar.setValue(this.getCurrentQuery());
        newSearchbar.setResponder(this::updateQuery);
        newSearchbar.setHint(Component.translatable("rrv.search_hint"));

        newSearchbar.visible = !Configs.CLIENT_SETTINGS.isShowOverlays().equals(OverlayManager.OverlayDisplay.DISABLED);

        this.searchbar = newSearchbar;
    }

    public void createButtons(InventoryPositionInfo info){

        back = SpriteIconButton.builder(Component.translatable("rrv.previous_page"), (button)-> {
            int fittingPerPage = this.fittingPerPage();
            this.startIndex = Math.max(0, this.startIndex - fittingPerPage);
            this.updateSlots();
        }, true).withTootip().sprite(ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "back"), 10, 10).width(16).build();
        next = SpriteIconButton.builder(Component.translatable("rrv.next_page"), (button)->{
            int fittingPerPage = this.fittingPerPage();
            this.startIndex = Math.min(this.startIndex + fittingPerPage, this.availableItems.size() - (this.availableItems.size() - (this.availableItems.size() / fittingPerPage) * fittingPerPage));
            this.updateSlots();
        }, true).withTootip().sprite(ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "next"), 10, 10).width(16).build();
        back.setPosition(ItemViewOverlay.INSTANCE.itemStartX+2, 3);
        next.setPosition(ItemViewOverlay.INSTANCE.itemEndX-16, 3);


        next.visible = ItemViewOverlay.INSTANCE.isEnabled();
        back.visible = ItemViewOverlay.INSTANCE.isEnabled();
    }

    public void openRecipeView(ItemStack stack, ActionType openType) {
        if (stack.isEmpty())
            return;

        //? fabric {
        if (ModCompat.POLYDEX && PolymerHelpers.isPolymerServerItem(stack)) {
            MinecraftServer server = ServerRecipeManager.INSTANCE.getServer();
            if (server != null) {
                RegistryAccess.Frozen registryManager = server.registryAccess();
                stack = PolymerHelpers.getRealItemStack(stack, registryManager);
            }
        }
        //?}

        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null)
            return;

        List<ReliableClientRecipe> foundRecipes = switch (openType) {
			case INPUT -> ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(stack);
			case RESULT -> ClientRecipeCache.INSTANCE.getRecipesForCraftingOutput(stack);
			case ANY -> {
                var b = ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(stack);
                b.addAll(ClientRecipeCache.INSTANCE.getRecipesForCraftingOutput(stack));
                yield b;
            }
		};

        if (!foundRecipes.isEmpty() || (ModCompat.POLYDEX && PolymerHelpers.isPolymerServerItem(stack))) {
            Screen parent = Minecraft.getInstance().screen;

            ArrayList<RecipeViewScreen> viewHistory = new ArrayList<>();

            if (parent instanceof RecipeViewScreen viewScreen) {
                parent = viewScreen.getMenu().getParentScreen();
                viewHistory = viewScreen.getMenu().getViewHistory();
            }

            int containerId = parent instanceof AbstractContainerScreen<? extends AbstractContainerMenu> containerScreen ? containerScreen.getMenu().containerId : 0;

            Minecraft.getInstance().setScreen(new RecipeViewScreen(new RecipeViewMenu(parent, containerId, clientPlayer.getInventory(), foundRecipes, stack, openType, viewHistory), clientPlayer.getInventory(), Component.empty()));
        }
    }

    public void openRecipeView(ReliableClientRecipeType clientRecipeType) {

        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null)
            return;

        //? fabric && <26.1 {
        if (ModCompat.POLYDEX && clientRecipeType instanceof PolydexClientRecipeType) {
            RrvClientNetworkManager.sendPacketToServer(new StackActionPayload(ActionType.ANY, ""));
        }
        //?}

        List<ReliableClientRecipe> foundRecipes = ClientRecipeCache.INSTANCE.getRecipes();;

        if (!foundRecipes.isEmpty()) {
            Screen parent = Minecraft.getInstance().screen;

            ArrayList<RecipeViewScreen> viewHistory = new ArrayList<>();

            if (parent instanceof RecipeViewScreen viewScreen) {
                parent = viewScreen.getMenu().getParentScreen();
                viewHistory = viewScreen.getMenu().getViewHistory();
            }

            int containerId = parent instanceof AbstractContainerScreen<? extends AbstractContainerMenu> containerScreen ? containerScreen.getMenu().containerId : 0;

            Minecraft.getInstance().setScreen(new RecipeViewScreen(new RecipeViewMenu(parent, containerId, clientPlayer.getInventory(), foundRecipes, ItemStack.EMPTY, ActionType.ANY, viewHistory, clientRecipeType), clientPlayer.getInventory(), Component.empty()));
        }


    }


    public EditBox getSearchbar() {
        return this.searchbar;
    }

    public boolean isItemFilterMode() {
        return this.itemFilterMode;
    }


    public String getCurrentQuery() {
        return this.currentQuery;
    }

	public boolean isSearching() {
		return searchbar != null && searchbar.isVisible() && !searchbar.getValue().isEmpty();
	}


}
