package cc.cassian.rrv.common.mixin.client.gui.screens;

import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.client.sharing.RecipeSharing;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.overlay.OverlayManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractContainerEventHandler implements Renderable {


    @Final
	@Shadow protected Font font;

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void extractRenderStateRecipeProgress(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci){
        String statusMsg = ClientRecipeManager.INSTANCE.status().get();

        if(!ClientRecipeManager.INSTANCE.status().isIdle()) {
			guiGraphics.text(this.font, statusMsg, guiGraphics.guiWidth() - this.font.width(statusMsg) - 2, 2, -1);
		}
    }

	@Inject(method = "extractBackground", at = @At("RETURN"))
	private void injectOverlayBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		if ((Screen) (Object) this instanceof AbstractContainerScreen<?>)
			OverlayManager.INSTANCE.renderAllBackground(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Inject(method = "defaultHandleGameClickEvent", at = @At("HEAD"), cancellable = true)
	private static void click(ClickEvent event, Minecraft minecraft, Screen activeScreen, CallbackInfo ci) {
		if (event instanceof ClickEvent.Custom(
				Identifier id, Optional<Tag> payload
		) && id.equals(ReliableRecipeViewer.of("click_recipe"))) {
			RecipeSharing.clickRecipe(Identifier.parse(payload.flatMap(Tag::asString).orElseThrow()));
			ci.cancel();
		}
	}
}
