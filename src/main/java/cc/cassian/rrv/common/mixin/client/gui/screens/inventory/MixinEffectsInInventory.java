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
import net.minecraft.resources.ResourceLocation;
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

    //? if >1.21.10 {
    /*@Inject(method = "renderEffects", at = @At("HEAD"))
    private void injectBlocking$0(GuiGraphics guiGraphics, final Collection<MobEffectInstance> activeEffects, final int x0, final int yStep, final int mouseX, final int mouseY, final int maxWidth, CallbackInfo ci){

        List<ResourceLocation> effectsToRemove = new ArrayList<>();
        for(BlockingGuiComponent guiBlock : OverlayManager.INSTANCE.allGuiBlockings()){

            if(!guiBlock.id().getPath().startsWith("mobeffect_"))
                continue;

            String descriptionId = guiBlock.id().getPath().split("mobeffect_")[1];

            if(this.minecraft.player.getActiveEffects().stream().noneMatch(mobEffectInstance -> mobEffectInstance.getDescriptionId().equals(descriptionId)))
                effectsToRemove.add(guiBlock.id());

        }

        OverlayManager.INSTANCE.removeGuiBlocking(effectsToRemove::contains, !effectsToRemove.isEmpty());
    }

    @Inject(method = "renderBackground", at = @At("RETURN"))
    private void injectBlocking$1(final GuiGraphics graphics, final Font font, final Component effectName, final Component duration, final int x0, final int y0, final boolean isAmbient, final int maxTextureWidth, CallbackInfoReturnable<Integer> cir){

        if (effectName.getContents() instanceof TranslatableContents translatableContents) {
            OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                    ResourceLocation.withDefaultNamespace("mobeffect_" +  translatableContents.getKey()), x0, y0, cir.getReturnValue(), 32
            ));
        }


    }
    *///?} else {
    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void injectBlocking$0(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci){

        List<ResourceLocation> effectsToRemove = new ArrayList<>();
        for(BlockingGuiComponent guiBlock : OverlayManager.INSTANCE.allGuiBlockings()){

            if(!guiBlock.id().getPath().startsWith("mobeffect_"))
                continue;

            String descriptionId = guiBlock.id().getPath().split("mobeffect_")[1];

            if(this.minecraft.player.getActiveEffects().stream().noneMatch(mobEffectInstance -> mobEffectInstance.getDescriptionId().equals(descriptionId)))
                effectsToRemove.add(guiBlock.id());

        }

        OverlayManager.INSTANCE.removeGuiBlocking(effectsToRemove::contains, !effectsToRemove.isEmpty());
    }
    @Inject(method = "renderBackgrounds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void injectBlocking$1(GuiGraphics guiGraphics, int x, int y, Iterable<MobEffectInstance> iterable, boolean large, CallbackInfo ci){

        int k = OverlayManager.INSTANCE.currentInfo().topPos();

        for (MobEffectInstance mobEffectInstance : iterable) {
            if (large) {
                OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                        ResourceLocation.withDefaultNamespace("mobeffect_" + mobEffectInstance.getDescriptionId()), x, k, 120, 32
                ));

            } else {
                OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                        ResourceLocation.withDefaultNamespace("mobeffect_" + mobEffectInstance.getDescriptionId()), x, k, 32, 32
                ));

            }
            k += y;
        }


    }
    //?}



}
