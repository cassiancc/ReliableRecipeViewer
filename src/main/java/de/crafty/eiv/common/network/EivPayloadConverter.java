package de.crafty.eiv.common.network;

import de.crafty.eiv.common.api.recipe.EivRecipeType;
import de.crafty.eiv.common.api.recipe.IEivServerRecipe;
import de.crafty.eiv.common.api.recipe.ItemView;
import de.crafty.eiv.common.network.payload.compat.ClientboundCompatPayload;
import de.crafty.eiv.common.network.payload.recipe.*;
import de.crafty.eiv.common.network.payload.stack.ClientboundFinishStackSensitivesPayload;
import de.crafty.eiv.common.network.payload.stack.ClientboundStackSensitivePayload;
import de.crafty.eiv.common.network.payload.stack.ClientboundStartStackSensitivesPayload;
import de.crafty.eiv.common.recipe.ServerRecipeManager;
import de.crafty.eiv.common.recipe.util.EivTagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public class EivPayloadConverter {


    public static void convertFromCompat(EivNetworkManager.ClientContext ctx, ClientboundCompatPayload payload) {

        CompoundTag payloadTag = payload.data();
        if (payloadTag.isEmpty())
            return;

        Identifier payloadType = Identifier.parse(payloadTag.getStringOr("payloadType", ""));
        CompoundTag data = payloadTag.getCompoundOrEmpty("payloadData");

        if (Minecraft.getInstance().getConnection() == null)
            return;

        if (payloadType.equals(ClientboundCacheStartPayload.TYPE.id())) {
            ClientboundCacheStartPayload p = new ClientboundCacheStartPayload(data.getIntOr("types", 0));
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundStartUpdatesPayload.TYPE.id())) {
            ClientboundStartUpdatesPayload p = new ClientboundStartUpdatesPayload();
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundTypeUpdateStartPayload.TYPE.id())) {
            ClientboundTypeUpdateStartPayload p = new ClientboundTypeUpdateStartPayload(EivRecipeType.byId(Identifier.parse(data.getStringOr("recipeType", ""))), data.getIntOr("amount", 0));
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundTypeUpdatePayload.TYPE.id())) {

            CompoundTag fullTag = data.getCompoundOrEmpty("entry");

            Identifier recipeId = Identifier.parse(fullTag.getStringOr("recipeId", ""));
            IEivServerRecipe recipe = ServerRecipeManager.ServerRecipeEntry.fromTag(fullTag.getCompoundOrEmpty("recipe"));

            ClientboundTypeUpdatePayload p = new ClientboundTypeUpdatePayload(new ServerRecipeManager.ServerRecipeEntry(recipeId, recipe));
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundTypeUpdateEndPayload.TYPE.id())) {
            ClientboundTypeUpdateEndPayload p = new ClientboundTypeUpdateEndPayload(EivRecipeType.byId(Identifier.parse(data.getStringOr("recipeType", ""))));
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundFinishUpdatesPayload.TYPE.id())) {
            ClientboundFinishUpdatesPayload p = new ClientboundFinishUpdatesPayload();
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundStartStackSensitivesPayload.TYPE.id())) {
            ClientboundStartStackSensitivesPayload p = new ClientboundStartStackSensitivesPayload(data.getIntOr("amount", 0));
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundStackSensitivePayload.TYPE.id())) {
            ClientboundStackSensitivePayload p = new ClientboundStackSensitivePayload(new ItemView.StackSensitive(EivTagUtil.decodeItemStackOnClient(data.getCompoundOrEmpty("sensitive"))));
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }

        if (payloadType.equals(ClientboundFinishStackSensitivesPayload.TYPE.id())) {
            ClientboundFinishStackSensitivesPayload p = new ClientboundFinishStackSensitivesPayload();
            Minecraft.getInstance().getConnection().handleCustomPayload(p);
        }


    }

}
