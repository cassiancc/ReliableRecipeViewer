package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(EnchantRandomlyFunction.class)
public interface EnchantRandomlyFunctionAccessor {

    @Accessor(value = "options")
    Optional<HolderSet<Enchantment>> options();

    @Accessor(value = "onlyCompatible")
    boolean onlyCompatible();

    @Accessor(value = "includeAdditionalCostComponent")
    boolean includeAdditionalCostComponent();
}
