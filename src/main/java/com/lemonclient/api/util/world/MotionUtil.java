// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world;

import java.util.Objects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;

public class MotionUtil
{
    public static boolean isMoving(final EntityLivingBase entity) {
        return entity.moveForward != 0.0f || entity.moveStrafing != 0.0f || entity.moveVertical != 0.0f || entity.motionY > -0.078;
    }
    
    public static boolean moving(final EntityLivingBase entity) {
        return entity.moveForward != 0.0f || entity.moveStrafing != 0.0f;
    }
    
    public static double getMotion(final EntityPlayer entity) {
        return Math.abs(entity.motionX) + Math.abs(entity.motionZ);
    }
    
    public static void setSpeed(final EntityLivingBase entity, final double speed) {
        final double[] dir = forward(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }
    
    public static double getBaseMoveSpeed() {
        double result = 0.2873;
        if (Minecraft.getMinecraft().player.isPotionActive(MobEffects.SPEED)) {
            result += 0.2873 * (Objects.requireNonNull(Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1) * 0.2;
        }
        if (Minecraft.getMinecraft().player.isPotionActive(MobEffects.SLOWNESS)) {
            result -= 0.2873 * (Objects.requireNonNull(Minecraft.getMinecraft().player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier() + 1) * 0.15;
        }
        return result;
    }
    
    public static double[] forward(final double speed) {
        float forward = Minecraft.getMinecraft().player.movementInput.moveForward;
        float side = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
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
    
    public static double[] forward(final double speed, final float yaw) {
        final float forward = 1.0f;
        final float side = 0.0f;
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
    
    public static double calcMoveYaw() {
        final float yawIn = Minecraft.getMinecraft().player.rotationYaw;
        final float moveForward = getRoundedMovementInput(Minecraft.getMinecraft().player.movementInput.moveForward);
        final float moveString = getRoundedMovementInput(Minecraft.getMinecraft().player.movementInput.moveStrafe);
        float strafe = 90.0f * moveString;
        strafe *= ((moveForward != 0.0f) ? (moveForward * 0.5f) : 1.0f);
        float yaw = yawIn - strafe;
        yaw -= ((moveForward < 0.0f) ? 180.0f : 0.0f);
        return Math.toRadians(yaw);
    }
    
    public static float getRoundedMovementInput(final float input) {
        if (input > 0.0f) {
            return 1.0f;
        }
        if (input < 0.0f) {
            return -1.0f;
        }
        return 0.0f;
    }
}
