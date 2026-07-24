package cc.cassian.rrv.common.mixin.integration;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(RecipesGui.class)
public class JeiRecipesGuiMixin {
	@ModifyArg(method = "onClose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
	private @Nullable Screen modifyParentScreen(@Nullable Screen screen) {
		if (screen instanceof RecipeViewScreen recipeViewScreen) {
			return (recipeViewScreen.getMenu().getParentScreen());
		}
		return screen;
	}
}
