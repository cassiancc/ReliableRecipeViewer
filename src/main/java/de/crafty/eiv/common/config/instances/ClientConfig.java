package de.crafty.eiv.common.config.instances;

import de.crafty.eiv.common.config.AbstractEivConfig;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;

public class ClientConfig extends AbstractEivConfig {


    private boolean background = true;
    private boolean itemWrapMode = true;
    private boolean appendModNamespace = true;
    private boolean rightIndex = true;
    private boolean centerSearch = true;

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
        ItemViewOverlay.setFooterHeight();
    }

    @Override
    protected void loadData() {
        this.background = this.data().get("background").getAsBoolean();
        this.itemWrapMode = this.data().get("itemWrapMode").getAsBoolean();
        this.appendModNamespace = this.data().get("appendModNamespace").getAsBoolean();
        this.rightIndex = this.data().get("rightIndex").getAsBoolean();
        this.centerSearch = this.data().get("centerSearch").getAsBoolean();
    }

    @Override
    protected void saveData() {
        this.data().addProperty("background", this.background);
        this.data().addProperty("itemWrapMode", this.itemWrapMode);
        this.data().addProperty("appendModNamespace", this.appendModNamespace);
        this.data().addProperty("rightIndex", this.rightIndex);
        this.data().addProperty("centerSearch", this.centerSearch);
    }
}
