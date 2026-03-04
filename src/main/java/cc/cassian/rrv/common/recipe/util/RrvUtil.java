package cc.cassian.rrv.common.recipe.util;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? >26 {
/*import net.minecraft.world.item.ItemStackTemplate;
*///?}
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

import static cc.cassian.rrv.common.ReliableRecipeViewer.LOGGER;

@ApiStatus.Internal
public class RrvUtil {


    public static boolean matchesAnyTransferClass(ReliableClientRecipe viewRecipe, Screen playerScreen) {
        if (playerScreen == null)
            return false;

        return viewRecipe.getTransferClasses().stream().anyMatch(screenClass -> screenClass.isInstance(playerScreen));
    }

    public static boolean hasPermission(Player sender) {
        //? if <1.21.11 {
        return sender.hasPermissions(2);
        //?} else {
        /*return sender.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
        *///?}
    }

    public static boolean hasPermission(CommandSourceStack sender) {
        //? if <1.21.11 {
        return sender.hasPermission(2);
        //?} else {
        /*return sender.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
         *///?}
    }

    //? >26 {
	/*public static ItemStack decodeTemplate(ItemStackTemplate template) {
		return new ItemStack(template.item(), template.count(), template.components());
	}
    *///?}

    public static ItemStack decodeTemplate(ItemStack template) {
        return template;
    }

    public static SlotContent readSlotContent(String key, String type, ResourceLocation identifier, JsonObject parsedRecipe) {
        JsonElement keyElement = parsedRecipe.get(key);
        if (keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString()) {
            var itemText = keyElement.getAsString();
            if (itemText.contains("#")) {
				TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(itemText.replace("#", "")));
                return SlotContent.of(tag);
            } else {
                var item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemText));
                return SlotContent.of(item);
            }
        } else if (keyElement.isJsonArray() && keyElement.getAsJsonArray().get(0).isJsonPrimitive()) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            keyElement.getAsJsonArray().forEach(jsonElement->{
                var itemText = jsonElement.getAsString();
                if (itemText.contains("#")) {
                    TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(itemText.replace("#", "")));
                    var items = BuiltInRegistries.ITEM.getTagOrEmpty(tag);
                    items.forEach(holder -> itemStacks.add(holder.value().getDefaultInstance()));
                } else {
                    var item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemText));
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
            return BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(keyElement.getAsString())).getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }
}
