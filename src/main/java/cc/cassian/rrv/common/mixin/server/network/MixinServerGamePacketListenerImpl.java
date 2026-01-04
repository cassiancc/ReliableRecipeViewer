package cc.cassian.rrv.common.mixin.server.network;

import cc.cassian.rrv.common.CommonRRV;
import cc.cassian.rrv.common.network.RrvNetworkManager;
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
    private void onRrvPayloadRecrrved(ServerboundCustomPayloadPacket packet, CallbackInfo ci){

        CustomPacketPayload payload = packet.payload();

        Identifier payloadId = payload.type().id();

        RrvNetworkManager.INSTANCE.getServerbound().forEach((Identifier, typeAndCodec) -> {

            if (!payloadId.equals(Identifier))
                return;

            if (RrvNetworkManager.INSTANCE.serverPayloadHandlers().containsKey(payloadId)){
                this.player.level().getServer().execute(() -> {
                    RrvNetworkManager.INSTANCE.serverPayloadHandlers().get(payloadId).handle(new RrvNetworkManager.ServerContext(this.player.level().getServer(), this.player), RrvNetworkManager.INSTANCE.castPayload(payload));
                });
            }
            else
                CommonRRV.LOGGER.error("Cannot resolve payload handler for id: {}", payloadId);

            ci.cancel();
        });

    }

}
