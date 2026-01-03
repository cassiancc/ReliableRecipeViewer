//? fabric {
package de.crafty.eiv.fabric.mixin.core.registries;

import de.crafty.eiv.fabric.FabricEIV;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class MixinBuiltInRegistries {


    @Inject(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/registries/BuiltInRegistries;createContents()V", shift = At.Shift.AFTER))
    private static void injectFluidItems(CallbackInfo ci) {
        FabricEIV.buildFluidItems();
    }
}
//?}