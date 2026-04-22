// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import net.minecraft.client.Minecraft;

public class SpeedUtil
{
    static Minecraft mc;
    public static final double LAST_JUMP_INFO_DURATION_DEFAULT = 3.0;
    public static boolean didJumpThisTick;
    public static boolean isJumping;
    public double firstJumpSpeed;
    public double lastJumpSpeed;
    public double percentJumpSpeedChanged;
    public double jumpSpeedChanged;
    public boolean didJumpLastTick;
    public long jumpInfoStartTime;
    public boolean wasFirstJump;
    public double speedometerCurrentSpeed;
    public HashMap<EntityPlayer, Info> playerInfo;
    
    public SpeedUtil() {
        this.firstJumpSpeed = 0.0;
        this.lastJumpSpeed = 0.0;
        this.percentJumpSpeedChanged = 0.0;
        this.jumpSpeedChanged = 0.0;
        this.didJumpLastTick = false;
        this.jumpInfoStartTime = 0L;
        this.wasFirstJump = true;
        this.speedometerCurrentSpeed = 0.0;
        this.playerInfo = new HashMap<EntityPlayer, Info>();
    }
    
    public static void setDidJumpThisTick(final boolean val) {
        SpeedUtil.didJumpThisTick = val;
    }
    
    public static void setIsJumping(final boolean val) {
        SpeedUtil.isJumping = val;
    }
    
    public float lastJumpInfoTimeRemaining() {
        return (Minecraft.getSystemTime() - this.jumpInfoStartTime) / 1000.0f;
    }
    
    public void update() {
        final double distTraveledLastTickX = SpeedUtil.mc.player.posX - SpeedUtil.mc.player.prevPosX;
        final double distTraveledLastTickZ = SpeedUtil.mc.player.posZ - SpeedUtil.mc.player.prevPosZ;
        this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
        if (SpeedUtil.didJumpThisTick && (!SpeedUtil.mc.player.onGround || SpeedUtil.isJumping)) {
            if (!this.didJumpLastTick) {
                this.wasFirstJump = (this.lastJumpSpeed == 0.0);
                this.percentJumpSpeedChanged = ((this.speedometerCurrentSpeed != 0.0) ? (this.speedometerCurrentSpeed / this.lastJumpSpeed - 1.0) : -1.0);
                this.jumpSpeedChanged = this.speedometerCurrentSpeed - this.lastJumpSpeed;
                this.jumpInfoStartTime = Minecraft.getSystemTime();
                this.lastJumpSpeed = this.speedometerCurrentSpeed;
                this.firstJumpSpeed = (this.wasFirstJump ? this.lastJumpSpeed : 0.0);
            }
            this.didJumpLastTick = SpeedUtil.didJumpThisTick;
        }
        else {
            this.didJumpLastTick = false;
            this.lastJumpSpeed = 0.0;
        }
        this.updatePlayers();
    }
    
    public void updatePlayers() {
        for (final EntityPlayer player : SpeedUtil.mc.world.playerEntities) {
            final int distance = 20;
            if (SpeedUtil.mc.player.getDistanceSq(player) >= distance * distance) {
                continue;
            }
            Vec3d lastPos = null;
            if (this.playerInfo.get(player) != null) {
                final Info info = this.playerInfo.get(player);
                lastPos = info.pos;
            }
            this.playerInfo.put(player, new Info(player, lastPos));
        }
    }
    
    public double getPlayerSpeed(final EntityPlayer player) {
        if (player == null) {
            return 0.0;
        }
        if (this.playerInfo.get(player) == null) {
            return 0.0;
        }
        return this.turnIntoKpH(this.playerInfo.get(player).speed);
    }
    
    public Vec3d getPlayerLastPos(final EntityPlayer player) {
        if (player == null) {
            return null;
        }
        if (this.playerInfo.get(player) == null) {
            return null;
        }
        return this.playerInfo.get(player).lastPos;
    }
    
    public double getPlayerMoveYaw(final EntityPlayer player) {
        if (player == null) {
            return 0.0;
        }
        if (this.playerInfo.get(player) == null) {
            return 0.0;
        }
        return this.playerInfo.get(player).yaw;
    }
    
    public double turnIntoKpH(final double input) {
        return MathHelper.sqrt(input) * 71.2729367892;
    }
    
    public double getSpeedKpH() {
        double speedometerkphdouble = this.turnIntoKpH(this.speedometerCurrentSpeed);
        speedometerkphdouble = Math.round(10.0 * speedometerkphdouble) / 10.0;
        return speedometerkphdouble;
    }
    
    public double getSpeedMpS() {
        double speedometerMpsdouble = this.turnIntoKpH(this.speedometerCurrentSpeed) / 3.6;
        speedometerMpsdouble = Math.round(10.0 * speedometerMpsdouble) / 10.0;
        return speedometerMpsdouble;
    }
    
    public static double calcSpeed(final EntityPlayer player) {
        final double distTraveledLastTickX = player.posX - player.prevPosX;
        final double distTraveledLastTickZ = player.posZ - player.prevPosZ;
        return distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
    }
    
    static {
        SpeedUtil.mc = Minecraft.getMinecraft();
        SpeedUtil.didJumpThisTick = false;
        SpeedUtil.isJumping = false;
    }
    
    public static class Info
    {
        double speed;
        Vec3d pos;
        Vec3d lastPos;
        double yaw;
        
        public Info(final EntityPlayer player, final Vec3d lastPos) {
            this.speed = SpeedUtil.calcSpeed(player);
            this.pos = player.getPositionVector();
            this.yaw = RotationUtil.getRotationTo(this.pos, new Vec3d(player.prevPosX, player.prevPosY, player.prevPosZ)).x;
            this.lastPos = lastPos;
        }
    }
}
