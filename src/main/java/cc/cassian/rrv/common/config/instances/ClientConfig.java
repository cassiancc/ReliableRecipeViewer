package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.config.options.OverlayDisplay;
import cc.cassian.rrv.common.config.options.SidePanel;
import cc.cassian.rrv.common.config.options.WrapScrolling;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;

public class ClientConfig extends AbstractRrvConfig {

	private OverlayDisplay showOverlays = OverlayDisplay.ENABLED;
	private SidePanel sidePanel = SidePanel.DISABLED;
	private boolean background = true;
	private boolean itemWrapMode = true;
	private WrapScrolling wrapScrolling = WrapScrolling.ON_BUTTONS;
	private boolean appendModNamespace = true;
	private boolean rightIndex = true;
	private boolean centerSearch = true;
	private boolean creativeIndexSource = false;

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

	public boolean isAppendModNamespace() {
		return appendModNamespace;
	}
	
	public void setAppendModNamespace(boolean appendModNamespace) {
		this.appendModNamespace = appendModNamespace;
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

	@Override
	protected void loadData() {
		this.showOverlays = OverlayDisplay.CODEC.decode(JsonOps.INSTANCE, this.data().get("enabled")).mapOrElse(Pair::getFirst, (e)-> OverlayDisplay.ENABLED);
		this.background = this.data().get("background").getAsBoolean();
		this.itemWrapMode = this.data().get("itemWrapMode").getAsBoolean();
		this.appendModNamespace = this.data().get("appendModNamespace").getAsBoolean();
		this.rightIndex = this.data().get("rightIndex").getAsBoolean();
		this.centerSearch = this.data().get("centerSearch").getAsBoolean();
		this.creativeIndexSource = this.data().get("indexSource").getAsBoolean();
		this.wrapScrolling = WrapScrolling.CODEC.decode(JsonOps.INSTANCE, this.data().get("wrapScrolling")).mapOrElse(Pair::getFirst, (e)->WrapScrolling.ON_BUTTONS);
		this.sidePanel = SidePanel.CODEC.decode(JsonOps.INSTANCE, this.data().get("sidePanel")).mapOrElse(Pair::getFirst, (e)-> SidePanel.BOOKMARKS);
	}

	@Override
	protected void saveData() {
		this.data().add("enabled", OverlayDisplay.CODEC.encodeStart(JsonOps.INSTANCE, this.showOverlays).getOrThrow());
		this.data().addProperty("background", this.background);
		this.data().addProperty("itemWrapMode", this.itemWrapMode);
		this.data().addProperty("appendModNamespace", this.appendModNamespace);
		this.data().addProperty("rightIndex", this.rightIndex);
		this.data().addProperty("centerSearch", this.centerSearch);
		this.data().addProperty("indexSource", this.creativeIndexSource);
		this.data().add("wrapScrolling", WrapScrolling.CODEC.encodeStart(JsonOps.INSTANCE, this.wrapScrolling).getOrThrow());
		this.data().add("sidePanel", SidePanel.CODEC.encodeStart(JsonOps.INSTANCE, this.sidePanel).getOrThrow());

	}

	public boolean isCreativeIndexSource() {
		return creativeIndexSource;
	}

	public void setCreativeIndexSource(boolean creativeIndexSource) {
		this.creativeIndexSource = creativeIndexSource;
		ServerRecipeManager.INSTANCE.reload();
	}

	public OverlayDisplay isShowOverlays() {
		return showOverlays;
	}

	public void setShowOverlays(OverlayDisplay showOverlays) {
		this.showOverlays = showOverlays;
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
}
