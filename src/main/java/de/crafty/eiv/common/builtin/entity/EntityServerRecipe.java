package de.crafty.eiv.common.builtin.entity;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EntityServerRecipe implements IEivServerRecipe {

    public static final EivRecipeType<EntityServerRecipe> TYPE = EivRecipeType.register(
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
        tag.put("stacks", EivTagUtil.writeList(this.drops, (origin, tag1) -> EivTagUtil.encodeItemStackOnServer(origin)));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {

        this.entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(tag.getStringOr("entity", "")));
        this.drops = EivTagUtil.readList(tag, "stacks", EivTagUtil::decodeItemStackOnClient);

    }

    @Override
    public EivRecipeType<? extends IEivServerRecipe> getRecipeType() {
        return TYPE;
    }

}
