package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.network.play.client.CPacketVehicleMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CPacketVehicleMove.class})
public interface AccessorCPacketVehicleMove {
    @Accessor(value="y")
    public void setY(double var1);

    @Accessor(value="x")
    public void setX(double var1);

    @Accessor(value="z")
    public void setZ(double var1);

    @Accessor(value="yaw")
    public void setYaw(float var1);

    @Accessor(value="pitch")
    public void setPitch(float var1);
}
