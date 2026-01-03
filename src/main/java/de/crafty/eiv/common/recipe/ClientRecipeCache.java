package de.crafty.eiv.common.recipe;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.common.api.recipe.IEivViewRecipe;
import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.ItemView;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ClientRecipeCache {

    public static final ClientRecipeCache INSTANCE = new ClientRecipeCache();


    private final LinkedHashMap<EivRecipeType<?>, List<ServerRecipeManager.ServerRecipeEntry>> serverEntryMap;

    private final HashMap<Identifier, List<Identifier>> multiRecipeMap;
    private final HashMap<Identifier, IEivViewRecipe> recipeMap;
    private final HashMap<Item, List<Identifier>> byItemIngredient, byItemResult;

    private final HashMap<Item, List<ItemView.StackSensitive>> stackSensitives;

    private ClientRecipeCache() {
        this.serverEntryMap = new LinkedHashMap<>();

        this.multiRecipeMap = new LinkedHashMap<>();
        this.recipeMap = new HashMap<>();
        this.byItemIngredient = new HashMap<>();
        this.byItemResult = new HashMap<>();

        this.stackSensitives = new HashMap<>();

    }

    public void clearStackSensitives() {
        this.stackSensitives.clear();
    }

    public void addStackSensitive(ItemView.StackSensitive stackSensitive) {
        List<ItemView.StackSensitive> present = this.stackSensitives.getOrDefault(stackSensitive.stack().getItem(), new ArrayList<>());
        present.add(stackSensitive);
        this.stackSensitives.put(stackSensitive.stack().getItem(), present);
    }

    public List<ItemView.StackSensitive> getStackSensitives(Item item) {
        return this.stackSensitives.getOrDefault(item, new ArrayList<>());
    }


    public IEivViewRecipe getRecipe(final Identifier recipeId) {
        return recipeMap.getOrDefault(recipeId, null);
    }


    public void updateType(EivRecipeType<?> type, List<ServerRecipeManager.ServerRecipeEntry> recipes) {
        this.serverEntryMap.getOrDefault(type, new ArrayList<>()).forEach(entry -> {
            this.multiRecipeMap.getOrDefault(entry.modRecipeId(), new ArrayList<>()).forEach(Identifier -> {
                this.recipeMap.remove(Identifier);

                this.byItemIngredient.forEach((item, Identifiers) -> {
                    Identifiers.remove(Identifier);
                });

                this.byItemResult.forEach((item, Identifiers) -> {
                    Identifiers.remove(Identifier);
                });
            });
        });

        this.serverEntryMap.put(type, recipes);
    }


    public List<IEivViewRecipe> getRecipesForCraftingInput(ItemStack inputStack) {
        List<IEivViewRecipe> recipes = new ArrayList<>();
        this.byItemIngredient.getOrDefault(inputStack.getItem(), List.of()).forEach(Identifier -> {
            recipes.add(this.recipeMap.get(Identifier));
        });

        recipes.removeIf(viewRecipe -> !viewRecipe.redirectsAsIngredient(inputStack) && (viewRecipe.getViewType().getCraftReferences().stream().noneMatch(itemStack -> itemStack.getItem() == inputStack.getItem()) || !viewRecipe.getViewType().getCraftReferenceCondition().matches(inputStack, viewRecipe)));

        return recipes;
    }

    public List<IEivViewRecipe> getRecipesForCraftingOutput(ItemStack outputStack) {

        List<IEivViewRecipe> recipes = new ArrayList<>();
        this.byItemResult.getOrDefault(outputStack.getItem(), List.of()).forEach(Identifier -> {
            recipes.add(this.recipeMap.get(Identifier));
        });

        recipes.removeIf(viewRecipe -> !viewRecipe.redirectsAsResult(outputStack));

        return recipes;
    }

    public void sortModType(EivRecipeType<?> type) {
        ItemViewRecipes.ClientRecipeWrapper<?> wrapper = ItemViewRecipes.INSTANCE.wrapperMap().getOrDefault(type, null);

        if (wrapper == null || !this.serverEntryMap.containsKey(type))
            return;


        for (ServerRecipeManager.ServerRecipeEntry modEntry : this.serverEntryMap.get(type)) {
            List<? extends IEivViewRecipe> wrappedRecipes;

            try {
                wrappedRecipes = wrapper.wrap(modEntry.asWrapped());
            }catch (Exception e) {
                CommonEIV.LOGGER.error("Failed to wrap recipe entry {}: {}, skipping it...", modEntry.modRecipeId(), e.getMessage());
                continue;
            }

            if (wrappedRecipes.isEmpty())
                continue;

            for (int id = 0; id < wrappedRecipes.size(); id++) {
                IEivViewRecipe wrapped = wrappedRecipes.get(id);

                Identifier uniqueId = this.getUniqueId(modEntry, id);
                List<Identifier> summarized = this.multiRecipeMap.getOrDefault(modEntry.modRecipeId(), new ArrayList<>());
                summarized.add(uniqueId);
                this.multiRecipeMap.put(modEntry.modRecipeId(), summarized);

                this.recipeMap.put(uniqueId, wrapped);

                wrapped.getIngredients().forEach(ingredient -> {
                    ingredient.getValidContents().forEach(stack -> {

                        List<Identifier> byIngredient = this.byItemIngredient.getOrDefault(stack.getItem(), new ArrayList<>());
                        byIngredient.remove(uniqueId);
                        byIngredient.add(uniqueId);
                        this.byItemIngredient.put(stack.getItem(), byIngredient);
                    });
                });

                wrapped.getViewType().getCraftReferences().forEach(reference -> {

                    if(!wrapped.getViewType().getCraftReferenceCondition().matches(reference, wrapped))
                        return;

                    List<Identifier> byIngredient = this.byItemIngredient.getOrDefault(reference.getItem(), new ArrayList<>());
                    byIngredient.remove(uniqueId);
                    byIngredient.add(uniqueId);
                    this.byItemIngredient.put(reference.getItem(), byIngredient);
                });

                wrapped.getResults().forEach(result -> {
                    result.getValidContents().forEach(stack -> {
                        List<Identifier> byResult = this.byItemResult.getOrDefault(stack.getItem(), new ArrayList<>());
                        byResult.remove(uniqueId);
                        byResult.add(uniqueId);
                        this.byItemResult.put(stack.getItem(), byResult);
                    });
                });
            }
        }
    }


    private Identifier getUniqueId(ServerRecipeManager.ServerRecipeEntry modEntry, int index) {
        return Identifier.fromNamespaceAndPath(modEntry.modRecipeId().getNamespace(), modEntry.modRecipeId().getPath() + "/" + index);
    }


}
