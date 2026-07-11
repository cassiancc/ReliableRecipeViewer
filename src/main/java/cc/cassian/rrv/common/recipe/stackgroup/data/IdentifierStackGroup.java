package cc.cassian.rrv.common.recipe.stackgroup.data;

import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class IdentifierStackGroup extends AbstractStackGroup {
    private final Set<Identifier> targetItems = new HashSet<>();
    private final Set<TagKey<Item>> targetTags = new HashSet<>();
    private final Set<DataComponentType<?>> targetComponents = new HashSet<>();
    private final Set<Identifier> excludedItems = new HashSet<>();
    private final List<Pattern> regexes = new ArrayList<>();

    public IdentifierStackGroup(Identifier id, Set<Identifier> targetItems, Set<TagKey<Item>> targetTags, Set<DataComponentType<?>> targetComponents, Set<Identifier> excludedItems, List<Pattern> regexes, Component name) {
        super(id, name);
        if (targetItems != null) this.targetItems.addAll(targetItems);
        if (targetTags != null) this.targetTags.addAll(targetTags);
        if (targetComponents != null) this.targetComponents.addAll(targetComponents);
        if (excludedItems != null) this.excludedItems.addAll(excludedItems);
        if (regexes != null) this.regexes.addAll(regexes);
    }

    public static IdentifierStackGroup parse(JsonElement json, Identifier filenameId) {
        try {
            if (!(json instanceof JsonObject obj)) throw new IllegalArgumentException(filenameId + " Not a JSON object");

            Identifier finalId = obj.has("id")
                    ? Identifier.parse(GsonHelper.getAsString(obj, "id"))
                    : filenameId;

            String nameKey = obj.has("name") ? GsonHelper.getAsString(obj, "name") : null;
            Component customName = nameKey != null ? Component.translatable(nameKey) : null;

            int priority = obj.has("priority") ? GsonHelper.getAsInt(obj, "priority", 0) : 0;

            Set<Identifier> targetItems = new HashSet<>();
            Set<TagKey<Item>> targetTags = new HashSet<>();
            Set<DataComponentType<?>> targetComponents = new HashSet<>();

            if (obj.has("tag")) {
                String tagName = GsonHelper.getAsString(obj, "tag");
                String registryName = GsonHelper.getAsString(obj, "registry", "minecraft:item");
                TagKey<Item> tagKey = TagKey.create(
                        ResourceKey.createRegistryKey(Identifier.parse(registryName)),
                        Identifier.parse(tagName)
                );
                targetTags.add(tagKey);
            }

            if (obj.has("component")) {
                String tagName = GsonHelper.getAsString(obj, "component");
                BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.tryParse(tagName)).ifPresentOrElse(targetComponents::add, ()-> {
                    throw new IllegalArgumentException("%s references data component %s which does not exist in the registry!".formatted(filenameId, tagName));
                });
            }

            if (GsonHelper.isArrayNode(obj, "contents")) {
                for (JsonElement e : obj.getAsJsonArray("contents")) {
                    parseTarget(e, targetItems, targetTags);
                }
            }

            List<Pattern> regexes = new ArrayList<>();
            if (obj.has("regex")) {
                regexes.add(Pattern.compile(GsonHelper.getAsString(obj, "regex")));
            }
            if (GsonHelper.isArrayNode(obj, "regexes")) {
                for (JsonElement e : obj.getAsJsonArray("regexes")) {
                    regexes.add(Pattern.compile(e.getAsString()));
                }
            }

            Set<Identifier> excluded = new HashSet<>();
            if (GsonHelper.isArrayNode(obj, "exclusions")) {
                for (JsonElement e : obj.getAsJsonArray("exclusions")) {
                    Set<Identifier> exItems = new HashSet<>();
                    Set<TagKey<Item>> exTags = new HashSet<>();
                    parseTarget(e, exItems, exTags);
                    excluded.addAll(exItems);
                    for (TagKey<Item> tag : exTags) {
                        BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(holder -> excluded.add(BuiltInRegistries.ITEM.getKey(holder.value())));
                    }
                }
            }

            IdentifierStackGroup group = new IdentifierStackGroup(finalId, targetItems, targetTags, targetComponents, excluded, regexes, customName);
            group.priority = priority;
            return group;
        } catch (Exception e) {
            return null;
        }
    }

    private static void parseTarget(JsonElement element, Set<Identifier> items, Set<TagKey<Item>> tags) {
        if (element.isJsonPrimitive()) {
            String str = element.getAsString();
            if (str.startsWith("#")) {
                tags.add(TagKey.create(Registries.ITEM, Identifier.parse(str.substring(1))));
            } else {
                items.add(Identifier.parse(str));
            }
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            String type = GsonHelper.getAsString(obj, "type", "item");
            String idStr = GsonHelper.getAsString(obj, "id");
            if ("tag".equals(type) || idStr.startsWith("#")) {
                String cleanId = idStr.startsWith("#") ? idStr.substring(1) : idStr;
                tags.add(TagKey.create(Registries.ITEM, Identifier.parse(cleanId)));
            } else {
                items.add(Identifier.parse(idStr));
            }
        }
    }

    @Override
    public Set<Identifier> getOptimizedIds() {
        if (!regexes.isEmpty() || !targetTags.isEmpty() || !targetComponents.isEmpty()) {
            return null;
        }
        return targetItems;
    }

    @Override
    public Component getName() {
        if (name != null) return name;
        String path = getId().getPath().replace("/", ".");
        String key = "stackgroup.rrv." + path;
        if (Language.getInstance().has(key)) {
            return Component.translatable(key);
        }
        String fallbackKey = "stackgroup.emixx." + path;
        if (Language.getInstance().has(fallbackKey)) {
            return Component.translatable(fallbackKey);
        }
        if (!targetTags.isEmpty()) {
            TagKey<Item> tag = targetTags.iterator().next();
            String tagKey = RrvUtil.getTranslationKey(tag);
            if (Language.getInstance().has(tagKey)) {
                return Component.translatable(tagKey);
            }
        }
        return super.getName();
    }

    @Override
    public boolean match(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);

        if (excludedItems.contains(itemId)) return false;

        String idStr = itemId.toString();
        for (Pattern pattern : regexes) {
            if (pattern.matcher(idStr).matches()) return true;
        }

        if (targetItems.contains(itemId)) return true;

        for (TagKey<Item> tag : targetTags) {
            if (stack.is(tag)) return true;
        }

        for (DataComponentType<?> tag : targetComponents) {
            if (stack.has(tag)) return true;
        }

        return false;
    }
}
