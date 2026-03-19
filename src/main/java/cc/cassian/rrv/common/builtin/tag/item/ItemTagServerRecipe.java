package cc.cassian.rrv.common.builtin.tag.item;

import cc.cassian.rrv.api.CommonTags;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTagServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<ItemTagServerRecipe> TYPE = ReliableServerRecipeType.register(
            ReliableRecipeViewer.of("item_tags"),
            () -> new ItemTagServerRecipe(null)
    );

    private TagKey<Item> tagKey;

	public ItemTagServerRecipe(TagKey<Item> tagKey) {
        this.tagKey = tagKey;
    }

    public TagKey<Item> getTagKey() {
        return this.tagKey;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.store("tag", TagKey.codec(Registries.ITEM), this.tagKey);
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.tagKey = tag.read("tag", TagKey.codec(Registries.ITEM)).orElse(CommonTags.EXCLUDED_ITEMS);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

}
