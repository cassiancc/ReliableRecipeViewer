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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget {

    public MixinEditBox(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    @SuppressWarnings("all")
    private void renderFilterMode(GuiGraphics instance, Function<ResourceLocation, RenderType> pipeline, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (((Object) this) instanceof SearchBar && ItemViewOverlay.INSTANCE.isItemFilterMode()) {
            original.call(pipeline, ResourceLocation.fromNamespaceAndPath(ReliableRecipeViewer.MOD_ID, "widget/searchbar_filtermode"), x, y, width, height);
        } else
            original.call(instance, pipeline, sprite, x, y, width, height);
    }

}
