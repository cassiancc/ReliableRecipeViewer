package cc.cassian.rrv.common.mixin.world.item.alchemy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net.minecraft.world.item.alchemy.PotionBrewing")
public interface PotionBrewingAccessor {
    //? if <26.3 {

    @Accessor("potionMixes")
    List<net.minecraft.world.item.alchemy.PotionBrewing.Mix<Potion>> getPotionMixes();

    @Accessor("containerMixes")
    List<net.minecraft.world.item.alchemy.PotionBrewing.Mix<Item>> getContainerMixes();
    //?}
}
