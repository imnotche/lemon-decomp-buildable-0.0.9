// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world;

import java.util.Arrays;
import com.lemonclient.api.util.misc.Wrapper;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.block.state.IBlockState;
import java.util.ArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.Vec3i;
import net.minecraft.init.Items;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import net.minecraft.util.EnumHand;
import java.util.Iterator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.block.Block;
import java.util.Collection;
import net.minecraft.util.NonNullList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.client.Minecraft;
import java.util.List;

public class BlockUtil
{
    public static final List shulkerList;
    public static final List blackList;
    public static final List unSolidBlocks;
    public static final List airBlocks;
    private static final Minecraft mc;
    static EnumFacing[] facing;
    
    public static AxisAlignedBB getBoundingBox(final BlockPos pos) {
        if (pos == null) {
            return null;
        }
        final AxisAlignedBB box = getState(pos).getCollisionBoundingBox(BlockUtil.mc.world, pos);
        return (box == null) ? null : new AxisAlignedBB(pos.x + box.minX, pos.y + box.minY, pos.z + box.minZ, pos.x + box.maxX, pos.y + box.maxY, pos.z + box.maxZ);
    }
    
    public static Vec3d[] convertVec3ds(final Vec3d vec3d, final Vec3d[] input) {
        final Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }
    
    public static Vec3d[] convertVec3ds(final EntityPlayer entity, final Vec3d[] input) {
        return convertVec3ds(entity.getPositionVector(), input);
    }
    
    public static NonNullList<BlockPos> getBox(final float range) {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(EntityUtil.getSphere(new BlockPos(Math.floor(BlockUtil.mc.player.posX), Math.floor(BlockUtil.mc.player.posY), Math.floor(BlockUtil.mc.player.posZ)), (double)range, 0.0, false, true, 0));
        return positions;
    }
    
    public static NonNullList<BlockPos> getBox(final float range, final BlockPos pos) {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(EntityUtil.getSphere(pos, (double)range, 0.0, false, true, 0));
        return positions;
    }
    
    public static boolean isBlockUnSolid(final BlockPos blockPos) {
        final Block block = getBlock(blockPos);
        return isBlockUnSolid(block) || !block.fullBlock;
    }
    
    public static boolean canOpen(final BlockPos blockPos) {
        return canOpen(BlockUtil.mc.world.getBlockState(blockPos).getBlock());
    }
    
    public static boolean isAir(final BlockPos blockPos) {
        return isAir(BlockUtil.mc.world.getBlockState(blockPos).getBlock());
    }
    
    public static boolean isAirBlock(final BlockPos blockPos) {
        return isAirBlock(BlockUtil.mc.world.getBlockState(blockPos).getBlock());
    }
    
    public static boolean raytraceCheck(final BlockPos pos, final float height) {
        return BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), false, true, false) == null;
    }
    
    public static boolean canBePlace(final BlockPos pos) {
        return !checkPlayer(pos) && canReplace(pos);
    }
    
    public static boolean canBePlace(final BlockPos pos, final double distance) {
        return BlockUtil.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= distance && !checkPlayer(pos) && canReplace(pos);
    }
    
    public static boolean checkPlayer(final BlockPos pos) {
        for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!entity.isDead && !(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityExpBottle) && !(entity instanceof EntityArrow)) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    public static EnumFacing getBestNeighboring(final BlockPos pos, final EnumFacing facing) {
        for (final EnumFacing i : EnumFacing.VALUES) {
            if (facing == null || !pos.offset(i).equals(pos.offset(facing, -1))) {
                if (i != EnumFacing.DOWN) {
                    for (final EnumFacing side : getPlacableFacings(pos.offset(i), true, true)) {
                        if (!canClick(pos.offset(i).offset(side))) {
                            continue;
                        }
                        return i;
                    }
                }
            }
        }
        EnumFacing bestFacing = null;
        double distance = 0.0;
        for (final EnumFacing j : EnumFacing.VALUES) {
            if (facing == null || !pos.offset(j).equals(pos.offset(facing, -1))) {
                if (j != EnumFacing.DOWN) {
                    for (final EnumFacing side2 : getPlacableFacings(pos.offset(j), true, false)) {
                        if (canClick(pos.offset(j).offset(side2))) {
                            if (bestFacing != null && BlockUtil.mc.player.getDistanceSq(pos.offset(j)) >= distance) {
                                continue;
                            }
                            bestFacing = j;
                            distance = BlockUtil.mc.player.getDistanceSq(pos.offset(j));
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static double distanceToXZ(final double x, final double z) {
        final double dx = BlockUtil.mc.player.posX - x;
        final double dz = BlockUtil.mc.player.posZ - z;
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    public static void placeBlock(final BlockPos pos, final boolean rotate, final boolean packet, final boolean strict, final boolean raytrace, final boolean swing) {
        placeBlock(pos, EnumHand.MAIN_HAND, rotate, packet, strict, raytrace, swing);
    }
    
    public static void placeBlock(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean attackEntity, final boolean strict, final boolean raytrace, final boolean swing) {
        if (attackEntity) {
            CrystalUtil.breakCrystal(pos, swing);
        }
        placeBlock(pos, hand, rotate, packet, strict, raytrace, swing);
    }
    
    public static boolean canBlockFacing(final BlockPos pos) {
        boolean airCheck = false;
        for (final EnumFacing side : EnumFacing.values()) {
            if (canClick(pos.offset(side))) {
                airCheck = true;
            }
        }
        return airCheck;
    }
    
    public static boolean canBlockFacing(final BlockPos pos, final BlockPos check) {
        boolean airCheck = false;
        for (final EnumFacing side : EnumFacing.values()) {
            if (canClick(pos.offset(side))) {
                if (!isPos2(pos.offset(side), check)) {
                    airCheck = true;
                }
            }
        }
        return airCheck;
    }
    
    public static boolean strictPlaceCheck(final BlockPos pos, final boolean strict, final boolean raytrace) {
        if (!strict) {
            return true;
        }
        for (final EnumFacing side : getPlacableFacings(pos, true, raytrace)) {
            if (!canClick(pos.offset(side))) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    public static boolean strictPlaceCheck(final BlockPos pos, final boolean strict, final boolean raytrace, final BlockPos check) {
        if (!strict) {
            return true;
        }
        for (final EnumFacing side : getPlacableFacings(pos, true, raytrace)) {
            if (canClick(pos.offset(side))) {
                if (isPos2(pos.offset(side), check)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    public static boolean canClick(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(pos), false);
    }
    
    public static void placeCrystal(final BlockPos pos, final boolean rotate) {
        final boolean offhand = BlockUtil.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        final BlockPos obsPos = pos.down();
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        final EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        final EnumFacing opposite = facing.getOpposite();
        final Vec3d vec = new Vec3d(obsPos).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()));
        if (rotate) {
            EntityUtil.faceVector(vec);
        }
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(obsPos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        BlockUtil.mc.player.swingArm(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
    }
    
    public static boolean canPlaceCrystal(final BlockPos pos, final double distance) {
        if (BlockUtil.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > distance) {
            return false;
        }
        final BlockPos obsPos = pos.down();
        final BlockPos boost = obsPos.up();
        final BlockPos boost2 = obsPos.up(2);
        return (getBlock(obsPos) == Blocks.BEDROCK || getBlock(obsPos) == Blocks.OBSIDIAN) && getBlock(boost) == Blocks.AIR && getBlock(boost2) == Blocks.AIR && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }
    
    public static boolean canPlaceCrystal(final BlockPos pos) {
        final BlockPos obsPos = pos.down();
        final BlockPos boost = obsPos.up();
        final BlockPos boost2 = obsPos.up(2);
        return (getBlock(obsPos) == Blocks.BEDROCK || getBlock(obsPos) == Blocks.OBSIDIAN) && getBlock(boost) == Blocks.AIR && getBlock(boost2) == Blocks.AIR && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }
    
    public static List<EnumFacing> getPlacableFacings(final BlockPos pos, final boolean strictDirection, final boolean rayTrace) {
        final ArrayList<EnumFacing> validFacings = new ArrayList<EnumFacing>();
        for (final EnumFacing side : EnumFacing.values()) {
            if (!getRaytrace(pos, side)) {
                getPlaceFacing(pos, strictDirection, validFacings, side);
            }
        }
        for (final EnumFacing side : EnumFacing.values()) {
            if (!rayTrace || !getRaytrace(pos, side)) {
                getPlaceFacing(pos, strictDirection, validFacings, side);
            }
        }
        return validFacings;
    }
    
    public static List<EnumFacing> getTrapPlacableFacings(final BlockPos pos, final boolean strictDirection, final boolean rayTrace) {
        final ArrayList<EnumFacing> validFacings = new ArrayList<EnumFacing>();
        for (final EnumFacing side : BlockUtil.facing) {
            if (!getRaytrace(pos, side)) {
                getPlaceFacing(pos, strictDirection, validFacings, side);
            }
        }
        for (final EnumFacing side : BlockUtil.facing) {
            if (!rayTrace || !getRaytrace(pos, side)) {
                getPlaceFacing(pos, strictDirection, validFacings, side);
            }
        }
        return validFacings;
    }
    
    private static boolean getRaytrace(final BlockPos pos, final EnumFacing side) {
        final Vec3d testVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(0.5));
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(BlockUtil.mc.player.getPositionEyes(1.0f), testVec);
        return result != null && result.typeOfHit != RayTraceResult.Type.MISS;
    }
    
    private static void getPlaceFacing(final BlockPos pos, final boolean strictDirection, final ArrayList<EnumFacing> validFacings, final EnumFacing side) {
        final BlockPos neighbour = pos.offset(side);
        if (strictDirection) {
            final Vec3d eyePos = BlockUtil.mc.player.getPositionEyes(1.0f);
            final Vec3d blockCenter = new Vec3d(neighbour.getX() + 0.5, neighbour.getY() + 0.5, neighbour.getZ() + 0.5);
            final IBlockState blockState2 = BlockUtil.mc.world.getBlockState(neighbour);
            final boolean isFullBox = blockState2.getBlock() == Blocks.AIR || blockState2.isFullBlock();
            final ArrayList<EnumFacing> validAxis = new ArrayList<EnumFacing>();
            validAxis.addAll(checkAxis(eyePos.x - blockCenter.x, EnumFacing.WEST, EnumFacing.EAST, !isFullBox));
            validAxis.addAll(checkAxis(eyePos.y - blockCenter.y, EnumFacing.DOWN, EnumFacing.UP, true));
            validAxis.addAll(checkAxis(eyePos.z - blockCenter.z, EnumFacing.NORTH, EnumFacing.SOUTH, !isFullBox));
            if (!validAxis.contains(side.getOpposite())) {
                return;
            }
        }
        final IBlockState blockState3;
        if (!(blockState3 = BlockUtil.mc.world.getBlockState(neighbour)).getBlock().canCollideCheck(blockState3, false) || blockState3.getMaterial().isReplaceable()) {
            return;
        }
        validFacings.add(side);
    }
    
    public static ArrayList<EnumFacing> checkAxis(final double diff, final EnumFacing negativeSide, final EnumFacing positiveSide, final boolean bothIfInRange) {
        final ArrayList<EnumFacing> valid = new ArrayList<EnumFacing>();
        if (diff < -0.5) {
            valid.add(negativeSide);
        }
        if (diff > 0.5) {
            valid.add(positiveSide);
        }
        if (bothIfInRange) {
            if (!valid.contains(negativeSide)) {
                valid.add(negativeSide);
            }
            if (!valid.contains(positiveSide)) {
                valid.add(positiveSide);
            }
        }
        return valid;
    }
    
    public static boolean canPlaceEnum(final BlockPos pos, final boolean strict, final boolean raytrace) {
        return canBlockFacing(pos) && strictPlaceCheck(pos, strict, raytrace);
    }
    
    public static boolean checkEntity(final BlockPos pos) {
        for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!entity.isDead && !(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityExpBottle)) {
                if (entity instanceof EntityArrow) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean canPlace(final BlockPos pos, final double distance, final boolean strict, final boolean raytrace) {
        return BlockUtil.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= distance && canBlockFacing(pos) && canReplace(pos) && strictPlaceCheck(pos, strict, raytrace) && !checkEntity(pos);
    }
    
    public static boolean canPlace(final BlockPos pos, final boolean strict, final boolean raytrace) {
        return BlockUtil.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 6.0 && canBlockFacing(pos) && canReplace(pos) && strictPlaceCheck(pos, strict, raytrace) && !checkEntity(pos);
    }
    
    public static boolean canPlaceWithoutBase(final BlockPos pos, final boolean strict, final boolean raytrace, final boolean base) {
        return (base || canBlockFacing(pos)) && canReplace(pos) && (base || strictPlaceCheck(pos, strict, raytrace)) && !checkEntity(pos);
    }
    
    public static boolean canPlaceWithoutBase(final BlockPos pos, final boolean strict, final boolean raytrace, final boolean base, final BlockPos check) {
        return (base || canBlockFacing(pos, check)) && canReplace(pos) && (base || strictPlaceCheck(pos, strict, raytrace, check));
    }
    
    public static void placeBlock(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean strict, final boolean raytrace, final boolean swing) {
        final EnumFacing side = getFirstFacing(pos, strict, raytrace);
        if (side == null) {
            return;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        boolean sneaking = false;
        if (!ColorMain.INSTANCE.sneaking && BlockUtil.blackList.contains(getBlock(neighbour))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) {
            faceVector(hitVec);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet, swing);
        if (sneaking) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
    
    public static boolean placeBlockBoolean(final BlockPos pos, final EnumHand hand, final boolean rotate, final boolean packet, final boolean strict, final boolean raytrace, final boolean swing) {
        final EnumFacing side = getFirstFacing(pos, strict, raytrace);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        boolean sneaking = false;
        if (!ColorMain.INSTANCE.sneaking && BlockUtil.blackList.contains(getBlock(neighbour))) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) {
            faceVector(hitVec);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet, swing);
        if (sneaking) {
            BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return true;
    }
    
    public static void faceVector(final Vec3d vec) {
        final float[] rotations = EntityUtil.getLegitRotations(vec);
        EntityUtil.sendPlayerRot(rotations[0], rotations[1], BlockUtil.mc.player.onGround);
    }
    
    public static boolean posHasCrystal(final BlockPos pos) {
        for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityEnderCrystal) {
                if (!new BlockPos(entity.posX, entity.posY, entity.posZ).equals(pos)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean canReplace(final BlockPos pos) {
        return pos != null && (getState(pos).getMaterial().isReplaceable() || isAir(pos));
    }
    
    public static boolean canReplace(final Vec3d vec3d) {
        if (vec3d == null) {
            return false;
        }
        final BlockPos pos = new BlockPos(vec3d);
        return getState(pos).getMaterial().isReplaceable() || isAir(pos);
    }
    
    public static boolean isBlockUnSolid(final Block block) {
        return BlockUtil.unSolidBlocks.contains(block);
    }
    
    public static boolean canOpen(final Block block) {
        return BlockUtil.blackList.contains(block);
    }
    
    public static boolean isAir(final Block block) {
        return BlockUtil.airBlocks.contains(block);
    }
    
    public static boolean isAirBlock(final Block block) {
        return block == Blocks.AIR;
    }
    
    public static double blockDistance2d(final double blockposx, final double blockposz, final Entity owo) {
        final double deltaX = owo.posX - blockposx;
        final double deltaZ = owo.posZ - blockposz;
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }
    
    public static EnumFacing getRayTraceFacing(final BlockPos pos) {
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        if (result == null || result.sideHit == null) {
            return EnumFacing.UP;
        }
        return result.sideHit;
    }
    
    public static EnumFacing getRayTraceFacing(final BlockPos pos, final EnumFacing facing) {
        final RayTraceResult result = BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        if (result == null || result.sideHit == null) {
            return facing;
        }
        return result.sideHit;
    }
    
    public static IBlockState getState(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }
    
    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }
    
    public static CPacketPlayer.Rotation getFaceVectorPacket(final Vec3d vec, final Boolean roundAngles) {
        final float[] rotations = getNeededRotations2(vec);
        final CPacketPlayer.Rotation e = new CPacketPlayer.Rotation(rotations[0], roundAngles ? ((float)MathHelper.normalizeAngle((int)rotations[1], 360)) : rotations[1], BlockUtil.mc.player.onGround);
        BlockUtil.mc.player.connection.sendPacket(e);
        return e;
    }
    
    public static float[] calcAngleNoY(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difZ = to.z - from.z;
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0) };
    }
    
    public static BlockPos[] toBlockPos(final Vec3d[] vec3ds) {
        final BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }
    
    public static boolean hasNeighbour(final BlockPos blockPos) {
        boolean canPlace = false;
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = blockPos.offset(side);
            if (BlockUtil.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                canPlace = true;
            }
        }
        return canPlace;
    }
    
    public static boolean canPlaceBlock(final BlockPos pos) {
        return (getBlock(pos) == Blocks.AIR || getBlock(pos) instanceof BlockLiquid) && hasNeighbour(pos) && !BlockUtil.blackList.contains(getBlock(pos));
    }
    
    public static boolean canPlaceBlockFuture(final BlockPos pos) {
        return (getBlock(pos) == Blocks.AIR || getBlock(pos) instanceof BlockLiquid) && !BlockUtil.blackList.contains(getBlock(pos));
    }
    
    public static void rightClickBlock(final BlockPos pos, final EnumFacing facing, final boolean packet) {
        final Vec3d hitVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        if (packet) {
            rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, facing, hitVec, EnumHand.MAIN_HAND);
            BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    public static void rightClickBlock(final BlockPos pos, final EnumFacing facing, final Vec3d hVec, final boolean packet) {
        final Vec3d hitVec = new Vec3d(pos).add(hVec).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        if (packet) {
            rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, facing, hitVec, EnumHand.MAIN_HAND);
            BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction) {
        final float f = (float)(vec.x - pos.getX());
        final float f2 = (float)(vec.y - pos.getY());
        final float f3 = (float)(vec.z - pos.getZ());
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        BlockUtil.mc.rightClickDelayTimer = 4;
    }
    
    public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction, final boolean packet, final boolean swing) {
        if (packet) {
            final float f = (float)(vec.x - pos.getX());
            final float f2 = (float)(vec.y - pos.getY());
            final float f3 = (float)(vec.z - pos.getZ());
            BlockUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        }
        else {
            BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, pos, direction, vec, hand);
        }
        if (swing) {
            BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        BlockUtil.mc.rightClickDelayTimer = 4;
    }
    
    public static int isPositionPlaceable(final BlockPos pos, final boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }
    
    public static EnumFacing getFirstFacing(final BlockPos pos, final boolean strict, final boolean raytrace) {
        if (!strict) {
            final Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        else {
            for (final EnumFacing side : getPlacableFacings(pos, true, raytrace)) {
                if (!canClick(pos.offset(side))) {
                    continue;
                }
                return side;
            }
        }
        return null;
    }
    
    public static EnumFacing getTrapFirstFacing(final BlockPos pos, final boolean strict, final boolean raytrace) {
        if (!strict) {
            final Iterator<EnumFacing> iterator = getTrapPossibleSides(pos).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        else {
            for (final EnumFacing side : getTrapPlacableFacings(pos, true, raytrace)) {
                if (!canClick(pos.offset(side))) {
                    continue;
                }
                return side;
            }
        }
        return null;
    }
    
    public static int isPositionPlaceable(final BlockPos pos, final boolean rayTrace, final boolean entityCheck) {
        final Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        }
        if (!rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityItem)) {
                    if (entity instanceof EntityXPOrb) {
                        continue;
                    }
                    return 1;
                }
            }
        }
        for (final EnumFacing side : getPossibleSides(pos)) {
            if (!canBeClicked(pos.offset(side))) {
                continue;
            }
            return 3;
        }
        return 2;
    }
    
    public static List<EnumFacing> getPossibleSides(final BlockPos pos) {
        final List<EnumFacing> facings = new ArrayList<EnumFacing>();
        if (BlockUtil.mc.world == null || pos == null) {
            return facings;
        }
        for (final EnumFacing side : EnumFacing.VALUES) {
            final BlockPos neighbour = pos.offset(side);
            final IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
            if (blockState.getBlock().canCollideCheck(blockState, false) && !blockState.getMaterial().isReplaceable()) {
                if (canBeClicked(neighbour)) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }
    
    public static List<EnumFacing> getTrapPossibleSides(final BlockPos pos) {
        final List<EnumFacing> facings = new ArrayList<EnumFacing>();
        if (BlockUtil.mc.world == null || pos == null) {
            return facings;
        }
        for (final EnumFacing side : BlockUtil.facing) {
            final BlockPos neighbour = pos.offset(side);
            final IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
            if (blockState != null && blockState.getBlock().canCollideCheck(blockState, false)) {
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }
    
    public static boolean rayTracePlaceCheck(final BlockPos pos, final boolean shouldCheck, final float height) {
        return !shouldCheck || BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), false, true, false) == null;
    }
    
    public static boolean rayTracePlaceCheck(final BlockPos pos, final boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }
    
    public static boolean rayTracePlaceCheck(final BlockPos pos) {
        return rayTracePlaceCheck(pos, true);
    }
    
    public static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }
    
    public static Block getBlock(final double x, final double y, final double z) {
        return BlockUtil.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }
    
    public static boolean canBeClicked(final Vec3d vec3d) {
        return getBlock(new BlockPos(vec3d)).canCollideCheck(getState(new BlockPos(vec3d)), false);
    }
    
    public static void faceVectorPacketInstant(final Vec3d vec, final Boolean roundAngles) {
        final float[] rotations = getNeededRotations2(vec);
        BlockUtil.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], roundAngles ? ((float)MathHelper.normalizeAngle((int)rotations[1], 360)) : rotations[1], BlockUtil.mc.player.onGround));
    }
    
    public static void faceVectorPacketInstant2(final Vec3d vec) {
        final float[] rotations = getLegitRotations(vec);
        Wrapper.getPlayer().connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], Wrapper.getPlayer().onGround));
    }
    
    public static float[] getLegitRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { Wrapper.getPlayer().rotationYaw + MathHelper.wrapDegrees(yaw - Wrapper.getPlayer().rotationYaw), Wrapper.getPlayer().rotationPitch + MathHelper.wrapDegrees(pitch - Wrapper.getPlayer().rotationPitch) };
    }
    
    private static float[] getNeededRotations2(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { BlockUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - BlockUtil.mc.player.rotationYaw), BlockUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - BlockUtil.mc.player.rotationPitch) };
    }
    
    public static Vec3d getEyesPos() {
        return new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ);
    }
    
    public static double blockDistance(final double blockposx, final double blockposy, final double blockposz, final Entity owo) {
        final double deltaX = owo.posX - blockposx;
        final double deltaY = owo.posY - blockposy;
        final double deltaZ = owo.posZ - blockposz;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    public static List<BlockPos> getCircle(final BlockPos loc, final int y, final float r, final boolean hollow) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cz = loc.getZ();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                    final BlockPos l = new BlockPos(x, y, z);
                    circleblocks.add(l);
                }
            }
        }
        return circleblocks;
    }
    
    public static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }
        return null;
    }
    
    public static EnumFacing getPlaceableSideExlude(final BlockPos pos, final ArrayList<EnumFacing> excluding) {
        for (final EnumFacing side : EnumFacing.values()) {
            if (!excluding.contains(side)) {
                final BlockPos neighbour = pos.offset(side);
                if (BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false)) {
                    final IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
                    if (!blockState.getMaterial().isReplaceable()) {
                        return side;
                    }
                }
            }
        }
        return null;
    }
    
    public static Vec3d getCenterOfBlock(final double playerX, final double playerY, final double playerZ) {
        final double newX = Math.floor(playerX) + 0.5;
        final double newY = Math.floor(playerY);
        final double newZ = Math.floor(playerZ) + 0.5;
        return new Vec3d(newX, newY, newZ);
    }
    
    static {
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        blackList = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE, Blocks.WALL_SIGN, Blocks.STANDING_SIGN, BlockUtil.shulkerList);
        unSolidBlocks = Arrays.asList(Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.LADDER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_WIRE, Blocks.AIR, Blocks.PORTAL, Blocks.END_PORTAL, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.SAPLING, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.TALLGRASS, Blocks.DEADBUSH, Blocks.VINE, Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH, Blocks.REDSTONE_TORCH, Blocks.WEB, Blocks.PISTON_HEAD, Blocks.PISTON_EXTENSION, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, BlockUtil.shulkerList);
        airBlocks = Arrays.asList(Blocks.AIR, Blocks.MAGMA, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.FIRE, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS);
        mc = Minecraft.getMinecraft();
        BlockUtil.facing = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.EAST };
    }
}
