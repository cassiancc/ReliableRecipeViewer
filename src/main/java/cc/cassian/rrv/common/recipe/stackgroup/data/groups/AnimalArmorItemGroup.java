package cc.cassian.rrv.common.recipe.stackgroup.data.groups;

import cc.cassian.rrv.common.recipe.stackgroup.data.StackGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class AnimalArmorItemGroup extends StackGroup {
    public AnimalArmorItemGroup() {
        super(Identifier.withDefaultNamespace("animal_armors"), null);
    }

    @Override
    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = id.getPath();
        return path.endsWith("_horse_armor") || path.equals("wolf_armor") || path.endsWith("_nautilus_armor");
    }
}
