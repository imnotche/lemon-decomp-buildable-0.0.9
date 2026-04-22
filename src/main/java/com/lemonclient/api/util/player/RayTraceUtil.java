// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import com.lemonclient.api.util.misc.MathUtil;
import net.minecraft.util.EnumFacing;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;

public class RayTraceUtil
{
    public static Minecraft mc;
    
    public static float[] hitVecToPlaceVec(final BlockPos pos, final Vec3d hitVec) {
        final float x = (float)(hitVec.x - pos.getX());
        final float y = (float)(hitVec.y - pos.getY());
        final float z = (float)(hitVec.z - pos.getZ());
        return new float[] { x, y, z };
    }
    
    public static RayTraceResult getRayTraceResult(final float yaw, final float pitch) {
        return getRayTraceResult(yaw, pitch, RayTraceUtil.mc.playerController.getBlockReachDistance());
    }
    
    public static RayTraceResult getRayTraceResultWithEntity(final float yaw, final float pitch, final Entity from) {
        return getRayTraceResult(yaw, pitch, RayTraceUtil.mc.playerController.getBlockReachDistance(), from);
    }
    
    public static RayTraceResult getRayTraceResult(final float yaw, final float pitch, final float distance) {
        return getRayTraceResult(yaw, pitch, distance, RayTraceUtil.mc.player);
    }
    
    public static RayTraceResult getRayTraceResult(final float yaw, final float pitch, final float d, final Entity from) {
        final Vec3d vec3d = getEyePos(from);
        final Vec3d lookVec = getVec3d(yaw, pitch);
        final Vec3d rotations = vec3d.add(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        return Optional.ofNullable(RayTraceUtil.mc.world.rayTraceBlocks(vec3d, rotations, false, false, false)).orElseGet(() -> new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos.ORIGIN));
    }
    
    public static Vec3d getVec3d(final float yaw, final float pitch) {
        final float vx = -MathHelper.sin(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        final float vz = MathHelper.cos(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        final float vy = -MathHelper.sin(MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }
    
    public static Vec3d getEyePos(final Entity entity) {
        return new Vec3d(entity.posX, getEyeHeight(entity), entity.posZ);
    }
    
    public static double getEyeHeight(final Entity entity) {
        return entity.posY + entity.getEyeHeight();
    }
    
    public static boolean canBeSeen(final double x, final double y, final double z, final Entity by) {
        return canBeSeen(new Vec3d(x, y, z), by.posX, by.posY, by.posZ, by.getEyeHeight());
    }
    
    public static boolean canBeSeen(final Vec3d toSee, final Entity by) {
        return canBeSeen(toSee, by.posX, by.posY, by.posZ, by.getEyeHeight());
    }
    
    public static boolean canBeSeen(final Vec3d toSee, final double x, final double y, final double z, final float eyeHeight) {
        final Vec3d start = new Vec3d(x, y + eyeHeight, z);
        return RayTraceUtil.mc.world.rayTraceBlocks(start, toSee, false, true, false) == null;
    }
    
    public static boolean canBeSeen(final Entity toSee, final EntityLivingBase by) {
        return by.canEntityBeSeen(toSee);
    }
    
    public static boolean raytracePlaceCheck(final Entity entity, final BlockPos pos) {
        return getFacing(entity, pos, false) != null;
    }
    
    public static EnumFacing getFacing(final Entity entity, final BlockPos pos, final boolean verticals) {
        for (final EnumFacing facing : EnumFacing.values()) {
            final RayTraceResult result = RayTraceUtil.mc.world.rayTraceBlocks(getEyePos(entity), new Vec3d(pos.getX() + 0.5 + facing.getDirectionVec().getX() * 1.0 / 2.0, pos.getY() + 0.5 + facing.getDirectionVec().getY() * 1.0 / 2.0, pos.getZ() + 0.5 + facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.getBlockPos().equals(pos)) {
                return facing;
            }
        }
        if (!verticals) {
            return null;
        }
        if (pos.getY() > RayTraceUtil.mc.player.posY + RayTraceUtil.mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }
    
    static {
        RayTraceUtil.mc = Minecraft.getMinecraft();
    }
}
