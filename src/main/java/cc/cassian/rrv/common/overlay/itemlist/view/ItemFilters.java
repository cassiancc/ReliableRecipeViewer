package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.client.builtin.BuiltInReliableRecipeViewerClientIntegration;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.config.options.IndexSource;
import cc.cassian.rrv.common.gui.ClientConfigScreen;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.jei.JeiCompatibilityUtil;
import cc.cassian.rrv.common.integration.polymer.PolymerHelpers;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.client.recipe.ResourceRecipeManager;
import cc.cassian.rrv.common.mixin.world.item.CreativeModeTabsAccessor;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.common.collect.HashMultimap;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.ApiStatus;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class ItemFilters {

    public static final HashMultimap<Item, String> ALIASES = HashMultimap.create();
    /// A list of [ItemStack]s that can be shown in the item view. Cleared when the player disconnects from a world, reloads resource packs, or changes the index source. Rebuilt by calling [ItemFilters#fullStackList()].
    private static final List<ItemStack> CACHED_STACKS = new ArrayList<>();
    /// A list of [ItemStack]s from the Unique Recipe Output index soruce.
    private static final List<ItemStack> CACHED_RECIPE_OUTPUTS = new ArrayList<>();

    @ApiStatus.Internal
    public static Runnable RESET_OVERLAY = () -> {};

    /// Standard filtering for single-word searches.
    public static List<ItemStack> filter(String newQuery) {
        String query = RrvUtil.lowercaseSubstring(newQuery);
        for (PrefixedFilter value : PrefixedFilter.values()) {
            if (newQuery.startsWith(value.prefix())) {
                return value.filter().apply(query);
            }
        }
        return ItemFilters.defaultFilter(newQuery);
    }

    /// Advanced filtering for multi-word searches.
    public static boolean advancedFilter(List<ItemStack> availableItems, String newQuery) {
        boolean filtered = false;
        String query = RrvUtil.lowercaseSubstring(newQuery);
        for (PrefixedFilter value : PrefixedFilter.values()) {
            if (newQuery.startsWith(value.prefix())) {
                availableItems.removeIf(stack-> !value.advancedFilter().apply(stack, query));
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
            else if (lowerCaseQuery.contains(" ") && Arrays.stream(lowerCaseQuery.split(" ")).allMatch(itemName::contains)) {
                thirdPrio.add(stack);
            }
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
            //~ if >26 'getTags' -> 'tags'
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

        //~ if >26 'getTags' -> 'tags'
        if (itemStack.tags().anyMatch(tag->tag.location().toString().toLowerCase(Locale.ROOT).contains(query))) {
            result.set(true);
        }

        return result.get();
    }


    /// Filters by data component
    /// @param query The query
    /// @return A list of matching item stacks
    public static List<ItemStack> component(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();
        List<ItemStack> thirdPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            for (TypedDataComponent<?> component : stack.getComponents()) {
                String componentId = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.type()).getPath().toString().toLowerCase(Locale.ROOT);

                if (componentId.equals(query))
                    add(firstPrio, stack);
                else if (componentId.startsWith(query))
                    add(secondPrio, stack);
                else if (componentId.contains(query))
                    add(thirdPrio, stack);
            }
        }

        List<ItemStack> results = new ArrayList<>(firstPrio);
        secondPrio.stream().filter(o -> !results.contains(o)).forEach(results::add);
        thirdPrio.stream().filter(o -> !results.contains(o)).forEach(results::add);
        return results;
    }

    /// Filters by an item's tags
    /// @param itemStack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the items tags
    public static boolean component(ItemStack itemStack, String query) {
        AtomicBoolean result = new AtomicBoolean(false);

        if (itemStack.getComponents().stream().anyMatch(tag->BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(tag.type()).toString().toLowerCase(Locale.ROOT).contains(query))) {
            result.set(true);
        }

        return result.get();
    }

    /// Filters by [String] (color)
    /// @param query The query
    /// @return A list of matching color
    public static List<ItemStack> color(String query) {
        if (!ModCompat.JEI) return List.of();
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemId = JeiCompatibilityUtil.getColorName(stack).toLowerCase(Locale.ROOT);

            if (itemId.startsWith(query))
                add(firstPrio, stack);
            else if (itemId.contains(query))
                add(secondPrio, stack);
        }

        List<ItemStack> results = new ArrayList<>(firstPrio);
        secondPrio.stream().filter(o -> !results.contains(o)).forEach(results::add);
        return results;
    }

    /// Filters by [String] (color)
    /// @param stack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the color
    public static boolean color(ItemStack stack, String query) {
        if (!ModCompat.JEI) return true;
        return JeiCompatibilityUtil.getColorName(stack).toLowerCase(Locale.ROOT).contains(query);
    }

    /// Returns the matching level of the item stack's tooltip with the query
    ///
    /// @param stack The item stack
    /// @param query The query
    /// @return 0 means no match; 1 means first priority; 2 means second priority
    ///
    /// Used for correct listing of item stacks by match accuracy
    protected static int getTooltipMatch(ItemStack stack, String query) {

        List<Component> lore = RRVClientUtil.getTooltipFromItem(stack);

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

    /// @return A cached list of all items that can be displayed in the ViewOverlay
    ///
    /// **Also includes all stack-sensitives**
    public static List<ItemStack> fullStackList() {
        if (needsCache()) {
            List<ItemStack> results = new ArrayList<>();

            Player player = RRVClientUtil.player();

            if (Configs.CLIENT_SETTINGS.getIndexSource(IndexSource.CREATIVE)) {
                if (player != null) {
                    FeatureFlagSet enabledFeatures = player.level().enabledFeatures();
                    boolean hasPermissions = RrvUtil.hasPermission(player);
                    RegistryAccess registryAccess = player.registryAccess();
                    boolean creativeTabsCached = CreativeModeTabsAccessor.getCachedParameters() != null || CreativeModeTabsAccessor.getCachedParameters().needsUpdate(enabledFeatures, hasPermissions, registryAccess);
                    if (!creativeTabsCached) {
                        CreativeModeTabs.tryRebuildTabContents(enabledFeatures, hasPermissions, registryAccess);
                    }
                }

                CreativeModeTabs.searchTab().getSearchTabDisplayItems().forEach(c->{
                    //? fabric {
                    results.add(RRVClientUtil.applyPolymerCheck(c.copy()));
                    //?} else {
                    /*results.add(c.copy());
                    *///?}
                });
            }

            if (Configs.CLIENT_SETTINGS.getIndexSource(IndexSource.REGISTRY)) {
                BuiltInRegistries.ITEM.forEach(item -> {
                    ItemStack e = new ItemStack(item);
                    if (results.stream().noneMatch(stack -> ItemStack.isSameItemSameComponents(stack, e)))
                        results.add(e);
                    results.addAll(ClientRecipeCache.INSTANCE.streamStackSensitives(item).filter(stack-> results.stream().noneMatch(c-> ItemStack.isSameItemSameComponents(stack, c))).toList());
                });
            }

            if (Configs.CLIENT_SETTINGS.getIndexSource(IndexSource.UNIQUE_RECIPE_OUTPUT)) {
                if (CACHED_RECIPE_OUTPUTS.isEmpty()) {
                    var list = new ArrayList<ItemStack>();
                    ClientRecipeCache.INSTANCE.getRecipes().stream().sorted(RRVClientUtil::compare).forEach(r-> {
                        addUniqueItem(r.getResults(), list);
                    });
                    RrvUtil.sortByName(list);
                    CACHED_RECIPE_OUTPUTS.addAll(list);
                }

                CACHED_RECIPE_OUTPUTS.forEach(stack -> {
                    if (results.stream().noneMatch(c-> makeDefaultChecks(stack, c))) {
                        results.add(stack);
                    }
                });
            }

            if (ModCompat.POLYMER)
                PolymerHelpers.polymerFilter(results);

            if (Configs.CLIENT_SETTINGS.getIndexSource(IndexSource.RESOURCE_PACKS))
                ResourceRecipeManager.replaceIndex(results);

            CACHED_STACKS.addAll(results);
        }

        return CACHED_STACKS;
    }

    private static void addUniqueItem(List<SlotContent> r, ArrayList<ItemStack> list) {
        r.forEach(l-> {
            l.getValidContents().forEach(stack -> {
                if (list.stream().noneMatch(c-> makeDefaultChecks(stack, c))) {
                    list.add(stack);
                }
            });
        });
    }

    private static boolean makeDefaultChecks(ItemStack stack, ItemStack ingredient) {
        List<BiPredicate<ItemStack, ItemStack>> checks = new ArrayList<>(ItemViewRecipes.CHECKS);
        checks.remove(BuiltInReliableRecipeViewerClientIntegration.TRIM_CHECK);
        for (BiPredicate<ItemStack, ItemStack> check : checks) {
            if (!check.test(stack, ingredient)) {
                return false;
            }
        }
        return true;
    }

    ///  Exports the contents of the index in a format compatible with resource packs.
    ///
    /// @return Text mentioning the result of the export.
    public static Component exportFullStackList() {
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
            Util.getPlatform().openPath(RRVPlatform.INSTANCE.getDataDirectory());
            return ClientConfigScreen.clientSetting("export_item_view.success");
        } catch (Exception e) {
            ReliableRecipeViewer.LOGGER.error("Unable to export full stack list!", e);
            return ClientConfigScreen.clientSetting("export_item_view.failed");
        }
    }

    /// Clear the cached stacks and force a rebuild.
    public static void clearCaches() {
        clearCaches(false);
    }

    /// Clear the cached stacks and force a rebuild.
	public static void clearCaches(boolean clearCachedRecipeOutputs) {
        ReliableRecipeViewer.LOGGER.info("RRV: Rebuilding cached index!");
		CACHED_STACKS.clear();
        if (clearCachedRecipeOutputs) {
            clearCachedRecipeOutputs();
        }
        RESET_OVERLAY.run();
	}

    /// Clear the cached stacks and force a rebuild.
    public static void clearCachedRecipeOutputs() {
        CACHED_RECIPE_OUTPUTS.clear();
    }

    /// Whether the cache needs to be rebuilt.
	public static boolean needsCache() {
        if (CACHED_RECIPE_OUTPUTS.isEmpty() && Configs.CLIENT_SETTINGS.getIndexSource(IndexSource.UNIQUE_RECIPE_OUTPUT)) {
            return true;
        }
		return CACHED_STACKS.isEmpty();
	}
}
