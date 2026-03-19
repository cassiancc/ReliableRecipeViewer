package cc.cassian.rrv.common.builtin.tag.block;

import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlockTagServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<BlockTagServerRecipe> TYPE = ReliableServerRecipeType.register(
            ReliableRecipeViewer.of("block_tags"),
            () -> new BlockTagServerRecipe(null)
    );

    private TagKey<Block> tagKey;

	public BlockTagServerRecipe(TagKey<Block> tagKey) {
        this.tagKey = tagKey;
    }

    public TagKey<Block> getTagKey() {
        return this.tagKey;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.putString("tag", this.tagKey.location().toString());
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.tagKey = TagKey.create(Registries.BLOCK, Identifier.parse(tag.getStringOr("tag", "")));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

}
