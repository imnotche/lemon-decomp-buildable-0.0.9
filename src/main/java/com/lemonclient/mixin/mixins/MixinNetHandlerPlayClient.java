package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.DeathEvent;
import com.lemonclient.api.util.player.Locks;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.TotemPopManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={NetHandlerPlayClient.class})
public class MixinNetHandlerPlayClient {
    @Inject(method={"handleEntityMetadata"}, at={@At(value="RETURN")}, cancellable=true)
    private void handleEntityMetadataHook(SPacketEntityMetadata sPacketEntityMetadata, CallbackInfo callbackInfo) {
        Entity entity;
        if (Minecraft.getMinecraft().world != null && (entity = Minecraft.getMinecraft().world.getEntityByID(sPacketEntityMetadata.getEntityId())) instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.getHealth() <= 0.0f) {
                LemonClient.EVENT_BUS.post(new DeathEvent(player));
                if (TotemPopManager.INSTANCE.sendMsgs) {
                    TotemPopManager.INSTANCE.death(player);
                }
            }
        }
    }

    @Redirect(method={"handleHeldItemChange"}, at=@At(value="FIELD", target="Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I"))
    public void handleHeldItemChangeHook(InventoryPlayer inventoryPlayer, int value) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
            inventoryPlayer.currentItem = value;
        });
    }
}
