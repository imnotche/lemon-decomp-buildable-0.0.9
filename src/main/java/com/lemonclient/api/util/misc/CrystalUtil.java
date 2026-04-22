// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import java.util.Arrays;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import java.util.Objects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketUseEntity;
import java.util.Iterator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import java.util.List;
import net.minecraft.client.Minecraft;

public class CrystalUtil
{
    public static Minecraft mc;
    private static final List<Block> valid;
    
    public static void placeCrystal(final BlockPos pos, final boolean rotate) {
        final boolean offhand = CrystalUtil.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        final BlockPos obsPos = pos.down();
        final RayTraceResult result = CrystalUtil.mc.world.rayTraceBlocks(new Vec3d(CrystalUtil.mc.player.posX, CrystalUtil.mc.player.posY + CrystalUtil.mc.player.getEyeHeight(), CrystalUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        final EnumFacing opposite = facing.getOpposite();
        final Vec3d vec = new Vec3d(obsPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()));
        if (rotate) {
            BlockUtil.faceVector(vec);
        }
        CrystalUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(obsPos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        CrystalUtil.mc.player.swingArm(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
    }
    
    public static boolean placeCrystal(final BlockPos pos, final EnumHand hand, final boolean packet, final boolean rotate, final boolean swing) {
        final EnumFacing facing = EnumFacing.UP;
        final EnumFacing opposite = facing.getOpposite();
        final Vec3d vec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()));
        if (rotate) {
            BlockUtil.faceVector(vec);
        }
        if (packet) {
            CrystalUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        }
        else {
            CrystalUtil.mc.playerController.processRightClickBlock(CrystalUtil.mc.player, CrystalUtil.mc.world, pos, facing, vec, hand);
        }
        if (swing) {
            CrystalUtil.mc.player.swingArm(hand);
        }
        return true;
    }
    
    public static boolean isNull(final RayTraceResult result, final Entity entity) {
        return result == null || result.sideHit == null || result.entityHit == entity;
    }
    
    public static boolean calculateRaytrace(final Entity entity) {
        if (entity == null) {
            return true;
        }
        final Vec3d vec = PlayerUtil.getEyeVec();
        final Vec3d vec3d = entity.getPositionVector();
        RayTraceResult result = CrystalUtil.mc.world.rayTraceBlocks(vec, vec3d);
        if (isNull(result, entity)) {
            return true;
        }
        final double x = entity.boundingBox.maxX - entity.boundingBox.minX;
        final double y = entity.boundingBox.maxY - entity.boundingBox.minY;
        final double z = entity.boundingBox.maxZ - entity.boundingBox.minZ;
        for (double addX = -x; addX <= x; addX += x) {
            for (double addY = 0.0; addY <= y; addY += y) {
                for (double addZ = -z; addZ <= z; addZ += z) {
                    result = CrystalUtil.mc.world.rayTraceBlocks(vec, vec3d.add(addX, addY, addZ));
                    if (isNull(result, entity)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isNull(final RayTraceResult result, final BlockPos pos) {
        if (result == null || result.getBlockPos() == pos) {
            return true;
        }
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            final double distance = CrystalUtil.mc.player.getDistance(result.entityHit);
            return distance <= PlayerUtil.getDistanceI(pos);
        }
        return false;
    }
    
    public static boolean isNull(final RayTraceResult result, final Vec3d vec3d) {
        final BlockPos pos = new BlockPos(vec3d);
        return isNull(result, pos);
    }
    
    public static boolean calculateRaytrace(final BlockPos pos) {
        final Vec3d vec = PlayerUtil.getEyeVec();
        final Vec3d vec3d = new Vec3d(pos);
        RayTraceResult result = CrystalUtil.mc.world.rayTraceBlocks(vec, vec3d);
        if (isNull(result, pos)) {
            return true;
        }
        final double x = 0.5;
        final double y = 0.5;
        final double z = 0.5;
        for (double addX = 0.0; addX <= 1.0; addX += x) {
            for (double addY = 0.0; addY <= 1.0; addY += y) {
                for (double addZ = 0.0; addZ <= 1.0; addZ += z) {
                    result = CrystalUtil.mc.world.rayTraceBlocks(vec, vec3d.add(addX, addY, addZ));
                    if (isNull(result, pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean calculateRaytrace(final EntityPlayer player, final Vec3d vec3d) {
        final Vec3d vec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        RayTraceResult result = CrystalUtil.mc.world.rayTraceBlocks(vec, vec3d);
        if (isNull(result, vec3d)) {
            return true;
        }
        final double x = 0.5;
        final double y = 0.5;
        final double z = 0.5;
        for (double addX = 0.0; addX <= 1.0; addX += x) {
            for (double addY = 0.0; addY <= 1.0; addY += y) {
                for (double addZ = 0.0; addZ <= 1.0; addZ += z) {
                    result = CrystalUtil.mc.world.rayTraceBlocks(vec, vec3d.add(addX, addY, addZ));
                    if (isNull(result, vec3d)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static RayTraceResult rayTraceBlocks(final Vec3d start, final Vec3d end) {
        return rayTraceBlocks(start, end, false, false, false);
    }
    
    public static RayTraceResult rayTraceBlocks(Vec3d vec31, final Vec3d vec32, final boolean stopOnLiquid, final boolean ignoreBlockWithoutBoundingBox, final boolean returnLastUnCollidableBlock) {
        if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
            return null;
        }
        if (Double.isNaN(vec32.x) || Double.isNaN(vec32.y) || Double.isNaN(vec32.z)) {
            return null;
        }
        final int i = MathHelper.floor(vec32.x);
        final int j = MathHelper.floor(vec32.y);
        final int k = MathHelper.floor(vec32.z);
        int l = MathHelper.floor(vec31.x);
        int i2;
        int j2;
        BlockPos blockpos = new BlockPos(l, i2 = MathHelper.floor(vec31.y), j2 = MathHelper.floor(vec31.z));
        IBlockState iblockstate = CrystalUtil.mc.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        if (!CrystalUtil.valid.contains(block)) {
            block = Blocks.AIR;
            iblockstate = Blocks.AIR.getBlockState().getBaseState();
        }
        if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(CrystalUtil.mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
            return iblockstate.collisionRayTrace(CrystalUtil.mc.world, blockpos, vec31, vec32);
        }
        RayTraceResult raytraceresult2 = null;
        int k2 = 200;
        while (k2-- >= 0) {
            if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                return null;
            }
            if (l == i && i2 == j && j2 == k) {
                return returnLastUnCollidableBlock ? raytraceresult2 : null;
            }
            boolean flag2 = true;
            boolean flag3 = true;
            boolean flag4 = true;
            double d0 = 999.0;
            double d2 = 999.0;
            double d3 = 999.0;
            if (i > l) {
                d0 = l + 1.0;
            }
            else if (i < l) {
                d0 = l + 0.0;
            }
            else {
                flag2 = false;
            }
            if (j > i2) {
                d2 = i2 + 1.0;
            }
            else if (j < i2) {
                d2 = i2 + 0.0;
            }
            else {
                flag3 = false;
            }
            if (k > j2) {
                d3 = j2 + 1.0;
            }
            else if (k < j2) {
                d3 = j2 + 0.0;
            }
            else {
                flag4 = false;
            }
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = 999.0;
            final double d7 = vec32.x - vec31.x;
            final double d8 = vec32.y - vec31.y;
            final double d9 = vec32.z - vec31.z;
            if (flag2) {
                d4 = (d0 - vec31.x) / d7;
            }
            if (flag3) {
                d5 = (d2 - vec31.y) / d8;
            }
            if (flag4) {
                d6 = (d3 - vec31.z) / d9;
            }
            if (d4 == -0.0) {
                d4 = -1.0E-4;
            }
            if (d5 == -0.0) {
                d5 = -1.0E-4;
            }
            if (d6 == -0.0) {
                d6 = -1.0E-4;
            }
            EnumFacing enumfacing;
            if (d4 < d5 && d4 < d6) {
                enumfacing = ((i > l) ? EnumFacing.WEST : EnumFacing.EAST);
                vec31 = new Vec3d(d0, vec31.y + d8 * d4, vec31.z + d9 * d4);
            }
            else if (d5 < d6) {
                enumfacing = ((j > i2) ? EnumFacing.DOWN : EnumFacing.UP);
                vec31 = new Vec3d(vec31.x + d7 * d5, d2, vec31.z + d9 * d5);
            }
            else {
                enumfacing = ((k > j2) ? EnumFacing.NORTH : EnumFacing.SOUTH);
                vec31 = new Vec3d(vec31.x + d7 * d6, vec31.y + d8 * d6, d3);
            }
            l = MathHelper.floor(vec31.x) - ((enumfacing == EnumFacing.EAST) ? 1 : 0);
            i2 = MathHelper.floor(vec31.y) - ((enumfacing == EnumFacing.UP) ? 1 : 0);
            j2 = MathHelper.floor(vec31.z) - ((enumfacing == EnumFacing.SOUTH) ? 1 : 0);
            blockpos = new BlockPos(l, i2, j2);
            IBlockState iblockstate2 = CrystalUtil.mc.world.getBlockState(blockpos);
            Block block2 = iblockstate2.getBlock();
            if (!CrystalUtil.valid.contains(block2)) {
                block2 = Blocks.AIR;
                iblockstate2 = Blocks.AIR.getBlockState().getBaseState();
            }
            if (ignoreBlockWithoutBoundingBox && iblockstate2.getMaterial() != Material.PORTAL && iblockstate2.getCollisionBoundingBox(CrystalUtil.mc.world, blockpos) == Block.NULL_AABB) {
                continue;
            }
            if (block2.canCollideCheck(iblockstate2, stopOnLiquid)) {
                return iblockstate2.collisionRayTrace(CrystalUtil.mc.world, blockpos, vec31, vec32);
            }
            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
        }
        return returnLastUnCollidableBlock ? raytraceresult2 : null;
    }
    
    public static boolean canPlaceCrystal(final BlockPos pos) {
        return BlockUtil.getBlock(pos.add(0, 1, 0)) == Blocks.AIR && BlockUtil.getBlock(pos.add(0, 2, 0)) == Blocks.AIR;
    }
    
    public static List<BlockPos> getSphere(final BlockPos pos, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final ArrayList<BlockPos> circleBlocks = new ArrayList<BlockPos>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                int y = sphere ? (cy - (int)r) : cy;
                while (true) {
                    final float f = (float)y;
                    final float f2 = sphere ? (cy + r) : ((float)(cy + h));
                    if (f >= f2) {
                        break;
                    }
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                    ++y;
                }
            }
        }
        return circleBlocks;
    }
    
    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean specialEntityCheck, final boolean onepointThirteen) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (!onepointThirteen) {
                if (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (CrystalUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || CrystalUtil.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
                }
                for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
                for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
            }
            else {
                if (CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CrystalUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (CrystalUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
                }
                for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) {
                        continue;
                    }
                    return false;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void breakCrystal(final BlockPos pos, final boolean swing) {
        if (pos == null) {
            return;
        }
        for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            breakCrystal(entity, swing);
            break;
        }
    }
    
    public static void breakCrystalPacket(final BlockPos pos, final boolean swing) {
        if (pos == null) {
            return;
        }
        for (final Entity entity : CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            breakCrystalPacket(entity, swing);
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
    
    public static void breakCrystal(final Entity crystal, final boolean packet, final boolean swing, final boolean packetSwitch, final boolean switchBack, final boolean antiWeakness, final boolean weaknessBypass) {
        int slot = -1;
        if (antiWeakness && CrystalUtil.mc.player.isPotionActive(MobEffects.WEAKNESS) && (!CrystalUtil.mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(CrystalUtil.mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
            for (int b = 0; b < (weaknessBypass ? 36 : 9); ++b) {
                final ItemStack stack = CrystalUtil.mc.player.inventory.getStackInSlot(b);
                if (stack != ItemStack.EMPTY) {
                    if (stack.getItem() instanceof ItemSword) {
                        slot = b;
                        break;
                    }
                    if (stack.getItem() instanceof ItemTool) {
                        slot = b;
                    }
                }
            }
        }
        switchTo(slot, weaknessBypass, packetSwitch, switchBack, () -> {
            if (packet) {
                breakCrystalPacket(crystal, swing);
            }
            else {
                breakCrystal(crystal, swing);
            }
        });
    }
    
    public static void windowClick(final int windowId, final int slotId, final int mouseButton, final ClickType type, final EntityPlayer player) {
        final short short1 = player.openContainer.getNextTransactionID(player.inventory);
        final ItemStack itemStack = player.openContainer.slotClick(slotId, mouseButton, type, player);
        CrystalUtil.mc.player.connection.sendPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, itemStack, short1));
        CrystalUtil.mc.playerController.updateController();
        CrystalUtil.mc.player.openContainer.detectAndSendChanges();
    }
    
    private static void switchTo(int slot, final boolean bypass, boolean packetSwitch, final boolean switchBack, final Runnable runnable) {
        final int oldslot = CrystalUtil.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (bypass) {
            if (slot < 9) {
                slot += 36;
            }
            final int id = CrystalUtil.mc.player.inventoryContainer.windowId;
            windowClick(id, slot, oldslot, ClickType.SWAP, CrystalUtil.mc.player);
            CrystalUtil.mc.player.openContainer.detectAndSendChanges();
            windowClick(id, slot, oldslot, ClickType.SWAP, CrystalUtil.mc.player);
        }
        else if (slot < 9) {
            if (!switchBack) {
                packetSwitch = false;
            }
            if (packetSwitch) {
                InventoryUtil.packetSwitch(slot);
            }
            else {
                InventoryUtil.switchSlot(slot);
            }
            runnable.run();
            if (switchBack) {
                if (packetSwitch) {
                    InventoryUtil.packetSwitch(oldslot);
                }
                else {
                    InventoryUtil.switchSlot(oldslot);
                }
            }
        }
    }
    
    static {
        CrystalUtil.mc = Minecraft.getMinecraft();
        valid = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
    }
}
