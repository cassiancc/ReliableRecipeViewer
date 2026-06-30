package cc.cassian.rrv.common.mixin.world.level.predicates;

import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net.minecraft.world.level.levelgen.blockpredicates.CombiningPredicate")
public interface CombiningPredicateAccessor {
	//? if >26.2 {
	/*@Accessor
	List<BlockPredicate> getPredicates();
	*///?}
}
