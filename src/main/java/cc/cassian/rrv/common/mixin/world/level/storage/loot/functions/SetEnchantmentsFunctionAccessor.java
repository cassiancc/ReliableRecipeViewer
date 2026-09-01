package cc.cassian.rrv.common.mixin.world.level.storage.loot.functions;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
//~ if >26.2 'NumberProvider'->'ints.ContextIntProvider'
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SetEnchantmentsFunction.class)
public interface SetEnchantmentsFunctionAccessor {

    @Accessor(value = "enchantments")
    //~ if >26.2 'NumberProvider'->'Holder<ContextIntProvider>'
    Map<Holder<Enchantment>, NumberProvider> enchantments();

    @Accessor(value = "add")
    boolean add();
}
