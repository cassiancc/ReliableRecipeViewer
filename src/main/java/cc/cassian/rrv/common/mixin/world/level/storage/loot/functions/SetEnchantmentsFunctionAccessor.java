package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SetEnchantmentsFunction.class)
public interface SetEnchantmentsFunctionAccessor {

    @Accessor(value = "enchantments")
    Map<Holder<Enchantment>, NumberProvider> enchantments();

    @Accessor(value = "add")
    boolean add();
}
