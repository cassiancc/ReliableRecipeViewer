package cc.cassian.rrv.common.recipe;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.builtin.info.InfoClientRecipe;
import cc.cassian.rrv.common.builtin.interaction.WorldInteractionClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.google.common.collect.LinkedHashMultimap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/// Internal (intermediate) class that connects [ItemView] (Api-class) with RRV logic
///
/// Also contains some helper functions
public class ItemViewRecipes {

    public static final ItemViewRecipes INSTANCE = new ItemViewRecipes();

    /// A map of recipe wrappers on the client side.
    private final HashMap<ReliableServerRecipeType<?>, ClientRecipeWrapper<?>> clientRecipeWrappers;

    /// A map of recipe providers on the server side.
    private final List<ServerRecipeProvider> serverRecipeProviders;


    /// A map of recipe providers on the client side.
    private final List<ClientRecipeProvider> clientRecipeProviders;

    /**
     * A map of items by fluid
     */
    @ApiStatus.Internal
    public final HashMap<Fluid, Item> fluidItemMap;

    private ItemViewRecipes() {
        this.clientRecipeWrappers = new HashMap<>();
        this.serverRecipeProviders = new ArrayList<>();
        this.clientRecipeProviders = new ArrayList<>();
        this.fluidItemMap = new HashMap<>();
    }


    /// Internal way to register recipe wrappers
    ///
    /// Will be removed soon
    /// @param recipeType
    /// @param wrapper
    /// @param <T>
    @Deprecated
    public <T extends ReliableServerRecipe> void registerRecipeWrapper(ReliableServerRecipeType<T> recipeType, ClientRecipeWrapper<T> wrapper) {
        this.clientRecipeWrappers.put(recipeType, wrapper);
    }

    /// Old way to register server recipe providers
    ///
    /// Will be removed soon
    /// @param provider
    @Deprecated
    public void addServerRecipeProvider(ServerRecipeProvider provider) {
        this.serverRecipeProviders.add(provider);
    }

    /// Internal way to register client recipe providers
    ///
    /// Will be removed soon
    /// @param provider
    @Deprecated
    public void addClientRecipeProvider(ClientRecipeProvider provider) {
        this.clientRecipeProviders.add(provider);
    }


    public HashMap<ReliableServerRecipeType<?>, ClientRecipeWrapper<?>> wrapperMap() {
        return this.clientRecipeWrappers;
    }

    public List<ServerRecipeProvider> getServerRecipeProviders() {
        return this.serverRecipeProviders;
    }

    public List<ClientRecipeProvider> getClientRecipeProviders() {
        return this.clientRecipeProviders;
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

    /// @return Whether any of the provided [SlotContent]s contain an [ItemStack] matching the components of the given stack
    public static boolean makeDefaultChecks(ItemStack stack, List<SlotContent> slots) {
        for (SlotContent slotContent : slots) {
            for (ItemStack validStack : slotContent.getValidContents()) {
                if (!stack.is(validStack.getItem()))
                    continue;

                if (ItemViewRecipes.makeDefaultChecks(stack, validStack))
                    return true;
            }
        }

        return false;
    }

    /// @return Whether the provided [ItemStack] matches the components of the given stack
    public static boolean makeDefaultChecks(ItemStack stack, ItemStack ingredient) {
        boolean potionRedirectCheck = ItemViewRecipes.makePotionCheck(stack, ingredient);
        boolean enchantmentRedirectCheck = ItemViewRecipes.makeEnchantmentCheck(stack, ingredient);
        boolean stewRedirectCheck = ItemViewRecipes.makeStewCheck(stack, ingredient);
        boolean fireworkRocketRedirectCheck = ItemViewRecipes.makeFireworkRocketCheck(stack, ingredient);
        return potionRedirectCheck && enchantmentRedirectCheck && stewRedirectCheck && fireworkRocketRedirectCheck;
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
     * @return Whether the suspicious stew component of two itemStacks matches
     */
    public static boolean makeStewCheck(ItemStack stack1, ItemStack stack2) {
        if (!(stack1.has(DataComponents.SUSPICIOUS_STEW_EFFECTS) && stack2.has(DataComponents.SUSPICIOUS_STEW_EFFECTS)))
            return true;

        SuspiciousStewEffects contents = stack1.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);
        SuspiciousStewEffects stackContents = stack2.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);

        return new HashSet<>(contents.effects()).containsAll(stackContents.effects());
    }

    /**
     * @return Whether the firework component of two itemStacks matches
     */
    public static boolean makeFireworkRocketCheck(ItemStack stack1, ItemStack stack2) {
        if (!(stack1.has(DataComponents.FIREWORKS) && stack2.has(DataComponents.FIREWORKS)))
            return true;

        Fireworks contents = stack1.getOrDefault(DataComponents.FIREWORKS, new Fireworks(0, List.of()));
        Fireworks stackContents = stack2.getOrDefault(DataComponents.FIREWORKS, new Fireworks(0, List.of()));

        return contents.flightDuration() == stackContents.flightDuration() && new HashSet<>(contents.explosions()).containsAll(stackContents.explosions());
    }

    /**
     * @return Whether the enchantments of two itemStacks match
     */
    public static boolean makeEnchantmentCheck(ItemStack stack1, ItemStack stack2) {
        if (!(stack1.has(DataComponents.STORED_ENCHANTMENTS) && stack2.has(DataComponents.STORED_ENCHANTMENTS)))
            return true;

        ItemEnchantments enchantments = stack1.getOrDefault(stack1.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments stackEnchantments = stack2.getOrDefault(stack2.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        return enchantments.keySet().stream().allMatch(enchantment -> {
            return stackEnchantments.getLevel(enchantment) == enchantments.getLevel(enchantment);
        }) && stackEnchantments.size() == enchantments.size();
    }

    /**
     * @return Whether the trims of two itemStacks match
     */
    public static boolean makeTrimCheck(ItemStack stack1, ItemStack stack2) {
        if (!(stack1.has(DataComponents.TRIM) && stack2.has(DataComponents.TRIM)))
            return true;

        ArmorTrim contents = stack1.get(DataComponents.TRIM);
        ArmorTrim stackContents = stack2.get(DataComponents.TRIM);

        if (contents == null || stackContents == null)
            return true;

        return stackContents.material() == contents.material() && stackContents.pattern() == contents.pattern();
    }

    /// A list of mob drops to be added to the index.
    public static final LinkedHashMultimap<EntityType<?>, SlotContent> MOB_DROPS = LinkedHashMultimap.create();
    /// A list of world interaction recipes to be added to the index.
    public static final List<WorldInteractionClientRecipe> WORLD_INTERACTION_RECIPES = new ArrayList<>();
    /// A list of info recipes to be added to the index.
    public static final List<InfoClientRecipe> INFO_RECIPES = new ArrayList<>();

    public static void addAllWorldInteractionRecipes(ArrayList<WorldInteractionClientRecipe> worldInteractionRecipes) {
        worldInteractionRecipes.addAll(WORLD_INTERACTION_RECIPES);
        WORLD_INTERACTION_RECIPES.clear();
    }

    public static void addAllInfoRecipes(ArrayList<InfoClientRecipe> infoClientRecipes) {
        infoClientRecipes.addAll(INFO_RECIPES);
        INFO_RECIPES.clear();
    }


    public interface ClientRecipeWrapper<T extends ReliableServerRecipe> {

        List<? extends ReliableClientRecipe> wrap(T unwrapped);

    }


    public interface ServerRecipeProvider {

        void provide(List<ReliableServerRecipe> recipeList);

    }

    public interface ClientRecipeProvider {

        void provide(List<ReliableClientRecipe> recipeList);

    }


}
