package cc.cassian.rrv.common.recipe.stackgroup.data.groups;

import cc.cassian.rrv.common.recipe.stackgroup.data.StackGroup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;

public class MinecartItemGroup extends StackGroup {
    public MinecartItemGroup() {
        super(Identifier.withDefaultNamespace("minecarts"), null);
    }

    @Override
    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof MinecartItem;
    }
}
