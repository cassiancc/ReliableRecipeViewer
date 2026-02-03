package cc.cassian.rrv.common.overlay.itemlist.view;

import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.tag.TagClientRecipe;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.PolymerHelpers;
import cc.cassian.rrv.common.recipe.ClientRecipeCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ItemFilters {

    /**
     * Filters just by the items display name and tooltip
     * @param query The query
     * @return A list of matching item stacks
     */
    protected static List<ItemStack> defaultFilter(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();
        List<ItemStack> thirdPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemName = stack.getDisplayName().getString().toLowerCase();

            if (itemName.startsWith(query.toLowerCase()))
                firstPrio.add(stack);
            else if (itemName.contains(query.toLowerCase()))
                secondPrio.add(stack);
            else if (stack.is(Items.ENCHANTED_BOOK)) {

                int compCheck = ItemFilters.getTooltipMatch(stack, query);
                if (compCheck == 1)
                    secondPrio.add(stack);
                if (compCheck == 2)
                    thirdPrio.add(stack);
            }

        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);
        results.addAll(thirdPrio);
        return results;
    }

    /**
     * Filters by mod name
     * @param query The query
     * @return A list of matching item stacks
     */
    protected static List<ItemStack> modName(String query) {

        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String modName = ReliableRecipeViewerClient.resolver().getModNameForItem(stack);
            if (modName == null)
                continue;

            modName = modName.toLowerCase();

            if (modName.startsWith(query.toLowerCase()))
                firstPrio.add(stack);
            else if (modName.contains(query.toLowerCase()))
                secondPrio.add(stack);

        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);
        return results;
    }

    /**
     * Filters by mod name
     * @param stack The item stack
     * @param query The query
     * @return Whether the item stack matches the mod name
     */
    protected static boolean modName(ItemStack stack, String query) {
        String modName = ReliableRecipeViewerClient.resolver().getModNameForItem(stack);
        if (modName == null)
            return false;

        modName = modName.toLowerCase();

        return modName.startsWith(query.toLowerCase()) || modName.contains(query.toLowerCase());
    }

    /**
     * Filters by Identifier (item id)
     * @param query The query
     * @return A list of matching item stacks
     */
    protected static List<ItemStack> id(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (ItemStack stack : fullStackList()) {

            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase();

            if (itemId.startsWith(query.toLowerCase()))
                firstPrio.add(stack);
            else if (itemId.contains(query.toLowerCase()))
                secondPrio.add(stack);
        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);
        return results;
    }

    /**
     * Filters by Identifier (item id)
     * @param stack The item stack
     * @param query The query
     * @return Whether the item stack matches the item id
     */
    protected static boolean id(ItemStack stack, String query) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().toLowerCase();
        return itemId.startsWith(query.toLowerCase()) || itemId.contains(query.toLowerCase());
    }

    /**
     * Filters by an items tags
     * @param query The query
     * @return A list of matching item stacks
     */
    protected static List<ItemStack> tag(String query) {
        List<ItemStack> firstPrio = new ArrayList<>();
        List<ItemStack> secondPrio = new ArrayList<>();

        for (TagKey<Item> tag : BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).toList()) {
            String tagName = tag.location().getPath().toLowerCase();

            if (tagName.startsWith(query.toLowerCase())) {
                BuiltInRegistries.ITEM.get(tag).ifPresent(items -> items.stream().map(itemHolder -> new ItemStack(itemHolder.value())).filter(item -> !firstPrio.contains(item)).forEach(stack -> {
                    firstPrio.add(stack);
                    firstPrio.addAll(ClientRecipeCache.INSTANCE.getStackSensitives(stack.getItem()).stream().map(ItemView.StackSensitive::stack).toList());
                }));

            } else if (tagName.contains(query.toLowerCase())) {
                BuiltInRegistries.ITEM.get(tag).ifPresent(items -> items.stream().map(itemHolder -> new ItemStack(itemHolder.value())).filter(item -> !firstPrio.contains(item) && !secondPrio.contains(item)).forEach(stack -> {
                    secondPrio.add(stack);
                    secondPrio.addAll(ClientRecipeCache.INSTANCE.getStackSensitives(stack.getItem()).stream().map(ItemView.StackSensitive::stack).toList());
                }));
            }

        }

        List<ItemStack> results = new ArrayList<>();
        results.addAll(firstPrio);
        results.addAll(secondPrio);

        return results;
    }

    /**
     * Filters by an items tags
     * @param stack The item stack
     * @param query The query
     * @return Whether the item stack matches the items tags
     */
    protected static boolean tag(ItemStack stack, String query) {
        AtomicBoolean result = new AtomicBoolean(false);

        for (TagKey<Item> tag : BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).toList()) {
            String tagName = tag.location().getPath().toLowerCase();

            if (tagName.contains(query.toLowerCase())) {
                BuiltInRegistries.ITEM.get(tag).ifPresent(items -> items.stream().map(itemHolder -> new ItemStack(itemHolder.value())).forEach(stack2 -> {
                    if (ItemStack.isSameItem(stack2, stack)) {
                        result.set(true);
                    }
                }));
            }

        }
        return result.get();
    }

    /**
     * Returns the matching level of the itemstacks tooltip with the query
     *
     * @param stack The itemstack
     * @param query The query
     * @return 0 means no match; 1 means first prio; 2 means second prio
     * <br>
     * <br>
     * Used for correct listing of itemstacks by match accuracy
     */
    private static int getTooltipMatch(ItemStack stack, String query) {

        List<Component> lore = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);

        for (Component line : lore) {

            if (line.getContents() instanceof TranslatableContents translatableContents && I18n.get(translatableContents.getKey()).toLowerCase().startsWith(query.toLowerCase()))
                return 1;

            if (line.getContents() instanceof TranslatableContents translatableContents && I18n.get(translatableContents.getKey()).toLowerCase().contains(query.toLowerCase()))
                return 2;
        }

        return 0;
    }

    /**
     * @return A list of all items that can be displayed in the ViewOverlay
     * <br>
     * <br>
     * <b>Also includes all stack-sensitives</b>
     */
    private static List<ItemStack> fullStackList() {
        List<ItemStack> results = new ArrayList<>();

        if (Configs.CLIENT_SETTINGS.isCreativeIndexSource()) {
			results.addAll(CreativeModeTabs.searchTab().getDisplayItems());
            BuiltInRegistries.ITEM.forEach(item -> {
                results.addAll(ClientRecipeCache.INSTANCE.getStackSensitives(item).stream().map(ItemView.StackSensitive::stack).toList());
            });
            BuiltInRegistries.FLUID.forEach(fluid -> {
                results.add(new FluidStack(fluid).createItemStack());
            });
		} else {
            BuiltInRegistries.ITEM.forEach(item -> {
                results.add(new ItemStack(item));
                results.addAll(ClientRecipeCache.INSTANCE.getStackSensitives(item).stream().map(ItemView.StackSensitive::stack).toList());
            });
        }

        if (ModCompat.POLYDEX)
            PolymerHelpers.polymerFilter(results);

        return results;
    }

}
