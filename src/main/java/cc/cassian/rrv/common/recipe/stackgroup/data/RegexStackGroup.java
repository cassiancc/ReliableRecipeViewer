package cc.cassian.rrv.common.recipe.stackgroup.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.regex.Pattern;

public class RegexStackGroup extends StackGroup {
    private final Pattern pattern;

    public RegexStackGroup(Identifier id, Pattern pattern, Component name) {
        super(id, name);
        this.pattern = pattern;
    }

    @Override
    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return pattern.matcher(itemId.toString()).matches();
    }
}
