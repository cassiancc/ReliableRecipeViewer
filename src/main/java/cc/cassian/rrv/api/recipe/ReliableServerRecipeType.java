package cc.cassian.rrv.api.recipe;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

/**
 * Representation of a server-side recipe type
 * @param <T> The class of the server recipe
 */
public interface ReliableServerRecipeType<T extends ReliableServerRecipe> {

    HashMap<ResourceLocation, ReliableServerRecipeType<?>> RRV_RECIPE_TYPES = new HashMap<>();


    /**
     *
     * @return A unique id for the recipe type used in network communication
     */
    ResourceLocation getId();

    /**
     *
     * @return A method that constructs a default instance of the server recipe which is later updated with network data on load in {@link ReliableServerRecipe}
     */
    EmptyRecipeConstructor<T> getEmptyConstructor();


    /**
     *
     * @param id A unique id
     * @param emptyRecipeConstructor A method creating a default instance of the server recipe
     * @return The recipe type
     * @param <S> The server recipe class
     */
    static <S extends ReliableServerRecipe> ReliableServerRecipeType<S> register(ResourceLocation id, EmptyRecipeConstructor<S> emptyRecipeConstructor) {

        ReliableServerRecipeType<S> type = new ReliableServerRecipeType<S>() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public EmptyRecipeConstructor<S> getEmptyConstructor() {
                return emptyRecipeConstructor;
            }


        };
        RRV_RECIPE_TYPES.put(id, type);
        return type;
    }

    /**
     *
     * @param id The id
     * @return The server recipe type by id
     */
    static ReliableServerRecipeType<?> byId(ResourceLocation id){
        return RRV_RECIPE_TYPES.getOrDefault(id, null);
    }

    /**
     *
     * @param recipeType The recipe type
     * @return The id of the type
     */
    static ResourceLocation idFromType(ReliableServerRecipeType<?> recipeType) {
        return recipeType.getId();
    }


    /**
     * Functional interface providing a construction method for a default server recipe instance
     * @param <T> The class of the recipe
     */
    interface EmptyRecipeConstructor<T extends ReliableServerRecipe> {

        T construct();

    }
}
