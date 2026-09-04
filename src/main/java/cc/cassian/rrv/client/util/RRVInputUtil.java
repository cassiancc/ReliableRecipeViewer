package cc.cassian.rrv.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RRVInputUtil {

	public static boolean isLeftClick(MouseButtonEvent mouseButtonEvent) {
		return mouseButtonEvent.button() == InputConstants.MOUSE_BUTTON_LEFT;
    }

	public static boolean isMiddleClick(MouseButtonEvent mouseButtonEvent) {
		return mouseButtonEvent.button() == InputConstants.MOUSE_BUTTON_MIDDLE;
    }

	public static boolean isRightClick(MouseButtonEvent mouseButtonEvent) {
		return mouseButtonEvent.button() == InputConstants.MOUSE_BUTTON_RIGHT;
    }
}
