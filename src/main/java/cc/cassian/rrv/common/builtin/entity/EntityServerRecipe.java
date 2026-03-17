package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EntityServerRecipe implements ReliableServerRecipe {

    public static final ReliableServerRecipeType<EntityServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.withDefaultNamespace("entity_loot"),
            () -> new EntityServerRecipe(null, List.of())
    );

    private EntityType<?> entityType;
    private List<SlotContent> drops;

    public EntityServerRecipe(EntityType<?> entityType, List<SlotContent> drops) {
        this.entityType = entityType;
        this.drops = drops;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    public List<SlotContent> getDrops() {
        return this.drops;
    }


    @Override
    public void writeToTag(CompoundTag tag) {
        tag.store("entity", BuiltInRegistries.ENTITY_TYPE.byNameCodec(), entityType);
        tag.put("stacks", TagUtil.writeList(this.drops, (origin, tag1) -> TagUtil.writeSlotContent(origin)));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.entityType = tag.read("entity", BuiltInRegistries.ENTITY_TYPE.byNameCodec()).orElse(null);
        this.drops = TagUtil.readList(tag, "stacks", TagUtil::readSlotContent);
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }

}
