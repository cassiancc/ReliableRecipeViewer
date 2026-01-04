package cc.cassian.rrv.common.builtin.entity;

import cc.cassian.rrv.common.api.recipe.RrvRecipeType;
import cc.cassian.rrv.common.api.recipe.IRrvServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EntityServerRecipe implements IRrvServerRecipe {

    public static final RrvRecipeType<EntityServerRecipe> TYPE = RrvRecipeType.register(
            Identifier.withDefaultNamespace("entity_loot"),
            () -> new EntityServerRecipe(null, List.of())
    );

    private EntityType<?> entityType;
    private List<ItemStack> drops;

    public EntityServerRecipe(EntityType<?> entityType, List<ItemStack> drops) {
        this.entityType = entityType;
        this.drops = drops;
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    public List<ItemStack> getDrops() {
        return this.drops;
    }


    @Override
    public void writeToTag(CompoundTag tag) {

        tag.putString("entity", BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType).toString());
        tag.put("stacks", RrvTagUtil.writeList(this.drops, (origin, tag1) -> RrvTagUtil.encodeItemStackOnServer(origin)));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(tag.getStringOr("entity", "")));
        this.drops = RrvTagUtil.readList(tag, "stacks", RrvTagUtil::decodeItemStackOnClient);

    }

    @Override
    public RrvRecipeType<? extends IRrvServerRecipe> getRecipeType() {
        return TYPE;
    }

}
