//? neoforge {
/*package cc.cassian.rrv.neoforge.mixin.neoforge.network.registration;

import cc.cassian.rrv.common.network.RrvNetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkRegistry.class, remap = false)
public abstract class MixinNetworkRegistry {

    @Inject(remap = false, method = "checkPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ServerCommonPacketListener;)V", at = @At("HEAD"), cancellable = true)
    private static void excludeClientboundRrvPackets(Packet<?> packet, ServerCommonPacketListener listener, CallbackInfo ci) {
        //When the payload id is present in the RrvNetworkManager, exclude it
        if (packet instanceof ClientboundCustomPayloadPacket payloadPacket && RrvNetworkManager.INSTANCE.getClientbound().containsKey(payloadPacket.payload().type().id()))
            ci.cancel();
    }

    @Inject(remap = false, method = "checkPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)V", at = @At("HEAD"), cancellable = true)
    private static void excludeServerboundRrvPackets(Packet<?> packet, ClientCommonPacketListener listener, CallbackInfo ci) {
        //When the payload id is present in the RrvNetworkManager, exclude it
        if (packet instanceof ServerboundCustomPayloadPacket payloadPacket && RrvNetworkManager.INSTANCE.getServerbound().containsKey(payloadPacket.payload().type().id()))
            ci.cancel();
    }

}
*///?}