//? neoforge {
/*package de.crafty.eiv.neoforge.mixin.neoforge.network.registration;

import de.crafty.eiv.common.network.EivNetworkManager;
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
    private static void excludeClientboundEivPackets(Packet<?> packet, ServerCommonPacketListener listener, CallbackInfo ci) {
        //When the payload id is present in the EivNetworkManager, exclude it
        if (packet instanceof ClientboundCustomPayloadPacket payloadPacket && EivNetworkManager.INSTANCE.getClientbound().containsKey(payloadPacket.payload().type().id()))
            ci.cancel();
    }

    @Inject(remap = false, method = "checkPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/protocol/common/ClientCommonPacketListener;)V", at = @At("HEAD"), cancellable = true)
    private static void excludeServerboundEivPackets(Packet<?> packet, ClientCommonPacketListener listener, CallbackInfo ci) {
        //When the payload id is present in the EivNetworkManager, exclude it
        if (packet instanceof ServerboundCustomPayloadPacket payloadPacket && EivNetworkManager.INSTANCE.getServerbound().containsKey(payloadPacket.payload().type().id()))
            ci.cancel();
    }

}
*///?}