package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.config.options.NamespaceTooltip;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.config.options.WrapScrolling;
import cc.cassian.rrv.common.integration.ModCompat;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;

public class ClientConfig extends AbstractRrvConfig {

	private boolean recipeBookButton;
    private OverlayDisplay showItemView = OverlayDisplay.ENABLED;
	private OverlayDisplay showSidePanel = OverlayDisplay.WITH_ITEM_VIEW;
	private SidePanel sidePanel = SidePanel.DISABLED;
	private boolean background = true;
	private boolean showProgressBar = true;
	private boolean itemWrapMode = true;
	private WrapScrolling wrapScrolling = WrapScrolling.ON_BUTTONS;
	private NamespaceTooltip namespaceTooltip = ModCompat.hasModNamespaceModsInstalled() ? NamespaceTooltip.HIDE : NamespaceTooltip.SHOW;
	private boolean showRecipeId = false;
	private boolean rightIndex = true;
	private boolean centerSearch = true;
	private boolean centerRecipeScreen = false;
	private boolean fluidUnitDroplets = false;
	private boolean showButtons = true;

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

	@Override
	protected void loadData() {
		this.showItemView = OverlayDisplay.CODEC.decode(JsonOps.INSTANCE, this.data().get("enabled")).mapOrElse(Pair::getFirst, (e)-> OverlayDisplay.ENABLED);
		this.showSidePanel = OverlayDisplay.CODEC.decode(JsonOps.INSTANCE, this.data().get("sidePanelEnabled")).mapOrElse(Pair::getFirst, (e)-> OverlayDisplay.ENABLED);
		this.background = this.data().get("background").getAsBoolean();
		this.itemWrapMode = this.data().get("itemWrapMode").getAsBoolean();
		this.namespaceTooltip = NamespaceTooltip.CODEC.decode(JsonOps.INSTANCE, this.data().get("appendNamespaceTooltip")).mapOrElse(Pair::getFirst, (e)->this.namespaceTooltip);
		this.rightIndex = this.data().get("rightIndex").getAsBoolean();
		this.centerSearch = this.data().get("centerSearch").getAsBoolean();
		this.showButtons = this.data().get("showButtons").getAsBoolean();
		this.showProgressBar = this.data().get("showProgressBar").getAsBoolean();
		this.fluidUnitDroplets = this.data().get("fluidUnitDroplets").getAsBoolean();
		this.centerRecipeScreen = this.data().get("centerRecipeScreen").getAsBoolean();
		this.showRecipeId = this.data().get("showRecipeId").getAsBoolean();
		this.recipeBookButton = this.data().get("recipeBookButton").getAsBoolean();
		this.wrapScrolling = WrapScrolling.CODEC.decode(JsonOps.INSTANCE, this.data().get("wrapScrolling")).mapOrElse(Pair::getFirst, (e)->WrapScrolling.ON_BUTTONS);
		this.sidePanel = SidePanel.CODEC.decode(JsonOps.INSTANCE, this.data().get("sidePanel")).mapOrElse(Pair::getFirst, (e)-> SidePanel.BOOKMARKS);
	}

	@Override
	protected void saveData() {
		this.data().add("enabled", OverlayDisplay.CODEC.encodeStart(JsonOps.INSTANCE, this.showItemView).getOrThrow());
		this.data().add("sidePanelEnabled", OverlayDisplay.CODEC.encodeStart(JsonOps.INSTANCE, this.showSidePanel).getOrThrow());
		this.data().addProperty("background", this.background);
		this.data().addProperty("itemWrapMode", this.itemWrapMode);
		this.data().add("namespaceTooltip", NamespaceTooltip.CODEC.encodeStart(JsonOps.INSTANCE, this.namespaceTooltip).getOrThrow());
		this.data().addProperty("rightIndex", this.rightIndex);
		this.data().addProperty("centerSearch", this.centerSearch);
		this.data().addProperty("showButtons", this.showButtons);
		this.data().addProperty("showProgressBar", this.showProgressBar);
		this.data().addProperty("fluidUnitDroplets", this.fluidUnitDroplets);
		this.data().addProperty("centerRecipeScreen", this.centerRecipeScreen);
		this.data().addProperty("showRecipeId", this.showRecipeId);
		this.data().addProperty("recipeBookButton", this.recipeBookButton);
		this.data().add("wrapScrolling", WrapScrolling.CODEC.encodeStart(JsonOps.INSTANCE, this.wrapScrolling).getOrThrow());
		this.data().add("sidePanel", SidePanel.CODEC.encodeStart(JsonOps.INSTANCE, this.sidePanel).getOrThrow());

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
}
