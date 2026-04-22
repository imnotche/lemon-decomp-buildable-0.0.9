// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.api.util.player.Locks;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.entity.Entity;
import com.lemonclient.client.manager.managers.TotemPopManager;
import com.lemonclient.api.event.events.DeathEvent;
import com.lemonclient.client.LemonClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ NetHandlerPlayClient.class })
public class MixinNetHandlerPlayClient
{
    @Inject(method = { "handleEntityMetadata" }, at = { @At("RETURN") }, cancellable = true)
    private void handleEntityMetadataHook(final SPacketEntityMetadata sPacketEntityMetadata, final CallbackInfo callbackInfo) {
        final Entity getEntityByID;
        final EntityPlayer entityPlayer;
        if (Minecraft.getMinecraft().world != null && (getEntityByID = Minecraft.getMinecraft().world.getEntityByID(sPacketEntityMetadata.getEntityId())) instanceof EntityPlayer && (entityPlayer = (EntityPlayer)getEntityByID).getHealth() <= 0.0f) {
            LemonClient.EVENT_BUS.post(new DeathEvent(entityPlayer));
            if (TotemPopManager.INSTANCE.sendMsgs) {
                TotemPopManager.INSTANCE.death(entityPlayer);
            }
        }
    }
    
    @Redirect(method = { "handleHeldItemChange" }, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I"))
    public void handleHeldItemChangeHook(final InventoryPlayer inventoryPlayer, int value) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> inventoryPlayer.currentItem = value);
    }
}
