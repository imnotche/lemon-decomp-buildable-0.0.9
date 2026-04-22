// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CPacketUseEntity.class })
public interface AccessorCPacketUseEntity
{
    @Accessor("entityId")
    int getId();
    
    @Accessor("entityId")
    void setId(final int p0);
    
    @Accessor("action")
    void setAction(final CPacketUseEntity.Action p0);
}
