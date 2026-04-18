package cc.cassian.rrv.common.mixin.stats;

import cc.cassian.rrv.common.network.payload.recipe.ClientboundUnlockedRecipesPayload;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ServerRecipeBook.class)
public class ServerPlayerMixin {
    @Shadow
    @Final
    protected Set<ResourceKey<Recipe<?>>> known;

    @Inject(method = "addRecipes", at = @At("HEAD"))
    private void sendUpdates(Collection<RecipeHolder<?>> recipes, ServerPlayer player, CallbackInfoReturnable<Integer> cir) {
        ServerPlayNetworking.send(player, new ClientboundUnlockedRecipesPayload(recipes.stream().map(RecipeHolder::id).map(ResourceKey::identifier).toList()));
    }

    @Inject(method = "sendInitialRecipeBook", at = @At("HEAD"))
    private void sendInitial(ServerPlayer player, CallbackInfo ci) {
        ServerPlayNetworking.send(player, new ClientboundUnlockedRecipesPayload(known.stream().map(ResourceKey::identifier).toList()));
    }
}
