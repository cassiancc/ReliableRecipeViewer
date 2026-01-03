package de.crafty.eiv.common.overlay;

import net.minecraft.resources.Identifier;

import java.awt.*;

/**
 *
 * @param id     The id of the gui component (later used for in- and excluding the components in different overlays)
 * @param x      The current x-position in the screen
 * @param y      The current y-position in the screen
 * @param width  The current width of the component
 * @param height The current height of the component
 */
public record BlockingGuiComponent(Identifier id, int x, int y, int width, int height) {


    public boolean hasIntersectionWith(int x, int y, int width, int height) {

        Rectangle rect = new Rectangle(this.x, this.y, this.width, this.height);
        Rectangle compared = new Rectangle(x, y, width, height);

        return rect.intersects(compared);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlockingGuiComponent(Identifier id1, int x1, int y1, int width1, int height1) &&
                id1.equals(this.id)
                && x1 == this.x
                && y1 == this.y
                && width1 == this.width
                && height1 == this.height;
    }
}
