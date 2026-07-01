package cc.cassian.rrv.common.gui.list;

import cc.cassian.rrv.common.ReliableRecipeViewer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class Switch extends AbstractWidget {
    private static final Identifier UNCHECKED = ReliableRecipeViewer.of("widget/switch/unchecked");
    private static final Identifier UNCHECKED_HOVERED = ReliableRecipeViewer.of("widget/switch/unchecked_hovered");
    private static final Identifier UNCHECKED_FOCUSED = ReliableRecipeViewer.of("widget/switch/unchecked_focused");
    private static final Identifier UNCHECKED_DISABLED = ReliableRecipeViewer.of("widget/switch/unchecked_disabled");

    private static final Identifier CHECKED = ReliableRecipeViewer.of("widget/switch/checked");
    private static final Identifier CHECKED_HOVERED = ReliableRecipeViewer.of("widget/switch/checked_hovered");
    private static final Identifier CHECKED_FOCUSED = ReliableRecipeViewer.of("widget/switch/checked_focused");
    private static final Identifier CHECKED_DISABLED = ReliableRecipeViewer.of("widget/switch/checked_disabled");

    private boolean isChecked;
    private OnCheckedChangeListener onCheckedChangeListener = (_, _) -> {
    };

    private Switch(int x, int y, Component message, boolean isChecked) {
        super(x, y, 29, 17, message);
        this.isChecked = isChecked;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (!active || !visible) return false;
        double mx = event.x();
        double my = event.y();
        if (event.button() == 0 && mx >= getX() && mx < getX() + width && my >= getY() && my < getY() + height) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            isChecked = !isChecked;
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
            return true;
        }
        return false;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, createNarrationMessage());
        if (active) {
            var component = Component.translatable(isFocused() ? "narration.switch.usage.focused" : "narration.switch.usage.hovered");
            output.add(NarratedElementType.USAGE, component);
        }
    }

    private Identifier getSprite() {
        if (!this.active) {
            return this.isChecked ? CHECKED_DISABLED : UNCHECKED_DISABLED;
        } else if (this.isHovered()) {
            return this.isChecked ? CHECKED_HOVERED : UNCHECKED_HOVERED;
        } else if (this.isFocused()) {
            return this.isChecked ? CHECKED_FOCUSED : UNCHECKED_FOCUSED;
        } else {
            return this.isChecked ? CHECKED : UNCHECKED;
        }
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, getSprite(), getX() - 1, getY() - 1, getX() - 1 + width, getY() - 1 + height, -1);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    @FunctionalInterface
    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch sw, boolean isChecked);
    }

    public static class Builder {
        private final Component message;
        private boolean isChecked = false;
        private OnCheckedChangeListener onCheckedChangeListener = (_, _) -> {
        };

        public Builder(Component message) {
            this.message = message;
        }

        public Builder setChecked(boolean checked) {
            this.isChecked = checked;
            return this;
        }

        public Builder onCheckedChangeListener(OnCheckedChangeListener listener) {
            this.onCheckedChangeListener = listener;
            return this;
        }

        public Switch build() {
            int x = 0;
            int y = 0;
            Switch sw = new Switch(x, y, message, isChecked);
            sw.onCheckedChangeListener = this.onCheckedChangeListener;
            return sw;
        }
    }
}