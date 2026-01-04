//? neoforge {
/*package cc.cassian.rrv.neoforge.mixin.neoforge.common;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.BasicItemListing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BasicItemListing.class)
public interface BasicItemListingAccessor {


    @Accessor("forSale")
    ItemStack offer();

    @Accessor("price")
    ItemStack price1();

    @Accessor("price2")
    ItemStack price2();

    @Accessor("xp")
    int villagerxp();

    @Accessor("maxTrades")
    int maxUses();

}
*///?}