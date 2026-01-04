package de.crafty.eiv.common.mixin.world.entity.npc;

import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? <26 {
import net.minecraft.world.entity.npc.villager.VillagerTrades;

@Mixin(VillagerTrades.SuspiciousStewForEmerald.class)
public interface SuspiciousStewForEmeraldAccessor {

    @Accessor("effects")
    SuspiciousStewEffects effects();

    @Accessor("xp")
    int xp();

}
//?} else {
/*@Mixin(VillagerType.class)
public interface SuspiciousStewForEmeraldAccessor {
}
*///?}