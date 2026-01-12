package cc.cassian.rrv.common.mixin.client.renderer.item;

import cc.cassian.rrv.common.recipe.item.FluidItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelWrapper.class)
public abstract class MixinSpecialModelWrapper {


    @Inject(method = "update", at = @At("HEAD"))
    private void makeFluidItemsAnimated(ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext displayContext, ClientLevel level, LivingEntity entity, int seed, CallbackInfo ci){
        if(itemStack.getItem() instanceof FluidItem)
            itemStackRenderState.setAnimated();
    }
}
