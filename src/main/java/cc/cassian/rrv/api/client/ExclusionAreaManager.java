package cc.cassian.rrv.api.client;

import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public interface ExclusionAreaManager {
	void removeExclusionArea(Identifier id, boolean updateOverlays);

	void removeExclusionArea(Predicate<Identifier> filter, boolean updateOverlays);

	void setExclusionArea(BlockingGuiComponent comp);

	List<BlockingGuiComponent> exclusionAreas();
}
