package cc.cassian.rrv.common.mixin.client.gui.components;

import cc.cassian.rrv.common.overlay.itemlist.view.SearchBar;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget {

    public MixinEditBox(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void extractRenderStateFilterMode(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (((EditBox) (Object) this) instanceof SearchBar) {
            if (ItemViewOverlay.INSTANCE.isItemFilterMode()) {
                sprite = ReliableRecipeViewer.of("widget/searchbar_filtermode");
            }
            else if (ItemViewOverlay.INSTANCE.availableItems().isEmpty()) {
                sprite = ReliableRecipeViewer.of( "widget/searchbar_no_results");
            }
        }
        original.call(instance, pipeline, sprite, x, y, width, height);
    }

}
