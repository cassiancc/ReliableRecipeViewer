package cc.cassian.rrv.api.event;

import cc.cassian.rrv.api.client.ExclusionAreaManager;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.gui.screens.Screen;

@SuppressWarnings("unused")
public class OverlayManagementEvents {

	///  Register an exclusion area that items will not overlap with.
	public static void registerExclusionArea(AddExclusionAreasEvent event) {
		OverlayManager.EXCLUSION_AREA_EVENTS.add(event);
	}

	@FunctionalInterface
	public interface AddExclusionAreasEvent {
		void addExclusionAreas(Screen screen, ExclusionAreaManager instance, float partialTicks);
	}
}
