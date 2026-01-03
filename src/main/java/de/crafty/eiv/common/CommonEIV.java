package de.crafty.eiv.common;

import de.crafty.eiv.common.recipe.ItemViewRecipes;
import de.crafty.eiv.common.network.EivNetworkManager;
import de.crafty.eiv.common.recipe.item.FluidItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CommonEIV {

    public static final String MODID = "eiv";

    public static final Logger LOGGER = LoggerFactory.getLogger("Extended ItemView");

    public static final String CONFIG_PATH = "config/eiv/";

    public static EivNetworkManager networkManager(){
        return EivNetworkManager.INSTANCE;
    }

}
