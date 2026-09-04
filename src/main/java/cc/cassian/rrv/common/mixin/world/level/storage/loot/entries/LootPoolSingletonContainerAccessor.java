package cc.cassian.rrv.common.mixin.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

//~ if >26.2 'LootPoolSingletonContainer'->'SingleEntryContainerBase'
@Mixin(value = net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.class)
public interface LootPoolSingletonContainerAccessor {

    //? if <26.3 {
    @Accessor("functions")
    List<LootItemFunction> getFunctions();
    //?}
}
