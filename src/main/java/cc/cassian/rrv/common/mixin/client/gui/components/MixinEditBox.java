package cc.cassian.rrv.common.mixin.client.gui.components;

import cc.cassian.rrv.common.config.widgets.IntegerEditBox;
import cc.cassian.rrv.common.overlay.itemlist.view.SearchBar;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? if >26.2 {
/*import com.mojang.renderpearl.api.pipeline.RenderPipeline;
*///?} else {
import com.mojang.blaze3d.pipeline.RenderPipeline;
//?}
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

    //~ if >26.2 '(Lcom/mojang/blaze3d/pipeline/RenderPipeline'->'(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline' {
    @WrapOperation(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void extractRenderStateFilterMode(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, Operation<Void> original) {
    //~}
        EditBox editBox = (EditBox) (Object) this;
        if (editBox instanceof SearchBar) {
            if (ItemViewOverlay.INSTANCE.isItemFilterMode()) {
                sprite = ReliableRecipeViewer.of("widget/searchbar_filtermode");
            }
            else if (ItemViewOverlay.INSTANCE.availableItems().isEmpty()) {
                sprite = ReliableRecipeViewer.of( "widget/searchbar_no_results");
            }
        } else if (editBox instanceof IntegerEditBox integerEditBox) {
            if (!integerEditBox.valid) {
                sprite = ReliableRecipeViewer.of( "widget/searchbar_no_results");
            }
        }
        original.call(instance, pipeline, sprite, x, y, width, height);
    }

}
