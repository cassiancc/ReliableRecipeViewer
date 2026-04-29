package cc.cassian.rrv.client.recipe;


import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.Platform;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientRecipeManager {

	private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecipeManager");

	public static final ClientRecipeManager INSTANCE = new ClientRecipeManager();

	public InternalRecipeManager.Status status() {
		return InternalRecipeManager.INSTANCE.status();
	}

	public RegistryOps<Tag> createSerializationContext() {
		return createSerializationContext(NbtOps.INSTANCE);
	}

	public <T> RegistryOps<T> createSerializationContext(final DynamicOps<T> parent) {
		return registryAccess().createSerializationContext(parent);
	}

	public RegistryAccess registryAccess() {
		ClientLevel level = Minecraft.getInstance().level;
		return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
	}

	/// This method can be used to retrieve recipe synchronized via [ServerRecipeManager#synchronizeRecipeType], which uses the Fabric/NeoForge recipe synchronization APIs.
	///
	/// ```java
	/// public class ExampleModClientIntegration implements ReliableRecipeViewerClientPlugin {
	///     @Override
	///     public void onIntegrationInitialize() {
	///         ItemView.addClientRecipeProvider(recipeList -> {
	///             ClientRecipeManager.getRecipesForType(ExampleModRecipes.UPGRADING_RECIPE_TYPE).forEach(upgradingRecipeHolder -> {
	///                 recipeList.add(new UpgradingClientRecipe(upgradingRecipeHolder.value()));
	///             });
	///           });
	///         }
	///     }
	/// }
	/// ```
    public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> getRecipesForType(RecipeType<T> type) {
        return ReliableRecipeViewerClient.LOCAL_RECIPES.byType(type);
    }
}
