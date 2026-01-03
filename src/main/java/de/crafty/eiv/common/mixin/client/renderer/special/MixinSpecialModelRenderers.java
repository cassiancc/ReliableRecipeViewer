package de.crafty.eiv.common.mixin.client.renderer.special;

import com.mojang.serialization.MapCodec;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.extra.FluidItemSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelRenderers.class)
public abstract class MixinSpecialModelRenderers {

    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void injectFluidItemRenderer(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.fromNamespaceAndPath(CommonEIV.MODID, "fluiditem"), FluidItemSpecialRenderer.Unbaked.MAP_CODEC);
    }
}
