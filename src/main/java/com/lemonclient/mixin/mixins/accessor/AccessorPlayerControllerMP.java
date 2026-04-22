// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ PlayerControllerMP.class })
public interface AccessorPlayerControllerMP
{
    @Accessor("blockHitDelay")
    int getBlockHitDelay();
    
    @Accessor("blockHitDelay")
    void setBlockHitDelay(final int p0);
    
    @Accessor("isHittingBlock")
    void setIsHittingBlock(final boolean p0);
    
    @Accessor("currentPlayerItem")
    int getCurrentPlayerItem();
    
    @Invoker("syncCurrentPlayItem")
    void invokeSyncCurrentPlayItem();
}
