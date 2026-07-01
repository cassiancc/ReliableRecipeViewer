package cc.cassian.rrv.common.recipe.stackgroup.data;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import java.util.Set;

public abstract class StackGroup {
    public final Component name;
    private final Identifier id;
    public boolean isEnabled = true;
    public int priority = 0;

    protected StackGroup(Identifier id, Component name) {
        this.id = id;
        this.name = name;
    }

    public Identifier getId() {
        return id;
    }

    public abstract boolean match(ItemStack stack);

    public Set<Identifier> getOptimizedIds() {
        return null;
    }

    public Component getName() {
        if (name != null) return name;
        String path = id.getPath();
        String key = "stackgroup.rrv." + path;
        String fallbackKey = "stackgroup.emixx." + path;
        if (Language.getInstance().has(key)) {
            return Component.translatable(key);
        }
        return Component.translatable(fallbackKey);
    }
}
