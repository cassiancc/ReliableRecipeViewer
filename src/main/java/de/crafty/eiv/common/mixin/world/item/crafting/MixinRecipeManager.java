package de.crafty.eiv.common.mixin.world.item.crafting;

import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class MixinRecipeManager extends SimplePreparableReloadListener<RecipeMap> implements RecipeAccess {


    @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void afterReload(RecipeMap recipeMap, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci) {
        if(!((RecipeManager) (Object) this).equals(ServerRecipeManager.INSTANCE.getVanillaRecipeManager()))
            ServerRecipeManager.INSTANCE.setRecipeManager((RecipeManager) (Object) this);


        ServerRecipeManager.INSTANCE.reload();
    }
}
