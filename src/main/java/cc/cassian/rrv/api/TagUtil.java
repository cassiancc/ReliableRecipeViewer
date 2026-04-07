package cc.cassian.rrv.api;

import cc.cassian.rrv.common.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Helper class for network encoding based on CompoundTags
 */
public class TagUtil {


    private static <T> ListTag createRegistryList(List<T> objects, DefaultedRegistry<T> registry) {
        ListTag list = new ListTag();
        objects.stream().map(t -> StringTag.valueOf(registryToString(t, registry))).forEach(list::add);
        return list;
    }

    private static <T> List<T> reconstructRegistryList(CompoundTag srcTag, String key, DefaultedRegistry<T> registry) {
        return srcTag.getListOrEmpty(key).stream().map(Tag::asString).map(s -> stringToRegistry(s.orElseThrow(), registry)).filter(Objects::nonNull).toList();
    }


    /**
     * Decodes an {@link ItemStack} on the client side
     * @param tag The tag to decode
     * @return The decoded stack
     */
    public static ItemStack decodeItemStackOnClient(CompoundTag tag) {
        return ItemStack.CODEC.parse(ClientRecipeManager.INSTANCE.createSerializationContext(), tag).result().orElse(ItemStack.EMPTY);
    }

    /**
     * Decodes an {@link ItemStackTemplate} on the client side
     * @param tag The tag to decode
     * @return The decoded stack
     */
    public static ItemStackTemplate decodeItemStackTemplateOnClient(CompoundTag tag) {
        return ItemStackTemplate.CODEC.parse(ClientRecipeManager.INSTANCE.createSerializationContext(), tag).result().orElse(null);
    }

    /**
     * Encodes an {@link ItemStack} on the client side
     * @param stack The stack to encode
     * @return The encoded stack as CompoundTag
     */
    public static CompoundTag encodeItemStackOnClient(ItemStack stack) {
        return ItemStack.CODEC.encode(stack, ClientRecipeManager.INSTANCE.createSerializationContext(), new CompoundTag()).mapOrElse(tag -> tag.asCompound().orElseGet(CompoundTag::new), tagError -> new CompoundTag());
    }

    /**
     * Encodes an {@link ItemStack} on the server side
     * @param stack The stack to encode
     * @return The encoded stack as CompoundTag
     */
    public static CompoundTag encodeItemStackOnServer(ItemStack stack) {
        return ItemStack.CODEC.encode(stack, ServerRecipeManager.INSTANCE.createSerializationContext(), new CompoundTag()).mapOrElse(tag -> tag.asCompound().orElseGet(CompoundTag::new), tagError -> new CompoundTag());
    }

    /**
     * Encodes an {@link ItemStackTemplate} on the server side
     * @param stack The stack to encode
     * @return The encoded stack as CompoundTag
     */
    public static CompoundTag encodeItemStackOnServer(ItemStackTemplate stack) {
        return ItemStackTemplate.CODEC.encode(stack, ServerRecipeManager.INSTANCE.createSerializationContext(), new CompoundTag()).mapOrElse(tag -> tag.asCompound().orElseGet(CompoundTag::new), tagError -> new CompoundTag());
    }

    /**
     * Decodes an ItemStack on the server side
     * @param tag The tag to decode
     * @return The decoded stack
     */
    public static ItemStack decodeItemStackOnServer(CompoundTag tag) {
        return ItemStack.CODEC.parse(ServerRecipeManager.INSTANCE.createSerializationContext(), tag).result().orElse(ItemStack.EMPTY);
    }

    /**
     * Encodes an {@link Ingredient}. Since 7.0.0, it's recommended to store data as a SlotContent rather than as a raw ingredient via {@link TagUtil#writeSlotContent(SlotContent)}.
     * @param ingredient The ingredient to encode
     * @return The encoded ingredient as CompoundTag
     */
    @Deprecated(since = "7.0.0")
    public static CompoundTag writeIngredient(Ingredient ingredient) {
        return writeSlotContent(SlotContent.of(ingredient));
    }

    /**
     * Decodes an {@link Ingredient}. Since 7.0.0, it's recommended to store data as a SlotContent rather than as a raw ingredient via {@link TagUtil#readSlotContent(CompoundTag)}.
     * @param tag The tag to decode
     * @return The decoded ingredient
     */
    @Deprecated(since = "7.0.0")
    public static @Nullable Ingredient readIngredient(CompoundTag tag) {
        if (tag.isEmpty())
            return null;

        SlotContent slotContent = readSlotContent(tag);
        return slotContent.asIngredient();
	}



    //----------------- Item, Block, Fluid -----------------

    /**
     * Pre defined method for encoding item lists
     * @param items The items to encode
     * @return The encoded item list as ListTag
     */
    public static ListTag createItemList(List<Item> items) {
        return createRegistryList(items, BuiltInRegistries.ITEM);
    }


    /**
     * Pre defined method for decoding item lists
     * @param srcTag The parent tag containing the list
     * @param key They key referring to the list
     * @return The decoded item list
     */
    public static List<Item> reconstructItemList(CompoundTag srcTag, String key) {
        return reconstructRegistryList(srcTag, key, BuiltInRegistries.ITEM);
    }

    /**
     * Pre defined method for encoding block lists
     * @param blocks The blocks to encode
     * @return The encoded block list as ListTag
     */
    public static ListTag createBlockList(List<Block> blocks) {
        return createRegistryList(blocks, BuiltInRegistries.BLOCK);
    }

    /**
     * Pre defined method for decoding block lists
     * @param srcTag The parent tag containing the list
     * @param key They key referring to the list
     * @return The decoded block list
     */
    public static List<Block> reconstructBlockList(CompoundTag srcTag, String key) {
        return reconstructRegistryList(srcTag, key, BuiltInRegistries.BLOCK);
    }

    /**
     * Pre defined method for encoding fluid lists
     * @param fluids The fluids to encode
     * @return The encoded fluid list as ListTag
     */
    public static ListTag createFluidList(List<Fluid> fluids) {
        return createRegistryList(fluids, BuiltInRegistries.FLUID);
    }


    /**
     * Pre defined method for decoding fluid lists
     * @param srcTag The parent tag containing the list
     * @param key They key referring to the list
     * @return The decoded fluid list
     */
    public static List<Fluid> reconstructFluidList(CompoundTag srcTag, String key) {
        return reconstructRegistryList(srcTag, key, BuiltInRegistries.FLUID);
    }

    //----------------- Custom Objects -----------------


    /**
     * Encodes a list of objects
     * @param list The list to encode
     * @param builder A CompoundBuilder defining the encoding method for a single object
     * @return The encoded list as CompoundTag
     * @param <T> The type of the list
     */
    public static <T> ListTag writeList(List<T> list, CompoundBuilder<T> builder) {
        ListTag tagList = new ListTag();
        list.stream().map(t -> builder.buildSingle(t, new CompoundTag())).forEach(tagList::add);
        return tagList;
    }

    /**
     * Decodes a list of objects
     * @param srcTag The parent tag containing the list
     * @param key The key referring to the list
     * @param builder A CompoundReconstructor defining the decoding method for a single object
     * @return The decoded list
     * @param <T> The type of the list
     */
    public static <T> List<T> readList(CompoundTag srcTag, String key, CompoundReconstructor<T> builder) {
        return srcTag.getListOrEmpty(key).stream().map(Tag::asCompound).map(compoundTag -> builder.reconstructSingle(compoundTag.orElseGet(CompoundTag::new))).toList();
    }


    //----------------- Registry-String Converter -----------------


    private static <T> String registryToString(T object, DefaultedRegistry<T> registry) {
        return registry.getKey(object).toString();
    }

    private static <T> T stringToRegistry(String string, DefaultedRegistry<T> registry) {
        if (string.isEmpty())
            return null;

        return registry.getOptional(Identifier.tryParse(string)).orElse(null);
    }

    /**
     * @param item The Item
     * @return The resource location of the given item as a string
     */
    public static String itemToString(Item item) {
        return registryToString(item, BuiltInRegistries.ITEM);
    }

    /**
     * @param s The resource location as a string
     * @return The item corresponding to the given resource location
     */
    public static Item itemFromString(String s) {
        return stringToRegistry(s, BuiltInRegistries.ITEM);
    }

    /**
     * @param block The Block
     * @return The resource location of the given block as a string
     */
    public static String blockToString(Block block) {
        return registryToString(block, BuiltInRegistries.BLOCK);
    }

    /**
     *
     * @param s The resource location as a string
     * @return The block corresponding to the given resource location
     */
    public static Block blockFromString(String s) {
        return stringToRegistry(s, BuiltInRegistries.BLOCK);
    }

    /**
     *
     * @param fluid The Fluid
     * @return The resource location of the given fluid as a string
     */
    public static String fluidToString(Fluid fluid) {
        return registryToString(fluid, BuiltInRegistries.FLUID);
    }

    /**
     *
     * @param s The resource location as a string
     * @return The fluid corresponding to the given resource location
     */
    public static Fluid fluidFromString(String s) {
        return stringToRegistry(s, BuiltInRegistries.FLUID);
    }

	public static CompoundTag writeSlotContent(SlotContent slotContent) {
        return (CompoundTag) SlotContent.CODEC.encodeStart(ServerRecipeManager.INSTANCE.createSerializationContext(), slotContent).result().orElse(new CompoundTag());
	}

    public static SlotContent readSlotContent(CompoundTag tag) {
        return SlotContent.CODEC.decode(ClientRecipeManager.INSTANCE.createSerializationContext(), tag).result().orElse(new Pair<>(SlotContent.of(), new CompoundTag())).getFirst();
    }

    //----------------- Custom object builder/reconstructor -----------------


    /**
     * Functional interface defining the encoding method for a single object
     * @param <T> The type of the object
     */
    public interface CompoundBuilder<T> {

        CompoundTag buildSingle(T origin, CompoundTag tag);

    }

    /**
     * Functional interface defining the decoding method for a single object
     * @param <T> The type of the object
     */
    public interface CompoundReconstructor<T> {

        T reconstructSingle(CompoundTag tag);

    }
}
