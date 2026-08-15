package cc.cassian.rrv.common.mixin.world.item;

import net.minecraft.world.item.CreativeModeTab;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.item.CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {
	@Accessor("CACHED_PARAMETERS")
	static CreativeModeTab.@Nullable ItemDisplayParameters getCachedParameters() {
		throw new UnsupportedOperationException();
	}
}
