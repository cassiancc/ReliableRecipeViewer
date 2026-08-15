package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.config.options.*;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.overlay.itemlist.view.PrefixedFilter;

import java.util.Map;

public class ClientConfig extends AbstractRrvConfig {

	private OverlayDisplay showItemView = OverlayDisplay.ENABLED;
	private OverlayDisplay showSidePanel = OverlayDisplay.WITH_ITEM_VIEW;
	private SidePanel sidePanel = SidePanel.DISABLED;
	private boolean background = true;
	private boolean showProgressBar = true;
	private boolean itemWrapMode = true;
	private WrapScrolling wrapScrolling = WrapScrolling.ON_BUTTONS;
	private WorkstationDisplay workstationDisplay = WorkstationDisplay.IN_FOOTER;
	private NamespaceTooltip namespaceTooltip = ModCompat.hasModNamespaceModsInstalled() ? NamespaceTooltip.HIDE : NamespaceTooltip.SHOW;
	private boolean showRecipeId = false;
	private boolean rightIndex = true;
	private boolean centerSearch = true;
	private boolean centerRecipeScreen = false;
	private boolean fluidUnitDroplets = false;
	private boolean showButtons = true;
	private boolean recipeBookButton = false;
	private boolean recipeBookTheme = true;
	private LocalFallback localFallback = LocalFallback.WHEN_NEEDED;
	private boolean recipeSharing = true;
	private Map<IndexSource, Boolean> indexSource = IndexSource.DEFAULT;
	private boolean jeiPanel = false;
	private boolean jeiRecipeScreen = true;
	private boolean prioritizeNativeRecipeScreens = true;
	private boolean clientSettingsButton = true;
	private boolean sidePanelSettingsButton = true;
	private Map<PrefixedFilter, PrefixedFilter.Configuration> searchFilters = PrefixedFilter.DEFAULT;

	public ClientConfig() {
		super("client_settings");
    }

	public boolean drawBackground() {
		return this.background;
	}

	public void setDrawBackground(boolean background) {
		this.background = background;
	}

	public boolean isItemWrapMode() {
		return this.itemWrapMode;
	}

	public void setItemWrapMode(boolean itemWrapMode) {
		this.itemWrapMode = itemWrapMode;
	}

	public NamespaceTooltip showNamespaceTooltip() {
		return namespaceTooltip;
	}
	
	public void setNamespaceTooltip(NamespaceTooltip namespaceTooltip) {
		this.namespaceTooltip = namespaceTooltip;
	}

	public boolean isRightIndex() {
		return rightIndex;
	}

	public void setRightIndex(boolean rightIndex) {
		this.rightIndex = rightIndex;
	}

	public boolean isCenterSearch() {
		return centerSearch;
	}

	public void setCenterSearch(boolean centerSearch) {
		this.centerSearch = centerSearch;
	}

	public OverlayDisplay isShowItemView() {
		return showItemView;
	}

	public void setShowItemView(OverlayDisplay showItemView) {
		this.showItemView = showItemView;
		ItemViewOverlay.INSTANCE.updateSearchBarVisibility();
	}

	public SidePanel getSidePanel() {
		return sidePanel;
	}

	public void setSidePanel(SidePanel sidePanel) {
		this.sidePanel = sidePanel;
	}

	public WrapScrolling isWrapScrolling() {
		return wrapScrolling;
	}

	public void setWrapScrolling(WrapScrolling wrapScrolling) {
		this.wrapScrolling = wrapScrolling;
	}

	public boolean isShowButtons() {
		return showButtons;
	}

	public void setShowButtons(boolean showButtons) {
		this.showButtons = showButtons;
	}

	public OverlayDisplay isShowSidePanel() {
		return showSidePanel;
	}

	public void setShowSidePanel(OverlayDisplay showSidePanel) {
		this.showSidePanel = showSidePanel;
	}

	public boolean isCenterRecipeScreen() {
		return centerRecipeScreen;
	}

	public void setCenterRecipeScreen(boolean centerRecipeScreen) {
		this.centerRecipeScreen = centerRecipeScreen;
	}

	public boolean isFluidUnitDroplets() {
		return fluidUnitDroplets;
	}

	public void setFluidUnitDroplets(boolean fluidUnitDroplets) {
		this.fluidUnitDroplets = fluidUnitDroplets;
	}

	public boolean isShowProgressBar() {
		return showProgressBar;
	}

	public void setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
	}

	public boolean isShowRecipeId() {
		return showRecipeId;
	}

	public void setShowRecipeId(boolean showRecipeId) {
		this.showRecipeId = showRecipeId;
	}

	public boolean isRecipeBookButton() {
		return recipeBookButton;
	}

	public void setRecipeBookButton(boolean recipeBookButton) {
		this.recipeBookButton = recipeBookButton;
	}

	public boolean isRecipeBookTheme() {
		return recipeBookTheme;
	}

	public void setRecipeBookTheme(boolean recipeBookTheme) {
		this.recipeBookTheme = recipeBookTheme;
	}

	public LocalFallback localFallbackAllowed() {
		return localFallback;
	}

	public void setLocalFallbackAllowed(LocalFallback localFallback) {
		this.localFallback = localFallback;
	}

	public WorkstationDisplay getWorkstationDisplay() {
		return workstationDisplay;
	}

	public void setWorkstationDisplay(WorkstationDisplay workstationDisplay) {
		this.workstationDisplay = workstationDisplay;
	}

	public boolean isRecipeSharing() {
		return recipeSharing;
	}

	public void setRecipeSharing(boolean recipeSharing) {
		this.recipeSharing = recipeSharing;
	}

	public Map<IndexSource, Boolean> getIndexSource() {
		return indexSource;
	}

	public boolean getIndexSource(IndexSource indexSource) {
		return getIndexSource().get(indexSource);
	}

	public void setIndexSource(Map<IndexSource, Boolean> indexSource) {
		this.indexSource = indexSource;
		ItemFilters.clearCaches(true);
	}

	public boolean isJeiPanel() {
		return ModCompat.JEI && jeiPanel;
	}

	public void setJeiPanel(boolean jeiPanel) {
		this.jeiPanel = jeiPanel;
		ItemViewOverlay.INSTANCE.updateSearchBarVisibility();
	}

	public boolean isJeiRecipeScreen() {
		return ModCompat.JEI && jeiRecipeScreen;
	}

	public void setJeiRecipeScreen(boolean jeiRecipeScreen) {
		this.jeiRecipeScreen = jeiRecipeScreen;
	}

	public boolean isPrioritizeNativeRecipeScreens() {
		return prioritizeNativeRecipeScreens;
	}

	public void setPrioritizeNativeRecipeScreens(boolean prioritizeNativeRecipeScreens) {
		this.prioritizeNativeRecipeScreens = prioritizeNativeRecipeScreens;
	}

	public boolean isClientSettingsButtonEnabled() {
		return clientSettingsButton;
	}

	public void setClientSettingsButton(boolean clientSettingsButton) {
		this.clientSettingsButton = clientSettingsButton;
	}

	public boolean isSidePanelSettingsButtonEnabled() {
		return sidePanelSettingsButton;
	}

	public void setSidePanelSettingsButton(boolean sidePanelSettingsButton) {
		this.sidePanelSettingsButton = sidePanelSettingsButton;
	}

	public Map<PrefixedFilter, PrefixedFilter.Configuration> getSearchFilters() {
		return searchFilters;
	}

	public void setSearchFilters(Map<PrefixedFilter, PrefixedFilter.Configuration> searchFilters) {
		this.searchFilters = searchFilters;
	}

	@Override
	protected void loadData() {
		boolean loadedDeprecatedConfig = false;
		if (data().has("enabled")) { // load deprecated config fields from v8.6.x and below
			this.showItemView = load("enabled", this.showItemView, OverlayDisplay.CODEC);
			this.showSidePanel = load("sidePanelEnabled", this.showSidePanel, OverlayDisplay.CODEC);
			this.background = load("background", this.background);
			this.itemWrapMode = load("itemWrapMode", this.itemWrapMode);
			this.namespaceTooltip = load("namespaceTooltip", this.namespaceTooltip, NamespaceTooltip.CODEC);
			this.rightIndex = load("rightIndex", this.rightIndex);
			this.centerSearch = load("centerSearch", this.centerSearch);
			this.showButtons = load("showButtons", showButtons);
			this.showProgressBar = load("showProgressBar", this.showProgressBar);
			this.fluidUnitDroplets = load("fluidUnitDroplets", this.fluidUnitDroplets);
			this.centerRecipeScreen = load("centerRecipeScreen", this.centerRecipeScreen);
			this.showRecipeId = load("showRecipeId", this.showRecipeId);
			this.recipeBookButton = load("recipeBookButton", this.recipeBookButton);
			this.recipeBookTheme = load("recipeBookTheme", this.recipeBookTheme);
			this.localFallback = load("localFallback", this.localFallback, LocalFallback.CODEC);
			this.wrapScrolling = load("wrapScrolling", this.wrapScrolling, WrapScrolling.CODEC);
			this.sidePanel = load("sidePanel", this.sidePanel, SidePanel.CODEC);
			this.workstationDisplay = load("workstationDisplay", this.workstationDisplay, WorkstationDisplay.CODEC);
			this.recipeSharing = load("recipeSharing", this.recipeSharing);
			loadedDeprecatedConfig = true;
		} else {
			this.showItemView = load("general", "show_item_view", this.showItemView, OverlayDisplay.CODEC);
			this.showSidePanel = load("general", "show_side_panel", this.showSidePanel, OverlayDisplay.CODEC);
			this.background = load("style", "background", this.background);
			this.itemWrapMode = load("style", "item_wrap_mode", this.itemWrapMode);
			this.namespaceTooltip = load("style", "namespace_tooltip", this.namespaceTooltip, NamespaceTooltip.CODEC);
			this.rightIndex = load("style","right_index", this.rightIndex);
			this.centerSearch = load( "style","center_search", this.centerSearch);
			this.showButtons = load("style", "show_buttons", showButtons);
			this.showProgressBar = load("style", "show_progress_bar", this.showProgressBar);
			this.fluidUnitDroplets = load("advanced", "fluid_unit_droplets", this.fluidUnitDroplets);
			this.centerRecipeScreen = load("style", "center_recipe_screen", this.centerRecipeScreen);
			this.showRecipeId = load("advanced", "show_recipe_id", this.showRecipeId);
			this.recipeBookButton = load("behaviour", "recipe_book_button", this.recipeBookButton);
			this.recipeBookTheme = load("style", "recipe_book_theme", this.recipeBookTheme);
			this.localFallback = load("advanced", "local_fallback", this.localFallback, LocalFallback.CODEC);
			this.wrapScrolling = load("behaviour", "wrap_scrolling", this.wrapScrolling, WrapScrolling.CODEC);
			this.sidePanel = load("behaviour", "side_panel_contents", this.sidePanel, SidePanel.CODEC);
			this.workstationDisplay = load("style", "workstation_display", this.workstationDisplay, WorkstationDisplay.CODEC);
			this.recipeSharing = load("behaviour", "recipe_sharing", this.recipeSharing);
		}

		this.clientSettingsButton = load("general", "client_settings_button", this.clientSettingsButton);
		this.sidePanelSettingsButton = load("general", "side_panel_settings_button", this.sidePanelSettingsButton);
		this.indexSource = load("advanced", "index_sources", this.indexSource, IndexSource.CODEC);
		this.searchFilters = load("advanced", "search_filters", this.searchFilters, PrefixedFilter.CODEC);
		if (ModCompat.JEI) {
			this.jeiPanel = load("jei","panel", this.jeiPanel);
			this.jeiRecipeScreen = load("jei", "recipe_screen", this.jeiRecipeScreen);
			this.prioritizeNativeRecipeScreens = load("jei", "prioritize_native_screens", this.prioritizeNativeRecipeScreens);
		}

		if (loadedDeprecatedConfig) {
			ReliableRecipeViewer.LOGGER.info("Upgraded config file from v8.6 to v8.7.");
			save();
		}
	}

	@Override
	protected void saveData() {
		save("general", "show_item_view", this.showItemView, OverlayDisplay.CODEC);
		save("general", "show_side_panel", this.showSidePanel, OverlayDisplay.CODEC);
		save("general", "client_settings_button", this.clientSettingsButton);
		save("general", "side_panel_settings_button", this.sidePanelSettingsButton);
		save("style", "background", this.background);
		save("style", "item_wrap_mode", this.itemWrapMode);
		save("style", "namespace_tooltip", this.namespaceTooltip, NamespaceTooltip.CODEC);
		save("style","right_index", this.rightIndex);
		save("style","center_search", this.centerSearch);
		save("style", "show_buttons", showButtons);
		save("style", "show_progress_bar", this.showProgressBar);
		save("advanced", "fluid_unit_droplets", this.fluidUnitDroplets);
		save("style", "center_recipe_screen", this.centerRecipeScreen);
		save("advanced", "show_recipe_id", this.showRecipeId);
		save("behaviour", "recipe_book_button", this.recipeBookButton);
		save("style", "recipe_book_theme", this.recipeBookTheme);
		save("advanced", "local_fallback", this.localFallback, LocalFallback.CODEC);
		save("behaviour", "wrap_scrolling", this.wrapScrolling, WrapScrolling.CODEC);
		save("behaviour", "side_panel_contents", this.sidePanel, SidePanel.CODEC);
		save("style", "workstation_display", this.workstationDisplay, WorkstationDisplay.CODEC);
		save("behaviour", "recipe_sharing", this.recipeSharing);
		save("advanced", "index_sources", this.indexSource, IndexSource.CODEC);
		save("advanced", "search_filters", this.searchFilters, PrefixedFilter.CODEC);
		if (ModCompat.JEI) {
			save("jei","panel", this.jeiPanel);
			save("jei", "recipe_screen", this.jeiRecipeScreen);
			save("jei", "prioritize_native_screens", this.prioritizeNativeRecipeScreens);
		}

		// remove deprecated config fields from v8.6.x and below
		remove("enabled");
		remove("sidePanelEnabled");
		remove("background");
		remove("itemWrapMode");
		remove("namespaceTooltip");
		remove("rightIndex");
		remove("centerSearch");
		remove("showButtons");
		remove("showProgressBar");
		remove("fluidUnitDroplets");
		remove("centerRecipeScreen");
		remove("showRecipeId");
		remove("recipeBookButton");
		remove("recipeBookTheme");
		remove("wrapScrolling");
		remove("sidePanel");
		remove("localFallback");
		remove("workstationDisplay");
		remove("recipeSharing");
		remove("indexSource");
	}

	private void remove(String oldKey) {
		this.data().remove(oldKey);
	}
}
