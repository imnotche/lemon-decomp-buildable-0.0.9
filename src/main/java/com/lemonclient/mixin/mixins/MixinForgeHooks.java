// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.lemonclient.api.util.player.Locks;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ForgeHooks.class })
public abstract class MixinForgeHooks
{
    @Redirect(method = { "onPickBlock" }, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I", ordinal = 1))
    private static void onPickBlockHook(final InventoryPlayer inventoryPlayer, int value) {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> inventoryPlayer.currentItem = value);
    }
}
