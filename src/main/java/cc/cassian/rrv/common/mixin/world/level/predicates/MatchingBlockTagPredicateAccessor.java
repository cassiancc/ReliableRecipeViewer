package cc.cassian.rrv.common.mixin.world.level.predicates;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.world.level.levelgen.blockpredicates.MatchingBlockTagPredicate.class)
public interface MatchingBlockTagPredicateAccessor {
	//? if >26.2 {
	/*@Accessor
	TagKey<Block> getTag();
	*///?}
}
