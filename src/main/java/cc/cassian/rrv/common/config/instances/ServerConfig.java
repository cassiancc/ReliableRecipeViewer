package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.config.options.*;
import cc.cassian.rrv.common.integration.ModCompat;

public class ServerConfig extends AbstractRrvConfig {


	private boolean recipeSharing = true;

	public ServerConfig() {
		super("server_settings");
    }

	public boolean isRecipeSharing() {
		return recipeSharing;
	}

	public void setRecipeSharing(boolean recipeSharing) {
		this.recipeSharing = recipeSharing;
	}

	@Override
	protected void loadData() {
		this.recipeSharing = load("recipeSharing", this.recipeSharing);
	}

	@Override
	protected void saveData() {
		save("recipeSharing", this.recipeSharing);
	}

}
