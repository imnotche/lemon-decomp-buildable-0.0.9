// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.BlockUtil;
import java.util.function.Consumer;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.Comparator;
import java.util.Collection;
import com.lemonclient.api.util.player.social.SocialManager;
import java.util.stream.Collectors;
import com.lemonclient.api.util.world.EntityUtil;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import java.util.Objects;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;

public class PlayerUtil
{
    private static final Minecraft mc;
    
    public static void setPosition(final double x, final double y, final double z) {
        PlayerUtil.mc.player.setPosition(x, y, z);
    }
    
    public static void setPosition(final BlockPos pos) {
        PlayerUtil.mc.player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
    
    public static Vec3d getMotionVector() {
        return new Vec3d(PlayerUtil.mc.player.motionX, PlayerUtil.mc.player.motionY, PlayerUtil.mc.player.motionZ);
    }
    
    public static void vClip(final double d) {
        PlayerUtil.mc.player.setPosition(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + d, PlayerUtil.mc.player.posZ);
    }
    
    public static void move(final double x, final double y, final double z) {
        PlayerUtil.mc.player.move(MoverType.SELF, x, y, z);
    }
    
    public static void setMotionVector(final Vec3d vec) {
        PlayerUtil.mc.player.motionX = vec.x;
        PlayerUtil.mc.player.motionY = vec.y;
        PlayerUtil.mc.player.motionZ = vec.z;
    }
    
    public static boolean isInsideBlock() {
        try {
            final AxisAlignedBB playerBoundingBox = PlayerUtil.mc.player.getEntityBoundingBox();
            for (int x = MathHelper.floor(playerBoundingBox.minX); x < MathHelper.floor(playerBoundingBox.maxX) + 1; ++x) {
                for (int y = MathHelper.floor(playerBoundingBox.minY); y < MathHelper.floor(playerBoundingBox.maxY) + 1; ++y) {
                    for (int z = MathHelper.floor(playerBoundingBox.minZ); z < MathHelper.floor(playerBoundingBox.maxZ) + 1; ++z) {
                        final Block block = PlayerUtil.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                        if (!(block instanceof BlockAir)) {
                            AxisAlignedBB boundingBox = Objects.requireNonNull(block.getCollisionBoundingBox(PlayerUtil.mc.world.getBlockState(new BlockPos(x, y, z)), PlayerUtil.mc.world, new BlockPos(x, y, z))).offset(x, y, z);
                            if (block instanceof BlockHopper) {
                                boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                            }
                            if (playerBoundingBox.intersects(boundingBox)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            return false;
        }
        return false;
    }
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(PlayerUtil.mc.player.posX), Math.floor(PlayerUtil.mc.player.posY + 0.5), Math.floor(PlayerUtil.mc.player.posZ));
    }
    
    public static BlockPos getPlayerFloorPos() {
        return new BlockPos(Math.floor(PlayerUtil.mc.player.posX), Math.floor(PlayerUtil.mc.player.posY), Math.floor(PlayerUtil.mc.player.posZ));
    }
    
    public static boolean isPlayerClipped() {
        return !PlayerUtil.mc.world.getCollisionBoxes(PlayerUtil.mc.player, PlayerUtil.mc.player.getEntityBoundingBox()).isEmpty();
    }
    
    public static void fakeJump() {
        fakeJump(5);
    }
    
    public static void fakeJump(final int packets) {
        if (packets > 0 && packets != 5) {
            PlayerUtil.mc.player.connection.sendPacket(new CPacketPlayer.Position(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY, PlayerUtil.mc.player.posZ, true));
        }
        if (packets > 1) {
            PlayerUtil.mc.player.connection.sendPacket(new CPacketPlayer.Position(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + 0.419999986887, PlayerUtil.mc.player.posZ, true));
        }
        if (packets > 2) {
            PlayerUtil.mc.player.connection.sendPacket(new CPacketPlayer.Position(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + 0.7531999805212, PlayerUtil.mc.player.posZ, true));
        }
        if (packets > 3) {
            PlayerUtil.mc.player.connection.sendPacket(new CPacketPlayer.Position(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + 1.0013359791121, PlayerUtil.mc.player.posZ, true));
        }
        if (packets > 4) {
            PlayerUtil.mc.player.connection.sendPacket(new CPacketPlayer.Position(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + 1.1661092609382, PlayerUtil.mc.player.posZ, true));
        }
    }
    
    public static double getDistance(final Entity entity) {
        return PlayerUtil.mc.player.getDistance(entity);
    }
    
    public static double getDistance(final BlockPos pos) {
        return PlayerUtil.mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public static double getDistanceI(final BlockPos pos) {
        return getEyeVec().distanceTo(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }
    
    public static double getDistanceL(final BlockPos pos) {
        final double x = pos.x - PlayerUtil.mc.player.posX;
        final double z = pos.z - PlayerUtil.mc.player.posZ;
        return Math.hypot(x, z);
    }
    
    public static BlockPos getEyesPos() {
        return new BlockPos(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + PlayerUtil.mc.player.getEyeHeight(), PlayerUtil.mc.player.posZ);
    }
    
    public static Vec3d getEyeVec() {
        return new Vec3d(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY + PlayerUtil.mc.player.getEyeHeight(), PlayerUtil.mc.player.posZ);
    }
    
    public static EntityPlayer getNearestPlayer(final double range) {
        final List<EntityPlayer> playerList = PlayerUtil.mc.world.playerEntities.stream().filter(p -> PlayerUtil.mc.player.getDistance(p) <= range).filter(p -> !EntityUtil.basicChecksEntity(p)).filter(p -> PlayerUtil.mc.player.entityId != p.entityId).filter(p -> !EntityUtil.isDead(p)).collect(Collectors.toList());
        final List<EntityPlayer> players = playerList.stream().filter(p -> SocialManager.isEnemy(p.getName())).collect(Collectors.toList());
        if (players.isEmpty()) {
            players.addAll(playerList);
        }
        return players.stream().min(Comparator.comparingDouble(PlayerUtil.mc.player::getDistance)).orElse(null);
    }
    
    public static EntityPlayer findLookingPlayer(final double rangeMax) {
        final ArrayList<EntityPlayer> listPlayer = new ArrayList<EntityPlayer>();
        for (final EntityPlayer playerSin : PlayerUtil.mc.world.playerEntities) {
            if (EntityUtil.basicChecksEntity(playerSin)) {
                continue;
            }
            if (PlayerUtil.mc.player.getDistance(playerSin) > rangeMax) {
                continue;
            }
            listPlayer.add(playerSin);
        }
        EntityPlayer target = null;
        final Vec3d positionEyes = PlayerUtil.mc.player.getPositionEyes(PlayerUtil.mc.getRenderPartialTicks());
        final Vec3d rotationEyes = PlayerUtil.mc.player.getLook(PlayerUtil.mc.getRenderPartialTicks());
        final int precision = 2;
        for (int i = 0; i < (int)rangeMax; ++i) {
            for (int j = precision; j > 0; --j) {
                for (final EntityPlayer targetTemp : listPlayer) {
                    final AxisAlignedBB playerBox = targetTemp.getEntityBoundingBox();
                    final double xArray = positionEyes.x + rotationEyes.x * i + rotationEyes.x / j;
                    final double yArray = positionEyes.y + rotationEyes.y * i + rotationEyes.y / j;
                    final double zArray = positionEyes.z + rotationEyes.z * i + rotationEyes.z / j;
                    if (playerBox.maxY >= yArray && playerBox.minY <= yArray && playerBox.maxX >= xArray && playerBox.minX <= xArray && playerBox.maxZ >= zArray && playerBox.minZ <= zArray) {
                        target = targetTemp;
                    }
                }
            }
        }
        return target;
    }
    
    public static List<EntityPlayer> getNearPlayers(final double range, final int count) {
        final List<EntityPlayer> targetList = new ArrayList<EntityPlayer>();
        final List<EntityPlayer> list = new ArrayList<EntityPlayer>();
        for (final EntityPlayer player : PlayerUtil.mc.world.playerEntities) {
            if (PlayerUtil.mc.player.getDistance(player) > range) {
                continue;
            }
            if (EntityUtil.basicChecksEntity(player)) {
                continue;
            }
            if (EntityUtil.isDead(player)) {
                continue;
            }
            targetList.add(player);
        }
        final List<EntityPlayer> players = targetList.stream().filter(p -> SocialManager.isEnemy(p.getName())).collect(Collectors.toList());
        if (players.isEmpty()) {
            players.addAll(targetList);
        }
        players.stream().sorted(Comparator.comparingDouble(PlayerUtil::getDistance)).forEach(list::add);
        return new ArrayList<EntityPlayer>(list.subList(0, Math.min(count, list.size())));
    }
    
    public static float getHealth() {
        return PlayerUtil.mc.player.getHealth() + PlayerUtil.mc.player.getAbsorptionAmount();
    }
    
    public static void centerPlayer() {
        double newX = -2.0;
        double newZ = -2.0;
        final int xRel = (PlayerUtil.mc.player.posX < 0.0) ? -1 : 1;
        final int zRel = (PlayerUtil.mc.player.posZ < 0.0) ? -1 : 1;
        if (BlockUtil.getBlock(PlayerUtil.mc.player.posX, PlayerUtil.mc.player.posY - 1.0, PlayerUtil.mc.player.posZ) instanceof BlockAir) {
            if (Math.abs(PlayerUtil.mc.player.posX % 1.0) * 100.0 <= 30.0) {
                newX = Math.round(PlayerUtil.mc.player.posX - 0.3 * xRel) + 0.5 * -xRel;
            }
            else if (Math.abs(PlayerUtil.mc.player.posX % 1.0) * 100.0 >= 70.0) {
                newX = Math.round(PlayerUtil.mc.player.posX + 0.3 * xRel) - 0.5 * -xRel;
            }
            if (Math.abs(PlayerUtil.mc.player.posZ % 1.0) * 100.0 <= 30.0) {
                newZ = Math.round(PlayerUtil.mc.player.posZ - 0.3 * zRel) + 0.5 * -zRel;
            }
            else if (Math.abs(PlayerUtil.mc.player.posZ % 1.0) * 100.0 >= 70.0) {
                newZ = Math.round(PlayerUtil.mc.player.posZ + 0.3 * zRel) - 0.5 * -zRel;
            }
        }
        if (newX == -2.0) {
            if (PlayerUtil.mc.player.posX > Math.round(PlayerUtil.mc.player.posX)) {
                newX = Math.round(PlayerUtil.mc.player.posX) + 0.5;
            }
            else if (PlayerUtil.mc.player.posX < Math.round(PlayerUtil.mc.player.posX)) {
                newX = Math.round(PlayerUtil.mc.player.posX) - 0.5;
            }
            else {
                newX = PlayerUtil.mc.player.posX;
            }
        }
        if (newZ == -2.0) {
            if (PlayerUtil.mc.player.posZ > Math.round(PlayerUtil.mc.player.posZ)) {
                newZ = Math.round(PlayerUtil.mc.player.posZ) + 0.5;
            }
            else if (PlayerUtil.mc.player.posZ < Math.round(PlayerUtil.mc.player.posZ)) {
                newZ = Math.round(PlayerUtil.mc.player.posZ) - 0.5;
            }
            else {
                newZ = PlayerUtil.mc.player.posZ;
            }
        }
        PlayerUtil.mc.player.connection.sendPacket(new CPacketPlayer.Position(newX, PlayerUtil.mc.player.posY, newZ, true));
        PlayerUtil.mc.player.setPosition(newX, PlayerUtil.mc.player.posY, newZ);
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
