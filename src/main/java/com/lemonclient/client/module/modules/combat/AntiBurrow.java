// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.world.World;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.block.BlockConcretePowder;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.Arrays;
import net.minecraft.block.Block;
import java.util.List;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoMineBurrow", category = Category.Combat)
public class AntiBurrow extends Module
{
    public static AntiBurrow INSTANCE;
    ModeSetting breakBlock;
    DoubleSetting balance;
    BooleanSetting up;
    BooleanSetting down;
    BooleanSetting first;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting ignore;
    BooleanSetting ignorePiston;
    BooleanSetting ignoreWeb;
    BooleanSetting fire;
    BooleanSetting sand;
    BooleanSetting rail;
    IntegerSetting range;
    BooleanSetting doubleMine;
    public double yaw;
    public double pitch;
    public boolean mining;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    public static final List<Block> airBlocks;
    
    public AntiBurrow() {
        this.breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
        this.balance = this.registerDouble("Reduce", 0.24, 0.0, 0.5);
        this.up = this.registerBoolean("Head", true);
        this.down = this.registerBoolean("Feet", true);
        this.first = this.registerBoolean("Head First", false, () -> this.up.getValue());
        this.swing = this.registerBoolean("Swing", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.ignore = this.registerBoolean("Ignore Bed", false);
        this.ignorePiston = this.registerBoolean("Ignore Piston", false);
        this.ignoreWeb = this.registerBoolean("Ignore Web", false);
        this.fire = this.registerBoolean("Fire", false);
        this.sand = this.registerBoolean("Falling Blocks", false);
        this.rail = this.registerBoolean("Rail", false);
        this.range = this.registerInteger("Range", 5, 0, 10);
        this.doubleMine = this.registerBoolean("Double Mine", false);
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (!this.rotate.getValue() || !this.mining) {
            }
            else {
                if (event.getPacket() instanceof CPacketPlayer) {
                    final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                    packet.yaw = (float)this.yaw;
                    packet.pitch = (float)this.pitch;
                }
            }
        }, new Predicate[0]);
        AntiBurrow.INSTANCE = this;
    }
    
    public void onDisable() {
        this.mining = false;
    }
    
    @Override
    public void onUpdate() {
        if (AntiBurrow.mc.world == null || AntiBurrow.mc.player == null || AntiBurrow.mc.player.isDead) {
            return;
        }
        this.mining = false;
        if (AntiRegear.INSTANCE.working) {
            return;
        }
        BlockPos instantPos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
        }
        if (instantPos != null) {
            if (instantPos.equals(new BlockPos(AntiBurrow.mc.player.posX, AntiBurrow.mc.player.posY + 2.0, AntiBurrow.mc.player.posZ))) {
                return;
            }
            if (instantPos.equals(new BlockPos(AntiBurrow.mc.player.posX, AntiBurrow.mc.player.posY - 1.0, AntiBurrow.mc.player.posZ))) {
                return;
            }
            if (AntiBurrow.mc.world.getBlockState(instantPos).getBlock() == Blocks.WEB) {
                return;
            }
        }
        final BlockPos pos = this.getCityPos(null);
        if (pos != null) {
            this.mining = true;
            if (this.doubleMine.getValue()) {
                BlockPos doublePos = null;
                if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                    doublePos = PacketMine.INSTANCE.doublePos;
                }
                if (doublePos == null) {
                    this.doBreak(this.getCityPos(pos));
                }
            }
            final double[] rotate = EntityUtil.calculateLookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, AntiBurrow.mc.player);
            this.yaw = rotate[0];
            this.pitch = rotate[1];
            this.doBreak(pos);
        }
    }
    
    public BlockPos getCityPos(final BlockPos pos) {
        final EntityPlayer player = PlayerUtil.getNearestPlayer(this.range.getValue());
        if (player == null) {
            return null;
        }
        final Vec3d[] sides = { new Vec3d(this.balance.getValue(), 0.0, this.balance.getValue()), new Vec3d(-this.balance.getValue(), 0.0, this.balance.getValue()), new Vec3d(this.balance.getValue(), 0.0, -this.balance.getValue()), new Vec3d(-this.balance.getValue(), 0.0, -this.balance.getValue()) };
        if (this.first.getValue() && this.up.getValue()) {
            for (int x = 1; x > -1 && (this.down.getValue() || x != 0); --x) {
                for (final Vec3d side : sides) {
                    final BlockPos burrowPos = new BlockPos(player.posX + side.x, player.posY + x, player.posZ + side.z);
                    if (this.intersect(player, burrowPos)) {
                        if (!this.isPos2(burrowPos, pos) && this.burrow(burrowPos)) {
                            return burrowPos;
                        }
                    }
                }
            }
        }
        else {
            for (int x = this.down.getValue() ? 0 : 1; x < 2; ++x) {
                if (!this.up.getValue() && x == 1) {
                    break;
                }
                for (final Vec3d side : sides) {
                    final BlockPos burrowPos = new BlockPos(player.posX + side.x, player.posY + x, player.posZ + side.z);
                    if (this.intersect(player, burrowPos)) {
                        if (!this.isPos2(burrowPos, pos) && this.burrow(burrowPos)) {
                            return burrowPos;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private boolean burrow(final BlockPos pos) {
        return !AntiBurrow.airBlocks.contains(AntiBurrow.mc.world.getBlockState(pos).getBlock()) && BlockUtil.getBlock(pos).blockHardness >= 0.0f && (!this.ignore.getValue() || AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.BED) && (!this.ignorePiston.getValue() || AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.PISTON_HEAD) && (!this.ignoreWeb.getValue() || AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.WEB) && (this.fire.getValue() || AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.FIRE) && (this.rail.getValue() || (AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.RAIL && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.ACTIVATOR_RAIL && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.DETECTOR_RAIL && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.GOLDEN_RAIL)) && (this.sand.getValue() || (AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.SAND && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.GRAVEL && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.ANVIL && !(AntiBurrow.mc.world.getBlockState(pos).getBlock() instanceof BlockConcretePowder)));
    }
    
    private void doBreak(final BlockPos pos) {
        if (pos == null) {
            this.mining = false;
            return;
        }
        BlockPos instantPos;
        BlockPos doublePos = instantPos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
            doublePos = PacketMine.INSTANCE.doublePos;
        }
        if (this.isPos2(instantPos, pos) || this.isPos2(doublePos, pos)) {
            return;
        }
        if (this.swing.getValue()) {
            AntiBurrow.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (this.breakBlock.getValue().equals("Packet")) {
            AntiBurrow.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            AntiBurrow.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
        }
        else {
            AntiBurrow.mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
        }
    }
    
    private boolean intersect(final EntityPlayer player, final BlockPos pos) {
        return player.boundingBox.intersects(AntiBurrow.mc.world.getBlockState(pos).getSelectedBoundingBox(AntiBurrow.mc.world, pos));
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    static {
        airBlocks = Arrays.asList(Blocks.AIR, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.GRASS);
    }
}
