package cc.cassian.rrv.common.mixin.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer")
public interface LootPoolSingletonContainerAccessor {

    @Accessor("functions")
    List<LootItemFunction> getFunctions();
}
