package de.crafty.eiv.common.api.recipe;

import net.minecraft.resources.Identifier;

import java.util.HashMap;

/**
 * Representation of a server-side recipe type
 * @param <T> The class of the server recipe
 */
public interface EivRecipeType<T extends IEivServerRecipe> {

    HashMap<Identifier, EivRecipeType<?>> EIV_RECIPE_TYPES = new HashMap<>();


    /**
     *
     * @return A unique id for the recipe type used in network communication
     */
    Identifier getId();

    /**
     *
     * @return A method that constructs a default instance of the server recipe which is later updated with network data on load in {@link IEivServerRecipe}
     */
    EmptyRecipeConstructor<T> getEmptyConstructor();


    /**
     *
     * @param id A unique id
     * @param emptyRecipeConstructor A method creating a default instance of the server recipe
     * @return The recipe type
     * @param <S> The server recipe class
     */
    static <S extends IEivServerRecipe> EivRecipeType<S> register(Identifier id, EmptyRecipeConstructor<S> emptyRecipeConstructor) {

        EivRecipeType<S> type = new EivRecipeType<S>() {
            @Override
            public Identifier getId() {
                return id;
            }

            @Override
            public EmptyRecipeConstructor<S> getEmptyConstructor() {
                return emptyRecipeConstructor;
            }


        };
        EIV_RECIPE_TYPES.put(id, type);
        return type;
    }

    /**
     *
     * @param id The id
     * @return The server recipe type by id
     */
    static EivRecipeType<?> byId(Identifier id){
        return EIV_RECIPE_TYPES.getOrDefault(id, null);
    }

    /**
     *
     * @param recipeType The recipe type
     * @return The id of the type
     */
    static Identifier idFromType(EivRecipeType<?> recipeType) {
        return recipeType.getId();
    }


    /**
     * Functional interface providing a construction method for a default server recipe instance
     * @param <T> The class of the recipe
     */
    interface EmptyRecipeConstructor<T extends IEivServerRecipe> {

        T construct();

    }
}
