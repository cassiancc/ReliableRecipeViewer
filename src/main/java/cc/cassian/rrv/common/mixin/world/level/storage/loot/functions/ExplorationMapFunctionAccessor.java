package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.Holder;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExplorationMapFunction.class)
public interface ExplorationMapFunctionAccessor {

    @Accessor(value = "mapDecoration")
    Holder<MapDecorationType> getDecorationType();

    @Accessor(value = "zoom")
    byte getZoom();

}
