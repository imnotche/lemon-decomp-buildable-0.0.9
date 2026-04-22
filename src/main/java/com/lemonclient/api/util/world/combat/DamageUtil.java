// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world.combat;

import com.lemonclient.api.util.world.combat.raytrace.RayTracer;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.RayTraceResult;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.modules.combat.AutoCrystal;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;

public class DamageUtil
{
    public static float calculateDamage(final EntityLivingBase entity, final Vec3d entityPos, final AxisAlignedBB entityBox, final EntityEnderCrystal crystal) {
        return calculateCrystalDamage(entity, entityPos, entityBox, crystal.posX, crystal.posY, crystal.posZ);
    }
    
    public static float calculateDamage(final EntityLivingBase entity, final Vec3d entityPos, final AxisAlignedBB entityBox, final double posX, final double posY, final double posZ, final float size, final String mode) {
        final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        final boolean isPlayer = entity instanceof EntityPlayer;
        if (isPlayer && entity.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return 0.0f;
        }
        float damage = calcRawDamage(entity, entityPos, entityBox, posX, posY, posZ, size * 2.0f, mutableBlockPos, mode);
        if (isPlayer) {
            damage = calcDifficultyDamage(entity, damage);
        }
        return calcReductionDamage(entity, damage);
    }
    
    public static float calcDamageIgnoreTerrain(final EntityLivingBase entity, final Vec3d entityPos, final AxisAlignedBB entityBox, final double crystalX, final double crystalY, final double crystalZ) {
        final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        final boolean isPlayer = entity instanceof EntityPlayer;
        if (isPlayer && entity.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return 0.0f;
        }
        mutableBlockPos.setPos((int)crystalX, (int)crystalY - 1, (int)crystalZ);
        float damage;
        if (isPlayer && crystalY - entityPos.y > 1.5652173822904127 && isResistant(entity.world.getBlockState(mutableBlockPos))) {
            damage = 1.0f;
        }
        else {
            damage = calcRawDamage(entity, entityPos, entityBox, crystalX, crystalY, crystalZ, 12.0f, mutableBlockPos, "Crystal");
        }
        if (isPlayer) {
            damage = calcDifficultyDamage(entity, damage);
        }
        return calcReductionDamage(entity, damage);
    }
    
    public static float calculateCrystalDamageMine(final EntityLivingBase entity, final Vec3d entityPos, final AxisAlignedBB entityBox, final double posX, final double posY, final double posZ) {
        final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        final boolean isPlayer = entity instanceof EntityPlayer;
        if (isPlayer && entity.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return 0.0f;
        }
        mutableBlockPos.setPos((int)posX, (int)posY - 1, (int)posZ);
        float damage;
        if (isPlayer && posY - entityPos.y > 1.5652173822904127 && ((int)posX != (int)entityPos.x || (int)posZ != (int)entityPos.z) && isResistantMine(mutableBlockPos)) {
            damage = 1.0f;
        }
        else {
            final float scaledDist = (float)(entityPos.distanceTo(new Vec3d(posX, posY, posZ)) / 12.0);
            if (scaledDist > 1.0f) {
                damage = 0.0f;
            }
            else {
                final float factor = (1.0f - scaledDist) * getBlockDensity(new Vec3d(posX, posY, posZ), entityBox, entity, true, true, true, true);
                damage = Math.abs((factor * factor + factor) * 12.0f * 3.5f);
            }
        }
        if (isPlayer) {
            damage = calcDifficultyDamage(entity, damage);
        }
        return calcReductionDamage(entity, damage);
    }
    
    public static float calculateCrystalDamage(final EntityLivingBase entity, final Vec3d entityPos, final AxisAlignedBB entityBox, final double posX, final double posY, final double posZ) {
        final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        final boolean isPlayer = entity instanceof EntityPlayer;
        if (isPlayer && entity.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return 0.0f;
        }
        mutableBlockPos.setPos((int)posX, (int)posY - 1, (int)posZ);
        float damage;
        if (isPlayer && posY - entityPos.y > 1.5652173822904127 && isResistant(entity.world.getBlockState(mutableBlockPos))) {
            damage = 1.0f;
        }
        else {
            final float scaledDist = (float)(entityPos.distanceTo(new Vec3d(posX, posY, posZ)) / 12.0);
            if (scaledDist > 1.0f) {
                damage = 0.0f;
            }
            else {
                final float factor = (1.0f - scaledDist) * getBlockDensity(new Vec3d(posX, posY, posZ), entityBox, entity, true, true, true, false);
                damage = Math.abs((factor * factor + factor) * 12.0f * 3.5f);
            }
        }
        if (isPlayer) {
            damage = calcDifficultyDamage(entity, damage);
        }
        return calcReductionDamage(entity, damage);
    }
    
    private static float calcRawDamage(final EntityLivingBase entity, final Vec3d entityPos, final AxisAlignedBB entityBox, final double posX, final double posY, final double posZ, final float doubleSize, final BlockPos.MutableBlockPos mutableBlockPos, final String mode) {
        final float scaledDist = (float)(entityPos.distanceTo(new Vec3d(posX, posY, posZ)) / doubleSize);
        if (scaledDist > 1.0f) {
            return 0.0f;
        }
        final float factor = (1.0f - scaledDist) * getBlockDensity(new Vec3d(posX, posY, posZ), entityBox, entity, true, true, true, false);
        return (factor * factor + factor) * doubleSize * 3.5f + 1.0f;
    }
    
    public static boolean getDistance(final BlockPos pos, final Vec3d vec) {
        if (pos == null || vec == null) {
            return false;
        }
        final double x = pos.x + 0.5 - vec.x;
        final double z = pos.z + 0.5 - vec.z;
        if (Math.hypot(x, z) >= 2.0) {
            return false;
        }
        final double y = pos.y - vec.y;
        return true;
    }
    
    public static float ignoreTerrainDensity(final Vec3d vec, final AxisAlignedBB bb, final EntityLivingBase entity, String mode) {
        if (mode.equals("CrystalMine")) {
            BlockPos instantPos = null;
            if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                instantPos = PacketMine.INSTANCE.packetPos;
            }
            if (!getDistance(instantPos, vec)) {
                mode = "Crystal";
            }
            if (!AutoCrystal.INSTANCE.civ.getValue() && (instantPos == null || (instantPos.y != vec.y && ((int)vec.x != (int)entity.posX || (int)vec.z != (int)entity.posZ)))) {
                mode = "Crystal";
            }
        }
        final double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        final double d2 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        final double d3 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        final double d4 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        final double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        if (d0 >= 0.0 && d2 >= 0.0 && d3 >= 0.0) {
            int j2 = 0;
            int k2 = 0;
            for (float f = 0.0f; f <= 1.0f; f += (float)d0) {
                for (float f2 = 0.0f; f2 <= 1.0f; f2 += (float)d2) {
                    for (float f3 = 0.0f; f3 <= 1.0f; f3 += (float)d3) {
                        final double d6 = bb.minX + (bb.maxX - bb.minX) * f;
                        final double d7 = bb.minY + (bb.maxY - bb.minY) * f2;
                        final double d8 = bb.minZ + (bb.maxZ - bb.minZ) * f3;
                        final Vec3d newVec = new Vec3d(d6 + d4, d7, d8 + d5);
                        final RayTraceResult result = entity.world.rayTraceBlocks(newVec, vec);
                        if (result == null) {
                            ++j2;
                        }
                        else {
                            final IBlockState state = BlockUtil.getState(result.getBlockPos());
                            if (getRaytrace(entity, mode, result.getBlockPos(), state).equals("SKIP")) {
                                ++j2;
                            }
                        }
                        ++k2;
                    }
                }
            }
            return j2 / (float)k2;
        }
        return 0.0f;
    }
    
    public static boolean isResistant(final IBlockState blockState) {
        return blockState.getMaterial() != Material.AIR && !(blockState instanceof BlockLiquid) && blockState.getBlock().blockResistance >= 19.7;
    }
    
    public static boolean isResistantMine(final BlockPos pos) {
        BlockPos instantPos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
        }
        final IBlockState blockState = BlockUtil.getState(pos);
        return blockState.getMaterial() != Material.AIR && !(blockState instanceof BlockLiquid) && blockState.getBlock().blockResistance >= 19.7 && (!isPos2(instantPos, pos) || BlockUtil.getState(pos).getBlockHardness(Minecraft.getMinecraft().world, pos) < 0.0f);
    }
    
    public static String getRaytrace(final EntityLivingBase entity, final String mode, final BlockPos pos, final IBlockState blockState) {
        switch (mode) {
            case "Crystal": {
                if (isResistant(blockState)) {
                    return "CALC";
                }
                return "SKIP";
            }
            case "CrystalMine": {
                if (isResistantMine(pos)) {
                    return "CALC";
                }
                return "SKIP";
            }
            case "Bed": {
                final Block block = blockState.getBlock();
                if (block == Blocks.AIR || block == Blocks.BED || !isResistant(blockState)) {
                    return "SKIP";
                }
                return "CALC";
            }
            case "Calc": {
                return "Calc";
            }
            case "Skip": {
                return "Skip";
            }
            default: {
                return (blockState.getCollisionBoundingBox(entity.world, pos) != null) ? "CALC" : "SKIP";
            }
        }
    }
    
    public static float calcReductionDamage(final EntityLivingBase entity, final float damage) {
        final PotionEffect potionEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE);
        final float resistance = (potionEffect == null) ? 1.0f : Math.max(1.0f - (potionEffect.getAmplifier() + 1) * 0.2f, 0.0f);
        final float blastReduction = 1.0f - Math.min(calcTotalEPF(entity), 20) / 25.0f;
        return CombatRules.getDamageAfterAbsorb(damage, (float)entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()) * resistance * blastReduction;
    }
    
    public static int calcTotalEPF(final EntityLivingBase entity) {
        int epf = 0;
        for (final ItemStack itemStack : entity.getArmorInventoryList()) {
            final NBTTagList nbtTagList = itemStack.getEnchantmentTagList();
            for (int i = 0; i <= nbtTagList.tagCount(); ++i) {
                final NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
                final int id = nbtTagCompound.getInteger("id");
                final int level = nbtTagCompound.getShort("lvl");
                if (id == 0) {
                    epf += level;
                }
                else if (id == 3) {
                    epf += level * 2;
                }
            }
        }
        return epf;
    }
    
    public static float calcDifficultyDamage(final EntityLivingBase entity, final float damage) {
        switch (entity.world.getDifficulty()) {
            case PEACEFUL: {
                return 0.0f;
            }
            case EASY: {
                return Math.min(damage * 0.5f + 1.0f, damage);
            }
            case HARD: {
                return damage * 1.5f;
            }
            default: {
                return damage;
            }
        }
    }
    
    public static boolean in(final double number, final double floor, final double ceil) {
        return number >= floor && number <= ceil;
    }
    
    public static boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    public static float getBlockDensity(final Vec3d vec, final AxisAlignedBB bb, final EntityLivingBase entity, final boolean ignoreWebs, final boolean ignoreBeds, final boolean terrainCalc, boolean mine) {
        if (mine) {
            BlockPos instantPos = null;
            if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                instantPos = PacketMine.INSTANCE.packetPos;
            }
            if (AutoCrystal.INSTANCE.rangeCheck.getValue() && !getDistance(instantPos, vec)) {
                mine = false;
            }
            if (!AutoCrystal.INSTANCE.civ.getValue() && (instantPos == null || (instantPos.y != vec.y && ((int)vec.x != (int)entity.posX || (int)vec.z != (int)entity.posZ)))) {
                mine = false;
            }
        }
        final double x = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        final double y = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        final double z = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        final double xFloor = (1.0 - Math.floor(1.0 / x) * x) / 2.0;
        final double zFloor = (1.0 - Math.floor(1.0 / z) * z) / 2.0;
        if (x >= 0.0 && y >= 0.0 && z >= 0.0) {
            int air = 0;
            int traced = 0;
            for (float a = 0.0f; a <= 1.0f; a += (float)x) {
                for (float b = 0.0f; b <= 1.0f; b += (float)y) {
                    for (float c = 0.0f; c <= 1.0f; c += (float)z) {
                        final double xOff = bb.minX + (bb.maxX - bb.minX) * a;
                        final double yOff = bb.minY + (bb.maxY - bb.minY) * b;
                        final double zOff = bb.minZ + (bb.maxZ - bb.minZ) * c;
                        final RayTraceResult result = rayTraceBlocks(new Vec3d(xOff + xFloor, yOff, zOff + zFloor), vec, entity.world, false, false, false, ignoreWebs, ignoreBeds, terrainCalc, mine);
                        if (result == null) {
                            ++air;
                        }
                        ++traced;
                    }
                }
            }
            return air / (float)traced;
        }
        return 0.0f;
    }
    
    public static RayTraceResult rayTraceBlocks(final Vec3d start, final Vec3d end, final IBlockAccess world, final boolean stopOnLiquid, final boolean ignoreNoBox, final boolean lastUncollidableBlock, final boolean ignoreWebs, final boolean ignoreBeds, final boolean terrainCalc, final boolean mine) {
        BlockPos instantPos;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
        }
        else {
            instantPos = null;
        }
        return RayTracer.trace(Minecraft.getMinecraft().world, world, start, end, stopOnLiquid, ignoreNoBox, lastUncollidableBlock, (b, p) -> (!terrainCalc || b.getExplosionResistance(Minecraft.getMinecraft().player) >= 100.0f || p.distanceSq(end.x, end.y, end.z) > 36.0) && (!mine || !isPos2(p, instantPos) || BlockUtil.getState(p).getBlockHardness(Minecraft.getMinecraft().world, p) < 0.0f) && (!ignoreBeds || !(b instanceof BlockBed)) && (!ignoreWebs || !(b instanceof BlockWeb)));
    }
}
