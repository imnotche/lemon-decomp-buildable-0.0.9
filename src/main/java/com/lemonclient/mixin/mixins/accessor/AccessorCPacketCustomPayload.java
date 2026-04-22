// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CPacketCustomPayload.class })
public interface AccessorCPacketCustomPayload
{
    @Accessor("data")
    void setData(final PacketBuffer p0);
}
