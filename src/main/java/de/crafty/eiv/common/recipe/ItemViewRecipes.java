package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.recipe.inventory.SlotContent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Internal (intermediate) class that connects {@link de.crafty.eiv.common.api.recipe.ItemView} (Api-class) with EIV logic
 * <br>
 * <br>
 * Also contains some helper functions
 */
public class ItemViewRecipes {

    public static final ItemViewRecipes INSTANCE = new ItemViewRecipes();


    /**
     * A map of recipe wrappers
     */
    private final HashMap<EivRecipeType<?>, ClientRecipeWrapper<?>> recipeWrappers;

    /**
     * A map of recipe providers
     */
    private final List<ServerRecipeProvider> recipeProviders;

    /**
     * A map of items by fluid
     */
    private final HashMap<Fluid, Item> fluidItemMap;

    private ItemViewRecipes() {
        this.recipeWrappers = new HashMap<>();
        this.recipeProviders = new ArrayList<>();
        this.fluidItemMap = new HashMap<>();
    }


    /**
     * Old way to register recipe wrappers
     * <br>
     * <br>
     * Will be removed soon
     * @param recipeType
     * @param wrapper
     * @param <T>
     */
    @Deprecated
    public <T extends IEivServerRecipe> void registerRecipeWrapper(EivRecipeType<T> recipeType, ClientRecipeWrapper<T> wrapper) {
        this.recipeWrappers.put(recipeType, wrapper);
    }

    /**
     * Old way to register recipe providers
     * <br>
     * <br>
     * Will be removed soon
     * @param provider
     */
    @Deprecated
    public void addRecipeProvider(ServerRecipeProvider provider) {
        this.recipeProviders.add(provider);
    }


    public HashMap<EivRecipeType<?>, ClientRecipeWrapper<?>> wrapperMap() {
        return this.recipeWrappers;
    }

    public List<ServerRecipeProvider> getRecipeProviders() {
        return this.recipeProviders;
    }

    public void setFluidItemMap(HashMap<Fluid, Item> fluidItemMap) {
        this.fluidItemMap.clear();
        this.fluidItemMap.putAll(fluidItemMap);
    }

    /**
     *
     * @param fluid The fluid
     * @return The corresponding item to a fluid
     */
    public Item itemForFluid(Fluid fluid) {
        return this.fluidItemMap.getOrDefault(fluid, Items.AIR);
    }

    /**
     *
     * @return Whether any of the listed SlotContents contains an itemStack matching the potion of the given stack
     */
    public static boolean makePotionRedirectCheck(ItemStack stack, List<SlotContent> slotContents) {
        if (!stack.has(DataComponents.POTION_CONTENTS))
            return true;

        for (SlotContent slotContent : slotContents) {
            for (ItemStack validStack : slotContent.getValidContents()) {
                if (!stack.is(validStack.getItem()))
                    continue;

                if (ItemViewRecipes.makePotionCheck(stack, validStack))
                    return true;
            }
        }

        return false;
    }

    /**
     *
     * @return Whether any of the listed SlotContents contains an itemStack matching the enchantments of the given stack
     */
    public static boolean makeEnchantedRedirectCheck(ItemStack stack, List<SlotContent> slotContents) {
        if (!stack.has(DataComponents.ENCHANTMENTS))
            return true;

        for (SlotContent slotContent : slotContents) {
            for (ItemStack validStack : slotContent.getValidContents()) {

                if (!stack.is(validStack.getItem()))
                    continue;

                if (ItemViewRecipes.makeEnchantmentCheck(stack, validStack))
                    return true;
            }

        }

        return false;
    }

    /**
     * @return Whether the potion component of two itemStacks matches
     */
    public static boolean makePotionCheck(ItemStack stack1, ItemStack stack2) {
        if (!(stack1.has(DataComponents.POTION_CONTENTS) && stack2.has(DataComponents.POTION_CONTENTS)))
            return true;

        PotionContents contents = stack1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        PotionContents stackContents = stack2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        return contents.potion().isPresent() && stackContents.potion().isPresent() && contents.is(stackContents.potion().orElseThrow());
    }

    /**
     * @return Whether the enchantments of two itemStacks match
     */
    public static boolean makeEnchantmentCheck(ItemStack stack1, ItemStack stack2) {
        if (!(stack1.has(DataComponents.ENCHANTMENTS) && stack2.has(DataComponents.ENCHANTMENTS)))
            return true;

        ItemEnchantments enchantments = stack1.getOrDefault(stack1.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments stackEnchantments = stack2.getOrDefault(stack2.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        return enchantments.keySet().stream().allMatch(enchantment -> {
            return stackEnchantments.getLevel(enchantment) == enchantments.getLevel(enchantment);
        }) && stackEnchantments.size() == enchantments.size();
    }


    public interface ClientRecipeWrapper<T extends IEivServerRecipe> {

        List<? extends IEivViewRecipe> wrap(T unwrapped);

    }


    public interface ServerRecipeProvider {

        void provide(List<IEivServerRecipe> recipeList);

    }


}
