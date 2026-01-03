package de.crafty.eiv.common.mixin.server.network;

import com.mojang.authlib.GameProfile;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.EivNetworkManager;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {

    
    @Shadow public ServerPlayer player;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onEivPayloadReceived(ServerboundCustomPayloadPacket packet, CallbackInfo ci){

        CustomPacketPayload payload = packet.payload();

        Identifier payloadId = payload.type().id();

        EivNetworkManager.INSTANCE.getServerbound().forEach((Identifier, typeAndCodec) -> {

            if (!payloadId.equals(Identifier))
                return;

            if (EivNetworkManager.INSTANCE.serverPayloadHandlers().containsKey(payloadId)){
                this.player.level().getServer().execute(() -> {
                    EivNetworkManager.INSTANCE.serverPayloadHandlers().get(payloadId).handle(new EivNetworkManager.ServerContext(this.player.level().getServer(), this.player), EivNetworkManager.INSTANCE.castPayload(payload));
                });
            }
            else
                CommonEIV.LOGGER.error("Cannot resolve payload handler for id: {}", payloadId);

            ci.cancel();
        });

    }

}
