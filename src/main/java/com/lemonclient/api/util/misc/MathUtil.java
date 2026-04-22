// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import java.math.RoundingMode;
import java.math.BigDecimal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Random;
import net.minecraft.client.Minecraft;

public class MathUtil
{
    private static final Minecraft mc;
    public static Random rnd;
    
    public static int getRandom(final int min, final int max) {
        return MathUtil.rnd.nextInt(max - min + 1) + min;
    }
    
    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }
    
    public static double calculateAngle(final double x1, final double y1, final double x2, final double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle += Math.ceil(-angle / 360.0) * 360.0;
        return angle;
    }
    
    public static int clamp(final int num, final int min, final int max) {
        return (num < min) ? min : Math.min(num, max);
    }
    
    public static float clamp(final float num, final float min, final float max) {
        return (num < min) ? min : Math.min(num, max);
    }
    
    public static double clamp(final double num, final double min, final double max) {
        return (num < min) ? min : Math.min(num, max);
    }
    
    public static long clamp(final long num, final long min, final long max) {
        return (num < min) ? min : Math.min(num, max);
    }
    
    public static BigDecimal clamp(final BigDecimal num, final BigDecimal min, final BigDecimal max) {
        return smallerThan(num, min) ? min : (biggerThan(num, max) ? max : num);
    }
    
    public static Vec3d roundVec(final Vec3d vec3d, final int places) {
        return new Vec3d(round(vec3d.x, places), round(vec3d.y, places), round(vec3d.z, places));
    }
    
    public static boolean biggerThan(final BigDecimal bigger, final BigDecimal than) {
        return bigger.compareTo(than) > 0;
    }
    
    public static boolean smallerThan(final BigDecimal smaller, final BigDecimal than) {
        return smaller.compareTo(than) < 0;
    }
    
    public static double round(final double value, final int places) {
        return (places < 0) ? value : new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static float round(final float value, final int places) {
        return (places < 0) ? value : new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).floatValue();
    }
    
    public static float round(final float value, final int places, final float min, final float max) {
        return MathHelper.clamp((places < 0) ? value : new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).floatValue(), min, max);
    }
    
    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }
    
    public static float rad(final float angle) {
        return (float)(angle * 3.141592653589793 / 180.0);
    }
    
    public static float[] calcAngleNoY(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difZ = to.z - from.z;
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0) };
    }
    
    public static Double calculateDoubleChange(final double oldDouble, final double newDouble, final int step, final int currentStep) {
        return oldDouble + (newDouble - oldDouble) * Math.max(0, Math.min(step, currentStep)) / step;
    }
    
    public static double square(final double input) {
        return input * input;
    }
    
    public static double[] directionSpeed(final double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
    
    public static float square(final float v1) {
        return v1 * v1;
    }
    
    public static double square(final Double v1) {
        return v1 * v1;
    }
    
    public static double calculateDistanceWithPartialTicks(final double n, final double n2, final float renderPartialTicks) {
        return n2 + (n - n2) * MathUtil.mc.getRenderPartialTicks();
    }
    
    public static Vec3d interpolateEntityClose(final Entity entity, final float renderPartialTicks) {
        return new Vec3d(calculateDistanceWithPartialTicks(entity.posX, entity.lastTickPosX, renderPartialTicks) - MathUtil.mc.getRenderManager().renderPosX, calculateDistanceWithPartialTicks(entity.posY, entity.lastTickPosY, renderPartialTicks) - MathUtil.mc.getRenderManager().renderPosY, calculateDistanceWithPartialTicks(entity.posZ, entity.lastTickPosZ, renderPartialTicks) - MathUtil.mc.getRenderManager().renderPosZ);
    }
    
    public static double radToDeg(final double rad) {
        return rad * 57.295780181884766;
    }
    
    public static double degToRad(final double deg) {
        return deg * 0.01745329238474369;
    }
    
    public static Vec3d direction(final float yaw) {
        return new Vec3d(Math.cos(degToRad(yaw + 90.0f)), 0.0, Math.sin(degToRad(yaw + 90.0f)));
    }
    
    public static float wrap(float val) {
        val %= 360.0f;
        if (val >= 180.0f) {
            val -= 360.0f;
        }
        if (val < -180.0f) {
            val += 360.0f;
        }
        return val;
    }
    
    public static double map(double value, final double a, final double b, final double c, final double d) {
        value = (value - a) / (b - a);
        return c + value * (d - c);
    }
    
    public static double linear(final double from, final double to, final double incline) {
        return (from < to - incline) ? (from + incline) : ((from > to + incline) ? (from - incline) : to);
    }
    
    public static double parabolic(final double from, final double to, final double incline) {
        return from + (to - from) / incline;
    }
    
    public static double getDistance(final Vec3d pos, final double x, final double y, final double z) {
        final double deltaX = pos.x - x;
        final double deltaY = pos.y - y;
        final double deltaZ = pos.z - z;
        return MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    public static double[] calcIntersection(final double[] line, final double[] line2) {
        final double a1 = line[3] - line[1];
        final double b1 = line[0] - line[2];
        final double c1 = a1 * line[0] + b1 * line[1];
        final double a2 = line2[3] - line2[1];
        final double b2 = line2[0] - line2[2];
        final double c2 = a2 * line2[0] + b2 * line2[1];
        final double delta = a1 * b2 - a2 * b1;
        return new double[] { (b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta };
    }
    
    public static boolean isIntersect(final AxisAlignedBB a, final AxisAlignedBB b) {
        return a.maxX > b.minX && a.minX < b.maxX && a.maxY > b.minY && a.minY < b.maxY && a.maxZ > b.minZ && a.minZ < b.maxZ;
    }
    
    static {
        mc = Minecraft.getMinecraft();
        MathUtil.rnd = new Random();
    }
}
