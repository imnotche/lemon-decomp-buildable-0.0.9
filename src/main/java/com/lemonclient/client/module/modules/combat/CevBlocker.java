// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.function.Function;
import java.util.Comparator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.util.math.Vec3i;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.Iterator;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import java.util.Objects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "CevBlocker", category = Category.Combat)
public class CevBlocker extends Module
{
    ModeSetting time;
    BooleanSetting high;
    BooleanSetting pa;
    BooleanSetting bevel;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting packetSwitch;
    private List<BlockPos> cevPositions;
    
    public CevBlocker() {
        this.time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Both", "Fast"), "Tick");
        this.high = this.registerBoolean("High Cev", true);
        this.pa = this.registerBoolean("Ignore Bedrock", true);
        this.bevel = this.registerBoolean("Bevel", true);
        this.packet = this.registerBoolean("Packet Place", true);
        this.swing = this.registerBoolean("Swing", true);
        this.rotate = this.registerBoolean("Rotate", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.cevPositions = new ArrayList<BlockPos>();
    }
    
    private void switchTo(final int slot, final Runnable runnable) {
        final int oldslot = CevBlocker.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (slot < 9) {
            final boolean packetSwitch = this.packetSwitch.getValue();
            if (packetSwitch) {
                CevBlocker.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                CevBlocker.mc.player.inventory.currentItem = slot;
                CevBlocker.mc.playerController.updateController();
            }
            runnable.run();
            if (packetSwitch) {
                CevBlocker.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                CevBlocker.mc.player.inventory.currentItem = oldslot;
                CevBlocker.mc.playerController.updateController();
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.time.getValue().equals("onUpdate") || this.time.getValue().equals("Both")) {
            this.doBlock();
        }
    }
    
    @Override
    public void onTick() {
        if (this.time.getValue().equals("Tick") || this.time.getValue().equals("Both")) {
            this.doBlock();
        }
    }
    
    @Override
    public void fast() {
        if (this.time.getValue().equals("Fast")) {
            this.doBlock();
        }
    }
    
    private void doBlock() {
        if (CevBlocker.mc.world == null || CevBlocker.mc.player == null) {
            return;
        }
        final BlockPos[] highpos = { new BlockPos(0, 3, 0), new BlockPos(0, 4, 0), new BlockPos(1, 2, 0), new BlockPos(-1, 2, 0), new BlockPos(0, 2, 1), new BlockPos(0, 2, -1) };
        final BlockPos[] hight2 = { new BlockPos(1, 2, 1), new BlockPos(1, 2, -1), new BlockPos(-1, 2, 1), new BlockPos(-1, 2, -1) };
        final BlockPos[] offsets = { new BlockPos(0, 2, 0), new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1) };
        final BlockPos[] offsets2 = { new BlockPos(1, 1, 1), new BlockPos(1, 1, -1), new BlockPos(-1, 1, 1), new BlockPos(-1, 1, -1) };
        for (final BlockPos offset : offsets) {
            this.check(offset);
        }
        if (this.high.getValue()) {
            for (final BlockPos offset : highpos) {
                this.check(offset);
            }
        }
        if (this.bevel.getValue()) {
            for (final BlockPos offset : offsets2) {
                this.check(offset);
            }
            if (this.high.getValue()) {
                for (final BlockPos offset : hight2) {
                    this.check(offset);
                }
            }
        }
        final Iterator<BlockPos> iterator = this.cevPositions.iterator();
        while (iterator.hasNext()) {
            final BlockPos pos = iterator.next();
            if (!Objects.isNull(this.getCrystal(pos))) {
                continue;
            }
            final int obby = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (obby == -1) {
                return;
            }
            this.switchTo(obby, () -> {
                if (CevBlocker.mc.world.isAirBlock(pos)) {
                    BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                    BurrowUtil.placeBlock(pos.up(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                }
                else {
                    BurrowUtil.placeBlock(pos.up(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                }
            });
            iterator.remove();
        }
    }
    
    public void check(final BlockPos offset) {
        final BlockPos playerPos = PlayerUtil.getPlayerPos();
        final BlockPos offsetPos = playerPos.add(offset);
        final Entity crystal = this.getCrystal(offsetPos);
        if (Objects.isNull(crystal)) {
            return;
        }
        final BlockPos crystalPos = EntityUtil.getEntityPos(crystal).down();
        if (this.pa.getValue() && !CevBlocker.mc.world.isAirBlock(crystalPos) && CevBlocker.mc.world.getBlockState(crystalPos).getBlock() != Blocks.OBSIDIAN) {
            return;
        }
        if (!CevBlocker.mc.world.isAirBlock(playerPos.up().up())) {
            CevBlocker.mc.player.connection.sendPacket(new CPacketPlayer.Position(CevBlocker.mc.player.posX, playerPos.getY() + 0.2, CevBlocker.mc.player.posZ, false));
        }
        CevBlocker.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        CevBlocker.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        if (!this.cevPositions.contains(crystalPos)) {
            this.cevPositions.add(crystalPos);
        }
    }
    
    private Entity getCrystal(final BlockPos pos) {
        return CevBlocker.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).filter(e -> EntityUtil.getEntityPos(e).down().equals(pos)).min(Comparator.comparingDouble(this::getDistance)).orElse(null);
    }
    
    public double getDistance(final Entity e) {
        return CevBlocker.mc.player.getDistance(e);
    }
    
    public void onDisable() {
        this.cevPositions = new ArrayList<BlockPos>();
    }
}
