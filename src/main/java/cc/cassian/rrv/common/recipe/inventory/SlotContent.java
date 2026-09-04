package cc.cassian.rrv.common.recipe.inventory;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.util.RRVClientUtil;
import cc.cassian.rrv.common.RRVPlatform;
import cc.cassian.rrv.common.mixin.world.item.crafting.IngredientAccessor;
import cc.cassian.rrv.common.recipe.util.RrvUtil;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//? neoforge {
/*import net.neoforged.neoforge.common.crafting.BlockTagIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
*///?}
//? fabric {
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
//?}
//? if >26.2 {
/*import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.world.item.alchemy.PotionContents;
*///?}
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SlotContent {

    public static final Codec<SlotContent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ItemStack.CODEC.listOf().fieldOf("id").forGetter(SlotContent::getValidContents),
                            TagKey.codec(Registries.ITEM).optionalFieldOf("item_tag").forGetter(SlotContent::itemTag),
                            TagKey.codec(Registries.BLOCK).optionalFieldOf("block_tag").forGetter(SlotContent::blockTag)
                    )
                    .apply(instance, SlotContent::new)
    );
    private final List<ItemStack> content;
    private int current;

    private TagKey<Item> itemTag;
    private TagKey<Block> blockTag;

    private ItemStack itemOrigin;
    private ActionType originType;

    private ActionType type;

    private SlotContent(List<ItemStack> content) {

        List<ItemStack> copied = new ArrayList<>();
        content.stream().map(ItemStack::copy)
        //? fabric {
        .map(i -> {
            if (RRVPlatform.INSTANCE.isClientSide())
			    return RRVClientUtil.applyPolymerCheck(i);
            return i;
		})
        //?}
        .forEach(copied::add);

        this.content = copied;
        this.current = 0;

        this.itemOrigin = ItemStack.EMPTY;
        this.originType = ActionType.ANY;

        this.type = ActionType.INPUT;
    }

    private SlotContent(List<ItemStack> content, Optional<TagKey<Item>> tagKey, Optional<TagKey<Block>> blockTagKey) {
        this(content);
        tagKey.ifPresent(itemTagKey -> this.itemTag = itemTagKey);
        blockTagKey.ifPresent(blockTag -> this.blockTag = blockTag);
    }

    public static SlotContent of(@Nullable HolderSet<Item> items) {
        if (items == null) return SlotContent.of();
        return SlotContent.of(Ingredient.of(items));
    }

    public static SlotContent of(SlotDisplay slotDisplay, Level level) {
        if (slotDisplay == null) return SlotContent.of();
        return SlotContent.of(slotDisplay.resolveForStacks(SlotDisplayContext.fromLevel(level)));
    }

    public static SlotContent of(SlotDisplay slotDisplay) {
        Level level = RrvUtil.getLevel();
        return SlotContent.of(slotDisplay, level);
    }

	public boolean contains(Item item) {
		return getValidContents().stream().anyMatch(i->i.is(item));
	}

    public boolean contains(TagKey<Item> itemTag) {
        return (this.itemTag().isPresent() && this.itemTag().get().equals(itemTag)) || getValidContents().stream().anyMatch(i->i.is(itemTag));
    }

    public boolean contains(ItemStack stack) {
        return getValidContents().contains(stack);
    }

	public void setType(ActionType type) {
        this.type = type;
    }

    public ActionType getType() {
        return this.type;
    }

    /**
	 * Internal method to bind an item tag to a {@code SlotContent}.
     * The preferred option is usually {@link SlotContent#of(TagKey)}.
	 */
    public SlotContent bindItemTag(TagKey<Item> tag) {
        this.itemTag = tag;
        this.setDataComponent("itemTag", tag.location());
        return this;
    }

    /**
     * Internal method to bind a result to a {@code SlotContent}.
     */
    public SlotContent bindResult(Identifier id) {
        this.setDataComponent("result", id);
        return this;
    }

    public Optional<TagKey<Item>> getItemTag() {
        return Optional.ofNullable(this.itemTag);
    }

    /**
     * Internal method to bind an item tag to a `SlotContent`.
     * The preferred option is usually {@link SlotContent#of(TagKey)}.
     */
    public SlotContent bindBlockTag(TagKey<Block> tag) {
        this.blockTag = tag;
        this.setDataComponent("blockTag", tag.location());
        return this;
    }

    public Optional<TagKey<Block>> blockTag() {
        return Optional.ofNullable(this.blockTag);
    }

    public void bindOrigin(ItemStack stack, ActionType originType) {
        this.itemOrigin = stack.copy();
        this.originType = originType;
    }

    public int size() {
        return this.content.size();
    }

    public boolean isEmpty() {
        return this.content.stream().filter(ItemStack::isEmpty).count() == this.content.size();
    }

    public int index() {

        if (this.hasItem(this.itemOrigin.getItem()) && this.originType == this.type)
            return this.getNextMatching(this.itemOrigin);

        return this.current;
    }

    public ItemStack getByIndex(int index) {
        return this.content.isEmpty() ? ItemStack.EMPTY : this.content.get(index).copy();
    }

    public ItemStack next() {
        this.current++;
        if (this.current >= this.content.size())
            this.current = 0;

        return this.getByIndex(this.index());
    }

    public ItemStack current() {
        return this.getByIndex(this.index());
    }

    public void resetPointer() {
        this.current = 0;
        this.itemOrigin = ItemStack.EMPTY;
        this.originType = ActionType.ANY;
    }


    public List<ItemStack> getValidContents() {
        return this.content;
    }

    private void setDataComponent(String key, Identifier id) {
        if (id == null)
            return;

        this.content.forEach(stack -> {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putString(ReliableRecipeViewer.MOD_ID + "_" + key, id.toString());
            CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        });

    }

    public boolean hasItem(Item check) {
        return this.content.stream().anyMatch(stack -> stack.getItem() == check);
    }

    public int getNextMatching(ItemStack origin) {
        return getNextMatching(origin, ItemViewRecipes::makeDefaultChecks);
    }

    public int getNextMatching(ItemStack origin, BiPredicate<ItemStack, ItemStack> predicate) {

        for (int i = this.current; i < this.content.size() + this.current; i++) {
            int index = i < this.content.size() ? i : i - this.content.size();

            ItemStack stack = this.content.get(index);

            if (stack.getItem() != origin.getItem())
                continue;

            if (predicate.test(origin, stack))
                return index;
        }

        return this.current;
    }

    public Optional<TagKey<Item>> itemTag() {
        return this.itemTag == null ? Optional.empty() : Optional.of(this.itemTag);
    }

    public static SlotContent of(){
        return new SlotContent(List.of());
    }

    public static SlotContent of(Item item) {
        if (item == null) return SlotContent.of();
        return new SlotContent(List.of(new ItemStack(item)));
    }

    public static SlotContent of(Item... items) {
        if (items == null) return SlotContent.of();
        return new SlotContent(Arrays.stream(items).map(ItemStack::new).toList());
    }

    public static SlotContent of(Block item) {
        if (item == null) return SlotContent.of();
        return new SlotContent(List.of(new ItemStack(item)));
    }

    public static SlotContent of(Block... blocks) {
        if (blocks == null) return SlotContent.of();
        return new SlotContent(Arrays.stream(blocks).map(ItemStack::new).toList());
    }

    public static SlotContent ofItemList(List<Item> items) {
        if (items == null) return SlotContent.of();
        List<ItemStack> stacks = new ArrayList<>();
        items.forEach(item -> stacks.add(new ItemStack(item)));
        return SlotContent.of(stacks);
    }

    public static SlotContent ofBlockList(List<Block> blocks) {
        if (blocks == null) return SlotContent.of();
        List<ItemStack> stacks = new ArrayList<>();
        blocks.forEach(block -> stacks.add(new ItemStack(block)));
        return SlotContent.of(stacks);
    }

    //? if >26.2 {
    /*public static SlotContent of(net.minecraft.world.item.crafting.PotionIngredient potionIngredient) {
        if (potionIngredient == null) return SlotContent.of();

        var patch = DataComponentPatch.builder();
        potionIngredient.potions().flatMap(PotionsPredicate::potions).ifPresent(p -> {
			p.forEach(potionHolder -> {
				patch.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder));
			});
		});

        if (patch.build().isEmpty()) {
            return SlotContent.of(potionIngredient.ingredient());
        }

        return SlotContent.of(DefaultCustomIngredients.components(potionIngredient.ingredient(), patch.build()));
    }

    public static SlotContent of(net.minecraft.world.item.crafting.TransmuteResult transmuteResult, Item fallback) {
        return SlotContent.of(new ItemStackTemplate(transmuteResult.item().orElse(fallback.builtInRegistryHolder()), transmuteResult.count(), transmuteResult.components()));
    }
    *///?}


    public static SlotContent of(FluidStack fluidStack) {
        if (fluidStack == null) return SlotContent.of();
        return new SlotContent(List.of(fluidStack.createItemStack()));
    }

    public static SlotContent ofFluidList(List<FluidStack> fluidStacks) {
        if (fluidStacks == null) return SlotContent.of();
        List<ItemStack> stacks = new ArrayList<>();
        fluidStacks.forEach(fluidStack -> stacks.add(fluidStack.createItemStack()));
        return new SlotContent(stacks);
    }

    public static SlotContent of(ItemStack stack) {
        if (stack == null) return SlotContent.of();
        return new SlotContent(List.of(stack));
    }

    public static SlotContent of(ItemStackTemplate stack) {
        if (stack == null) return SlotContent.of();
        return new SlotContent(List.of(stack.create()));
    }

    public static SlotContent of(List<ItemStack> stacks) {
        if (stacks == null) return SlotContent.of();
        return new SlotContent(stacks);
    }

    public static SlotContent of(SlotContent content) {
        if (content == null || content.content == null) return SlotContent.of();
        return of(content.content);
    }

    public static SlotContent ofTemplates(List<ItemStackTemplate> stacks) {
        if (stacks == null) return SlotContent.of();
        return new SlotContent(stacks.stream().map(ItemStackTemplate::create).toList());
    }

    public static SlotContent of(TagKey<Item> itemTag) {
        if (itemTag == null) return SlotContent.of();
        if (BuiltInRegistries.ITEM.get(itemTag).isEmpty()) return SlotContent.of();
        List<ItemStack> stacks = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(itemTag).forEach(holder -> {
            stacks.add(holder.value().getDefaultInstance());
        });

        return new SlotContent(stacks).bindItemTag(itemTag);
    }

    public static SlotContent ofBlockTag(TagKey<Block> blockTag) {
        if (blockTag == null) return SlotContent.of();
        if (BuiltInRegistries.BLOCK.get(blockTag).isEmpty()) return SlotContent.of();
        List<ItemStack> stacks = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag).forEach(holder -> {
            stacks.add(holder.value().asItem().getDefaultInstance());
        });

        return new SlotContent(stacks).bindBlockTag(blockTag);
    }

    public static SlotContent of(Ingredient ingredient) {
        if (ingredient == null) return SlotContent.of();
        if (RrvUtil.getLevel() == null) return SlotContent.withoutLevel(ingredient);
        SlotContent slotContent = SlotContent.of(ingredient.display());
        //? neoforge {
        /*ingredient.getValues().unwrap().ifLeft(slotContent::bindItemTag);
        if (ingredient.getCustomIngredient() instanceof BlockTagIngredient blockTagIngredient) {
            TagKey<Block> blockTag = blockTagIngredient.getTag();
            slotContent.bindBlockTag(blockTag);
        }
        *///?} else {
        ingredient.values.unwrap().ifLeft(slotContent::bindItemTag);
        //?}
        return slotContent;
    }

    private static SlotContent withoutLevel(Ingredient ingredient) {
        if (ingredient == null) return SlotContent.of();

        Either<TagKey<Item>, List<Holder<Item>>> ingredientContent = ((IngredientAccessor) (Object) ingredient).getValues().unwrap();

        if (ingredientContent.right().isPresent()) {
            List<ItemStack> stacks = new ArrayList<>();
            ingredientContent.right().get().forEach(holder -> stacks.add(new ItemStack(holder.value())));
            return new SlotContent(stacks);
        }

        return ingredientContent.left().isPresent() ? SlotContent.of(ingredientContent.left().get()) : SlotContent.of(Items.AIR);
    }

    public static Optional<HolderSet.Named<Item>> getItemsFromTag(TagKey<Item> tag) {
        return BuiltInRegistries.ITEM.get(tag);
    }

    /**
	 * Allows for representing a {@link SlotContent} as an {@link  Ingredient}. Not recommended for general use as this loses modded data.
	 */
    @Deprecated
	public @Nullable Ingredient asIngredient() {
        if (this.itemTag != null) {
            TagKey<Item> tagKey = this.itemTag;
            if (BuiltInRegistries.ITEM.get(tagKey).isEmpty())
                return null;
            return Ingredient.of(Objects.requireNonNull(BuiltInRegistries.ITEM.get(tagKey).get()));
        }
        List<Holder<Item>> itemList = this.content.stream().map(ItemStack::getItem).map(Holder::direct).toList();
		if (!itemList.isEmpty()) return Ingredient.of(HolderSet.direct(itemList));
		return null;
	}
}
