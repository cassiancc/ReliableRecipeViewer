package cc.cassian.rrv.common.recipe.inventory;

import cc.cassian.rrv.api.ActionType;
//? if >26 {
/*import cc.cassian.rrv.common.recipe.util.RrvUtil;
*///?}
import com.mojang.datafixers.util.Either;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.mixin.world.item.crafting.IngredientAccessor;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SlotContent {

    private final List<ItemStack> content;
    private int current;

    private TagKey<Item> itemTag;

    private ItemStack itemOrigin;
    private ActionType originType;

    private ActionType type;

    private SlotContent(List<ItemStack> content) {

        List<ItemStack> copied = new ArrayList<>();
        content.stream().map(ItemStack::copy).forEach(copied::add);

        this.content = copied;
        this.current = 0;

        this.itemOrigin = ItemStack.EMPTY;
        this.originType = ActionType.ANY;

        this.type = ActionType.INPUT;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public void setType(Type type) {
        this.type = ActionType.of(type);
    }

    public ActionType getType() {
        return this.type;
    }

    /**
	 * Internal method to bind an item tag to a `SlotContent`.
     * The preferred option is usually {@link SlotContent#of(TagKey)}.
	 */
    public SlotContent bindItemTag(TagKey<Item> tag) {
        this.itemTag = tag;
        this.setDataComponent();
        return this;
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

    public void resetPointer() {
        this.current = 0;
        this.itemOrigin = ItemStack.EMPTY;
        this.originType = ActionType.ANY;
    }


    public List<ItemStack> getValidContents() {
        return this.content;
    }

    private void setDataComponent() {
        if (this.itemTag().isEmpty())
            return;

        this.content.forEach(stack -> {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putString(ReliableRecipeViewer.MOD_ID + "_recipeTag", this.itemTag().get().location().toString());
            CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        });

    }

    public boolean hasItem(Item check) {
        return this.content.stream().anyMatch(stack -> stack.getItem() == check);
    }

    public int getNextMatching(ItemStack origin) {

        for (int i = this.current; i < this.content.size() + this.current; i++) {
            int index = i < this.content.size() ? i : i - this.content.size();

            ItemStack stack = this.content.get(index);

            if (stack.getItem() != origin.getItem())
                continue;

            boolean potionCheck = ItemViewRecipes.makePotionCheck(origin, stack);
            boolean enchantCheck = ItemViewRecipes.makeEnchantmentCheck(origin, stack);

            if(potionCheck && enchantCheck)
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

    public static SlotContent of(Block item) {
        if (item == null) return SlotContent.of();
        return new SlotContent(List.of(new ItemStack(item)));
    }

    public static SlotContent ofItemList(List<Item> items) {
        if (items == null) return SlotContent.of();
        List<ItemStack> stacks = new ArrayList<>();
        items.forEach(item -> stacks.add(new ItemStack(item)));
        return SlotContent.of(stacks);
    }

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

    //? >26 {
    /*public static SlotContent of(net.minecraft.world.item.ItemStackTemplate stack) {
        if (stack == null) return SlotContent.of();
        return new SlotContent(List.of(RrvUtil.decodeTemplate(stack)));
    }
    *///?}

    public static SlotContent of(List<ItemStack> stacks) {
        if (stacks == null) return SlotContent.of();
        return new SlotContent(stacks);
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

    public static SlotContent of(Ingredient ingredient) {
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
	 * Moved to standardized system - see {@link ActionType}.
     * <p>{@link Type#INGREDIENT} moved to {@link ActionType#INPUT}</p>
     * <p>{@link Type#RESULT} moved to {@link ActionType#RESULT}</p>
     * <p>{@link Type#ANY} moved to {@link ActionType#ANY}</p>
	 */
    @Deprecated(since = "6.4.0", forRemoval = true)
    public enum Type {
        INGREDIENT,
        RESULT,
        ANY
    }

}
