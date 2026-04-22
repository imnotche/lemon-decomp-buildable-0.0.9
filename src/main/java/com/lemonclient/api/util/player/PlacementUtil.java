// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.client.Minecraft;

public class PlacementUtil
{
    private static final Minecraft mc;
    private static int placementConnections;
    private static boolean isSneaking;
    
    public static void onEnable() {
        ++PlacementUtil.placementConnections;
    }
    
    public static void onDisable() {
        --PlacementUtil.placementConnections;
        if (PlacementUtil.placementConnections == 0 && PlacementUtil.isSneaking) {
            PlacementUtil.mc.player.connection.sendPacket(new CPacketEntityAction(PlacementUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            PlacementUtil.isSneaking = false;
        }
    }
    
    public static void stopSneaking() {
        if (PlacementUtil.isSneaking) {
            PlacementUtil.isSneaking = false;
            PlacementUtil.mc.player.connection.sendPacket(new CPacketEntityAction(PlacementUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
    
    public static boolean placeBlock(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Class<? extends Block> blockToPlace) {
        final int oldSlot = PlacementUtil.mc.player.inventory.currentItem;
        final int newSlot = InventoryUtil.findFirstBlockSlot(blockToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.player.inventory.currentItem = newSlot;
        final boolean output = place(blockPos, hand, rotate);
        PlacementUtil.mc.player.inventory.currentItem = oldSlot;
        return output;
    }
    
    public static boolean placeItem(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Class<? extends Item> itemToPlace) {
        final int oldSlot = PlacementUtil.mc.player.inventory.currentItem;
        final int newSlot = InventoryUtil.findFirstItemSlot(itemToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.player.inventory.currentItem = newSlot;
        final boolean output = place(blockPos, hand, rotate);
        PlacementUtil.mc.player.inventory.currentItem = oldSlot;
        return output;
    }
    
    public static boolean place(final BlockPos blockPos, final EnumHand hand, final boolean rotate) {
        return placeBlock(blockPos, hand, rotate, true, null);
    }
    
    public static boolean place(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final ArrayList<EnumFacing> forceSide) {
        return placeBlock(blockPos, hand, rotate, true, forceSide);
    }
    
    public static boolean holeFill(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean swing, final ArrayList<EnumFacing> forceSide) {
        return holeFillBlock(blockPos, hand, rotate, swing, forceSide);
    }
    
    public static boolean holeFillawa(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean swing) {
        return holeFillBlockawa(blockPos, hand, rotate, swing);
    }
    
    public static boolean place(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean checkAction) {
        return placeBlock(blockPos, hand, rotate, checkAction, null);
    }
    
    public static boolean holeFill(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean swing) {
        return holeFillBlock(blockPos, hand, rotate, swing, null);
    }
    
    public static CPacketPlayer.Rotation placeBlockGetRotate(final BlockPos blockPos, final EnumHand hand, final boolean checkAction, final ArrayList<EnumFacing> forceSide, final boolean swingArm) {
        final EntityPlayerSP player = PlacementUtil.mc.player;
        final WorldClient world = PlacementUtil.mc.world;
        final PlayerControllerMP playerController = PlacementUtil.mc.playerController;
        if (player == null || world == null || playerController == null) {
            return null;
        }
        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return null;
        }
        final EnumFacing side = (forceSide != null) ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) {
            return null;
        }
        final BlockPos neighbour = blockPos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return null;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = world.getBlockState(neighbour).getBlock();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        final EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            if (swingArm) {
                player.swingArm(hand);
                PlacementUtil.mc.rightClickDelayTimer = 4;
            }
            else {
                player.connection.sendPacket(new CPacketAnimation(hand));
            }
        }
        return BlockUtil.getFaceVectorPacket(hitVec, true);
    }
    
    public static boolean placeBlock(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean checkAction, final ArrayList<EnumFacing> forceSide) {
        final EntityPlayerSP player = PlacementUtil.mc.player;
        final WorldClient world = PlacementUtil.mc.world;
        final PlayerControllerMP playerController = PlacementUtil.mc.playerController;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }
        final EnumFacing side = (forceSide != null) ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = blockPos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = world.getBlockState(neighbour).getBlock();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        final EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            player.swingArm(hand);
            PlacementUtil.mc.rightClickDelayTimer = 4;
        }
        return action == EnumActionResult.SUCCESS;
    }
    
    public static boolean holeFillBlock(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean swing, final ArrayList<EnumFacing> forceSide) {
        final EntityPlayerSP player = PlacementUtil.mc.player;
        final WorldClient world = PlacementUtil.mc.world;
        final PlayerControllerMP playerController = PlacementUtil.mc.playerController;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }
        final EnumFacing side = (forceSide != null) ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = blockPos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = world.getBlockState(neighbour).getBlock();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        final EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, hitVec, hand);
        if (swing) {
            player.swingArm(hand);
        }
        return action == EnumActionResult.SUCCESS;
    }
    
    public static boolean holeFillBlockawa(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean swing) {
        final EntityPlayerSP player = PlacementUtil.mc.player;
        final WorldClient world = PlacementUtil.mc.world;
        final PlayerControllerMP playerController = PlacementUtil.mc.playerController;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }
        BlockPos neighbour;
        EnumFacing opposite;
        if (!PlacementUtil.mc.world.isAirBlock(blockPos.south())) {
            neighbour = blockPos.offset(EnumFacing.SOUTH);
            opposite = EnumFacing.SOUTH.getOpposite();
        }
        else if (!PlacementUtil.mc.world.isAirBlock(blockPos.north())) {
            neighbour = blockPos.offset(EnumFacing.NORTH);
            opposite = EnumFacing.NORTH.getOpposite();
        }
        else if (!PlacementUtil.mc.world.isAirBlock(blockPos.east())) {
            neighbour = blockPos.offset(EnumFacing.EAST);
            opposite = EnumFacing.EAST.getOpposite();
        }
        else {
            if (PlacementUtil.mc.world.isAirBlock(blockPos.west())) {
                return false;
            }
            neighbour = blockPos.offset(EnumFacing.WEST);
            opposite = EnumFacing.WEST.getOpposite();
        }
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.8, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = world.getBlockState(neighbour).getBlock();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        final EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, hitVec, hand);
        if (swing) {
            player.swingArm(hand);
        }
        return action == EnumActionResult.SUCCESS;
    }
    
    public static boolean placePrecise(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Vec3d precise, final EnumFacing forceSide, final boolean onlyRotation, final boolean support) {
        final EntityPlayerSP player = PlacementUtil.mc.player;
        final WorldClient world = PlacementUtil.mc.world;
        final PlayerControllerMP playerController = PlacementUtil.mc.playerController;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false;
        }
        final EnumFacing side = (forceSide == null) ? BlockUtil.getPlaceableSide(blockPos) : forceSide;
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = blockPos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = world.getBlockState(neighbour).getBlock();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        if (rotate && !support) {
            BlockUtil.faceVectorPacketInstant((precise == null) ? hitVec : precise, true);
        }
        if (!onlyRotation) {
            final EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, (precise == null) ? hitVec : precise, hand);
            if (action == EnumActionResult.SUCCESS) {
                player.swingArm(hand);
                PlacementUtil.mc.rightClickDelayTimer = 4;
            }
            return action == EnumActionResult.SUCCESS;
        }
        return true;
    }
    
    static {
        mc = Minecraft.getMinecraft();
        PlacementUtil.placementConnections = 0;
        PlacementUtil.isSneaking = false;
    }
}
