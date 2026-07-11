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
	/*TODO: Wait for NeoForge and Fabric to merge the PRs that implement `shouldDisplayAllInformation`.
	@Override
	public boolean shouldDisplayAllInformation() {
		return true;
	}
	*/
}
