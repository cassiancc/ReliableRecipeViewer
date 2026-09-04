package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
//~ if >26.2 'NumberProvider'->'ints.ContextIntProvider'
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(EnchantWithLevelsFunction.class)
public interface EnchantWithLevelsFunctionAccessor {

    //~ if >26.2 'NumberProvider'->'Holder<ContextIntProvider>' {
    @Accessor(value = "levels")
    NumberProvider getLevels();
    //~}

    @Accessor(value = "options")
    Optional<HolderSet<Enchantment>> options();

    //? if >26 {
    @Accessor(value = "includeAdditionalCostComponent")
    boolean includeAdditionalCostComponent();
    //?}
}
