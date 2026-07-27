//? if fabric || >26 {
package cc.cassian.rrv.fabric;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.client.util.RRVClientUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import org.jetbrains.annotations.ApiStatus;

/// Separated out from standard Fabric classes so it can be reused for Launchpad.
@ApiStatus.Internal
public class FabricClientUtil {

	/// Initialize client entrypoints from `fabric.mod.json` files.
	public static void initializeClient() {
		for (EntrypointContainer<ReliableRecipeViewerClientPlugin> container : FabricLoader.getInstance().getEntrypointContainers("rrv_client", ReliableRecipeViewerClientPlugin.class)) {
			RRVClientUtil.initializeEntrypoint(container.getProvider().getMetadata().getId(), container.getEntrypoint());
		}
	}
}
//?}