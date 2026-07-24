package cc.cassian.rrv.common.mixin.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.library.load.PluginLoader;
import mezz.jei.library.plugins.vanilla.VanillaPlugin;
import mezz.jei.library.runtime.JeiHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({PluginLoader.class})
public class JeiPluginLoaderMixin {
	@Inject(method = "createRecipeCategories", at = @At(value = "HEAD"))
	private static void hideJeiButtons(List<IModPlugin> plugins, VanillaPlugin vanillaPlugin, JeiHelpers jeiHelpers, CallbackInfoReturnable<List<IRecipeCategory<?>>> cir) {
		cc.cassian.rrv.common.integration.jei.JeiHelpers.PLUGINS.addAll(plugins.stream().map(iModPlugin -> iModPlugin.getPluginUid().getNamespace()).toList());
	}
}
