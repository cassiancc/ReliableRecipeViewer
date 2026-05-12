package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.gui.ClientConfigScreen;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.PolymerHelpers;
import cc.cassian.rrv.client.recipe.ClientRecipeCache;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ResourceRecipeManager;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import com.google.common.collect.HashMultimap;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ItemFilters {

    private static final List<Item> EXCLUDED_ITEMS = List.of(Items.POTION, Items.TIPPED_ARROW, Items.ENCHANTED_BOOK);
    public static final HashMultimap<Item, String> ALIASES = HashMultimap.create();

    /// Filters just by the items display name and tooltip
    /// @param query The query
    /// @return A list of matching item stacks
    protected static List<ItemStack> defaultFilter(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();
        List<ItemStack> thirdPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemName = stack.getDisplayName().getString().toLowerCase();
            Set<String> aliases = ALIASES.get(stack.getItem());

            if (itemName.startsWith(query.toLowerCase()))
                firstPrio.add(stack);
            else if (itemName.contains(query.toLowerCase()))
                secondPrio.add(stack);
            else if (stack.has(DataComponents.STORED_ENCHANTMENTS)) {

                int compCheck = ItemFilters.getTooltipMatch(stack, query);
                if (compCheck == 1)
                    secondPrio.add(stack);
                if (compCheck == 2)
                    thirdPrio.add(stack);
            } else if (!aliases.isEmpty()) {
                aliases.forEach(alias -> {
                    if (alias.toLowerCase().contains(query.toLowerCase())) {
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
    protected static List<ItemStack> modNamespace(String query) {

        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String modNamespace = ReliableRecipeViewerClient.resolver().getModNamespaceForItem(stack);
            if (modNamespace == null)
                continue;

            modNamespace = modNamespace.toLowerCase();

            if (modNamespace.startsWith(query.toLowerCase()))
                add(firstPrio, stack);
            else if (modNamespace.contains(query.toLowerCase()))
                add(secondPrio, stack);

        }

		List<ItemStack> results = new ArrayList<>(firstPrio);
        secondPrio.stream().filter(results::contains).forEach(results::add);

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
    protected static boolean modNamespace(ItemStack stack, String query) {
        String modNamespace = ReliableRecipeViewerClient.resolver().getModNamespaceForItem(stack);
        if (modNamespace == null)
            return false;

        modNamespace = modNamespace.toLowerCase();

        return modNamespace.startsWith(query.toLowerCase()) || modNamespace.contains(query.toLowerCase());
    }

    /// Filters by Identifier (item id)
    /// @param query The query
    /// @return A list of matching item stacks
    protected static List<ItemStack> id(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase();

            if (itemId.startsWith(query.toLowerCase()))
                add(firstPrio, stack);
            else if (itemId.contains(query.toLowerCase()))
                add(secondPrio, stack);
        }

        List<ItemStack> results = new ArrayList<>(firstPrio);
        secondPrio.stream().filter(results::contains).forEach(results::add);
        return results;
    }

    /// Filters by [Identifier] (item id)
    /// @param stack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the item id
    protected static boolean id(ItemStack stack, String query) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase();
        return itemId.startsWith(query.toLowerCase()) || itemId.contains(query.toLowerCase());
    }

    /// Filters by an item's tags
    /// @param query The query
    /// @return A list of matching item stacks
    protected static List<ItemStack> tag(String query) {
        List<ItemStack> results = new ArrayList<>();

        for (ItemStack itemStack : fullStackList()) {
            if (itemStack.tags().anyMatch(tag->tag.location().toString().toLowerCase().contains(query.toLowerCase()))) {
                results.add(itemStack);
            }
        }

        return results;
    }

    /// Filters by an item's tags
    /// @param itemStack The item stack
    /// @param query The query
    /// @return Whether the item stack matches the items tags
    protected static boolean tag(ItemStack itemStack, String query) {
        AtomicBoolean result = new AtomicBoolean(false);

        if (itemStack.tags().anyMatch(tag->tag.location().toString().toLowerCase().contains(query.toLowerCase()))) {
            result.set(true);
        }

        return result.get();
    }

    /// Returns the matching level of the item stack's tooltip with the query
    ///
    /// @param stack The itemstack
    /// @param query The query
    /// @return 0 means no match; 1 means first priority; 2 means second priority
    ///
    /// Used for correct listing of item stacks by match accuracy
    private static int getTooltipMatch(ItemStack stack, String query) {

        List<Component> lore = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);

        for (Component line : lore) {

            if (line.getContents() instanceof TranslatableContents translatableContents && RrvUtil.get(translatableContents.getKey()).toLowerCase().startsWith(query.toLowerCase()))
                return 1;

            if (line.getContents() instanceof TranslatableContents translatableContents && RrvUtil.get(translatableContents.getKey()).toLowerCase().contains(query.toLowerCase()))
                return 2;
        }

        return 0;
    }

    /// @return A list of all items that can be displayed in the ViewOverlay
    ///
    /// **Also includes all stack-sensitives**
    private static List<ItemStack> fullStackList() {
        List<ItemStack> results = new ArrayList<>();

        BuiltInRegistries.ITEM.forEach(item -> {
            if (!EXCLUDED_ITEMS.contains(item))
                results.add(new ItemStack(item));
            results.addAll(ClientRecipeCache.INSTANCE.getStackSensitives(item).stream().map(ItemView.StackSensitive::stack).toList());
        });

        if (ModCompat.POLYMER)
            PolymerHelpers.polymerFilter(results);

        ResourceRecipeManager.replaceIndex(results);

        return results;
    }

    public static void exportFullStackList(Button button) {
        try (var output = Files.newOutputStream(Platform.INSTANCE.getDataDirectory().resolve("rrv_index.json")); var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
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
            Util.getPlatform().openPath(Platform.INSTANCE.getDataDirectory());
        } catch (Exception e) {
            button.setMessage(ClientConfigScreen.clientSetting("export_item_view.failed"));
            ReliableRecipeViewer.LOGGER.error("Unable to export full stack list!", e);
        }
    }
}
