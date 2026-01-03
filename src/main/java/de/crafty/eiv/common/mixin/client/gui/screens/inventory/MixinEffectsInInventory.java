package de.crafty.eiv.common.mixin.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.crafty.eiv.common.overlay.BlockingGuiComponent;
import de.crafty.eiv.common.overlay.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mixin(EffectsInInventory.class)
public abstract class MixinEffectsInInventory {


    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private AbstractContainerScreen<?> screen;

    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void injectBlocking$0(GuiGraphics guiGraphics, Collection<MobEffectInstance> collection, int i, int j, int k, int l, int m, CallbackInfo ci){

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

    @Inject(method = "renderBackground", at = @At("HEAD"))
    private void injectBlocking$1(GuiGraphics guiGraphics, Font font, Component component, Component component2, int i, int j, boolean bl, int k2, CallbackInfoReturnable<Integer> cir){

        if (component.getContents() instanceof TranslatableContents translatableContents) {
            int k = OverlayManager.INSTANCE.currentInfo().topPos();

            if (bl) {
                OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                        Identifier.withDefaultNamespace("mobeffect_" + translatableContents.getKey()), i, k, 120, 32
                ));

            } else {
                OverlayManager.INSTANCE.setGuiBlocking(new BlockingGuiComponent(
                        Identifier.withDefaultNamespace("mobeffect_" +  translatableContents.getKey()), i, k, 32, 32
                ));

            }
        }


    }

}
