package de.crafty.eiv.common.mixin.world.level.storage.loot.entries;

import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CompositeEntryBase.class)
public interface CompositeEntryBaseAccessor {


    @Accessor("children")
    List<LootPoolEntryContainer> getChildren();
}
