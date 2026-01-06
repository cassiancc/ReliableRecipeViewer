package cc.cassian.rrv.common.mixin.client.gui.screens.inventory;

import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(EffectsInInventory.class)
public abstract class MixinEffectsInInventory {


    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void injectBlocking$0(GuiGraphics guiGraphics, final Collection<MobEffectInstance> activeEffects, final int x0, final int yStep, final int mouseX, final int mouseY, final int maxWidth, CallbackInfo ci){

        List<Identifier> effectsToRemove = new ArrayList<>();
        for(BlockingGuiComponent guiBlock : OverlayManager.INSTANCE.allGuiBlockings()){

            if(!guiBlock.id().getPath().startsWith("mobeffect_"))
                continue;

            String descriptionId = guiBlock.id().getPath().split("_")[1];

            if(this.minecraft.player.getActiveEffects().stream().noneMatch(mobEffectInstance -> mobEffectInstance.getDescriptionId().equals(descriptionId)))
                effectsToRemove.add(guiBlock.id());

        }

        OverlayManager.INSTANCE.removeGuiBlocking(effectsToRemove::contains, !effectsToRemove.isEmpty());
    }

    @Inject(method = "renderBackground", at = @At("RETURN"))
    private void injectBlocking$1(final GuiGraphics graphics, final Font font, final Component effectName, final Component duration, final int x0, final int y0, final boolean isAmbient, final int maxTextureWidth, CallbackInfoReturnable<Integer> cir){

        if (effectName.getContents() instanceof TranslatableContents translatableContents) {
            int k = OverlayManager.INSTANCE.currentInfo().topPos();

            OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                    Identifier.withDefaultNamespace("mobeffect_" +  translatableContents.getKey()), x0, k, cir.getReturnValue(), 32
            ));
        }


    }

}
