package cc.cassian.rrv.client.util;

import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RRVInputUtil {

	public static boolean isLeftClick(MouseButtonEvent mouseButtonEvent) {
		int mouseButton = mouseButtonEvent.button();
		//? if >26.2 {
		/*return mouseButton == 1;
		*///?} else {
		return mouseButton == 0;
		 //?}
    }

	public static boolean isMiddleClick(MouseButtonEvent mouseButtonEvent) {
		int mouseButton = mouseButtonEvent.button();
		//? if >26.2 {
		/*return mouseButton == 2;
		*///?} else {
		return mouseButton == 3;
		 //?}
    }

	public static boolean isRightClick(MouseButtonEvent mouseButtonEvent) {
		int mouseButton = mouseButtonEvent.button();
		//? if >26.2 {
		/*return mouseButton == 3;
		*///?} else {
		return mouseButton == 1;
		 //?}
    }
}
