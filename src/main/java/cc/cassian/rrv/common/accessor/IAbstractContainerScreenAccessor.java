package cc.cassian.rrv.common.accessor;


import net.minecraft.client.gui.components.events.GuiEventListener;

public interface IAbstractContainerScreenAccessor {


    void rrv$removeWidget(GuiEventListener guiEventListener);


    void rrv$addRenderableWidget(GuiEventListener guiEventListener);

    void rrv$addWidget(GuiEventListener guiEventListener);
}
