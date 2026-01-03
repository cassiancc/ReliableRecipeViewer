package de.crafty.eiv.common.mixin.client.multiplayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.recipe.ClientRecipeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {


    @Shadow public CommandDispatcher<SharedSuggestionProvider> commands;

    @Shadow @Final private static Logger LOGGER;

    protected MixinClientPacketListener(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void requestRecipes(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        ClientRecipeManager.INSTANCE.requestServerEivData();
        ItemView.getClientReloadCallbacks().forEach(ItemView.ReloadCallback::onReload);
    }


    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onEivPayloadReceived(CustomPacketPayload payload, CallbackInfo ci) {
        Identifier payloadId = payload.type().id();

        EivNetworkManager.INSTANCE.getClientbound().forEach((Identifier, typeAndCodec) -> {

            if (!payloadId.equals(Identifier))
                return;

            if (EivNetworkManager.INSTANCE.clientPayloadHandlers().containsKey(payloadId)) {
                EivNetworkManager.INSTANCE.clientPayloadHandlers().get(payloadId).handle(new EivNetworkManager.ClientContext(this.minecraft), EivNetworkManager.INSTANCE.castPayload(payload));
            } else
                CommonEIV.LOGGER.error("Cannot resolve payload handler for id: {}", payloadId);

            ci.cancel();
        });

    }
}
