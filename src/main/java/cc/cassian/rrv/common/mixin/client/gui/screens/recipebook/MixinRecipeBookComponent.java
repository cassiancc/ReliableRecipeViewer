package cc.cassian.rrv.common.mixin.client.gui.screens.recipebook;

import cc.cassian.rrv.common.overlay.BlockingGuiComponent;
import cc.cassian.rrv.common.overlay.OverlayManager;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
//? if <26.4 {
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class MixinRecipeBookComponent
    //? if >=26.4
        //extends AbstractContainerWidget
{

    //? if <26.4 {

    @Shadow
    public abstract int getXOrigin();

    @Shadow
    protected abstract int getYOrigin();


    @Shadow
    private boolean visible;

    @Shadow
    @Final
    private List<RecipeBookTabButton> tabButtons;
	//?} else {

    /*@Shadow
    protected abstract boolean isVisibleAccordingToBookData();

    public MixinRecipeBookComponent(int x, int y, int width, int height, Component message, ScrollbarSettings scrollbarSettings) {
        super(x, y, width, height, message, scrollbarSettings);
    }
    *///?}

    @Inject(method = "init", at = @At("TAIL"))
    private void injectBlocking$0(CallbackInfo ci) {
        this.setGuiBlocking();
    }

    @Inject(method = "toggleVisibility", at = @At("TAIL"))
    private void injectBlocking$1(CallbackInfo ci) {
        this.setGuiBlocking();
    }


    //We do not need to update the slots because the inventory image position also changes (this causes an update)

    @Unique
    private void setGuiBlocking() {


        //? if <26.4 {
        if (!this.visible) {
            OverlayManager.INSTANCE.removeExclusionArea(Identifier -> Identifier.getPath().startsWith("recipetabbutton_"), false);
            OverlayManager.INSTANCE.removeExclusionArea(Identifier.withDefaultNamespace("recipebook"), false);
            return;
        }

        //Width and height hardcoded
        OverlayManager.INSTANCE.setExclusionArea(new BlockingGuiComponent(
                Identifier.withDefaultNamespace("recipebook"),
                this.getXOrigin(),
                this.getYOrigin(),
                147,
                166
        ));
        //?} else {
        /*if (isVisibleAccordingToBookData())
            OverlayManager.INSTANCE.setExclusionArea(new BlockingGuiComponent(Identifier.withDefaultNamespace("recipe_book"),getRectangle()));
        else
            OverlayManager.INSTANCE.removeExclusionArea(Identifier.withDefaultNamespace("recipe_book"), true);
        *///?}


    }

    @Inject(method = "updateTabs", at = @At("TAIL"))
    private void injectBlocking$2(CallbackInfo ci) {

        //? if <26.4 {
        OverlayManager.INSTANCE.removeExclusionArea(Identifier -> Identifier.getPath().startsWith("recipetabbutton_"), false);


        for (int i = 0; i < this.tabButtons.size(); i++) {
            RecipeBookTabButton tabButton = this.tabButtons.get(i);

            if (tabButton.visible)
                OverlayManager.INSTANCE.setExclusionArea(new BlockingGuiComponent(
                        Identifier.withDefaultNamespace("recipetabbutton_" + i),
                        tabButton.getX(),
                        tabButton.getY(),
                        tabButton.getWidth(),
                        tabButton.getHeight()
                ));
        }
        //?} else {
        /*if (isVisibleAccordingToBookData())
            OverlayManager.INSTANCE.setExclusionArea(new BlockingGuiComponent(Identifier.withDefaultNamespace("recipe_book"),getRectangle()));
        else
            OverlayManager.INSTANCE.removeExclusionArea(Identifier.withDefaultNamespace("recipe_book"), true);
        *///?}
    }
}
