package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import com.mojang.serialization.JsonOps;

public class ClientConfig extends AbstractRrvConfig {


    private OverlayManager.OverlayDisplay showOverlays = OverlayManager.OverlayDisplay.ENABLED;
    private boolean background = true;
    private boolean itemWrapMode = true;
    private boolean appendModNamespace = true;
    private boolean rightIndex = true;
    private boolean centerSearch = true;
    private boolean showBookmarks = true;
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

    public boolean isShowBookmarks() {
        return showBookmarks;
    }

    public void setShowBookmarks(boolean showBookmarks) {
        this.showBookmarks = showBookmarks;
    }

    @Override
    protected void loadData() {
        this.showOverlays = OverlayManager.OverlayDisplay.CODEC.decode(JsonOps.INSTANCE, this.data().get("enabled")).getOrThrow().getFirst();
        this.background = this.data().get("background").getAsBoolean();
        this.itemWrapMode = this.data().get("itemWrapMode").getAsBoolean();
        this.appendModNamespace = this.data().get("appendModNamespace").getAsBoolean();
        this.rightIndex = this.data().get("rightIndex").getAsBoolean();
        this.centerSearch = this.data().get("centerSearch").getAsBoolean();
        this.creativeIndexSource = this.data().get("indexSource").getAsBoolean();
    }

    @Override
    protected void saveData() {
        this.data().add("enabled", OverlayManager.OverlayDisplay.CODEC.encodeStart(JsonOps.INSTANCE, this.showOverlays).getOrThrow());
        this.data().addProperty("background", this.background);
        this.data().addProperty("itemWrapMode", this.itemWrapMode);
        this.data().addProperty("appendModNamespace", this.appendModNamespace);
        this.data().addProperty("rightIndex", this.rightIndex);
        this.data().addProperty("centerSearch", this.centerSearch);
        this.data().addProperty("indexSource", this.creativeIndexSource);
    }

	public boolean isCreativeIndexSource() {
		return creativeIndexSource;
	}

	public void setCreativeIndexSource(boolean creativeIndexSource) {
		this.creativeIndexSource = creativeIndexSource;
        ServerRecipeManager.INSTANCE.reload();
	}

	public OverlayManager.OverlayDisplay isShowOverlays() {
		return showOverlays;
	}

	public void setShowOverlays(OverlayManager.OverlayDisplay showOverlays) {
		this.showOverlays = showOverlays;
	}
}
