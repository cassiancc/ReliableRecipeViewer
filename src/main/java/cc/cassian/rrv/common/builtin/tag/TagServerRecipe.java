package cc.cassian.rrv.common.builtin.tag;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class TagServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<TagServerRecipe> TYPE = ReliableServerRecipeType.register(
            ReliableRecipeViewer.of("item_tags"),
            () -> new TagServerRecipe(null)
    );

    private TagKey<Item> tagKey;

	public TagServerRecipe(TagKey<Item> tagKey) {
        this.tagKey = tagKey;
    }

    public TagKey<Item> getTagKey() {
        return this.tagKey;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.putString("tag", this.tagKey.location().toString());
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.tagKey = TagKey.create(Registries.ITEM, Identifier.parse(tag.getStringOr("tag", "")));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

}
