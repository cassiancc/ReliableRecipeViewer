package cc.cassian.rrv.common.recipe.inventory;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Internal
public record SlotPlacement(RecipeSlot.Properties properties, Function<RecipeSlot.Properties, RecipeSlot> function) {
}
