package cc.cassian.rrv.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CommonTags {
	public static final TagKey<Item> EXCLUDED_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Block> EXCLUDED_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Fluid> EXCLUDED_FLUIDS = TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Enchantment> EXCLUDED_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Potion> EXCLUDED_POTIONS = TagKey.create(Registries.POTION, Identifier.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Item> FLOWERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "flowers"));

}
