// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.api.util.player.Locks;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ InventoryPlayer.class })
public abstract class MixinInventoryPlayer
{
    @Redirect(method = { "setPickedItemStack" }, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I", opcode = 181))
    public void setPickedItemStackHook(final InventoryPlayer inventoryPlayer, int value) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> inventoryPlayer.currentItem = value);
    }
    
    @Redirect(method = { "pickItem" }, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I", opcode = 181))
    public void pickItemHook(final InventoryPlayer inventoryPlayer, int value) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> inventoryPlayer.currentItem = value);
    }
}
