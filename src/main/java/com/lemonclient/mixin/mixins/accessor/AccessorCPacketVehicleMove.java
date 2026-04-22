// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.network.play.client.CPacketVehicleMove;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CPacketVehicleMove.class })
public interface AccessorCPacketVehicleMove
{
    @Accessor("y")
    void setY(final double p0);
    
    @Accessor("x")
    void setX(final double p0);
    
    @Accessor("z")
    void setZ(final double p0);
    
    @Accessor("yaw")
    void setYaw(final float p0);
    
    @Accessor("pitch")
    void setPitch(final float p0);
}
