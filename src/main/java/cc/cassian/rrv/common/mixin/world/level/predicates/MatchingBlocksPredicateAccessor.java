package cc.cassian.rrv.common.mixin.world.level.predicates;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate")
public interface MatchingBlocksPredicateAccessor {
	//? if >26.2 {
	/*@Accessor
	HolderSet<Block> getBlocks();
	*///?}
}
