//? neoforge {
/*package cc.cassian.rrv.neoforge.builtin;

import cc.cassian.rrv.common.api.recipe.ItemView;
import cc.cassian.rrv.common.builtin.BuiltInRrvIntegration;
import cc.cassian.rrv.common.builtin.villager.VillagerServerRecipe;
import cc.cassian.rrv.common.recipe.util.RrvTagUtil;
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

public class NeoForgeBuiltinRrvIntegration extends BuiltInRrvIntegration {



    public static final VillagerServerRecipe.VillagerOfferType<BasicItemListing> NEOFORGE_BASIC = VillagerServerRecipe.VillagerOfferType.register(
            Identifier.fromNamespaceAndPath("neoforge", "basic"),
            BasicItemListing.class,
            (listing, out) -> {

                BasicItemListingAccessor accessor = (BasicItemListingAccessor) listing;

                out.put("offerStack", RrvTagUtil.encodeItemStackOnServer(accessor.offer()));
                out.put("price", RrvTagUtil.encodeItemStackOnServer(accessor.price1()));
                out.put("price2", RrvTagUtil.encodeItemStackOnServer(accessor.price2()));
                out.putInt("villagerXp", accessor.villagerxp());
                out.putInt("maxUses", accessor.maxUses());

            },
            (profession, professionLevel, in) -> {

                ItemStack offerStack = RrvTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("offerStack"));
                ItemStack price = RrvTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("price"));
                ItemStack price2 = RrvTagUtil.decodeItemStackOnClient(in.getCompoundOrEmpty("price2"));

                int villagerXp = in.getIntOr("villagerXp", 0);
                int maxUses = in.getIntOr("maxUses", 0);

                ResourceKey<VillagerType> villagerType = !in.contains("requiredType") ? null : BuiltInRegistries.VILLAGER_TYPE.get(Identifier.parse(in.getString("requiredType").orElseThrow())).orElseThrow().key();

                return List.of(new VillagerServerRecipe.VillagerOffer(profession, professionLevel, villagerType, List.of(offerStack), List.of(price), List.of(price2), villagerXp, maxUses));
            }
    );

    @Override
    public void onIntegrationInitialize() {
        super.onIntegrationInitialize();


        ItemView.addRecipeProvider(recipeList -> {
            VillagerTrades.TRADES.forEach((profession, byProfessionLevel) -> {

                byProfessionLevel.forEach((professionLevel, itemListings) -> {
                    Arrays.asList(itemListings).forEach(listing -> {

                        if(listing instanceof BasicItemListing basicItemListing)
                            recipeList.add(new VillagerServerRecipe(profession, professionLevel, new VillagerServerRecipe.VillagerDataObject<>(NEOFORGE_BASIC, basicItemListing)));

                    });
                });

            });
        });
    }
}
*///?}