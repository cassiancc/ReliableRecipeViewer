package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.IndexSource;
import cc.cassian.rrv.common.gui.ClientConfigScreen;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.PolymerHelpers;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.unlocking.ServerUnlockManager;
import cc.cassian.rrv.client.recipe.ResourceRecipeManager;
//? fabric {
import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
//?}
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.common.collect.HashMultimap;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.*;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ItemFilters {

    public static final HashMultimap<Item, String> ALIASES = HashMultimap.create();

    public static List<ItemStack> filter(String newQuery) {
        String query = RrvUtil.lowercaseSubstring(newQuery);
        for (PrefixedFilter value : PrefixedFilter.values()) {
            if (newQuery.startsWith(value.prefix())) {
                return value.filter.apply(query);
            }
        }
        return ItemFilters.defaultFilter(newQuery);
    }

    public static boolean advancedFilter(List<ItemStack> availableItems, String newQuery) {
        boolean filtered = false;
        String query = RrvUtil.lowercaseSubstring(newQuery);
        for (PrefixedFilter value : PrefixedFilter.values()) {
            if (newQuery.startsWith(value.prefix())) {
                availableItems.removeIf(stack-> !value.advancedFilter.apply(stack, query));
                filtered = true;
            }
        }
        return filtered;
    }

    /// Filters just by the items display name and tooltip
    /// @param query The query
    /// @return A list of matching item stacks
    public static List<ItemStack> defaultFilter(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();
        List<ItemStack> thirdPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemName = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
            Set<String> aliases = ALIASES.get(stack.getItem());

            String lowerCaseQuery = query.toLowerCase(Locale.ROOT);
            if (itemName.startsWith(lowerCaseQuery))
                firstPrio.add(stack);
            else if (itemName.contains(lowerCaseQuery))
                secondPrio.add(stack);
            else if (stack.has(DataComponents.STORED_ENCHANTMENTS)) {
                int compCheck = ItemFilters.getTooltipMatch(stack, query);
                if (compCheck == 1)
                    secondPrio.add(stack);
                if (compCheck == 2)
                    thirdPrio.add(stack);
            } else if (!aliases.isEmpty()) {
                aliases.forEach(alias -> {
                    if (alias.toLowerCase(Locale.ROOT).contains(lowerCaseQuery)) {
                        if (!secondPrio.contains(stack))
                            secondPrio.add(stack);
                    }
                });
            }
        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);
        results.addAll(thirdPrio);
        return results;
    }

    /// Filters by mod namespace
    /// @param query The query
    /// @return A list of matching item stacks
    public static List<ItemStack> modNamespace(String query) {

        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String modNamespace = RRVPlatform.INSTANCE.getModNamespaceForItem(stack);
            if (modNamespace == null)
                continue;

            modNamespace = modNamespace.toLowerCase(Locale.ROOT);

            if (modNamespace.startsWith(query))
                add(firstPrio, stack);
            else if (modNamespace.contains(query))
                add(secondPrio, stack);

        }

		List<ItemStack> results = new ArrayList<>(firstPrio);
        secondPrio.stream().filter(o -> !results.contains(o)).forEach(results::add);

        return results;
    }

    private static void add(List<ItemStack> results, ItemStack stack) {
        if (!results.contains(stack)) {
            results.add(stack);
        }
    }

    /// Filters by mod name
    /// @param stack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the mod name
    public static boolean modNamespace(ItemStack stack, String query) {
        String modNamespace = RRVPlatform.INSTANCE.getModNamespaceForItem(stack);
        if (modNamespace == null)
            return false;

        modNamespace = modNamespace.toLowerCase(Locale.ROOT);

        return modNamespace.startsWith(query) || modNamespace.contains(query);
    }

    /// Filters by Identifier (item id)
    /// @param query The query
    /// @return A list of matching item stacks
    public static List<ItemStack> id(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase(Locale.ROOT);

            if (itemId.startsWith(query))
                add(firstPrio, stack);
            else if (itemId.contains(query))
                add(secondPrio, stack);
        }

        List<ItemStack> results = new ArrayList<>(firstPrio);
        secondPrio.stream().filter(o -> !results.contains(o)).forEach(results::add);
        return results;
    }

    /// Filters by [Identifier] (item id)
    /// @param stack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the item id
    public static boolean id(ItemStack stack, String query) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase(Locale.ROOT).contains(query);
    }

    /// Filters by [CreativeModeTab] (creative tab)
    /// @param query The query
    /// @return A list of item stacks from all creative groups with matching names.
    public static List<ItemStack> creativeTab(String query) {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (CreativeModeTab tab : CreativeModeTabs.tabs()) {
            if (tab.getType() != CreativeModeTab.Type.SEARCH && tab.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(query)) {
                stacks.addAll(tab.getDisplayItems());
            }
        }
        return stacks;
    }

    /// Filters by [CreativeModeTab] (creative tab)
    /// @param stack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the item id
    public static boolean creativeTab(ItemStack stack, String query) {
        for (CreativeModeTab tab : CreativeModeTabs.tabs()) {
            if (tab.getType() != CreativeModeTab.Type.SEARCH && tab.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(query) && tab.contains(stack)) {
                return true;
            }
        }
        return false;
    }

    /// Filters by an item's tags
    /// @param query The query
    /// @return A list of matching item stacks
    public static List<ItemStack> tag(String query) {
        List<ItemStack> results = new ArrayList<>();

        for (ItemStack itemStack : fullStackList()) {
            if (itemStack.tags().anyMatch(tag->tag.location().toString().toLowerCase(Locale.ROOT).contains(query))) {
                results.add(itemStack);
            }
        }

        return results;
    }

    /// Filters by an item's tags
    /// @param itemStack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the items tags
    public static boolean tag(ItemStack itemStack, String query) {
        AtomicBoolean result = new AtomicBoolean(false);

        if (itemStack.tags().anyMatch(tag->tag.location().toString().toLowerCase(Locale.ROOT).contains(query))) {
            result.set(true);
        }

        return result.get();
    }

    /// Returns the matching level of the item stack's tooltip with the query
    ///
    /// @param stack The item stack
    /// @param query The query
    /// @return 0 means no match; 1 means first priority; 2 means second priority
    ///
    /// Used for correct listing of item stacks by match accuracy
    protected static int getTooltipMatch(ItemStack stack, String query) {

        List<Component> lore = RRVClientUtil.getTooltipFromItem(Minecraft.getInstance(), stack);

        for (Component line : lore) {
            if (line.getContents() instanceof TranslatableContents translatableContents) {
                var key = RrvUtil.get(translatableContents.getKey()).toLowerCase(Locale.ROOT);
                if (key.startsWith(query))
                    return 1;

                if (key.contains(query))
                    return 2;
            }


        }

        return 0;
    }

    public static boolean cached;

    /// @return A list of all items that can be displayed in the ViewOverlay
    ///
    /// **Also includes all stack-sensitives**
    private static List<ItemStack> fullStackList() {
        List<ItemStack> results = new ArrayList<>();

		if (Configs.CLIENT_SETTINGS.getIndexSource() == IndexSource.REGISTRY) {
			BuiltInRegistries.ITEM.forEach(item -> {
				results.add(new ItemStack(item));
				results.addAll(ClientRecipeCache.INSTANCE.streamStackSensitives(item).toList());
			});
		} else {
			Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (!cached && player != null && !player.hasInfiniteMaterials()) {
				CreativeModeTabs.tryRebuildTabContents(FeatureFlags.VANILLA_SET, false, player.registryAccess());
			}

			results.addAll(CreativeModeTabs.searchTab().getSearchTabDisplayItems().stream()
                            //? fabric {
                            .map(RRVClientUtil::applyPolymerCheck)
                    //?}
                    .toList()
            );

			BuiltInRegistries.ITEM.forEach(item -> {
                if (Configs.CLIENT_SETTINGS.getIndexSource().equals(IndexSource.CREATIVE_AND_REGISTRY)) {
                    ItemStack e = new ItemStack(item);
                    if (results.stream().noneMatch(stack -> ItemStack.isSameItemSameComponents(stack, e)))
                        results.add(e);
                }
				results.addAll(ClientRecipeCache.INSTANCE.streamStackSensitives(item).filter(stack-> results.stream().noneMatch(c-> ItemStack.isSameItemSameComponents(stack, c))).toList());
			});
		}


		if (ModCompat.POLYMER)
            PolymerHelpers.polymerFilter(results);

        ResourceRecipeManager.replaceIndex(results);

        return results;
    }

    public static void exportFullStackList(Button button) {
        try (var output = Files.newOutputStream(RRVPlatform.INSTANCE.getDataDirectory().resolve("rrv_index.json")); var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            JsonObject index = new JsonObject();
            JsonArray encodedStacks = new JsonArray();
            JsonObject encodedAliases = new JsonObject();
            for (ItemStack itemStack : fullStackList()) {
                if (!itemStack.isEmpty()) {
                    JsonObject result = ItemStack.CODEC.encodeStart(ClientRecipeManager.INSTANCE.createSerializationContext(JsonOps.INSTANCE), itemStack).getOrThrow().getAsJsonObject();
                    // dump aliases
                    Set<String> aliasSet = ItemFilters.ALIASES.get(itemStack.getItem());
                    if (!aliasSet.isEmpty()) {
                        JsonArray aliases = new JsonArray();
                        aliasSet.stream().map(JsonPrimitive::new).forEach(aliases::add);
                        encodedAliases.add(result.get("id").getAsString(), aliases);
                    }
                    // add to encodedStacks
                    result.remove("count");
                    if (result.has("components")) {
                        encodedStacks.add(result);
                    } else {
                        encodedStacks.add(result.get("id"));
                    }
                }
            }
            index.addProperty("replace", true);
            index.add("values", encodedStacks);
            index.add("aliases", encodedAliases);
            ReliableRecipeViewer.GSON.toJson(index, writer);
            button.setMessage(ClientConfigScreen.clientSetting("export_item_view.success"));
            Util.getPlatform().openPath(RRVPlatform.INSTANCE.getDataDirectory());
        } catch (Exception e) {
            button.setMessage(ClientConfigScreen.clientSetting("export_item_view.failed"));
            ReliableRecipeViewer.LOGGER.error("Unable to export full stack list!", e);
        }
    }
}
