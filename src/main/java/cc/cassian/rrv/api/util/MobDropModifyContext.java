package cc.cassian.rrv.api.util;

import cc.cassian.rrv.common.recipe.inventory.SlotContent;

import java.util.function.Predicate;

public record MobDropModifyContext(Predicate<net.minecraft.world.entity.EntityType<?>> entityTypePredicate, Predicate<SlotContent> slotContentPredicate, SlotContent newDrop) {

}