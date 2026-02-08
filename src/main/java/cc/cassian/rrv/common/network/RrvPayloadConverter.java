package cc.cassian.rrv.common.network;

import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.RrvClientNetworkManager;
import cc.cassian.rrv.common.network.payload.compat.ClientboundCompatPayload;
import cc.cassian.rrv.common.network.payload.recipe.*;
import cc.cassian.rrv.common.network.payload.stack.ClientboundFinishStackSensitivesPayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundStackSensitivePayload;
import cc.cassian.rrv.common.network.payload.stack.ClientboundStartStackSensitivesPayload;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import cc.cassian.rrv.api.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public class RrvPayloadConverter {


    public static void convertFromCompat(RrvClientNetworkManager.ClientContext ctx, ClientboundCompatPayload payload) {

        CompoundTag payloadTag = payload.data();
        if (payloadTag.isEmpty())
            return;

        Identifier payloadType = Identifier.parse(payloadTag.getStringOr("payloadType", ""));
        CompoundTag data = payloadTag.getCompoundOrEmpty("payloadData");

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null)
            return;

        if (payloadType.equals(ClientboundCacheStartPayload.TYPE.id())) {
            ClientboundCacheStartPayload p = new ClientboundCacheStartPayload(data.getIntOr("types", 0));
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundStartUpdatesPayload.TYPE.id())) {
            ClientboundStartUpdatesPayload p = new ClientboundStartUpdatesPayload();
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundTypeUpdateStartPayload.TYPE.id())) {
            ClientboundTypeUpdateStartPayload p = new ClientboundTypeUpdateStartPayload(ReliableServerRecipeType.byId(Identifier.parse(data.getStringOr("recipeType", ""))), data.getIntOr("amount", 0));
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundTypeUpdatePayload.TYPE.id())) {

            CompoundTag fullTag = data.getCompoundOrEmpty("entry");

            Identifier recipeId = Identifier.parse(fullTag.getStringOr("recipeId", ""));
            ReliableServerRecipe recipe = ServerRecipeManager.ServerRecipeEntry.fromTag(fullTag.getCompoundOrEmpty("recipe"));

            ClientboundTypeUpdatePayload p = new ClientboundTypeUpdatePayload(new ServerRecipeManager.ServerRecipeEntry(recipeId, recipe));
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundTypeUpdateEndPayload.TYPE.id())) {
            ClientboundTypeUpdateEndPayload p = new ClientboundTypeUpdateEndPayload(ReliableServerRecipeType.byId(Identifier.parse(data.getStringOr("recipeType", ""))));
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundFinishUpdatesPayload.TYPE.id())) {
            ClientboundFinishUpdatesPayload p = new ClientboundFinishUpdatesPayload();
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundStartStackSensitivesPayload.TYPE.id())) {
            ClientboundStartStackSensitivesPayload p = new ClientboundStartStackSensitivesPayload(data.getIntOr("amount", 0));
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundStackSensitivePayload.TYPE.id())) {
            ClientboundStackSensitivePayload p = new ClientboundStackSensitivePayload(new ItemView.StackSensitive(TagUtil.decodeItemStackOnClient(data.getCompoundOrEmpty("sensitive"))));
            connection.handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundFinishStackSensitivesPayload.TYPE.id())) {
            ClientboundFinishStackSensitivesPayload p = new ClientboundFinishStackSensitivesPayload();
            connection.handleCustomPayload(p);
        }


    }

}
