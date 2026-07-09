package cc.cassian.rrv.common.config.options;

import cc.cassian.rrv.common.recipe.stackgroup.data.AbstractStackGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

/// Configuration for a [AbstractStackGroup] that can be serialized to the config JSON.
public record ConfiguredStackGroup(Identifier id, boolean enabled, int priority, List<String> order) {
	public static final Codec<ConfiguredStackGroup> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							Identifier.CODEC.fieldOf("id").forGetter(ConfiguredStackGroup::id),
							Codec.BOOL.optionalFieldOf("enabled", true).forGetter(ConfiguredStackGroup::enabled),
							Codec.INT.optionalFieldOf("priority", 0).forGetter(ConfiguredStackGroup::priority),
							Codec.STRING.listOf().optionalFieldOf("order", List.of()).forGetter(ConfiguredStackGroup::order)
					)
					.apply(instance, ConfiguredStackGroup::new)
	);
}
