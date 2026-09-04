package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.component.DataComponentPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.world.level.storage.loot.functions.SetComponentsFunction.class)
public interface SetComponentsFunctionAccessor {
	@Accessor
	DataComponentPatch getComponents();
}
