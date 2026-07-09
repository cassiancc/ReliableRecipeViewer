package cc.cassian.rrv.common.recipe.stackgroup.data.groups;

import cc.cassian.rrv.common.recipe.stackgroup.data.AbstractStackGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class CopperBlockItemGroup extends AbstractStackGroup {

    public CopperBlockItemGroup() {
        super(Identifier.withDefaultNamespace("copper_blocks"), null);
    }

    @Override
    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return path.equals("copper_block") || path.equals("cut_copper") ||
                path.equals("exposed_copper") || path.equals("exposed_cut_copper") ||
                path.equals("weathered_copper") || path.equals("weathered_cut_copper") ||
                path.equals("oxidized_copper") || path.equals("oxidized_cut_copper") ||
                path.equals("waxed_copper_block") || path.equals("waxed_cut_copper") ||
                path.equals("waxed_exposed_copper") || path.equals("waxed_exposed_cut_copper") ||
                path.equals("waxed_weathered_copper") || path.equals("waxed_weathered_cut_copper") ||
                path.equals("waxed_oxidized_copper") || path.equals("waxed_oxidized_cut_copper");
    }
}
