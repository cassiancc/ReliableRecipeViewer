//? fabric {
package cc.cassian.rrv.fabric;

import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.common.command.RrvCommand;
import cc.cassian.rrv.common.network.RrvNetworkManager;
import cc.cassian.rrv.common.recipe.ItemViewRecipes;
import cc.cassian.rrv.common.recipe.item.FluidItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.HashMap;

import static cc.cassian.rrv.common.ReliableRecipeViewer.*;

public class FabricEntrypoint implements ModInitializer {


    @Override
    public void onInitialize() {
        LOGGER.info("Hello Minecraft!");

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> RrvCommand.register(commandDispatcher));

        FabricLoader.getInstance().invokeEntrypoints("rrv", ReliableRecipeViewerPlugin.class, ReliableRecipeViewerPlugin::onIntegrationInitialize);

        RrvNetworkManager.INSTANCE.registerPayloads();
    }


    public static void buildFluidItems() {

        //Add FluidItems
        HashMap<Fluid, Item> fluidItemMap = new HashMap<>();

        BuiltInRegistries.FLUID.forEach(fluid -> {

            if (fluid == Fluids.EMPTY)
                return;

            if(BuiltInRegistries.ITEM.containsKey(BuiltInRegistries.FLUID.getKey(fluid))){
                fluidItemMap.put(fluid, BuiltInRegistries.ITEM.getValue(BuiltInRegistries.FLUID.getKey(fluid)));
                return;
            }

            if (!fluid.isSource(fluid.defaultFluidState()))
                return;

            Identifier itemLocation = BuiltInRegistries.FLUID.getKey(fluid);
            Item item = Registry.register(
                    BuiltInRegistries.ITEM,
                    itemLocation,
                    new FluidItem(fluid.defaultFluidState().createLegacyBlock().getBlock(),
                            new FluidItem.FluidItemProperties()
                                    .fluid(fluid)
                                    .setItemId(ResourceKey.create(Registries.ITEM, itemLocation))
                    ));

            fluidItemMap.put(fluid, item);
        });

        ItemViewRecipes.INSTANCE.setFluidItemMap(fluidItemMap);
    }

}
//?}