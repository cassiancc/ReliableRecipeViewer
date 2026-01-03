package de.crafty.eiv.common.mixin.network.protocol.common.custom;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.EivNetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/network/protocol/common/custom/CustomPacketPayload$1")
public abstract class MixinCustomPacketPayload {

    @Shadow
    protected abstract <B extends FriendlyByteBuf> StreamCodec<B, ? extends CustomPacketPayload> findCodec(Identifier $$0x);


    @Redirect(method = "decode(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$1;findCodec(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/network/codec/StreamCodec;"))
    private <B extends FriendlyByteBuf> StreamCodec<? super B, ? extends CustomPacketPayload> overwriteCodec$1(@Coerce StreamCodec<B, CustomPacketPayload> instance, Identifier $$0) {

        if (EivNetworkManager.INSTANCE.getClientbound().containsKey($$0)) {
            return (StreamCodec<? super B, ? extends CustomPacketPayload>) EivNetworkManager.INSTANCE.getClientbound().get($$0).codec();
        }

        if (EivNetworkManager.INSTANCE.getServerbound().containsKey($$0)) {
            return (StreamCodec<? super B, ? extends CustomPacketPayload>) EivNetworkManager.INSTANCE.getServerbound().get($$0).codec();
        }

        return this.findCodec($$0);
    }

    @Redirect(method = "writeCap", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$1;findCodec(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/network/codec/StreamCodec;"))
    private <B extends FriendlyByteBuf> StreamCodec<? super B, ? extends CustomPacketPayload> overwriteCodec$2(@Coerce StreamCodec<B, CustomPacketPayload> instance, Identifier $$0) {

        if (EivNetworkManager.INSTANCE.getClientbound().containsKey($$0)) {
            return (StreamCodec<? super B, ? extends CustomPacketPayload>) EivNetworkManager.INSTANCE.getClientbound().get($$0).codec();
        }

        if (EivNetworkManager.INSTANCE.getServerbound().containsKey($$0)) {
            return (StreamCodec<? super B, ? extends CustomPacketPayload>) EivNetworkManager.INSTANCE.getServerbound().get($$0).codec();
        }

        return this.findCodec($$0);
    }
}
