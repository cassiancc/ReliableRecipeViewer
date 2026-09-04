package cc.cassian.rrv.common.mixin.world.item;

import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTabs.class)
public class CreativeModeTabsMixin {
	@Inject(method = "buildAllTabContents", at = @At("RETURN"))
	private static void afterReload(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
		ItemFilters.clearCaches();
	}
}
