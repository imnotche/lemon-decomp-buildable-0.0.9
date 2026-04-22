// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.client.Minecraft;

public class RotationUtil
{
    private static final Minecraft mc;
    
    public static EnumFacing getFacing(final double rotationYaw) {
        return EnumFacing.byHorizontalIndex(MathHelper.floor(rotationYaw * 4.0 / 360.0 + 0.5) & 0x3);
    }
    
    public static Vec2f getRotationTo(final AxisAlignedBB box) {
        final EntityPlayerSP player = RotationUtil.mc.player;
        if (player == null) {
            return Vec2f.ZERO;
        }
        final Vec3d eyePos = player.getPositionEyes(1.0f);
        if (player.getEntityBoundingBox().intersects(box)) {
            return getRotationTo(eyePos, box.getCenter());
        }
        final double x = MathHelper.clamp(eyePos.x, box.minX, box.maxX);
        final double y = MathHelper.clamp(eyePos.y, box.minY, box.maxY);
        final double z = MathHelper.clamp(eyePos.z, box.minZ, box.maxZ);
        return getRotationTo(eyePos, new Vec3d(x, y, z));
    }
    
    public static Vec2f getRotationTo(final Vec3d posTo) {
        final EntityPlayerSP player = RotationUtil.mc.player;
        return (player != null) ? getRotationTo(player.getPositionEyes(1.0f), posTo) : Vec2f.ZERO;
    }
    
    public static Vec2f getRotationTo(final Vec3d posFrom, final Vec3d posTo) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }
    
    public static Vec2f getRotationFromVec(final Vec3d vec) {
        final double lengthXZ = Math.hypot(vec.x, vec.z);
        final double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        final double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, lengthXZ)));
        return new Vec2f((float)yaw, (float)pitch);
    }
    
    public static double normalizeAngle(double angle) {
        angle %= 360.0;
        if (angle >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }
    
    public static float normalizeAngle(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }
    
    public static boolean isInFov(final BlockPos pos) {
        return pos != null && (RotationUtil.mc.player.getDistanceSq(pos) < 4.0 || yawDist(pos) < getHalvedfov() + 2.0f);
    }
    
    public static boolean isInFov(final Entity entity) {
        return entity != null && (RotationUtil.mc.player.getDistanceSq(entity) < 4.0 || yawDist(entity) < getHalvedfov() + 2.0f);
    }
    
    public static double yawDist(final BlockPos pos) {
        if (pos != null) {
            final Vec3d difference = new Vec3d(pos).subtract(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
            final double d = Math.abs(RotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return (d > 180.0) ? (360.0 - d) : d;
        }
        return 0.0;
    }
    
    public static double yawDist(final Entity e) {
        if (e != null) {
            final Vec3d difference = e.getPositionVector().add(0.0, e.getEyeHeight() / 2.0f, 0.0).subtract(RotationUtil.mc.player.getPositionEyes(RotationUtil.mc.getRenderPartialTicks()));
            final double d = Math.abs(RotationUtil.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return (d > 180.0) ? (360.0 - d) : d;
        }
        return 0.0;
    }
    
    public static float transformYaw() {
        float yaw = RotationUtil.mc.player.rotationYaw % 360.0f;
        if (RotationUtil.mc.player.rotationYaw > 0.0f) {
            if (yaw > 180.0f) {
                yaw = -180.0f + (yaw - 180.0f);
            }
        }
        else if (yaw < -180.0f) {
            yaw = 180.0f + (yaw + 180.0f);
        }
        if (yaw < 0.0f) {
            return 180.0f + yaw;
        }
        return -180.0f + yaw;
    }
    
    public static boolean isInFov(final Vec3d vec3d, final Vec3d other) {
        Label_0069: {
            if (RotationUtil.mc.player.rotationPitch > 30.0f) {
                if (other.y <= RotationUtil.mc.player.posY) {
                    break Label_0069;
                }
            }
            else if (RotationUtil.mc.player.rotationPitch >= -30.0f || other.y >= RotationUtil.mc.player.posY) {
                break Label_0069;
            }
            return true;
        }
        final float angle = BlockUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
        if (angle < -270.0f) {
            return true;
        }
        final float fov = RotationUtil.mc.gameSettings.fovSetting / 2.0f;
        return angle < fov + 10.0f && angle > -fov - 10.0f;
    }
    
    public static float getFov() {
        return RotationUtil.mc.gameSettings.fovSetting;
    }
    
    public static float getHalvedfov() {
        return getFov() / 2.0f;
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
