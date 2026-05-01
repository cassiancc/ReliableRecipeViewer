package cc.cassian.rrv.client.recipe;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.config.Configs;
import cc.cassian.rrv.common.integration.ModCompat;
import cc.cassian.rrv.common.integration.polymer.client.ClientPolymerItemUtils;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.ResourceRecipeManager;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
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
    private final HashMap<Identifier, Identifier> clientEntryMap;
    private final HashMap<Item, List<Identifier>> byItemIngredient, byItemResult;

    private final HashMap<Item, List<ItemView.StackSensitive>> stackSensitives;

    public boolean localCacheBuilt() {
        return localCacheBuilt;
    }

    private boolean localCacheBuilt;

    private ClientRecipeCache() {
        this.serverEntryMap = new LinkedHashMap<>();
        this.clientEntryMap = new LinkedHashMap<>();

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

    public List<ReliableClientRecipe> getRecipes(final Identifier recipeId) {
        ArrayList<ReliableClientRecipe> clientRecipes = new ArrayList<>();
        List<Identifier> identifiers = multiRecipeMap.get(recipeId);
        if (identifiers == null) return clientRecipes;
        identifiers.stream().map(recipeMap::get).forEach(recipe -> {
            if (recipe != null && !clientRecipes.contains(recipe))
                clientRecipes.add(recipe);
        });
        return clientRecipes;
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
        List<ReliableClientRecipe> recipes = new ArrayList<>();
        this.byItemIngredient.getOrDefault(inputStack.getItem(), List.of()).forEach(Identifier -> recipes.add(getRecipe(Identifier)));

		recipes.removeIf(clientRecipe -> !clientRecipe.redirectsAsIngredient(inputStack) && (clientRecipe.getType().getCraftReferences().stream().noneMatch(itemStack -> itemStack.getItem() == inputStack.getItem()) || !clientRecipe.getType().getCraftReferenceCondition().matches(inputStack, clientRecipe)));
        recipes.removeIf(this::disabled);

        return recipes;
    }

    private boolean disabled(ReliableClientRecipe clientRecipe) {
        return !enabled(clientRecipe);
    }

    private boolean enabled(ReliableClientRecipe clientRecipe) {
        return Configs.CATEGORIES.enabled(clientRecipe.getType());
    }

    public List<ReliableClientRecipe> getRecipesForCraftingOutput(ItemStack outputStack) {
        List<ReliableClientRecipe> recipes = new ArrayList<>();
        this.byItemResult.getOrDefault(outputStack.getItem(), List.of()).forEach(Identifier -> recipes.add(getRecipe(Identifier)));

		recipes.removeIf(clientRecipe -> !clientRecipe.redirectsAsResult(outputStack));
        recipes.removeIf(this::disabled);

        return recipes;
    }

    public void sortModType(ReliableServerRecipeType<?> type) {
        // wrapped recipes
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

            for (int id = 0; id < wrappedRecipes.size(); id++) {
                ReliableClientRecipe wrapped = wrappedRecipes.get(id);
                handleClientRecipe(Objects.requireNonNullElse(wrapped.getId(), modEntry.modRecipeId()), wrapped, id, false);
            }
        }
    }

    public void buildRecipeCache(boolean rebuildFromSynchronizedRecipes) {
        ReliableRecipeViewer.LOGGER.info("RRV: Rebuilding client recipe cache {}", rebuildFromSynchronizedRecipes ? "from synchronized recipes." : "from client recipe folder.");

        //? fabric
        if (localCacheBuilt && !rebuildFromSynchronizedRecipes) return;

        if (rebuildFromSynchronizedRecipes)
            InternalRecipeManager.INSTANCE.setRecipesSynced(true);
        else if (Configs.CLIENT_SETTINGS.localFallbackAllowed()) {
            //? fabric {
            ResourceRecipeManager.getLocalRecipes();
            //?}
            localCacheBuilt = true;
        }

        ClientRecipeCache.INSTANCE.clear();

        for (ItemViewRecipes.ClientRecipeProvider clientRecipeProvider : ItemViewRecipes.INSTANCE.getClientRecipeProviders()) {
            List<ReliableClientRecipe> recipes = new ArrayList<>();
            try {
                clientRecipeProvider.provide(recipes);
            } catch (Exception e) {
                ReliableRecipeViewer.LOGGER.atError().setCause(e).log(
                        "Failed to add client recipes from {}, skipping it...",
                        clientRecipeProvider.getClass().getName());
                continue;
            }
            for (int id = 0; id < recipes.size(); id++) {
                ReliableClientRecipe clientRecipe = recipes.get(id);
                handleClientRecipe(clientRecipe.entryId(), clientRecipe, id, true);
            }
        }
    }

    private void handleClientRecipe(Identifier modEntryId, ReliableClientRecipe wrapped, int id, boolean fromNewSystem) {
        // disabled categories
        if (ItemView.getExcludedRecipeTypes().contains(wrapped.getType().getId()))
            return;
        // disabled recipes
        if (ItemView.getExcludedRecipes().containsKey(wrapped.getType().getId())) {
            if (ItemView.getExcludedRecipes().get(wrapped.getType().getId()).contains(modEntryId)) {
                return;
            }
        }

        // prevent people from locking up the world with duplicate recipes
        Identifier uniqueId = this.getUniqueId(modEntryId, id);
        List<Identifier> summarized = this.multiRecipeMap.getOrDefault(modEntryId, new ArrayList<>());
        summarized.add(uniqueId);
        this.multiRecipeMap.put(modEntryId, summarized);


        if (fromNewSystem)
            this.clientEntryMap.put(uniqueId, modEntryId);
        this.recipeMap.put(uniqueId, wrapped);

        // populate ingredient map with ingredients and workstations
        wrapped.getIngredients().forEach(ingredient -> ingredient.getValidContents().forEach(stack -> {
            List<Identifier> byIngredient = this.byItemIngredient.getOrDefault(stack.getItem(), new ArrayList<>());
            byIngredient.remove(uniqueId);
            byIngredient.add(uniqueId);
            this.byItemIngredient.put(stack.getItem(), byIngredient);
        }));

        var craftReferences = wrapped.getType().getCraftReferences();
        craftReferences.forEach(reference -> {
            if(!wrapped.getType().getCraftReferenceCondition().matches(reference, wrapped))
                return;

            List<Identifier> byIngredient = this.byItemIngredient.getOrDefault(reference.getItem(), new ArrayList<>());
            byIngredient.remove(uniqueId);
            byIngredient.add(uniqueId);
            this.byItemIngredient.put(reference.getItem(), byIngredient);
        });

        // populate result map
        wrapped.getResults().forEach(result -> result.getValidContents().forEach(stack -> {
            Item item = stack.getItem();
            List<Identifier> byResult = this.byItemResult.getOrDefault(item, new ArrayList<>());
            byResult.remove(uniqueId);
            byResult.add(uniqueId);
            this.byItemResult.put(item, byResult);
        }));
    }


    private Identifier getUniqueId(Identifier modEntry, int index) {
        return Identifier.fromNamespaceAndPath(modEntry.getNamespace(), modEntry.getPath() + "/" + index);
    }

    public void clear() {
        this.clientEntryMap.forEach((id, identifier2) -> {
            this.recipeMap.remove(id);

            this.byItemIngredient.forEach((item, identifiers) -> identifiers.remove(id));

            this.byItemResult.forEach((item, identifiers) -> identifiers.remove(id));
        });
    }
}
