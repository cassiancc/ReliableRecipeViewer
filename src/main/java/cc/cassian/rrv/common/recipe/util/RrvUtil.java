package cc.cassian.rrv.common.recipe.util;

import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.mixin.world.item.crafting.IngredientAccessor;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cc.cassian.rrv.common.ReliableRecipeViewer.LOGGER;
import static net.minecraft.server.permissions.Permissions.*;

@ApiStatus.Internal
public class RrvUtil {

    public static boolean hasPermission(Player sender) {
        return sender.permissions().hasPermission(COMMANDS_GAMEMASTER);
    }

    public static boolean hasPermission(CommandSourceStack sender) {
        return sender.permissions().hasPermission(COMMANDS_GAMEMASTER);
    }

    public static SlotContent readSlotContent(String key, String type, Identifier identifier, JsonObject parsedRecipe) {
        JsonElement keyElement = parsedRecipe.get(key);
        if (keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString()) {
            var itemText = keyElement.getAsString();
            if (itemText.contains("#")) {
				TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(itemText.replace("#", "")));
                return SlotContent.of(tag);
            } else {
                var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemText));
                return SlotContent.of(item);
            }
        } else if (keyElement.isJsonArray() && keyElement.getAsJsonArray().get(0).isJsonPrimitive()) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            keyElement.getAsJsonArray().forEach(jsonElement->{
                var itemText = jsonElement.getAsString();
                if (itemText.contains("#")) {
                    TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(itemText.replace("#", "")));
                    var items = BuiltInRegistries.ITEM.getTagOrEmpty(tag);
                    items.forEach(holder -> itemStacks.add(holder.value().getDefaultInstance()));
                } else {
                    var item = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemText));
                    itemStacks.add(item.getDefaultInstance());
                }
            });
           return SlotContent.of(itemStacks);
        } else if (keyElement.isJsonObject()) {
            return SlotContent.of(getItemStack(keyElement));
        } else {
            LOGGER.error("Could not parse {} recipe '{}' as it was missing a key!", type, identifier);
        }
        return SlotContent.of();
    }

    public static ItemStack getItemStack(JsonElement keyElement) {
        if (keyElement.isJsonObject())
            return ItemStack.CODEC.parse(ClientRecipeManager.INSTANCE.createSerializationContext(JsonOps.INSTANCE), keyElement).result().orElseThrow();
        else if (keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString()) {
            return BuiltInRegistries.ITEM.getValue(Identifier.parse(keyElement.getAsString())).getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }

    public static List<Item> getItemsFromIngredient(Ingredient ingredient) {
        var ingredientContent = ((IngredientAccessor) (Object) ingredient).getValues().unwrap();
        List<Item> ingredients = new ArrayList<>();
        if (ingredientContent.left().isPresent()) {
            SlotContent.getItemsFromTag(ingredientContent.left().get()).ifPresent(holders -> {
                holders.forEach(holder -> ingredients.add(holder.value()));
            });
        }

        if (ingredientContent.right().isPresent())
            ingredients.addAll(ingredientContent.right().get().stream().filter(Holder::isBound).map(Holder::value).toList());
        return ingredients;
    }

    public static String ingredientSuffix(Ingredient ingredient) {
        List<Item> itemsFromIngredient = getItemsFromIngredient(ingredient);
        if (itemsFromIngredient.isEmpty()) return "";
        return "_from_" + itemsFromIngredient.getFirst().builtInRegistryHolder().key().identifier().getPath();
    }

    public static String blockName(Block block) {
        return getIdentifier(block).map(Identifier::toString).orElse("").replace(":", "_");
    }

    public static Identifier blockName(String prefix, Block block) {
        return getIdentifier(block).orElseThrow().withPath(path-> "%s%s".formatted(prefix, path.replace(":", "_")));
    }

    private static @NonNull Optional<Identifier> getIdentifier(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).map(ResourceKey::identifier);
    }

    public static Level getLevel() {
        MinecraftServer server = ServerRecipeManager.INSTANCE.getServer();
        if (server != null) {
            return server.overworld();
        } else {
            return RRVClientUtil.level();
        }
    }

    /// Replaces I18n.get
    public static String get(String key) {
        return Language.getInstance().getOrDefault(key);
    }

    /// Replaces I18n.exists - removed in 26.2.
    public static boolean has(String key) {
        return Language.getInstance().has(key);
    }
}
