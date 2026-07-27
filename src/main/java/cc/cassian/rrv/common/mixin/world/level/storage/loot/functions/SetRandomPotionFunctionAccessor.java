package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(targets = "net.minecraft.world.level.storage.loot.functions.SetRandomPotionFunction")
public interface SetRandomPotionFunctionAccessor {
    @Accessor(value = "options")
    Optional<HolderSet<Potion>> getOptions();
}
