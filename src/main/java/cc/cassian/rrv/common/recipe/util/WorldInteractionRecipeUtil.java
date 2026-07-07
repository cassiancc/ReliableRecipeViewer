package cc.cassian.rrv.common.recipe.util;

//? if >26.2 {
/*import net.minecraft.core.component.BlockTransformer;

import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.mixin.world.level.predicates.MatchingBlockTagPredicateAccessor;
import cc.cassian.rrv.common.mixin.world.level.predicates.MatchingBlocksPredicateAccessor;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlockTagPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
*///?}

import java.util.ArrayList;
import java.util.List;

import static cc.cassian.rrv.common.recipe.util.RrvUtil.blockName;

public class WorldInteractionRecipeUtil {

	//? if >26.2 {

	/*public static void addTransformerRecipes(List<BlockTransformer.BlockTransformData> transforms, ArrayList<WorldInteractionClientRecipe> recipeList, SlotContent tool) {
		for (BlockTransformer.BlockTransformData transform : transforms) {
			BlockStateProvider blockStateProvider = transform.blockStateProvider();

			if (blockStateProvider instanceof RuleBasedStateProvider ruleBasedStateProvider) {
				for (RuleBasedStateProvider.Rule rule : ruleBasedStateProvider.rules()) {
					BlockPredicate predicate = rule.ifTrue();
					if (predicate instanceof MatchingBlocksPredicate matchingBlocksPredicate) {
						var before = SlotContent.ofBlockList(((MatchingBlocksPredicateAccessor) matchingBlocksPredicate).getBlocks().stream().map(Holder::value).toList());
						createTransformerRecipeFromRule(recipeList, tool, rule, before);
					}
					else if (predicate instanceof MatchingBlockTagPredicate matchingBlocksPredicate) {
						var before = SlotContent.ofBlockTag(((MatchingBlockTagPredicateAccessor) matchingBlocksPredicate).getTag());
						createTransformerRecipeFromRule(recipeList, tool, rule, before);
					}
				}
			}
		}
	}

	private static void createTransformerRecipeFromRule(ArrayList<WorldInteractionClientRecipe> recipeList, SlotContent tool, RuleBasedStateProvider.Rule rule, SlotContent before) {
		if (rule.then() instanceof SimpleStateProvider simpleStateProvider) {
			createTransformerRecipe(recipeList, tool, simpleStateProvider, before);
		} else if (rule.then() instanceof CopyPropertiesProvider(BlockStateProvider source)) {
			createTransformerRecipe(recipeList, tool, source, before);
		}
	}

	private static void createTransformerRecipe(ArrayList<WorldInteractionClientRecipe> recipeList, SlotContent tool, BlockStateProvider stateProvider, SlotContent before) {
		if (stateProvider instanceof SimpleStateProvider simpleStateProvider) {
			var after = simpleStateProvider.getState(null, null, null).getBlock();
			recipeList.add(new WorldInteractionClientRecipe(blockName("/world_interaction/transform_to_", after), SlotContent.of(before), tool, SlotContent.of(after)));
		}
	}
	*///?}
}
