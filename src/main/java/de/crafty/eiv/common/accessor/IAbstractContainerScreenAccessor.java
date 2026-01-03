package de.crafty.eiv.common.accessor;


import net.minecraft.client.gui.components.events.GuiEventListener;

public interface IAbstractContainerScreenAccessor {


    void eiv$removeWidget(GuiEventListener guiEventListener);


    void eiv$addRenderableWidget(GuiEventListener guiEventListener);

    void eiv$addWidget(GuiEventListener guiEventListener);
}
