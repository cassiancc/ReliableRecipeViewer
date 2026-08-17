package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.overlay.OverlayView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.ClientNetworkManager;
import cc.cassian.rrv.client.recipe.InternalRecipeManager;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.LocalFallback;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.gui.ClientConfigScreen;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.jei.JeiCompatibilityUtil;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import cc.cassian.rrv.common.integration.polymer.PolymerHelpers;
import cc.cassian.rrv.common.integration.polymer.network.StackActionPayload;
import cc.cassian.rrv.common.integration.polymer.recipe.PolydexClientRecipeType;
import cc.cassian.rrv.common.overlay.itemlist.AbstractRrvItemListOverlay;
import cc.cassian.rrv.common.overlay.itemlist.bookmark.BookmarkManager;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.overlay.itemlist.panel.SidePanelOverlay;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static cc.cassian.rrv.common.overlay.ItemSlot.ITEM_ENTRY_SIZE;

public class ItemViewOverlay extends AbstractRrvItemListOverlay {

    public static final ItemViewOverlay INSTANCE = new ItemViewOverlay();
    private static final Identifier SETTINGS_WHEEL = ReliableRecipeViewer.of("settings_wheel");

    private SearchBar searchbar = null;

    private static final int HEADER_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 20;

    private String currentQuery;
    boolean itemFilterMode;
    private boolean warned = false;
    private final List<ItemStack> filteredItems = new ArrayList<>();

    public ItemViewOverlay() {
        super(-1, -1, -1, -1);
        this.currentQuery = "";
        this.itemFilterMode = false;

    }

    @Override
    public boolean isEnabled() {
        return
                super.isEnabled() &&
                (
                        Configs.CLIENT_SETTINGS.isShowItemView().equals(OverlayDisplay.ENABLED) ||
                        (Configs.CLIENT_SETTINGS.isShowItemView().equals(OverlayDisplay.WHEN_SEARCHING) && ItemViewOverlay.INSTANCE.isSearching())
                ) &&
                !Configs.CLIENT_SETTINGS.isJeiPanel();
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean prev = this.isEnabled();
        super.setEnabled(enabled);

        if (prev != enabled) {
            this.searchbar.visible = enabled;
        }
        updateButtons(getPageCountText());
    }

    @Override
    public void onScreenChanged(InventoryPositionInfo info) {
        this.initForScreen(info.screen(), info);
        super.onScreenChanged(info);
        this.updateQuery(this.getCurrentQuery());
        this.createSearchbarElement(OverlayManager.INSTANCE.currentInfo());
        this.createButtons(getPageCountText(), checkedX()+10, itemEndX - 28, checkedX()+4, itemEndX - 16);
    }

    @Override
    protected void placeWidgets(ScreenContext ctx) {
        super.placeWidgets(ctx);
        ctx.addRenderable(this.searchbar);

        InventoryPositionInfo info = OverlayManager.INSTANCE.currentInfo();

        int buttonSize = buttonSize();

        //---- Client Settings Button ----
        ReliableSpriteIconButton settingsButton = new ReliableSpriteIconButton(
                buttonSize,
                        Component.translatable("rrv.client_settings.btn"),
                        14,
                        SETTINGS_WHEEL,
                button -> RRVClientUtil.setScreen(new ClientConfigScreen(info.screen()))
        );

        int position = 0;
        if (!Configs.CLIENT_SETTINGS.isRightIndex()) {
            position = info.screenWidth() - buttonSize;
        }

        settingsButton.setPosition(position, info.screenHeight() - buttonSize);
        settingsButton.visible = Configs.CLIENT_SETTINGS.isClientSettingsButtonEnabled();

        if (Configs.CLIENT_SETTINGS.isJeiPanel()) {
            JeiCompatibilityUtil.placeSettingsButton(settingsButton);
        }

        ctx.addRenderable(settingsButton);
        //---- Side Panel Settings Button ----
        ReliableSpriteIconButton sidePanelButton = new SidePanelButton(position, info);
        ctx.addRenderable(sidePanelButton);
    }

    protected int buttonSize() {
        return Configs.CLIENT_SETTINGS.isJeiPanel() ? 20 : 18;
    }

    private void initForScreen(Screen screen, InventoryPositionInfo invInfo) {

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

        if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
            this.itemStartX+=8;
            this.itemStartY+=26;
            this.itemEndY-=10;
        }
    }


    /**
     * Handles searchbar changes => responsible for custom prefixes
     *
     * @param newQuery The text that will be searched for.
     */
    private void updateQuery(String newQuery) {
        if (!newQuery.equals(this.currentQuery))
            this.startIndex = 0;

        this.currentQuery = newQuery;

        // advanced filtering
        if (newQuery.contains(" ")) {

            ArrayList<String> objects = new ArrayList<>();

            for (String query : newQuery.split(" ")) {
                if (!PrefixedFilter.startsWithPrefix(query)) {
                    objects.add(query);
                }
            }

            this.filteredItems.clear();
            this.filteredItems.addAll(ItemFilters.defaultFilter(String.join(" ", objects).strip()));

            for (String query : getCurrentQueries()) {
                ItemFilters.advancedFilter(filteredItems, query);
            }
        // standard filtering
        } else {
            this.filteredItems.clear();
            this.filteredItems.addAll(ItemFilters.filter(newQuery));
        }

        this.filteredItems.removeIf(ItemView::isExcludedItem);

        this.updateDisplayedItems();

        SidePanelOverlay.INSTANCE.updateSidePanelIndex(SidePanelOverlay.Reason.SEARCH);

        this.updateButtons(getPageCountText());
    }

    public void updateDisplayedItems() {
        List<ItemStack> items = this.filteredItems;
        if (Configs.STACK_GROUPS.areStackGroupsEnabled()) {
            boolean isSearching = isSearchingStackGroups();
            if (isSearching) {
                items = StackGroupManager.appendMatchingGroups(this.currentQuery, items);
            }
            items = StackGroupManager.applyGrouping(items, isSearching);
        }
        this.availableItems = items;
        this.availableItems.removeIf(ItemView::isExcludedItem);
        this.updateSlots();
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
				BookmarkManager.INSTANCE.bookmarkItem(slot.getStack());
			}
        }

        return false;
    }

    @Override
    protected @NonNull Identifier getReportedOverlayId() {
        return OverlayView.ITEM_VIEW;
    }

    @Override
    protected void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.fittingPerPage() == 0)
            return;

        if (Configs.CLIENT_SETTINGS.isRecipeBookTheme())
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, ReliableRecipeViewer.of("recipe_book"), checkedX(), checkedY()+20, checkedWidth()-4, checkedY()+checkedHeight()-40, -1);
        else
            guiGraphics.fill(checkedX(), checkedY(), checkedX() + checkedWidth(), checkedY() + checkedHeight(), new Color(0, 0, 0, 64).getRGB());
    }

    @Override
    protected void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;


        var page = getPageCountText();

        if (this.fittingPerPage() > 0) {
            int titleX = checkedX() + checkedWidth() / 2;
            int titleY = checkedY() + 10;
            if (Configs.CLIENT_SETTINGS.isRecipeBookTheme()) {
                titleY+=26;
            }
            this.drawScaledString(font, guiGraphics, page, titleX, titleY, -1);
        }


        ItemSlot.currentFrameSlots = this.itemSlots();
        for (ItemSlot slot : this.itemSlots()) {
            slot.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        }
        ItemSlot.currentFrameSlots = null;


        this.renderItemHighlighting(OverlayManager.INSTANCE.currentInfo().screen(), guiGraphics, mouseX, mouseY, partialTicks);


        drawProgressBar(guiGraphics, !Configs.CLIENT_SETTINGS.isRightIndex(), false);

    }

    public void renderItemHighlighting(Screen screen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.itemFilterMode)
            return;

        if (screen instanceof AbstractContainerScreen<?> abstractContainerScreen) {

            abstractContainerScreen.getMenu().slots.forEach(slot -> {

                if (!slot.isActive() || !slot.isHighlightable())
                    return;

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(OverlayManager.INSTANCE.currentInfo().leftPos() - 1, OverlayManager.INSTANCE.currentInfo().topPos() - 1);

                if (!slot.hasItem()
                        || this.availableItems.stream().noneMatch(stack -> stack.getItem() == slot.getItem().getItem())
                        && ItemFilters.getTooltipMatch(slot.getItem(), this.currentQuery) == 0) {
                    guiGraphics.fill(slot.x, slot.y, slot.x + 18, slot.y + 18, new Color(0, 0, 0, 128).getRGB());
                }
                guiGraphics.pose().popMatrix();

            });
        }


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

        updateSearchBarVisibility(newSearchbar);

        this.searchbar = newSearchbar;
    }

    private void updateSearchBarVisibility(SearchBar newSearchbar) {
        if (newSearchbar == null) return;
        newSearchbar.visible = !Configs.CLIENT_SETTINGS.isShowItemView().equals(OverlayDisplay.DISABLED) && !Configs.CLIENT_SETTINGS.isJeiPanel();
    }

    public void updateSearchBarVisibility() {
        updateSearchBarVisibility(this.searchbar);
    }

    /// Open a recipe view screen showing all recipes that either result in or create an item stack - dependent on the supplied [ActionType].
    public void openRecipeView(ItemStack stack, ActionType openType) {
        openRecipeView(stack, openType, ReliableClientRecipeType.NONE);
    }

    /// Open a recipe view screen showing all recipes that either result in or create an item stack - dependent on the supplied [ActionType].
    public void openRecipeView(ItemStack stack, ActionType openType, ReliableClientRecipeType type) {
        if (stack.isEmpty()) return;

        if (!InternalRecipeManager.INSTANCE.isRecipesSynced() && !warned) {
            RRVClientUtil.sendMessage(Component.translatable("recipe_sync.rrv.denied"));
            warned = true;
            if (Configs.CLIENT_SETTINGS.localFallbackAllowed().equals(LocalFallback.WHEN_NEEDED) || Configs.CLIENT_SETTINGS.localFallbackAllowed().equals(LocalFallback.ENABLED)) {
                ClientRecipeCache.INSTANCE.buildRecipeCache(false);
            }
        }

        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return;

        String namespace = RRVPlatform.INSTANCE.getModNamespaceForItem(stack);
        boolean isFromModWithIntegration = RrvUtil.getInitializedMods().contains(namespace) && RRVClientUtil.getInitializedMods().contains(namespace);
        if (Configs.CLIENT_SETTINGS.isJeiRecipeScreen() && (!Configs.CLIENT_SETTINGS.isPrioritizeNativeRecipeScreens() || !isFromModWithIntegration) && JeiCompatibilityUtil.hasRecipesForItem(stack, openType) && !stack.hasNonDefault(DataComponents.ITEM_MODEL) && !(ModCompat.POLYMER && PolymerHelpers.isPolymerServerItem(stack))) {
            JeiCompatibilityUtil.openJEI(stack, openType, RRVClientUtil.currentScreen());
            return;
        }

        List<ReliableClientRecipe> foundRecipes = switch (openType) {
            case INPUT -> ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(stack);
            case RESULT -> ClientRecipeCache.INSTANCE.getRecipesForCraftingOutput(stack);
            case ANY -> {
                var b = ClientRecipeCache.INSTANCE.getRecipesForCraftingInput(stack);
                b.addAll(ClientRecipeCache.INSTANCE.getRecipesForCraftingOutput(stack));
                yield b;
            }
        };

        if (!foundRecipes.isEmpty() || (ModCompat.POLYDEX && PolymerHelpers.isPolymerServerItem(stack)) || (ModCompat.JEI && JeiCompatibilityUtil.hasRecipesForItem(stack, openType))) {
            openRecipeView(stack, openType, clientPlayer, foundRecipes, type, false);
        }
    }

    /// Open a recipe view screen showing all recipes of a specific type.
    public void openRecipeView(ReliableClientRecipeType clientRecipeType) {

        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return;

        //? fabric {
        if (ModCompat.POLYDEX && clientRecipeType instanceof PolydexClientRecipeType) {
            ClientNetworkManager.sendPacketToServer(new StackActionPayload(ActionType.ANY, ""));
        }
        //?}

        openRecipeView(ItemStack.EMPTY, ActionType.ANY, clientPlayer, ClientRecipeCache.INSTANCE.getRecipes(), clientRecipeType, false);
    }

    /// Open a recipe view screen showing all recipes with a specific id.
    public void openRecipeView(Identifier recipeId, boolean shouldAttemptQuickCraft) {
        openRecipeView(ItemStack.EMPTY, ActionType.ANY, Minecraft.getInstance().player, ClientRecipeCache.INSTANCE.getRecipes(recipeId), ReliableClientRecipeType.NONE, shouldAttemptQuickCraft);
    }

    //// Open a recipe view screen. Should be called from a specific scenario with a list of recipes.
    private void openRecipeView(ItemStack stack, ActionType openType, LocalPlayer clientPlayer, List<ReliableClientRecipe> foundRecipes, ReliableClientRecipeType reliableClientRecipeType, boolean shouldAttemptQuickCraft) {
        if (clientPlayer == null) return;
        if (foundRecipes.isEmpty()) return;

        Screen parent = RRVClientUtil.currentScreen();

        ArrayList<RecipeViewScreen> viewHistory = new ArrayList<>();

        if (parent instanceof RecipeViewScreen viewScreen) {
            parent = viewScreen.getMenu().getParentScreen();
            viewHistory = viewScreen.getMenu().getViewHistory();
        }

        int containerId = parent instanceof AbstractContainerScreen<? extends AbstractContainerMenu> containerScreen ? containerScreen.getMenu().containerId : 0;

        RecipeViewMenu recipeViewMenu = new RecipeViewMenu(parent, containerId, clientPlayer.getInventory(), foundRecipes, stack, openType, viewHistory, reliableClientRecipeType);

        if (shouldAttemptQuickCraft && foundRecipes.size() == 1) {
            recipeViewMenu.quickCraft(foundRecipes.getFirst(), 0);
            return;
        }

        if (RRVClientUtil.currentScreen() instanceof RecipeViewScreen recipeViewScreen) {
            recipeViewScreen.setMenu(recipeViewMenu);
            recipeViewScreen.checkGui();
            recipeViewMenu.setViewScreen(recipeViewScreen);
        } else {
            RRVClientUtil.setScreen(new RecipeViewScreen(recipeViewMenu, clientPlayer.getInventory(), Component.empty()));
        }
    }


    public SearchBar getSearchbar() {
        return this.searchbar;
    }

    ///  Whether the user is currently using the search bar to filter their inventory.
    public boolean isItemFilterMode() {
        return this.itemFilterMode;
    }

    ///  The current value in the search bar.
    public String getCurrentQuery() {
        return this.currentQuery;
    }

    ///  Whether the search bar contains a value.
	public boolean isSearching() {
		return searchbar != null && searchbar.isVisible() && !searchbar.getValue().isEmpty();
	}

    /// Stack groups are always expanded when the user is searching.
    public boolean isSearchingStackGroups() {
        return this.currentQuery != null && !this.currentQuery.isEmpty();
    }

    public void setButtonVisibility(boolean b) {
        searchbar.visible = b;
        next.visible = b;
        back.visible = b;
    }

    ///  Set the status of whether the user was shown a warning that they're connected to an incompatible/vanilla server.
    public void setWarned(boolean b) {
        warned = b;
    }

    ///  Whether the user was shown a warning that they're connected to an incompatible/vanilla server.
    public boolean wasWarned() {
        return warned;
    }

    /// Returns an array of search parameters.
	public String[] getCurrentQueries() {
		if (currentQuery.contains(" ")) {
            return currentQuery.split(" ");
        } else {
            return new String[]{currentQuery};
		}
	}
}
