//? neoforge {
/*package cc.cassian.rrv.neoforge.mixin.neoforge.registries;

import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.registries.GameData;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;

@Mixin(GameData.class)
public class MixinGameData {


    @Shadow @Final private static Logger LOGGER;

    @WrapOperation(method = "postRegisterEvents", at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModLoader;postEventWrapContainerInModOrder(Lnet/neoforged/bus/api/Event;)V"))
    private static <T extends Event & IModBusEvent> void injectFluidItems(T e, Operation<Void> original){
        original.call(e);

        RegisterEvent event = (RegisterEvent) e;
        if(!event.getRegistryKey().identifier().equals(Registries.ITEM.identifier()))
            return;

        HashMap<Fluid, Item> fluidItemMap = new HashMap<>();

        BuiltInRegistries.FLUID.forEach(fluid -> {

            if (fluid == Fluids.EMPTY || !fluid.isSource(fluid.defaultFluidState()))
                return;

            Identifier fluidLocation = BuiltInRegistries.FLUID.getKey(fluid);

            if(event.getRegistry().containsKey(fluidLocation)){
                fluidItemMap.put(fluid, (Item) event.getRegistry().getValue(fluidLocation));
                return;
            }

            event.register(Registries.ITEM, itemRegisterHelper -> {
                itemRegisterHelper.register(fluidLocation, new FluidItem(fluid.defaultFluidState().createLegacyBlock().getBlock(),
                        new FluidItem.FluidItemProperties()
                                .fluid(fluid)
                                .setItemId(ResourceKey.create(Registries.ITEM, fluidLocation))
                ));
            });
        });

        ItemViewRecipes.INSTANCE.setFluidItemMap(fluidItemMap);

    }

}
*///?}
