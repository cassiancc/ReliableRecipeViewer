package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(EnchantWithLevelsFunction.class)
public interface EnchantWithLevelsFunctionAccessor {

    @Accessor(value = "levels")
    NumberProvider getLevels();

    @Accessor(value = "options")
    Optional<HolderSet<Enchantment>> options();

    @Accessor(value = "includeAdditionalCostComponent")
    boolean includeAdditionalCostComponent();
}
