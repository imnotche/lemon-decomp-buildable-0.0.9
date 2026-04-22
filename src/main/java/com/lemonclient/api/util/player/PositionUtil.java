// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.client.Minecraft;

public class PositionUtil
{
    Minecraft mc;
    private double x;
    private double y;
    private double z;
    private boolean onground;
    
    public PositionUtil() {
        this.mc = Minecraft.getMinecraft();
    }
    
    public void updatePosition() {
        this.x = this.mc.player.posX;
        this.y = this.mc.player.posY;
        this.z = this.mc.player.posZ;
        this.onground = this.mc.player.onGround;
    }
    
    public void restorePosition() {
        this.mc.player.posX = this.x;
        this.mc.player.posY = this.y;
        this.mc.player.posZ = this.z;
        this.mc.player.onGround = this.onground;
    }
    
    public void setPlayerPosition(final double x, final double y, final double z) {
        this.mc.player.posX = x;
        this.mc.player.posY = y;
        this.mc.player.posZ = z;
    }
    
    public void setPlayerPosition(final double x, final double y, final double z, final boolean onground) {
        this.mc.player.posX = x;
        this.mc.player.posY = y;
        this.mc.player.posZ = z;
        this.mc.player.onGround = onground;
    }
    
    public void setPositionPacket(final double x, final double y, final double z, final boolean onGround, final boolean setPos, final boolean noLagBack) {
        this.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
        if (setPos) {
            this.mc.player.setPosition(x, y, z);
            if (noLagBack) {
                this.updatePosition();
            }
        }
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
}
