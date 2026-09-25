package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

//~ if <26 'SetRandomPotionFunction'->'EnchantRandomlyFunction'
@Mixin(net.minecraft.world.level.storage.loot.functions.SetRandomPotionFunction.class)
public interface SetRandomPotionFunctionAccessor {
    @Accessor(value = "options")
    Optional<HolderSet<Potion>> getOptions();
}
