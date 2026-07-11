package cc.cassian.rrv.common.recipe.stackgroup.data;

import cc.cassian.rrv.common.config.options.ConfiguredStackGroup;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;
import java.util.Set;

public abstract class AbstractStackGroup {
    protected final Component name;
    private final Identifier id;
    public boolean isEnabled = true;
    public int priority = 0;

    protected AbstractStackGroup(Identifier id, Component name) {
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
        String path = id.getPath().replace("/", ".");
        String key = "stackgroup.rrv." + path;
        if (Language.getInstance().has(key)) {
            return Component.translatable(key);
        }
        String fallbackKey = "stackgroup.emixx." + path;
        if (Language.getInstance().has(fallbackKey)) {
            return Component.translatable(fallbackKey);
        }
        return Component.literal(WordUtils.capitalize(path.replace("_", " ")));
    }

	public ConfiguredStackGroup asConfiguredGroup() {
		return new ConfiguredStackGroup(this.getId(), this.isEnabled, this.priority, List.of());
	}
}
