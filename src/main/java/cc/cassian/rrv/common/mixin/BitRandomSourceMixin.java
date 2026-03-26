package cc.cassian.rrv.common.mixin;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.levelgen.BitRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BitRandomSource.class)
public interface BitRandomSourceMixin {

	@Inject(method = "nextInt()I", at = @At("HEAD"), cancellable = true)
	private void threading1(CallbackInfoReturnable<Integer> cir) {
		if (ServerRecipeManager.INSTANCE.isOffThread()) {
			ReliableRecipeViewer.LOGGER.debug("Suppressing multithreading crash from nextInt.");
			cir.setReturnValue(ReliableRecipeViewer.RANDOM.nextInt());
		}
	}

	@Inject(method = "nextInt(I)I", at = @At("HEAD"), cancellable = true)
	private void threading2(int bound, CallbackInfoReturnable<Integer> cir) {
		if (ServerRecipeManager.INSTANCE.isOffThread()) {
			ReliableRecipeViewer.LOGGER.debug("Suppressing multithreading crash from bounded nextInt.");
			cir.setReturnValue(ReliableRecipeViewer.RANDOM.nextInt(bound));
		}
	}

	@Inject(method = "nextFloat", at = @At("HEAD"), cancellable = true)
	private void threading3(CallbackInfoReturnable<Float> cir) {
		if (ServerRecipeManager.INSTANCE.isOffThread()) {
			ReliableRecipeViewer.LOGGER.debug("Suppressing multithreading crash from nextFloat.");
			cir.setReturnValue(ReliableRecipeViewer.RANDOM.nextFloat());
		}
	}
}
