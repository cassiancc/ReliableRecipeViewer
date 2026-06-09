package cc.cassian.rrv.common.config.instances;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.config.AbstractRrvConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ServerConfig extends AbstractRrvConfig {

	public ServerConfig() {
		super("server_settings");
	}

	private int version = 1;
	private boolean recipeSharing = true;

	//? fabric {
	private List<Identifier> synchronizedRecipeSerializers = addVanillaRecipeSerializers();

	private List<Identifier> addVanillaRecipeSerializers() {
		var list = Stream.of(
				ShapedRecipe.SERIALIZER,
				ShapelessRecipe.SERIALIZER,
				DyeRecipe.SERIALIZER,
				ImbueRecipe.SERIALIZER,
				TransmuteRecipe.SERIALIZER,
				DecoratedPotRecipe.SERIALIZER,
				BookCloningRecipe.SERIALIZER,
				MapExtendingRecipe.SERIALIZER,
				FireworkRocketRecipe.SERIALIZER,
				FireworkStarRecipe.SERIALIZER, // TODO
				FireworkStarFadeRecipe.SERIALIZER, // TODO
				BannerDuplicateRecipe.SERIALIZER, // TODO
				ShieldDecorationRecipe.SERIALIZER,
				RepairItemRecipe.SERIALIZER,
				SmeltingRecipe.SERIALIZER,
				BlastingRecipe.SERIALIZER,
				CampfireCookingRecipe.SERIALIZER,
				SmokingRecipe.SERIALIZER,
				StonecutterRecipe.SERIALIZER,
				SmithingTrimRecipe.SERIALIZER,
				SmithingTransformRecipe.SERIALIZER
		).map(BuiltInRegistries.RECIPE_SERIALIZER::getKey).toList();
		System.out.println(list);
		return list;
	}
	public void addRecipeSerializer(Identifier newSerializer) {
		var list = new ArrayList<>(synchronizedRecipeSerializers);
		list.add(newSerializer);
		synchronizedRecipeSerializers = list;
	}

	//?} else {
	/*private List<Identifier> synchronizedRecipeTypes = addVanillaRecipeTypes();

	private List<Identifier> addVanillaRecipeTypes() {
		return Stream.of(RecipeType.CRAFTING, RecipeType.SMELTING, RecipeType.BLASTING, RecipeType.CAMPFIRE_COOKING, RecipeType.SMOKING, RecipeType.SMITHING).map(BuiltInRegistries.RECIPE_TYPE::getKey).toList();
	}

	public void addRecipeType(Identifier newType) {
		var list = new ArrayList<>(synchronizedRecipeTypes);
		list.add(newType);
		synchronizedRecipeTypes = list;
	}


	*///?}

	public boolean isRecipeSharing() {
		return recipeSharing;
	}

	public void setRecipeSharing(boolean recipeSharing) {
		this.recipeSharing = recipeSharing;
	}

	//? fabric {
	public List<Identifier> getSynchronizedRecipeSerializers() {
		return synchronizedRecipeSerializers;
	}

	public void setSynchronizedRecipeSerializers(List<Identifier> synchronizedRecipeSerializers) {
		this.synchronizedRecipeSerializers = synchronizedRecipeSerializers;
	}
	//?} else {
	/*public List<Identifier> getSynchronizedRecipeTypes() {
		return synchronizedRecipeTypes;
	}

	public void setSynchronizedRecipeTypes(List<Identifier> synchronizedRecipeTypes) {
		this.synchronizedRecipeTypes = synchronizedRecipeTypes;
	}
	*///?}

	@Override
	protected void loadData() {
		int newVersion = load("config_version_do_not_touch", this.version);
		if (newVersion == this.version) {
			this.recipeSharing = load("recipeSharing", this.recipeSharing);
			//? fabric
			this.synchronizedRecipeSerializers = load("synchronizedRecipeSerializers", this.synchronizedRecipeSerializers, Identifier.CODEC.listOf());
			//? neoforge
			//this.synchronizedRecipeTypes = load("synchronizedRecipeTypes", this.synchronizedRecipeTypes, Identifier.CODEC.listOf());
		} else {
			ReliableRecipeViewer.LOGGER.error("Failed to read server config! It claimed to be version {}, when the correct version is {}.", newVersion, this.version);
		}
	}

	@Override
	protected void saveData() {
		save("config_version_do_not_touch", this.version);
		save("recipeSharing", this.recipeSharing);
		//? fabric
		save("synchronizedRecipeSerializers", synchronizedRecipeSerializers, Identifier.CODEC.listOf());
		//? neoforge
		//save("synchronizedRecipeTypes", synchronizedRecipeTypes, Identifier.CODEC.listOf());
	}
}
