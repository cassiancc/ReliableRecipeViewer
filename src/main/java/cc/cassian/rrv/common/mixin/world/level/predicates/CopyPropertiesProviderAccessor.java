package cc.cassian.rrv.common.mixin.world.level.predicates;

import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider")
public interface CopyPropertiesProviderAccessor {
	//? if >26.2 {
	/*@Invoker
	BlockStateProvider callGetBaseBlockState();
	*///?}
}
