//? neoforge && <26 {
/*package cc.cassian.rrv.neoforge.builtin;

import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.BuiltInReliableRecipeViewerIntegration;
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.neoforge.mixin.neoforge.common.BasicItemListingAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.BasicItemListing;

import java.util.Arrays;
import java.util.List;

public class NeoForgeBuiltinRrvIntegration {

    public static final VillagerServerRecipe.VillagerOfferType<BasicItemListing> NEOFORGE_BASIC = VillagerServerRecipe.VillagerOfferType.register(
            Identifier.fromNamespaceAndPath("neoforge", "basic"),
            BasicItemListing.class,
            (listing, out) -> {

                BasicItemListingAccessor accessor = (BasicItemListingAccessor) listing;

                out.put("offerStack", TagUtil.encodeItemStackOnServer(accessor.offer()));
                out.put("price", TagUtil.encodeItemStackOnServer(accessor.price1()));
                out.put("price2", TagUtil.encodeItemStackOnServer(accessor.price2()));
                out.putInt("villagerXp", accessor.villagerxp());
                out.putInt("maxUses", accessor.maxUses());

            },
            (profession, professionLevel, in) -> {

                ItemStack offerStack = TagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("offerStack"));
                ItemStack price = TagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("price"));
                ItemStack price2 = TagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("price2"));

                int villagerXp = in.getIntOr("villagerXp", 0);
                int maxUses = in.getIntOr("maxUses", 0);

                ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                return List.of(new VillagerServerRecipe.VillagerOffer(profession, professionLevel, villagerType, List.of(offerStack), List.of(price), List.of(price2), villagerXp, maxUses));
            }
    );
}
*///?}