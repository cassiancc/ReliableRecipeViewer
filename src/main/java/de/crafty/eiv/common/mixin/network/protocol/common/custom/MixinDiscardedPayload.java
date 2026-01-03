package de.crafty.eiv.common.mixin.network.protocol.common.custom;

import de.crafty.eiv.common.CommonEIV;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiscardedPayload.class)
public abstract class MixinDiscardedPayload implements CustomPacketPayload {


    @Inject(method = "codec", at = @At("HEAD"))
    private static <T extends FriendlyByteBuf> void injectCompat(Identifier Identifier, int maxPayloadSize, CallbackInfoReturnable<StreamCodec<T, DiscardedPayload>> cir){
        CommonEIV.LOGGER.info("Injecting DiscardedPayload codec for {}", Identifier);
    }

}
