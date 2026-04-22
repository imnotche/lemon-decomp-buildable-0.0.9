// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import java.util.Comparator;
import java.util.ArrayList;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.init.Blocks;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import com.lemonclient.client.module.modules.dev.BedCevBreaker;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoCity", category = Category.Combat)
public class AutoCityDev extends Module
{
    public static AutoCityDev INSTANCE;
    ModeSetting breakBlock;
    IntegerSetting range;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting ignore;
    public boolean working;
    float pitch;
    float yaw;
    BlockPos blockMine;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public AutoCityDev() {
        this.breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
        this.range = this.registerInteger("Range", 6, 0, 10);
        this.swing = this.registerBoolean("Swing", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.ignore = this.registerBoolean("Ignore Bed", false);
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (!this.rotate.getValue()) {
            }
            else {
                if (event.getPacket() instanceof CPacketPlayer) {
                    final CPacketPlayer packet = (CPacketPlayer)event.getPacket();
                    packet.yaw = this.yaw;
                    packet.pitch = this.pitch;
                }
            }
        }, new Predicate[0]);
        AutoCityDev.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        if (AutoCityDev.mc.world == null || AutoCityDev.mc.player == null || AutoCityDev.mc.player.isDead) {
            return;
        }
        this.working = false;
        if (AntiBurrow.INSTANCE.mining || AntiRegear.INSTANCE.working || CevBreaker.INSTANCE.working || BedCevBreaker.INSTANCE.working) {
            return;
        }
        BlockPos instantPos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
        }
        if (instantPos != null) {
            if (instantPos.equals(new BlockPos(AutoCityDev.mc.player.posX, AutoCityDev.mc.player.posY + 2.0, AutoCityDev.mc.player.posZ))) {
                return;
            }
            if (instantPos.equals(new BlockPos(AutoCityDev.mc.player.posX, AutoCityDev.mc.player.posY - 1.0, AutoCityDev.mc.player.posZ))) {
                return;
            }
            if (AutoCityDev.mc.world.getBlockState(instantPos).getBlock() == Blocks.WEB) {
                return;
            }
            if (this.blockMine != null && !isPos2(this.blockMine, instantPos)) {
                this.blockMine = null;
            }
        }
        final EntityPlayer aimTarget = PlayerUtil.getNearestPlayer(this.range.getValue() + 2);
        if (aimTarget == null) {
            return;
        }
        final BlockPos[] offsets = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        final BlockPos playerPos = EntityUtil.getEntityPos(aimTarget);
        if (this.blockMine != null) {
            if (AutoCityDev.mc.player.getDistance(this.blockMine.x + 0.5, this.blockMine.y + 0.5, this.blockMine.z + 0.5) > this.range.getValue()) {
                this.blockMine = null;
            }
            else {
                boolean same = false;
                for (final BlockPos offset : offsets) {
                    if (isPos2(playerPos.add(offset), this.blockMine)) {
                        same = true;
                    }
                }
                if (!same) {
                    this.blockMine = null;
                }
            }
        }
        boolean hole = true;
        for (final BlockPos offset : offsets) {
            final BlockPos pos = playerPos.add(offset);
            final IBlockState blockState = BlockUtil.getState(pos);
            if (BlockUtil.isAir(pos) || (this.ignore.getValue() && blockState == Blocks.BED)) {
                hole = false;
            }
        }
        if (!hole) {
            return;
        }
        if (this.blockMine != null) {
            this.working = true;
            return;
        }
        final EnumFacing facing = RotationUtil.getFacing(PlayerPacketManager.INSTANCE.getServerSideRotation().x);
        this.blockMine = playerPos.offset(facing, -1);
        if (AutoCityDev.mc.player.getDistance(this.blockMine.x + 0.5, this.blockMine.y + 0.5, this.blockMine.z + 0.5) > this.range.getValue() || (this.ignore.getValue() && BlockUtil.getBlock(this.blockMine) == Blocks.BED) || BlockUtil.getBlock(this.blockMine).blockHardness < 0.0f) {
            final List<BlockPos> posList = new ArrayList<BlockPos>();
            for (final BlockPos offset2 : offsets) {
                final BlockPos pos2 = playerPos.add(offset2);
                if (AutoCityDev.mc.player.getDistanceSq(pos2) <= this.range.getValue() * this.range.getValue()) {
                    if (BlockUtil.getBlock(pos2) != Blocks.BEDROCK) {
                        if (this.ignore.getValue() && BlockUtil.getBlock(pos2) == Blocks.BED) {
                            return;
                        }
                        if (AutoCityDev.mc.player.getDistance(pos2.x + 0.5, pos2.y + 0.5, pos2.z + 0.5) <= this.range.getValue()) {
                            posList.add(pos2);
                        }
                    }
                }
            }
            this.blockMine = posList.stream().min(Comparator.comparing(p -> AutoCityDev.mc.player.getDistance(p.x + 0.5, p.y + 0.5, p.z + 0.5))).orElse(null);
        }
        if (this.blockMine == null) {
            return;
        }
        this.working = true;
        if (this.swing.getValue()) {
            AutoCityDev.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (this.breakBlock.getValue().equalsIgnoreCase("Packet")) {
            AutoCityDev.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
            AutoCityDev.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
        }
        else {
            AutoCityDev.mc.playerController.onPlayerDamageBlock(this.blockMine, EnumFacing.UP);
        }
    }
    
    private static boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
}
