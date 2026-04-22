// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.ArrayList;
import net.minecraft.block.BlockEnderChest;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import com.lemonclient.api.util.misc.CrystalUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.block.BlockLiquid;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BurrowBypass", category = Category.Combat)
public class BurrowBypass extends Module
{
    BooleanSetting multiPlace;
    BooleanSetting tpCenter;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting strict;
    BooleanSetting raytrace;
    ModeSetting jumpMode;
    ModeSetting bypassMode;
    ModeSetting rubberBand;
    DoubleSetting offsetX;
    DoubleSetting offsetY;
    DoubleSetting offsetZ;
    BooleanSetting head;
    BooleanSetting onlyOnGround;
    BooleanSetting air;
    ModeSetting mode;
    BooleanSetting packetSwitch;
    BooleanSetting breakCrystal;
    BooleanSetting packetBreak;
    BooleanSetting antiWk;
    BooleanSetting weakBypass;
    BooleanSetting testMode;
    BooleanSetting move;
    boolean moved;
    Vec3d[] offsets;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveListener;
    
    public BurrowBypass() {
        this.multiPlace = this.registerBoolean("MultiPlace", false);
        this.tpCenter = this.registerBoolean("TPCenter", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.packet = this.registerBoolean("PacketPlace", true);
        this.swing = this.registerBoolean("Swing", true);
        this.strict = this.registerBoolean("Strict", true);
        this.raytrace = this.registerBoolean("RayTrace", true);
        this.jumpMode = this.registerMode("JumpMode", Arrays.asList("Normal", "Future", "Strict"), "Normal");
        this.bypassMode = this.registerMode("Bypass", Arrays.asList("Normal", "Middle", "Test"), "Normal");
        this.rubberBand = this.registerMode("RubberBand", Arrays.asList("Cn", "Strict", "Future", "FutureStrict", "Troll", "Void", "Auto", "Test", "Custom"), "Cn");
        this.offsetX = this.registerDouble("OffsetX", -7.0, -10.0, 10.0, () -> this.rubberBand.getValue().equals("Custom"));
        this.offsetY = this.registerDouble("OffsetY", -7.0, -10.0, 10.0, () -> this.rubberBand.getValue().equals("Custom"));
        this.offsetZ = this.registerDouble("OffsetZ", -7.0, -10.0, 10.0, () -> this.rubberBand.getValue().equals("Custom"));
        this.head = this.registerBoolean("Head", true);
        this.onlyOnGround = this.registerBoolean("OnGroundOnly", true);
        this.air = this.registerBoolean("NotAir", true);
        this.mode = this.registerMode("BlockMode", Arrays.asList("Obsidian", "EChest", "ObbyEChest", "EChestObby"), "ObbyEChest");
        this.packetSwitch = this.registerBoolean("Packet Switch", false);
        this.breakCrystal = this.registerBoolean("BreakCrystal", true);
        this.packetBreak = this.registerBoolean("PacketBreak", true, () -> this.breakCrystal.getValue());
        this.antiWk = this.registerBoolean("AntiWeak", true, () -> this.breakCrystal.getValue());
        this.weakBypass = this.registerBoolean("BypassSwitch", true, () -> this.breakCrystal.getValue() && this.antiWk.getValue());
        this.testMode = this.registerBoolean("TestMode", true);
        this.move = this.registerBoolean("Move", true, () -> this.testMode.getValue());
        this.offsets = new Vec3d[] { new Vec3d(0.3, 0.0, 0.3), new Vec3d(-0.3, 0.0, 0.3), new Vec3d(0.3, 0.0, -0.3), new Vec3d(-0.3, 0.0, -0.3) };
        this.playerMoveListener = new Listener<PlayerMoveEvent>(event -> {
            if (BurrowBypass.mc.player.isEntityAlive() && !BurrowBypass.mc.player.isElytraFlying() && !BurrowBypass.mc.player.capabilities.isFlying) {
                if (!this.moved) {
                    BlockPos blockPos = PlayerUtil.getPlayerPos();
                    final Vec3d[] array = { new Vec3d(0.4, 0.0, 0.4), new Vec3d(0.4, 0.0, -0.4), new Vec3d(-0.4, 0.0, 0.4), new Vec3d(-0.4, 0.0, -0.4) };
                    final int length = array.length;
                    int i = 0;
                    while (i < length) {
                        final Vec3d vec3d = array[i];
                        final BlockPos pos = new BlockPos(BurrowBypass.mc.player.posX + vec3d.x, BurrowBypass.mc.player.posY, BurrowBypass.mc.player.posZ + vec3d.z);
                        if (BlockUtil.isAir(pos.down()) && BurrowBypass.mc.world.isAirBlock(pos) && BurrowBypass.mc.world.isAirBlock(pos.up()) && BurrowBypass.mc.world.isAirBlock(pos.up(2))) {
                            blockPos = pos;
                            break;
                        }
                        else {
                            ++i;
                        }
                    }
                    final double x = this.roundToClosest(BurrowBypass.mc.player.posX, blockPos.x + 0.02, blockPos.x + 0.98);
                    final double y = BurrowBypass.mc.player.posY;
                    final double z = this.roundToClosest(BurrowBypass.mc.player.posZ, blockPos.z + 0.02, blockPos.z + 0.98);
                    final Vec3d playerPos = BurrowBypass.mc.player.getPositionVector();
                    final double yawRad = Math.toRadians(RotationUtil.getRotationTo(playerPos, new Vec3d(x, y, z)).x);
                    final double dist = Math.hypot(x - playerPos.x, z - playerPos.z);
                    if (x - playerPos.x == 0.0 && z - playerPos.z == 0.0) {
                        this.moved = true;
                    }
                    final double playerSpeed = MotionUtil.getBaseMoveSpeed() * ((EntityUtil.isColliding(0.0, -0.5, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.91 : 1.0);
                    final double speed = Math.min(dist, playerSpeed);
                    event.setX(-Math.sin(yawRad) * speed);
                    event.setZ(Math.cos(yawRad) * speed);
                    if (LemonClient.speedUtil.getPlayerSpeed(BurrowBypass.mc.player) == 0.0) {
                        this.moved = true;
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public void breakCrystal() {
        final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(getFlooredPosition(BurrowBypass.mc.player));
        final List<Entity> l = BurrowBypass.mc.world.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);
        for (final Entity entity : l) {
            if (entity instanceof EntityEnderCrystal) {
                CrystalUtil.breakCrystal(entity, this.packetBreak.getValue(), this.swing.getValue(), this.packetSwitch.getValue(), true, this.antiWk.getValue(), this.weakBypass.getValue());
                break;
            }
        }
    }
    
    public static void back() {
        for (final Entity crystal : BurrowBypass.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> BurrowBypass.mc.player.getDistance(e))).collect(Collectors.toList())) {
            if (crystal instanceof EntityEnderCrystal) {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
            }
        }
    }
    
    private double roundToClosest(final double num, final double low, final double high) {
        final double d2 = high - num;
        final double d3 = num - low;
        if (d2 > d3) {
            return low;
        }
        return high;
    }
    
    private boolean canGoTo(final BlockPos pos) {
        return isAir(pos) && isAir(pos.up());
    }
    
    public void onEnable() {
        this.moved = !this.move.getValue();
        if (this.onlyOnGround.getValue() && !BurrowBypass.mc.player.onGround) {
            this.disable();
            return;
        }
        if (this.air.getValue() && BurrowBypass.mc.world.getBlockState(getFlooredPosition(BurrowBypass.mc.player).offset(EnumFacing.DOWN)).getBlock().equals(Blocks.AIR)) {
            this.disable();
        }
    }
    
    @Override
    public void onUpdate() {
        final BlockPos playerPos = new BlockPos(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.5, BurrowBypass.mc.player.posZ);
        final Vec3d vecPos = new Vec3d(BurrowBypass.mc.player.posX, (int)(BurrowBypass.mc.player.posY + 0.5), BurrowBypass.mc.player.posZ);
        final int a = BurrowBypass.mc.player.inventory.currentItem;
        int slot = -1;
        final String s = this.mode.getValue();
        switch (s) {
            case "Obsidian": {
                slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                break;
            }
            case "EChest": {
                slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
                break;
            }
            case "EChestObby": {
                slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
                if (slot == -1) {
                    slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                    break;
                }
                break;
            }
            case "ObbyEChest": {
                slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                if (slot == -1) {
                    slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
                    break;
                }
                break;
            }
        }
        if (slot == -1) {
            this.disable();
            return;
        }
        if (this.testMode.getValue()) {
            if (!this.moved) {
                return;
            }
            boolean burrow = false;
            for (final Vec3d vec3d : this.offsets) {
                if (!this.isPos2(new BlockPos(vecPos.add(vec3d)), playerPos)) {
                    burrow = true;
                    break;
                }
            }
            if (!burrow) {
                this.disable();
                return;
            }
        }
        if (this.breakCrystal.getValue()) {
            back();
        }
        if (!BurrowBypass.mc.world.isBlockLoaded(BurrowBypass.mc.player.getPosition()) || BurrowBypass.mc.player.isInLava() || BurrowBypass.mc.player.isInWater() || BurrowBypass.mc.player.isInWeb) {
            this.disable();
            return;
        }
        if (this.tpCenter.getValue()) {
            PlayerUtil.centerPlayer();
        }
        boolean bypassed;
        if (!this.fakeBBoxCheck()) {
            if ((this.testMode.getValue() && !this.bypassBurrowed()) || ((!BlockUtil.canReplace(playerPos) || !BlockUtil.canReplace(playerPos.up())) && this.intersect(playerPos.up()))) {
                this.gotoPos(playerPos);
            }
            else {
                final List<BlockPos> posList = new ArrayList<BlockPos>();
                final List<BlockPos> airList = new ArrayList<BlockPos>();
                if (this.testMode.getValue()) {
                    airList.add(playerPos);
                    for (final Vec3d vec : this.offsets) {
                        final BlockPos pos = new BlockPos(vecPos.add(vec));
                        if (BlockUtil.isAir(pos)) {
                            posList.add(pos);
                        }
                    }
                }
                else {
                    for (final Vec3d vec : this.offsets) {
                        boolean air = true;
                        final BlockPos pos2 = new BlockPos(vecPos.add(vec));
                        for (int i = 0; i < 2; ++i) {
                            final BlockPos blockPos = pos2.up(i);
                            if (!isAir(blockPos)) {
                                air = false;
                            }
                        }
                        if (this.intersect(pos2) && !air) {
                            posList.add(pos2);
                        }
                        else {
                            airList.add(pos2);
                        }
                    }
                }
                final BlockPos movePos = posList.isEmpty() ? airList.stream().min(Comparator.comparing(p -> BurrowBypass.mc.player.getDistance(p.x + 0.5, BurrowBypass.mc.player.posY, p.z + 0.5))).orElse(null) : posList.stream().min(Comparator.comparing(p -> BurrowBypass.mc.player.getDistance(p.x + 0.5, BurrowBypass.mc.player.posY, p.z + 0.5))).orElse(null);
                this.gotoPos(movePos);
            }
            bypassed = true;
        }
        else {
            bypassed = false;
            final String s2 = this.jumpMode.getValue();
            switch (s2) {
                case "Normal": {
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.419999986886978, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.7531999805212015, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.001335979112147, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.166109260938214, BurrowBypass.mc.player.posZ, false));
                    break;
                }
                case "Future": {
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.419997486886978, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.7500025, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.999995, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.170005001788139, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.2426050013947485, BurrowBypass.mc.player.posZ, false));
                    break;
                }
                case "Strict": {
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.419998586886978, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.7500014, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.9999972, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.170002801788139, BurrowBypass.mc.player.posZ, false));
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.170009801788139, BurrowBypass.mc.player.posZ, false));
                    break;
                }
            }
        }
        InventoryUtil.run(slot, this.packetSwitch.getValue(), () -> {
            if (!this.multiPlace.getValue()) {
                this.placeBlock(new BlockPos(this.getPlayerPosFixY(BurrowBypass.mc.player)));
            }
            else {
                final Vec3d[] offsets4 = this.offsets;
                int n6 = 0;
                for (int length4 = offsets4.length; n6 < length4; ++n6) {
                    final Vec3d vec3d2 = offsets4[n6];
                    this.placeBlock(vecPos.add(vec3d2));
                }
                if (this.head.getValue() && bypassed) {
                    final Vec3d[] offsets5 = this.offsets;
                    int n7 = 0;
                    for (int length5 = offsets5.length; n7 < length5; ++n7) {
                        final Vec3d vec3d3 = offsets5[n7];
                        this.placeBlock(vecPos.add(vec3d3).add(0.0, 1.0, 0.0));
                    }
                }
            }
        });
        final String s3 = this.rubberBand.getValue();
        switch (s3) {
            case "Cn": {
                double distance = 0.0;
                BlockPos bestPos = null;
                for (final BlockPos pos : BlockUtil.getBox(6.0f)) {
                    if (this.canGoTo(pos)) {
                        if (BurrowBypass.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 3.0) {
                            continue;
                        }
                        if (bestPos != null && BurrowBypass.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) >= distance) {
                            continue;
                        }
                        bestPos = pos;
                        distance = BurrowBypass.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    }
                }
                if (bestPos != null) {
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(bestPos.getX() + 0.5, bestPos.getY(), bestPos.getZ() + 0.5, false));
                    break;
                }
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, -7.0, BurrowBypass.mc.player.posZ, false));
                break;
            }
            case "Future": {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.242609801394749, BurrowBypass.mc.player.posZ, false));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 2.340028003576279, BurrowBypass.mc.player.posZ, false));
                break;
            }
            case "FutureStrict": {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.315205001001358, BurrowBypass.mc.player.posZ, false));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 1.315205001001358, BurrowBypass.mc.player.posZ, false));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 2.485225002789497, BurrowBypass.mc.player.posZ, false));
                break;
            }
            case "Troll": {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 3.3400880035762786, BurrowBypass.mc.player.posZ, false));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY - 1.0, BurrowBypass.mc.player.posZ, false));
                break;
            }
            case "Strict": {
                double distance = 0.0;
                BlockPos bestPos = null;
                for (int j = 0; j < 20; ++j) {
                    final BlockPos pos = new BlockPos(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.5 + j, BurrowBypass.mc.player.posZ);
                    if (this.canGoTo(pos) && BurrowBypass.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 5.0 && (bestPos == null || BurrowBypass.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < distance)) {
                        bestPos = pos;
                        distance = BurrowBypass.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    }
                }
                if (bestPos != null) {
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(bestPos.getX() + 0.5, bestPos.getY(), bestPos.getZ() + 0.5, false));
                    break;
                }
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, -7.0, BurrowBypass.mc.player.posZ, false));
                break;
            }
            case "Void": {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, -7.0, BurrowBypass.mc.player.posZ, false));
                break;
            }
            case "Auto": {
                for (int k = -10; k < 10; ++k) {
                    if (k == -1) {
                        k = 4;
                    }
                    if (BurrowBypass.mc.world.getBlockState(getFlooredPosition(BurrowBypass.mc.player).add(0, k, 0)).getBlock().equals(Blocks.AIR) && BurrowBypass.mc.world.getBlockState(getFlooredPosition(BurrowBypass.mc.player).add(0, k + 1, 0)).getBlock().equals(Blocks.AIR)) {
                        final BlockPos pos3 = getFlooredPosition(BurrowBypass.mc.player).add(0, k, 0);
                        BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos3.getX() + 0.3, pos3.getY(), pos3.getZ() + 0.3, false));
                        break;
                    }
                }
                break;
            }
            case "Custom": {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX + this.offsetX.getValue(), BurrowBypass.mc.player.posY + this.offsetY.getValue(), BurrowBypass.mc.player.posZ + this.offsetZ.getValue(), false));
                break;
            }
        }
        this.disable();
    }
    
    private void gotoPos(final BlockPos pos) {
        final String s = this.bypassMode.getValue();
        switch (s) {
            case "Normal": {
                if (Math.abs(pos.getX() + 0.5 - BurrowBypass.mc.player.posX) < Math.abs(pos.getZ() + 0.5 - BurrowBypass.mc.player.posZ)) {
                    BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + 0.2, pos.getZ() + 0.5, true));
                    break;
                }
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.getX() + 0.5, BurrowBypass.mc.player.posY + 0.2, BurrowBypass.mc.player.posZ, true));
                break;
            }
            case "Middle": {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.getX() + 0.5, BurrowBypass.mc.player.posY + 0.2, pos.getZ() + 0.5, true));
                break;
            }
            case "Test": {
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX + (pos.getX() + 0.5 - BurrowBypass.mc.player.posX) * 0.42132, BurrowBypass.mc.player.posY + 0.12160004615784, BurrowBypass.mc.player.posZ + (pos.getZ() + 0.5 - BurrowBypass.mc.player.posZ) * 0.42132, false));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX + (pos.getX() + 0.5 - BurrowBypass.mc.player.posX) * 0.95, BurrowBypass.mc.player.posY + 0.200000047683716, BurrowBypass.mc.player.posZ + (pos.getZ() + 0.5 - BurrowBypass.mc.player.posZ) * 0.95, false));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX + (pos.getX() + 0.5 - BurrowBypass.mc.player.posX) * 1.03, BurrowBypass.mc.player.posY + 0.200000047683716, BurrowBypass.mc.player.posZ + (pos.getZ() + 0.5 - BurrowBypass.mc.player.posZ) * 1.03, false));
                BurrowBypass.mc.player.connection.sendPacket(new CPacketPlayer.Position(BurrowBypass.mc.player.posX + (pos.getX() + 0.5 - BurrowBypass.mc.player.posX) * 1.0933, BurrowBypass.mc.player.posY + 0.12160004615784, BurrowBypass.mc.player.posZ + (pos.getZ() + 0.5 - BurrowBypass.mc.player.posZ) * 1.0933, false));
                break;
            }
        }
    }
    
    private boolean intersect(final BlockPos pos) {
        final AxisAlignedBB box = BlockUtil.getBoundingBox(pos);
        return box != null && BurrowBypass.mc.player.boundingBox.intersects(box);
    }
    
    public static BlockPos getFlooredPosition(final Entity entity) {
        return new BlockPos(Math.floor(entity.posX), (double)Math.round(entity.posY), Math.floor(entity.posZ));
    }
    
    private boolean fakeBBoxCheck() {
        Vec3d playerPos = BurrowBypass.mc.player.getPositionVector();
        playerPos = new Vec3d(playerPos.x, (int)(playerPos.y + 0.5), playerPos.z);
        for (final Vec3d vec : this.offsets) {
            for (int i = 0; i < 3; ++i) {
                final BlockPos pos = new BlockPos(playerPos.add(vec).add(0.0, i, 0.0));
                if (i >= 2 || this.intersect(pos)) {
                    if (!isAir(pos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean isAir(final Vec3d vec3d) {
        return isAir(new BlockPos(vec3d));
    }
    
    public static boolean isAir(final BlockPos pos) {
        return BlockUtil.canReplace(pos);
    }
    
    private void placeBlock(final BlockPos pos) {
        BlockUtil.placeBlockBoolean(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue());
    }
    
    public static Vec3d getEyesPos() {
        return new Vec3d(BurrowBypass.mc.player.posX, BurrowBypass.mc.player.posY + BurrowBypass.mc.player.getEyeHeight(), BurrowBypass.mc.player.posZ);
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    private void placeBlock(final Vec3d vec3d) {
        final BlockPos pos = new BlockPos(vec3d);
        if (this.testMode.getValue() && (!this.bypassBurrowed() || !this.head.getValue()) && this.isPos2(pos, PlayerUtil.getPlayerPos())) {
            return;
        }
        this.placeBlock(pos);
    }
    
    private BlockPos getPlayerPosFixY(final EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), (double)Math.round(player.posY), Math.floor(player.posZ));
    }
    
    private boolean bypassBurrowed() {
        final Vec3d pos = new Vec3d(BurrowBypass.mc.player.posX, (int)(BurrowBypass.mc.player.posY + 0.5), BurrowBypass.mc.player.posZ);
        for (final Vec3d vec3d : this.offsets) {
            if (!BlockUtil.isAir(new BlockPos(pos.add(vec3d)).up())) {
                return true;
            }
        }
        return false;
    }
}
