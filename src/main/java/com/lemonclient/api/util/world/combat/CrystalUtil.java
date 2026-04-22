// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world.combat;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import java.util.Iterator;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.stream.Collectors;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;

public class CrystalUtil
{
    private static final Minecraft mc;
    
    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean newPlacement) {
        if (notValidBlock(CrystalUtil.mc.world.getBlockState(blockPos).getBlock())) {
            return false;
        }
        final BlockPos posUp = blockPos.up();
        if (newPlacement) {
            if (!CrystalUtil.mc.world.isAirBlock(posUp)) {
                return false;
            }
        }
        else if (notValidMaterial(CrystalUtil.mc.world.getBlockState(posUp).getMaterial()) || notValidMaterial(CrystalUtil.mc.world.getBlockState(posUp.up()).getMaterial())) {
            return false;
        }
        final AxisAlignedBB box = new AxisAlignedBB(posUp.getX(), posUp.getY(), posUp.getZ(), posUp.getX() + 1.0, posUp.getY() + 2.0, posUp.getZ() + 1.0);
        return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, box, entity -> !entity.isDead).isEmpty();
    }
    
    public static boolean canPlaceCrystalExcludingCrystals(final BlockPos blockPos, final boolean newPlacement) {
        if (notValidBlock(CrystalUtil.mc.world.getBlockState(blockPos).getBlock())) {
            return false;
        }
        final BlockPos posUp = blockPos.up();
        if (newPlacement) {
            if (!CrystalUtil.mc.world.isAirBlock(posUp)) {
                return false;
            }
        }
        else if (notValidMaterial(CrystalUtil.mc.world.getBlockState(posUp).getMaterial()) || notValidMaterial(CrystalUtil.mc.world.getBlockState(posUp.up()).getMaterial())) {
            return false;
        }
        final AxisAlignedBB box = new AxisAlignedBB(posUp.getX(), posUp.getY(), posUp.getZ(), posUp.getX() + 1.0, posUp.getY() + 2.0, posUp.getZ() + 1.0);
        return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, box, entity -> !entity.isDead && !(entity instanceof EntityEnderCrystal)).isEmpty();
    }
    
    public static boolean notValidBlock(final Block block) {
        return block != Blocks.BEDROCK && block != Blocks.OBSIDIAN;
    }
    
    public static boolean notValidMaterial(final Material material) {
        return material.isLiquid() || !material.isReplaceable();
    }
    
    public static List<BlockPos> findCrystalBlocks(final float placeRange, final boolean mode) {
        return EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (double)placeRange, (double)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, mode)).collect(Collectors.toList());
    }
    
    public static List<BlockPos> findCrystalBlocksExcludingCrystals(final float placeRange, final boolean mode) {
        return EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (double)placeRange, (double)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystalExcludingCrystals(pos, mode)).collect(Collectors.toList());
    }
    
    public static void breakCrystal(final BlockPos pos, final boolean swing) {
        for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            CrystalUtil.mc.playerController.attackEntity(CrystalUtil.mc.player, entity);
            if (swing) {
                CrystalUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                break;
            }
            break;
        }
    }
    
    public static void breakCrystal(final Entity crystal, final boolean swing) {
        CrystalUtil.mc.playerController.attackEntity(CrystalUtil.mc.player, crystal);
        if (swing) {
            CrystalUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    public static void breakCrystalPacket(final Entity crystal, final boolean swing) {
        CrystalUtil.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        if (swing) {
            CrystalUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
