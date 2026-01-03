package de.crafty.eiv.common.mixin.client.multiplayer;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.EivNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class MixinClientCommonPacketListenerImpl {


    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    protected abstract void handleCustomPayload(CustomPacketPayload var1);

    @Inject(method = "handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V", at = @At("HEAD"), cancellable = true)
    private void onEivPayloadReceived(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {

        if (!EivNetworkManager.INSTANCE.getClientbound().containsKey(packet.payload().type().id()))
            return;

        CustomPacketPayload custompacketpayload = packet.payload();
        if (!(custompacketpayload instanceof DiscardedPayload)) {
            PacketUtils.ensureRunningOnSameThread(packet, ((ClientCommonPacketListenerImpl) (Object) this), this.minecraft.packetProcessor());
            this.handleCustomPayload(custompacketpayload);
        }

        ci.cancel();
    }

}
