//? neoforge {
/*package cc.cassian.rrv.neoforge.mixin.neoforge.client.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.RegistrySnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistrySnapshot.class)
public interface RegistrySnapshotAccessor {

    @Accessor("ids")
    Int2ObjectSortedMap<Identifier> accessIds();

}
*///?}
