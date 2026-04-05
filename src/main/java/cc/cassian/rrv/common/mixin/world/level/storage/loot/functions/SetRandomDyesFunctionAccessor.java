package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.world.level.storage.loot.functions.SetRandomDyesFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SetRandomDyesFunction.class)
public interface SetRandomDyesFunctionAccessor {
    @Accessor(value = "numberOfDyes")
    NumberProvider getNumberOfDyes();
}
