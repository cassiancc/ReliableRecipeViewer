package cc.cassian.rrv.common.mixin.world.entity.npc;


import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? <26 {
import net.minecraft.world.entity.npc.VillagerTrades;

@Mixin(VillagerTrades.DyedArmorForEmeralds.class)
public interface DyedArmorForEmeraldsAccessor {

    @Accessor("villagerXp")
    int getVillagerXp();

    @Accessor("maxUses")
    int getMaxUses();


    @Accessor("item")
    Item getItem();

    @Accessor("value")
    int getValue();

}

//?} else {
/*@Mixin(Structure.class)
public interface DyedArmorForEmeraldsAccessor {
}
*///?}
