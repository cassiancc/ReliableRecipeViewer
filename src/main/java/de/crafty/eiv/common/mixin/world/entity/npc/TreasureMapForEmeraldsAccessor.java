package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerTrades.TreasureMapForEmeralds.class)
public interface TreasureMapForEmeraldsAccessor {

    @Accessor("maxUses")
    int maxUses();

    @Accessor("villagerXp")
    int villagerXp();

    @Accessor("emeraldCost")
    int emeraldCost();

    @Accessor("destination")
    TagKey<Structure> destination();

    @Accessor("displayName")
    String displayName();
    
    @Accessor("destinationType")
    Holder<MapDecorationType> destinationType();
    
}
