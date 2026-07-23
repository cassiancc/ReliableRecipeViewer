package cc.cassian.rrv.client.util;

import net.minecraft.world.item.TooltipFlag;

public record ExtendedTooltipFlag(boolean advanced, boolean creative) implements TooltipFlag {

	public static ExtendedTooltipFlag NORMAL = new ExtendedTooltipFlag(false, false);
	public static ExtendedTooltipFlag ADVANCED = new ExtendedTooltipFlag(true, false);

	@Override
	public boolean isAdvanced() {
		return this.advanced;
	}

	@Override
	public boolean isCreative() {
		return this.creative;
	}

	// No override, this is currently only 26.2 Neo but will be on all versions eventually.
	public boolean shouldDisplayAllInformation() {
		return true;
	}
}
