package cc.cassian.rrv.common.recipe.stackgroup.data.groups;

import cc.cassian.rrv.common.recipe.stackgroup.data.StackGroup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.InfestedBlock;

public class InfestedBlockItemGroup extends StackGroup {
    public InfestedBlockItemGroup() {
        super(Identifier.withDefaultNamespace("infested_blocks"), null);
    }

    @Override
    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item instanceof BlockItem bi && bi.getBlock() instanceof InfestedBlock;
    }
}
