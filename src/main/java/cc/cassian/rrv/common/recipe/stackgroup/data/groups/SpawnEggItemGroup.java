package cc.cassian.rrv.common.recipe.stackgroup.data.groups;

import cc.cassian.rrv.common.recipe.stackgroup.data.StackGroup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class SpawnEggItemGroup extends StackGroup {
    public SpawnEggItemGroup() {
        super(Identifier.withDefaultNamespace("spawn_eggs"), null);
    }

    @Override
    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof SpawnEggItem;
    }
}
