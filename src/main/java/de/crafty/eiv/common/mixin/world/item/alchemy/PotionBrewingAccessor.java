package de.crafty.eiv.common.mixin.world.item.alchemy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PotionBrewing.class)
public interface PotionBrewingAccessor {

    @Accessor("potionMixes")
    List<PotionBrewing.Mix<Potion>> getPotionMixes();

    @Accessor("containerMixes")
    List<PotionBrewing.Mix<Item>> getContainerMixes();
}
