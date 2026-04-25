package cc.cassian.rrv.common.mixin;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
//? if >26.1 {
/*import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.EntitySubPredicate;
*///?} else {
import net.minecraft.advancements.criterion.EntityPredicate;
//?}

import java.util.Map;

@Mixin(EntityPredicate.class)
public interface EntityPredicateAccessor {
    //? if >26.1 {
    /*@Accessor
    Map<Codec<? extends EntitySubPredicate>, EntitySubPredicate> getParts();
    *///?}
}
