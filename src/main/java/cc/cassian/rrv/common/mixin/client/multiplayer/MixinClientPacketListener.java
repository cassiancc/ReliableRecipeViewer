package cc.cassian.rrv.common.mixin.client.multiplayer;

import cc.cassian.rrv.client.recipe.InternalRecipeManager;
import cc.cassian.rrv.api.recipe.ItemView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {

    protected MixinClientPacketListener(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void requestRecipes(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        InternalRecipeManager.INSTANCE.requestServerRrvData();
        ItemView.getClientReloadCallbacks().forEach(ItemView.ReloadCallback::onReload);
    }
}
