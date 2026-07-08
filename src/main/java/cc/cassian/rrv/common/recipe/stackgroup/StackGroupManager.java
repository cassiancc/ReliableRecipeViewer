package cc.cassian.rrv.common.recipe.stackgroup;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.instances.StackGroupConfig;
import cc.cassian.rrv.common.config.options.ConfiguredStackGroup;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import cc.cassian.rrv.common.recipe.stackgroup.data.IdentifierStackGroup;
import cc.cassian.rrv.common.recipe.stackgroup.data.RegexStackGroup;
import cc.cassian.rrv.common.recipe.stackgroup.data.StackGroup;
import cc.cassian.rrv.common.recipe.stackgroup.data.groups.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class StackGroupManager {
    public static final List<StackGroup> stackGroups = new ArrayList<>();
    private static final Map<Item, StackGroup> itemToGroupCache = new IdentityHashMap<>();
    private static final Map<String, BiFunction<Identifier, JsonObject, StackGroup>> typeRegistry = new HashMap<>();

    static {
        registerType("rrv:group", (id, json) -> IdentifierStackGroup.parse(json, id));
        registerType("rrv:tag", StackGroupManager::parseTagGroup);
        registerType("rrv:component", StackGroupManager::parseComponentGroup);
        registerType("rrv:regex", StackGroupManager::parseRegexGroup);
        registerType("rrv:pressure_plates", (_, _) -> new PressurePlateItemGroup());
        registerType("rrv:minecarts", (_, _) -> new MinecartItemGroup());
        registerType("rrv:infested_blocks", (_, _) -> new InfestedBlockItemGroup());
        registerType("rrv:copper_blocks", (_, _) -> new CopperBlockItemGroup());
        registerType("rrv:coral", (_, _) -> new CoralItemGroup());
    }

    public static void registerType(String type, BiFunction<Identifier, JsonObject, StackGroup> factory) {
        typeRegistry.put(type, factory);
    }

    private static StackGroup parseTagGroup(Identifier id, JsonObject json) {
        String tagName = GsonHelper.getAsString(json, "tag");
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, Identifier.parse(tagName));
        String nameKey = json.has("name") ? GsonHelper.getAsString(json, "name") : null;
        Component customName = nameKey != null ? Component.translatable(nameKey) : null;
        int priority = json.has("priority") ? GsonHelper.getAsInt(json, "priority", 0) : 0;

        IdentifierStackGroup group = new IdentifierStackGroup(id, Set.of(), Set.of(tagKey), Set.of(), Set.of(), List.of(), customName);
        group.priority = priority;
        return group;
    }

    private static StackGroup parseComponentGroup(Identifier id, JsonObject json) {
        String tagName = GsonHelper.getAsString(json, "component");
        DataComponentType<?> dataComponent = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.parse(tagName));
        if (dataComponent == null) throw new IllegalArgumentException("%s references data component %s which does not exist in the registry!".formatted(id, tagName));
        String nameKey = json.has("name") ? GsonHelper.getAsString(json, "name") : null;
        Component customName = nameKey != null ? Component.translatable(nameKey) : null;
        int priority = json.has("priority") ? GsonHelper.getAsInt(json, "priority", 0) : 0;

        IdentifierStackGroup group = new IdentifierStackGroup(id, Set.of(), Set.of(), Set.of(dataComponent), Set.of(), List.of(), customName);
        group.priority = priority;
        return group;
    }

    private static StackGroup parseRegexGroup(Identifier id, JsonObject json) {
        String regexString = GsonHelper.getAsString(json, "regex");
        String nameKey = json.has("name") ? GsonHelper.getAsString(json, "name") : null;
        Component customName = nameKey != null ? Component.translatable(nameKey) : null;
        try {
            return new RegexStackGroup(id, Pattern.compile(regexString), customName);
        } catch (Exception e) {
            return null;
        }
    }

    public static Path getGroupPath(Identifier tag) {
        String name = tag.getPath().replace('/', '_');
        String filename = tag.getNamespace() + "_" + name + ".json";
        return ReliableRecipeViewer.CONFIG_PATH.resolve("stack_groups").resolve(filename);
    }

    public static boolean hasGroup(Identifier tag) {
        for (StackGroup g : stackGroups) {
            if (g.getId().equals(tag)) return true;
        }
        return false;
    }

    public static StackGroup getGroup(String id) {
        for (StackGroup group : stackGroups) {
            if (group.getId().toString().equals(id)) {
                return group;
            }
        }
        return null;
    }

    public static void toggleTagGroup(Identifier tag) {
        boolean nextState = !hasGroup(tag);
        saveGroupConfig(tag, nextState);
        reload();
    }

    private static void saveGroupConfig(Identifier tag, boolean enabled) {
        Path file = getGroupPath(tag);
        try {
            Files.createDirectories(file.getParent());
            JsonObject json = new JsonObject();
            if (enabled) {
                json.addProperty("type", "rrv:tag");
                json.addProperty("id", tag.toString());
                json.addProperty("tag", tag.toString());
                json.addProperty("enabled", true);
            } else {
                json.addProperty("id", tag.toString());
                json.addProperty("enabled", false);
            }
            try (var writer = Files.newBufferedWriter(file)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
            }
        } catch (Exception ignored) {
        }
    }

    public static void reload() {
        stackGroups.clear();
        itemToGroupCache.clear();

        if (!Configs.STACK_GROUPS.areStackGroupsEnabled()) return;

        stackGroups.add(new PressurePlateItemGroup());
        stackGroups.add(new MinecartItemGroup());
        stackGroups.add(new InfestedBlockItemGroup());
        stackGroups.add(new CopperBlockItemGroup());
        stackGroups.add(new CoralItemGroup());

        Map<Identifier, StackGroup> loaded = new LinkedHashMap<>();

        try {
            var resourceManager = Minecraft.getInstance().getResourceManager();
            var resources = resourceManager.listResources("rrv/stack_groups", loc -> loc.getPath().endsWith(".json"));
            for (var entry : resources.entrySet()) {
                var location = entry.getKey();
                var resource = entry.getValue();
                String namespace = location.getNamespace();
                String path = location.getPath().substring("rrv/stack_groups/".length());
                path = path.substring(0, path.length() - ".json".length());
                Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
                try (var reader = resource.openAsReader()) {
                    loadGroup(id, JsonParser.parseReader(reader).getAsJsonObject(), loaded);
                }
            }
        } catch (Exception ignored) {
        }

        Path configDir = ReliableRecipeViewer.CONFIG_PATH.resolve("stack_groups");
        if (Files.exists(configDir)) {
            try (var stream = Files.walk(configDir)) {
                stream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".json")).forEach(path -> {
                    try (var reader = Files.newBufferedReader(path)) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        String idString;
						if (json.has("id")) idString = json.get("id").getAsString();
                        else if (json.has("tag")) idString = json.get("tag").getAsString();
                        else idString = json.has("component") ? json.get("component").getAsString() : null;
                        if (idString != null) {
                            loadGroup(Identifier.parse(idString), json, loaded);
                        }
                    } catch (Exception ignored) {
                    }
                });
            } catch (Exception ignored) {
            }
        }

        for (StackGroup stackGroup : loaded.values()) {
            if (stackGroups.stream().noneMatch(existing -> existing.getId().equals(stackGroup.getId()))) {
                stackGroups.add(stackGroup);
            }
        }

        for (StackGroup stackGroup : stackGroups) {
            stackGroup.isEnabled = Configs.STACK_GROUPS.getOrDefault(stackGroup.getId()).enabled();
            stackGroup.priority = Configs.STACK_GROUPS.getOrDefault(stackGroup.getId()).priority();
        }

        stackGroups.sort(Comparator.<StackGroup>comparingInt(g -> -g.priority).thenComparing(stackGroup -> stackGroup.getId().toString()));
    }

    private static void loadGroup(Identifier id, JsonObject json, Map<Identifier, StackGroup> loaded) {
        try {
            boolean enabled = GsonHelper.getAsBoolean(json, "enabled", true);
            if (!enabled) {
                loaded.remove(id);
                return;
            }

            String type = GsonHelper.getAsString(json, "type", "rrv:group");
            BiFunction<Identifier, JsonObject, StackGroup> factory = typeRegistry.get(type);

            if (factory != null) {
                StackGroup group = factory.apply(id, json);
                if (group != null) {
                    loaded.put(id, group);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static StackGroup getGroupForItem(ItemStack stack) {
        if (stack.isEmpty()) return null;
        Item item = stack.getItem();
        if (itemToGroupCache.containsKey(item)) {
            return itemToGroupCache.get(item);
        }

        for (StackGroup group : stackGroups) {
            if (group.match(stack)) {
                itemToGroupCache.put(item, group);
                return group;
            }
        }

        itemToGroupCache.put(item, null);
        return null;
    }

    public static boolean isExpanded(Identifier groupId) {
        return Configs.STACK_GROUPS.getOrDefault(groupId).expanded();
    }

    public static void toggleGroup(Identifier groupId) {
        Configs.STACK_GROUPS.set(groupId, Configs.STACK_GROUPS.getOrDefault(groupId).toggle());
        Configs.STACK_GROUPS.save();
    }

    public static List<ItemStack> applyGrouping(List<ItemStack> source) {
        if (!Configs.STACK_GROUPS.areStackGroupsEnabled() || source == null || source.isEmpty()) {
            return source;
        }

        // First pass: count matches
        Map<StackGroup, List<ItemStack>> groupMatches = new IdentityHashMap<>();
        for (ItemStack stack : source) {
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CompoundTag data = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                if (data.contains("rrv_stack_group_id")) continue;
            }

            StackGroup group = getGroupForItem(stack);
            if (group != null && group.isEnabled) {
                groupMatches.computeIfAbsent(group, _ -> new ArrayList<>()).add(stack);
            }
        }

        // Second pass: sort matches according to configuration
        for (Map.Entry<StackGroup, List<ItemStack>> entry : groupMatches.entrySet()) {
            Identifier groupId = entry.getKey().getId();
            List<String> savedOrder = Configs.STACK_GROUPS.getOrDefault(groupId).order();
            if (savedOrder != null && !savedOrder.isEmpty()) {
                entry.getValue().sort((a, b) -> {
                    String idA = BuiltInRegistries.ITEM.getKey(a.getItem()).toString();
                    String idB = BuiltInRegistries.ITEM.getKey(b.getItem()).toString();
                    int idxA = savedOrder.indexOf(idA);
                    int idxB = savedOrder.indexOf(idB);
                    if (idxA == -1 && idxB == -1) return 0;
                    if (idxA == -1) return 1;
                    if (idxB == -1) return -1;
                    return Integer.compare(idxA, idxB);
                });
            }
        }

        // Third pass: build displayed list
        List<ItemStack> result = new ArrayList<>();
        Set<StackGroup> addedGroups = Collections.newSetFromMap(new IdentityHashMap<>());

        for (ItemStack stack : source) {
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CompoundTag data = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                if (data.contains("rrv_stack_group_id")) continue;
            }

            StackGroup group = getGroupForItem(stack);
            if (group != null && group.isEnabled) {
                List<ItemStack> matches = groupMatches.get(group);
                if (matches != null && matches.size() >= 2) {
                    if (addedGroups.add(group)) {
                        ItemStack repStack = createGroupRepresentativeStack(group, matches);
                        result.add(repStack);
                        if (isExpanded(group.getId())) {
                            result.addAll(matches);
                        }
                    }
                    continue;
                }
            }
            result.add(stack);
        }
        return result;
    }

    private static ItemStack createGroupRepresentativeStack(StackGroup group, List<ItemStack> matches) {
        ItemStack stack = matches.getFirst().copy();
        stack.setCount(1);
        CompoundTag tag = new CompoundTag();
        tag.putString("rrv_stack_group_id", group.getId().toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    public static List<ItemStack> getGroupItems(String groupId) {
        for (StackGroup group : stackGroups) {
            if (group.getId().toString().equals(groupId)) {
                List<ItemStack> items = new ArrayList<>();
                BuiltInRegistries.ITEM.forEach(item -> {
                    ItemStack stack = new ItemStack(item);
                    ClientRecipeCache.INSTANCE.getStackSensitives(item).stream().map(ItemView.StackSensitive::stack).forEach(e -> {
                        if (group.match(e))
                            items.add(e);
                    });
                    if (group.match(stack)) {
                        items.add(stack);
                    }
                });

                List<String> savedOrder = Configs.STACK_GROUPS.getOrDefault(Identifier.parse(groupId)).order();
                if (savedOrder != null && !savedOrder.isEmpty()) {
                    items.sort((a, b) -> {
                        String idA = BuiltInRegistries.ITEM.getKey(a.getItem()).toString();
                        String idB = BuiltInRegistries.ITEM.getKey(b.getItem()).toString();
                        int idxA = savedOrder.indexOf(idA);
                        int idxB = savedOrder.indexOf(idB);
                        if (idxA == -1 && idxB == -1) return 0;
                        if (idxA == -1) return 1;
                        if (idxB == -1) return -1;
                        return Integer.compare(idxA, idxB);
                    });
                }
                return items;
            }
        }
        return Collections.emptyList();
    }

    public static List<ItemStack> appendMatchingGroups(String query, List<ItemStack> results) {
        if (!Configs.STACK_GROUPS.areStackGroupsEnabled()) return results;

        String lower = query.toLowerCase(Locale.ROOT);
        List<ItemStack> extendedResults = new ArrayList<>(results);

        for (StackGroup group : stackGroups) {
            boolean match = false;
            if (group.getId().toString().toLowerCase(Locale.ROOT).contains(lower)) {
                match = true;
            } else {
                Component groupName = group.getName();
                if (groupName != null && groupName.getString().toLowerCase(Locale.ROOT).contains(lower)) {
                    match = true;
                }
            }

            if (match) {
                List<ItemStack> groupContents = getGroupItems(group.getId().toString());
                for (ItemStack stack : groupContents) {
                    if (extendedResults.stream().noneMatch(existing -> ItemStack.isSameItemSameComponents(existing, stack))) {
                        extendedResults.add(stack);
                    }
                }
            }
        }
        return extendedResults;
    }
}
