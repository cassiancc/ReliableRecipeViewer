package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.ConfiguredStackGroup;
import cc.cassian.rrv.common.recipe.stackgroup.StackGroupManager;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;

import java.util.*;

public class StackGroupConfig extends AbstractRrvConfig {

	private boolean enabled = true;
	private final LinkedHashMap<Identifier, ConfiguredStackGroup> STACK_GROUPS = new LinkedHashMap<>();

	public ConfiguredStackGroup getOrDefault(Identifier groupId) {
		return Configs.STACK_GROUPS.STACK_GROUPS.getOrDefault(groupId, StackGroupManager.getGroup(groupId.toString()).asConfiguredGroup());
	}

	public ConfiguredStackGroup set(Identifier groupId, ConfiguredStackGroup configuredStackGroup) {
		return Configs.STACK_GROUPS.STACK_GROUPS.put(groupId, configuredStackGroup);
	}

	public StackGroupConfig() {
		super("stack_groups");
    }

	public boolean areStackGroupsEnabled() {
		return this.enabled;
	}

	public void setStackGroupsEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	protected void loadData() {
		this.enabled = load("enabled", this.enabled);
		if (this.data().has("stack_groups")) {
			this.data().getAsJsonObject("stack_groups").asMap().forEach((key, element) -> {
				try {
					ConfiguredStackGroup encodedItem = ConfiguredStackGroup.CODEC.decode(JsonOps.INSTANCE, element).getOrThrow().getFirst();
					Identifier id = Identifier.parse(key);
					STACK_GROUPS.put(id, new ConfiguredStackGroup(encodedItem.id(), encodedItem.enabled(), false, encodedItem.priority(), encodedItem.order()));
				} catch (Exception e) {
					ReliableRecipeViewer.LOGGER.error("Failed to load stack group from json: {}", key);
				}
			});
		}
	}

	@Override
	protected void saveData() {
		save("enabled", this.enabled);
		JsonObject itemList = new JsonObject();

		STACK_GROUPS.forEach((key, category) -> {
			itemList.add(category.id().toString(), ConfiguredStackGroup.CODEC.encodeStart(JsonOps.INSTANCE, category).getOrThrow());
		});

		this.data().add("stack_groups", itemList);
	}
}
