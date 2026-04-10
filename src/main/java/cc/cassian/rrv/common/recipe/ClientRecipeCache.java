package cc.cassian.rrv.common.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class ClientRecipeCache {

    public static final ClientRecipeCache INSTANCE = new ClientRecipeCache();


    private final LinkedHashMap<ReliableServerRecipeType<?>, List<ServerRecipeManager.ServerRecipeEntry>> serverEntryMap;

    private final HashMap<Identifier, List<Identifier>> multiRecipeMap;
    private final HashMap<Identifier, ReliableClientRecipe> recipeMap;
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


    public ReliableClientRecipe getRecipe(final Identifier recipeId) {
        return recipeMap.getOrDefault(recipeId, null);
    }


    public void updateType(ReliableServerRecipeType<?> type, List<ServerRecipeManager.ServerRecipeEntry> recipes) {
        this.serverEntryMap.getOrDefault(type, new ArrayList<>()).forEach(entry -> this.multiRecipeMap.getOrDefault(entry.modRecipeId(), new ArrayList<>()).forEach(id -> {
            this.recipeMap.remove(id);

            this.byItemIngredient.forEach((item, identifiers) -> identifiers.remove(id));

            this.byItemResult.forEach((item, identifiers) -> identifiers.remove(id));
        }));

        this.serverEntryMap.put(type, recipes);
    }

    public List<ReliableClientRecipe> getRecipes() {
        return List.copyOf(recipeMap.values()).stream().filter(this::enabled).toList();
    }


    public List<ReliableClientRecipe> getRecipesForCraftingInput(ItemStack inputStack) {
        if (ModCompat.POLYDEX && ClientPolymerItemUtils.isPolyItem(inputStack)) {
            inputStack = ClientPolymerItemUtils.getServerItem(inputStack);
        }
        List<ReliableClientRecipe> recipes = new ArrayList<>();
        this.byItemIngredient.getOrDefault(inputStack.getItem(), List.of()).forEach(Identifier -> recipes.add(getRecipe(Identifier)));

        ItemStack finalInputStack = inputStack;
        recipes.removeIf(clientRecipe -> !clientRecipe.redirectsAsIngredient(finalInputStack) && (clientRecipe.getViewType().getCraftReferences().stream().noneMatch(itemStack -> itemStack.getItem() == finalInputStack.getItem()) || !clientRecipe.getViewType().getCraftReferenceCondition().matches(finalInputStack, clientRecipe)));
        recipes.removeIf(this::disabled);

        return recipes;
    }

    private boolean disabled(ReliableClientRecipe clientRecipe) {
        return !enabled(clientRecipe);
    }

    private boolean enabled(ReliableClientRecipe clientRecipe) {
        return Configs.CATEGORIES.enabled(clientRecipe.getViewType());
    }

    public List<ReliableClientRecipe> getRecipesForCraftingOutput(ItemStack outputStack) {
        if (ClientPolymerItemUtils.isPolyItem(outputStack)) {
            outputStack = ClientPolymerItemUtils.getServerItem(outputStack);
        }


        List<ReliableClientRecipe> recipes = new ArrayList<>();
        this.byItemResult.getOrDefault(outputStack.getItem(), List.of()).forEach(Identifier -> recipes.add(getRecipe(Identifier)));

        ItemStack finalOutputStack = outputStack;
        recipes.removeIf(viewRecipe -> !viewRecipe.redirectsAsResult(finalOutputStack));
        recipes.removeIf(this::disabled);

        return recipes;
    }

    public void sortModType(ReliableServerRecipeType<?> type) {
        int id;
        for (ItemViewRecipes.ClientRecipeProvider clientRecipeProvider : ItemViewRecipes.INSTANCE.getClientRecipeProviders()) {
            List<ReliableClientRecipe> recipes = new ArrayList<>();
            clientRecipeProvider.provide(recipes);
            for (id = 0; id < recipes.size(); id++) {
                ReliableClientRecipe wrapped = recipes.get(id);
                handleClientRecipe(Objects.requireNonNullElse(wrapped.getId(), wrapped.getViewType().getId().withSuffix("/"+id)), wrapped, id);
            }
        }


        ItemViewRecipes.ClientRecipeWrapper<?> wrapper = ItemViewRecipes.INSTANCE.wrapperMap().getOrDefault(type, null);

        if (wrapper == null || !this.serverEntryMap.containsKey(type))
            return;


        for (ServerRecipeManager.ServerRecipeEntry modEntry : this.serverEntryMap.get(type)) {
            List<? extends ReliableClientRecipe> wrappedRecipes;

            try {
                wrappedRecipes = wrapper.wrap(modEntry.asWrapped());
            }catch (Exception e) {
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    ReliableRecipeViewer.LOGGER.error("Failed to wrap recipe entry {}: {}, skipping it...", modEntry.modRecipeId(), e.getMessage());
                } else {
                    ReliableRecipeViewer.LOGGER.debug("Failed to wrap recipe entry {}: {}, skipping it...", modEntry.modRecipeId(), e.getMessage());
                }
                continue;
            }

            if (wrappedRecipes.isEmpty())
                continue;

            for (id = 0; id < wrappedRecipes.size(); id++) {
                ReliableClientRecipe wrapped = wrappedRecipes.get(id);
                handleClientRecipe(modEntry.modRecipeId(), wrapped, id);
            }
        }
    }

    private void handleClientRecipe(Identifier modEntryId, ReliableClientRecipe wrapped, int id) {

        Identifier uniqueId = this.getUniqueId(modEntryId, id);
        List<Identifier> summarized = this.multiRecipeMap.getOrDefault(modEntryId, new ArrayList<>());
        summarized.add(uniqueId);
        this.multiRecipeMap.put(modEntryId, summarized);

        this.recipeMap.put(uniqueId, wrapped);

        wrapped.getIngredients().forEach(ingredient -> ingredient.getValidContents().forEach(stack -> {

            List<Identifier> byIngredient = this.byItemIngredient.getOrDefault(stack.getItem(), new ArrayList<>());
            byIngredient.remove(uniqueId);
            byIngredient.add(uniqueId);
            this.byItemIngredient.put(stack.getItem(), byIngredient);
        }));

        var craftReferences = wrapped.getViewType().getCraftReferences();

        craftReferences.forEach(reference -> {

            if(!wrapped.getViewType().getCraftReferenceCondition().matches(reference, wrapped))
                return;

            List<Identifier> byIngredient = this.byItemIngredient.getOrDefault(reference.getItem(), new ArrayList<>());
            byIngredient.remove(uniqueId);
            byIngredient.add(uniqueId);
            this.byItemIngredient.put(reference.getItem(), byIngredient);
        });

        wrapped.getResults().forEach(result -> result.getValidContents().forEach(stack -> {
            List<Identifier> byResult = this.byItemResult.getOrDefault(stack.getItem(), new ArrayList<>());
            byResult.remove(uniqueId);
            byResult.add(uniqueId);
            this.byItemResult.put(stack.getItem(), byResult);
        }));
    }


    private Identifier getUniqueId(Identifier modEntry, int index) {
        return Identifier.fromNamespaceAndPath(modEntry.getNamespace(), modEntry.getPath() + "/" + index);
    }


}
