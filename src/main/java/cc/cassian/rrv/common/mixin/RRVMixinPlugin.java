package cc.cassian.rrv.common.mixin;

import cc.cassian.rrv.common.RRVPlatform;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RRVMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String s) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (compatMixin(mixinClassName, "controlify")) return false;
		else if (compatMixin(mixinClassName, "jei")) return false;
		return true;
	}

	private static boolean compatMixin(String mixinClassName, String modName) {
		return mixinClassName.toLowerCase(Locale.ROOT).startsWith("cc.cassian.rrv.common.mixin.integration."+ modName) && !RRVPlatform.INSTANCE.isLoadingLoaded(modName);
	}

	@Override
	public void acceptTargets(Set<String> set, Set<String> set1) {

	}

	@Override
	public List<String> getMixins() {
		return List.of();
	}

	@Override
	public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

	}

	@Override
	public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

	}
}
