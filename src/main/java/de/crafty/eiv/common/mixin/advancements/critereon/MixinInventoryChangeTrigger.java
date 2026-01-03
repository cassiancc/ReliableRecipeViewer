package de.crafty.eiv.common.mixin.advancements.critereon;

import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.network.payload.transfer.ClientboundUpdateTransferCachePayload;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryChangeTrigger.class)
public abstract class MixinInventoryChangeTrigger extends SimpleCriterionTrigger<InventoryChangeTrigger.TriggerInstance> {


    @Inject(method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private void onInventoryChange(ServerPlayer serverPlayer, Inventory inventory, ItemStack itemStack, CallbackInfo ci){
        CommonEIV.networkManager().sendPacket(serverPlayer, new ClientboundUpdateTransferCachePayload());
    }

}
