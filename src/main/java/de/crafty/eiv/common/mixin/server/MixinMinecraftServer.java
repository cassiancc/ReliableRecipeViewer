package de.crafty.eiv.common.mixin.server;

import de.crafty.eiv.common.recipe.ServerRecipeManager;
import net.minecraft.server.*;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {


    @Inject(method = "setPlayerList", at = @At("RETURN"))
    private void setServer(PlayerList $$0, CallbackInfo ci) {
        ServerRecipeManager.INSTANCE.setServer((MinecraftServer) (Object) this);
    }
}
