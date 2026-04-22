// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.misc.MathUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;

public class RotationUtil
{
    public static Minecraft mc;
    
    public static float getYawChangeGiven(final double posX, final double posZ, final float yaw) {
        final double deltaX = posX - Minecraft.getMinecraft().player.posX;
        final double deltaZ = posZ - Minecraft.getMinecraft().player.posZ;
        double yawToEntity;
        if (deltaZ < 0.0 && deltaX < 0.0) {
            yawToEntity = 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else if (deltaZ < 0.0 && deltaX > 0.0) {
            yawToEntity = -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapDegrees(-(yaw - (float)yawToEntity));
    }
    
    public static float[] getRotations(final BlockPos pos, final EnumFacing facing) {
        return getRotations(pos, facing, getRotationPlayer());
    }
    
    public static float[] getRotations(final BlockPos pos, final EnumFacing facing, final Entity from) {
        return getRotations(pos, facing, from, RotationUtil.mc.world, RotationUtil.mc.world.getBlockState(pos));
    }
    
    public static float[] getRotations(final BlockPos pos, final EnumFacing facing, final Entity from, final World world, final IBlockState state) {
        final AxisAlignedBB bb = state.getSelectedBoundingBox(world, pos);
        double x = pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;
        if (facing != null) {
            x += facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }
        return getRotations(x, y, z, from);
    }
    
    public static float[] getRotations(final double x, final double y, final double z, final double fromX, final double fromY, final double fromZ, final float fromHeight) {
        final double xDiff = x - fromX;
        final double yDiff = y - (fromY + fromHeight);
        final double zDiff = z - fromZ;
        final double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        final float prevYaw = RotationUtil.mc.player.prevRotationYaw;
        float diff = yaw - prevYaw;
        if (diff < -180.0f || diff > 180.0f) {
            final float round = (float)Math.round(Math.abs(diff / 360.0f));
            diff = ((diff < 0.0f) ? (diff + 360.0f * round) : (diff - 360.0f * round));
        }
        return new float[] { prevYaw + diff, pitch };
    }
    
    public static float[] getRotations(final double x, final double y, final double z, final Entity f) {
        return getRotations(x, y, z, f.posX, f.posY, f.posZ, f.getEyeHeight());
    }
    
    public static Vec3d getVec3d(final float yaw, final float pitch) {
        final float vx = -MathHelper.sin(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        final float vz = MathHelper.cos(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        final float vy = -MathHelper.sin(MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }
    
    public static EntityPlayer getRotationPlayer() {
        final EntityPlayer rotationEntity = RotationUtil.mc.player;
        return (rotationEntity == null) ? RotationUtil.mc.player : rotationEntity;
    }
    
    public static float[] getNeededRotations(final Vec3d vec) {
        final Vec3d playerVector = new Vec3d(RotationUtil.mc.player.posX, RotationUtil.mc.player.posY + RotationUtil.mc.player.getEyeHeight(), RotationUtil.mc.player.posZ);
        final double y = vec.y - playerVector.y;
        final double x = vec.x - playerVector.x;
        final double z = vec.z - playerVector.z;
        final double dff = Math.sqrt(x * x + z * z);
        final float yaw = (float)Math.toDegrees(Math.atan2(z, x)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(y, dff)));
        return new float[] { MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch) };
    }
    
    public static float[] getNeededFacing(final Vec3d target, final Vec3d from) {
        final double diffX = target.x - from.x;
        final double diffY = target.y - from.y;
        final double diffZ = target.z - from.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch) };
    }
    
    public static float[] getRotations(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }
    
    public static boolean isInFov(final BlockPos pos) {
        return pos != null && (RotationUtil.mc.player.getDistanceSq(pos) < 4.0 || isInFov(new Vec3d(pos), RotationUtil.mc.player.getPositionVector()));
    }
    
    public static float[] getRotations(final EntityLivingBase ent) {
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = ent.posY + ent.getEyeHeight() / 2.0f;
        return getRotationFromPosition(x, z, y);
    }
    
    public static float[] getRotationFromPosition(final double x, final double z, final double y) {
        final double xDiff = x - Minecraft.getMinecraft().player.posX;
        final double zDiff = z - Minecraft.getMinecraft().player.posZ;
        final double yDiff = y - Minecraft.getMinecraft().player.posY - 1.2;
        final double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793);
        return new float[] { yaw, pitch };
    }
    
    public static boolean isInFov(final Vec3d vec3d, final Vec3d other) {
        if (RotationUtil.mc.player.rotationPitch > 30.0f) {
            if (other.y > RotationUtil.mc.player.posY) {
                return true;
            }
        }
        else if (RotationUtil.mc.player.rotationPitch < -30.0f && other.y < RotationUtil.mc.player.posY) {
            return true;
        }
        final float angle = MathUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
        if (angle < -270.0f) {
            return true;
        }
        final float fov = RotationUtil.mc.gameSettings.fovSetting / 2.0f;
        return angle < fov + 10.0f && angle > -fov - 10.0f;
    }
    
    public static float transformYaw() {
        float yaw = RotationUtil.mc.player.rotationYaw % 360.0f;
        if (RotationUtil.mc.player.rotationYaw > 0.0f && yaw > 180.0f) {
            yaw = -180.0f + (yaw - 180.0f);
        }
        return yaw;
    }
    
    public static float[] getRotationsBlock(final BlockPos block, final EnumFacing face, final boolean Legit) {
        final double x = block.getX() + 0.5 - RotationUtil.mc.player.posX + face.getXOffset() / 2.0;
        final double z = block.getZ() + 0.5 - RotationUtil.mc.player.posZ + face.getZOffset() / 2.0;
        double y = block.getY() + 0.5;
        if (Legit) {
            y += 0.5;
        }
        final double d1 = RotationUtil.mc.player.posY + RotationUtil.mc.player.getEyeHeight() - y;
        final double d2 = MathHelper.sqrt(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(Math.atan2(d1, d2) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[] { yaw, pitch };
    }
    
    static {
        RotationUtil.mc = Minecraft.getMinecraft();
    }
}
