package cc.cassian.rrv.common.mixin.client.gui.components;

import cc.cassian.rrv.common.overlay.itemlist.view.SearchBar;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import cc.cassian.rrv.common.ReliableRecipeViewer;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget {

    public MixinEditBox(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    @SuppressWarnings("all")
    private void renderFilterMode(GuiGraphics instance, RenderPipeline pipeline, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (((Object) this) instanceof SearchBar && ItemViewOverlay.INSTANCE.isItemFilterMode()) {
            instance.blitSprite(pipeline, ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "widget/searchbar_filtermode"), x, y, width, height);
        } else
            original.call(instance, pipeline, sprite, x, y, width, height);
    }

}
