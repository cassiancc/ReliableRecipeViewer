package de.crafty.eiv.common.mixin.client.gui.components;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.crafty.eiv.common.CommonEIV;
import de.crafty.eiv.common.overlay.itemlist.view.ItemViewOverlay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget {

    @Unique
    private static final Identifier FILTERMODE_LOCATION = Identifier.fromNamespaceAndPath(CommonEIV.MODID, "widget/searchbar_filtermode");

    public MixinEditBox(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
    private void renderFilterMode(GuiGraphics instance, RenderPipeline renderPipeline, Identifier Identifier, int i, int j, int k, int l) {


        if (this.getMessage().contains(Component.literal("eiv:searchbar")) && ItemViewOverlay.INSTANCE.isItemFilterMode()) {
            instance.blitSprite(renderPipeline, FILTERMODE_LOCATION, i, j, k, l);
        } else
            instance.blitSprite(renderPipeline, Identifier, i, j, k, l);
    }

}
