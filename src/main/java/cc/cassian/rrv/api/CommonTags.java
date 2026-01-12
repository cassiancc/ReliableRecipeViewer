package cc.cassian.rrv.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CommonTags {
	public static final TagKey<Item> EXCLUDED_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Block> EXCLUDED_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Fluid> EXCLUDED_FLUIDS = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Enchantment> EXCLUDED_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));
	public static final TagKey<Potion> EXCLUDED_POTIONS = TagKey.create(Registries.POTION, ResourceLocation.fromNamespaceAndPath("c", "hidden_from_recipe_viewers"));

}
