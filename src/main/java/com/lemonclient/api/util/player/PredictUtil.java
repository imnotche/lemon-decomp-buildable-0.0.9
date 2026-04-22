// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import java.util.Iterator;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.block.BlockAir;
import com.lemonclient.api.util.world.HoleUtil;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.Minecraft;

public class PredictUtil
{
    static final Minecraft mc;
    
    public static EntityPlayer predictPlayer(final EntityLivingBase entity, final PredictSettings settings) {
        double[] posVec = { entity.posX, entity.posY, entity.posZ };
        final double motionX = entity.posX - entity.lastTickPosX;
        double motionY = entity.posY - entity.lastTickPosY;
        final double motionZ = entity.posZ - entity.lastTickPosZ;
        boolean isHole = false;
        if (settings.manualOutHole && motionY > 0.2) {
            if (HoleUtil.isHole(EntityUtil.getPosition(entity), false, true, false).getType() != HoleUtil.HoleType.NONE && BlockUtil.getBlock(EntityUtil.getPosition(entity).add(0, 2, 0)) instanceof BlockAir) {
                isHole = true;
            }
            else if (settings.aboveHoleManual && HoleUtil.isHole(EntityUtil.getPosition(entity).add(0, -1, 0), false, true, false).getType() != HoleUtil.HoleType.NONE) {
                isHole = true;
            }
            if (isHole) {
                final double[] array = posVec;
                final int n = 1;
                ++array[n];
            }
        }
        boolean allowPredictStair = false;
        int stairPredicted = 0;
        if (settings.stairPredict) {
            allowPredictStair = (Math.hypot(motionX, motionZ) > settings.speedActivationStairs);
        }
        for (int i = 0; i < settings.tick; ++i) {
            boolean predictedStair = false;
            if (settings.splitXZ) {
                final double[] array2;
                double[] newPosVec = array2 = posVec.clone();
                final int n2 = 0;
                array2[n2] += motionX;
                if (calculateRaytrace(posVec, newPosVec)) {
                    posVec = newPosVec.clone();
                }
                else if (settings.stairPredict && allowPredictStair) {
                    if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 1.0, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                        final double[] array3 = posVec;
                        final int n3 = 1;
                        ++array3[n3];
                        predictedStair = true;
                    }
                    else if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 3.0, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                        final double[] array4 = posVec;
                        final int n4 = 1;
                        array4[n4] += 2.0;
                        predictedStair = true;
                    }
                }
                final double[] array5;
                newPosVec = (array5 = posVec.clone());
                final int n5 = 2;
                array5[n5] += motionZ;
                if (calculateRaytrace(posVec, newPosVec)) {
                    posVec = newPosVec.clone();
                }
                else if (settings.stairPredict && allowPredictStair) {
                    if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 1.0, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                        final double[] array6 = posVec;
                        final int n6 = 1;
                        ++array6[n6];
                        predictedStair = true;
                    }
                    else if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 3.0, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                        final double[] array7 = posVec;
                        final int n7 = 1;
                        ++array7[n7];
                        predictedStair = true;
                    }
                }
            }
            else {
                final double[] array8;
                final double[] newPosVec = array8 = posVec.clone();
                final int n8 = 0;
                array8[n8] += motionX;
                final double[] array9 = newPosVec;
                final int n9 = 2;
                array9[n9] += motionZ;
                if (calculateRaytrace(posVec, newPosVec)) {
                    posVec = newPosVec.clone();
                }
                else if (settings.stairPredict && allowPredictStair) {
                    if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 1.0, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                        final double[] array10 = posVec;
                        final int n10 = 1;
                        ++array10[n10];
                        predictedStair = true;
                    }
                    else if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 3.0, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                        final double[] array11 = posVec;
                        final int n11 = 1;
                        ++array11[n11];
                        predictedStair = true;
                    }
                }
            }
            if (settings.calculateY && !isHole && !predictedStair) {
                final double[] newPosVec = posVec.clone();
                final double decreasePow = settings.startDecrease / Math.pow(10.0, settings.exponentStartDecrease);
                double decreasePowY = settings.decreaseY / Math.pow(10.0, settings.exponentDecreaseY);
                if (entity.isInWater() || entity.isInLava() || entity.isElytraFlying()) {
                    decreasePowY = 0.0;
                    final double[] array12 = newPosVec;
                    final int n12 = 1;
                    array12[n12] += motionY;
                }
                else {
                    motionY += decreasePowY;
                    if (Math.abs(motionY) > decreasePow) {
                        motionY = decreasePowY;
                    }
                    final double[] array13 = newPosVec;
                    final int n13 = 1;
                    array13[n13] += -1.0 * motionY;
                }
                if (calculateRaytrace(posVec, newPosVec)) {
                    posVec = newPosVec.clone();
                }
                else {
                    motionY -= decreasePowY;
                }
            }
        }
        final EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(PredictUtil.mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), entity.getName()));
        clonedPlayer.setPosition(posVec[0], posVec[1], posVec[2]);
        if (entity instanceof EntityPlayer) {
            clonedPlayer.inventory.copyInventory(((EntityPlayer)entity).inventory);
        }
        clonedPlayer.setHealth(entity.getHealth());
        clonedPlayer.prevPosX = entity.prevPosX;
        clonedPlayer.prevPosY = entity.prevPosY;
        clonedPlayer.prevPosZ = entity.prevPosZ;
        for (final PotionEffect effect : entity.getActivePotionEffects()) {
            clonedPlayer.addPotionEffect(effect);
        }
        return clonedPlayer;
    }
    
    public static boolean calculateRaytrace(final double[] posVec, final double[] newPosVec) {
        final RayTraceResult result = PredictUtil.mc.world.rayTraceBlocks(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], newPosVec[1], newPosVec[2]));
        final RayTraceResult result2 = PredictUtil.mc.world.rayTraceBlocks(new Vec3d(posVec[0] + 0.3, posVec[1], posVec[2] + 0.3), new Vec3d(newPosVec[0] - 0.3, newPosVec[1], newPosVec[2] - 0.3));
        final RayTraceResult result3 = PredictUtil.mc.world.rayTraceBlocks(new Vec3d(posVec[0] + 0.3, posVec[1], posVec[2] - 0.3), new Vec3d(newPosVec[0] - 0.3, newPosVec[1], newPosVec[2] + 0.3));
        final RayTraceResult result4 = PredictUtil.mc.world.rayTraceBlocks(new Vec3d(posVec[0] - 0.3, posVec[1], posVec[2] + 0.3), new Vec3d(newPosVec[0] + 0.3, newPosVec[1], newPosVec[2] - 0.3));
        final RayTraceResult result5 = PredictUtil.mc.world.rayTraceBlocks(new Vec3d(posVec[0] - 0.3, posVec[1], posVec[2] - 0.3), new Vec3d(newPosVec[0] + 0.3, newPosVec[1], newPosVec[2] + 0.3));
        return (result == null || result.typeOfHit == RayTraceResult.Type.ENTITY) && (result2 == null || result2.typeOfHit == RayTraceResult.Type.ENTITY) && (result3 == null || result3.typeOfHit == RayTraceResult.Type.ENTITY) && (result4 == null || result4.typeOfHit == RayTraceResult.Type.ENTITY) && (result5 == null || result5.typeOfHit == RayTraceResult.Type.ENTITY);
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
    
    public static class PredictSettings
    {
        final int tick;
        final boolean calculateY;
        final int startDecrease;
        final int exponentStartDecrease;
        final int decreaseY;
        final int exponentDecreaseY;
        final boolean splitXZ;
        final boolean manualOutHole;
        final boolean aboveHoleManual;
        final boolean stairPredict;
        final int nStairs;
        final double speedActivationStairs;
        
        public PredictSettings(final int tick, final boolean calculateY, final int startDecrease, final int exponentStartDecrease, final int decreaseY, final int exponentDecreaseY, final boolean splitXZ, final boolean manualOutHole, final boolean aboveHoleManual, final boolean stairPredict, final int nStairs, final double speedActivationStairs) {
            this.tick = tick;
            this.calculateY = calculateY;
            this.startDecrease = startDecrease;
            this.exponentStartDecrease = exponentStartDecrease;
            this.decreaseY = decreaseY;
            this.exponentDecreaseY = exponentDecreaseY;
            this.splitXZ = splitXZ;
            this.manualOutHole = manualOutHole;
            this.aboveHoleManual = aboveHoleManual;
            this.stairPredict = stairPredict;
            this.nStairs = nStairs;
            this.speedActivationStairs = speedActivationStairs;
        }
    }
}
