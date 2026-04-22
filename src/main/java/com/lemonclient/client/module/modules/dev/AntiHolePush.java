// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.init.Blocks;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonMoving;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumHand;
import net.minecraft.block.state.IBlockState;
import java.util.List;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.Arrays;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiHolePush", category = Category.Dev)
public class AntiHolePush extends Module
{
    ModeSetting timeMode;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting strict;
    BooleanSetting raytrace;
    BooleanSetting trap;
    BooleanSetting packetSwitch;
    BooleanSetting entityCheck;
    BooleanSetting breakPiston;
    
    public AntiHolePush() {
        this.timeMode = this.registerMode("Time Mode", Arrays.asList("onUpdate", "Tick", "Both", "Fast"), "Fast");
        this.packet = this.registerBoolean("Packet Place", false);
        this.swing = this.registerBoolean("Swing", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.strict = this.registerBoolean("Strict", true);
        this.raytrace = this.registerBoolean("RayTrace", true);
        this.trap = this.registerBoolean("Trap", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", false);
        this.entityCheck = this.registerBoolean("Entity Check", true);
        this.breakPiston = this.registerBoolean("Break Piston", false);
    }
    
    private void switchTo(final int slot, final Runnable runnable) {
        final int oldslot = AntiHolePush.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (slot < 9) {
            final boolean packetSwitch = this.packetSwitch.getValue();
            if (packetSwitch) {
                AntiHolePush.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AntiHolePush.mc.player.inventory.currentItem = slot;
            }
            runnable.run();
            if (packetSwitch) {
                AntiHolePush.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                AntiHolePush.mc.player.inventory.currentItem = oldslot;
            }
        }
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AntiHolePush.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onUpdate() {
        if (this.timeMode.getValue().equalsIgnoreCase("onUpdate") || this.timeMode.getValue().equalsIgnoreCase("Both")) {
            this.block();
        }
    }
    
    @Override
    public void onTick() {
        if (this.timeMode.getValue().equalsIgnoreCase("Tick") || this.timeMode.getValue().equalsIgnoreCase("Both")) {
            this.block();
        }
    }
    
    @Override
    public void fast() {
        if (this.timeMode.getValue().equalsIgnoreCase("Fast")) {
            this.block();
        }
    }
    
    private void block() {
        if (AntiHolePush.mc.player == null || AntiHolePush.mc.world == null) {
            return;
        }
        final BlockPos pos = new BlockPos(AntiHolePush.mc.player.posX, AntiHolePush.mc.player.posY, AntiHolePush.mc.player.posZ);
        final int obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (obsidian == -1) {
            return;
        }
        final BlockPos head = pos.add(0, 2, 0);
        final BlockPos pos2 = pos.add(1, 1, 0);
        final BlockPos pos3 = pos.add(-1, 1, 0);
        final BlockPos pos4 = pos.add(0, 1, 1);
        final BlockPos pos5 = pos.add(0, 1, -1);
        if (!this.airBlock(head)) {
            return;
        }
        final List<BlockPos> posList = new ArrayList<BlockPos>();
        if (this.isPiston(pos2) && isFacing(pos2, EnumFacing.WEST)) {
            final BlockPos pos6 = pos.add(-1, 2, 0);
            if (this.airBlock(pos3) && this.airBlock(pos6)) {
                posList.add(pos3);
            }
            if (this.trap.getValue() && this.airBlock(head)) {
                posList.add(pos3.up());
                posList.add(head);
            }
            if (this.breakPiston.getValue()) {
                AntiHolePush.mc.playerController.onPlayerDamageBlock(pos2, BlockUtil.getRayTraceFacing(pos4));
            }
        }
        if (this.isPiston(pos3) && isFacing(pos3, EnumFacing.EAST)) {
            final BlockPos pos7 = pos.add(1, 2, 0);
            if (this.airBlock(pos2) && this.airBlock(pos7)) {
                posList.add(pos2);
            }
            if (this.trap.getValue() && this.airBlock(head)) {
                posList.add(pos2.up());
                posList.add(head);
            }
            if (this.breakPiston.getValue()) {
                AntiHolePush.mc.playerController.onPlayerDamageBlock(pos3, BlockUtil.getRayTraceFacing(pos4));
            }
        }
        if (this.isPiston(pos4) && isFacing(pos4, EnumFacing.NORTH)) {
            final BlockPos pos8 = pos.add(0, 2, -1);
            if (this.airBlock(pos5) && this.airBlock(pos8)) {
                posList.add(pos5);
            }
            if (this.trap.getValue() && this.airBlock(head)) {
                posList.add(pos5.up());
                posList.add(head);
            }
            if (this.breakPiston.getValue()) {
                AntiHolePush.mc.playerController.onPlayerDamageBlock(pos4, BlockUtil.getRayTraceFacing(pos4));
            }
        }
        if (this.isPiston(pos5) && isFacing(pos5, EnumFacing.SOUTH)) {
            final BlockPos pos9 = pos.add(0, 2, 1);
            if (this.airBlock(pos4) && this.airBlock(pos9)) {
                posList.add(pos4);
            }
            if (this.trap.getValue() && this.airBlock(head)) {
                posList.add(pos4.up());
                posList.add(head);
            }
            if (this.breakPiston.getValue()) {
                AntiHolePush.mc.playerController.onPlayerDamageBlock(pos5, BlockUtil.getRayTraceFacing(pos4));
            }
        }
        if (!posList.isEmpty()) {
            this.switchTo(obsidian, () -> {
                for (final BlockPos placePos : posList) {
                    this.perform(placePos);
                }
            });
        }
    }
    
    private IBlockState getBlock(final BlockPos block) {
        return AntiHolePush.mc.world.getBlockState(block);
    }
    
    private boolean airBlock(final BlockPos pos) {
        return BlockUtil.airBlocks.contains(this.getBlock(pos).getBlock());
    }
    
    private void perform(final BlockPos pos) {
        if ((this.entityCheck.getValue() && this.intersectsWithEntity(pos)) || !BlockUtil.canPlace(pos, this.strict.getValue(), this.raytrace.getValue())) {
            return;
        }
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue());
    }
    
    public static boolean isFacing(final BlockPos pos, final EnumFacing enumFacing) {
        final ImmutableMap<IProperty<?>, Comparable<?>> properties = AntiHolePush.mc.world.getBlockState(pos).getProperties();
        for (final IProperty<?> prop : properties.keySet()) {
            if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation")) && properties.get(prop) == enumFacing) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isPiston(final BlockPos pos) {
        return AntiHolePush.mc.world.getBlockState(pos).getBlock() instanceof BlockPistonMoving || AntiHolePush.mc.world.getBlockState(pos).getBlock() instanceof BlockPistonBase || AntiHolePush.mc.world.getBlockState(pos).getBlock() == Blocks.PISTON || AntiHolePush.mc.world.getBlockState(pos).getBlock() == Blocks.STICKY_PISTON;
    }
}
