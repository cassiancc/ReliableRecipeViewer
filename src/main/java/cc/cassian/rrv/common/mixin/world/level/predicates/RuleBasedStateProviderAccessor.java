package cc.cassian.rrv.common.mixin.world.level.predicates;

import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider.class)
public interface RuleBasedStateProviderAccessor {
	//? if >26.2 {
	/*@Accessor
	List<RuleBasedStateProvider.Rule> getRules();
	*///?}
}
