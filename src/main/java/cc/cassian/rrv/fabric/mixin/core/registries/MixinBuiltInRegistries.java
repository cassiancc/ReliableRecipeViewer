//? fabric {
package cc.cassian.rrv.fabric.mixin.core.registries;

import cc.cassian.rrv.fabric.FabricRRV;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class MixinBuiltInRegistries {


    @Inject(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/registries/BuiltInRegistries;createContents()V", shift = At.Shift.AFTER))
    private static void injectFluidItems(CallbackInfo ci) {
        FabricRRV.buildFluidItems();
    }
}
//?}