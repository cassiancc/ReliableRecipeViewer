package cc.cassian.rrv.common.mixin.world.level.storage.loot;


import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
//~ if >26.2 'NumberProvider'->'ints.ContextIntProvider'
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Mixin(LootPool.class)
public interface LootPoolAccessor {

    @Accessor("entries")
    List<LootPoolEntryContainer> entries();

    //? if <26.3 {
    @Accessor("conditions")
    List<LootItemCondition> conditions();
    //?} else {
    /*@Accessor("condition")
    Optional<Holder<LootItemCondition>> conditions();
    *///?}
    
}
