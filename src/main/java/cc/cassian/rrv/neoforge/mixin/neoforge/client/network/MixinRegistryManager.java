//? neoforge {
/*package cc.cassian.rrv.neoforge.mixin.neoforge.client.network;

import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.RegistrySnapshot;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(value = RegistryManager.class, remap = false)
public class MixinRegistryManager {


    @Inject(method = "applySnapshot(Ljava/util/Map;Z)Ljava/util/Set;", at = @At("HEAD"))
    private static void test(Map<Identifier, RegistrySnapshot> snapshots, boolean isLocalWorld, CallbackInfoReturnable<Set<ResourceKey<?>>> cir) {
        snapshots.forEach((location, snapshot) -> {
            if (!location.equals(Registries.ITEM.identifier()))
                return;

            BuiltInRegistries.FLUID.forEach(fluid -> {

                Item fluidItem = ItemViewRecipes.INSTANCE.itemForFluid(fluid);

                if (fluidItem == Items.AIR)
                    return;

                if (!snapshot.getIds().containsKey(Item.getId(fluidItem))) {
                    ((RegistrySnapshotAccessor) snapshot).accessIds().put(Item.getId(fluidItem), BuiltInRegistries.ITEM.getKey(fluidItem));
                }

            });
        });
    }
}
*///?}