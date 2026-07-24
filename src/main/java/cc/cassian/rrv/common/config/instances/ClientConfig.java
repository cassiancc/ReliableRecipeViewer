package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.config.options.*;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;

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
	private IndexSource indexSource = IndexSource.CREATIVE_AND_REGISTRY;
	private boolean jeiPanel = false;
	private boolean jeiRecipeScreen = true;

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

	public IndexSource getIndexSource() {
		return indexSource;
	}

	public void setIndexSource(IndexSource indexSource) {
		this.indexSource = indexSource;
		ItemFilters.clearCaches();
	}

	public boolean isJeiPanel() {
		return ModCompat.JEI && jeiPanel;
	}

	public void setJeiPanel(boolean jeiPanel) {
		this.jeiPanel = jeiPanel;
	}

	public boolean isJeiRecipeScreen() {
		return jeiRecipeScreen;
	}

	public void setJeiRecipeScreen(boolean jeiRecipeScreen) {
		this.jeiRecipeScreen = jeiRecipeScreen;
	}

	@Override
	protected void loadData() {
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
		this.indexSource = load("indexSource", this.indexSource, IndexSource.CODEC);
		if (ModCompat.JEI) {
			this.jeiPanel = load("jeiPanel", this.jeiPanel);
			this.jeiRecipeScreen = load("jeiRecipeScreen", this.jeiRecipeScreen);
		}
	}

	@Override
	protected void saveData() {
		save("enabled", this.showItemView, OverlayDisplay.CODEC);
		save("sidePanelEnabled", this.showSidePanel, OverlayDisplay.CODEC);
		save("background", this.background);
		save("itemWrapMode", this.itemWrapMode);
		save("namespaceTooltip", this.namespaceTooltip, NamespaceTooltip.CODEC);
		save("rightIndex", this.rightIndex);
		save("centerSearch", this.centerSearch);
		save("showButtons", showButtons);
		save("showProgressBar", this.showProgressBar);
		save("fluidUnitDroplets", this.fluidUnitDroplets);
		save("centerRecipeScreen", this.centerRecipeScreen);
		save("showRecipeId", this.showRecipeId);
		save("recipeBookButton", this.recipeBookButton);
		save("recipeBookTheme", this.recipeBookTheme);
		save("wrapScrolling", this.wrapScrolling, WrapScrolling.CODEC);
		save("sidePanel", this.sidePanel, SidePanel.CODEC);
		save("localFallback", this.localFallback, LocalFallback.CODEC);
		save("workstationDisplay", this.workstationDisplay, WorkstationDisplay.CODEC);
		save("recipeSharing", this.recipeSharing);
		save("indexSource", this.indexSource, IndexSource.CODEC);
		if (ModCompat.JEI) {
			save("jeiPanel", this.jeiPanel);
			save("jeiRecipeScreen", this.jeiRecipeScreen);
		}
	}
}
