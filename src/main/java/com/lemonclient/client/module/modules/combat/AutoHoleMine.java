// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.block.BlockConcretePowder;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import java.util.function.Function;
import java.util.Comparator;
import java.util.ArrayList;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3i;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.init.Blocks;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import com.lemonclient.client.module.modules.dev.BedCevBreaker;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoHoleMine", category = Category.Combat)
public final class AutoHoleMine extends Module
{
    public static AutoHoleMine INSTANCE;
    BooleanSetting breakTrap;
    BooleanSetting doubleMine;
    BooleanSetting ignore;
    BooleanSetting ignorePiston;
    BooleanSetting ignoreWeb;
    BooleanSetting fire;
    BooleanSetting sand;
    public boolean working;
    BlockPos[] side;
    
    public AutoHoleMine() {
        this.breakTrap = this.registerBoolean("Break Trap", false);
        this.doubleMine = this.registerBoolean("Double Mine", true);
        this.ignore = this.registerBoolean("Ignore Bed", false);
        this.ignorePiston = this.registerBoolean("Ignore Piston", false);
        this.ignoreWeb = this.registerBoolean("Ignore Web", false);
        this.fire = this.registerBoolean("Fire", false);
        this.sand = this.registerBoolean("Falling Blocks", false);
        this.side = new BlockPos[] { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0) };
        AutoHoleMine.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        if (AutoHoleMine.mc.world == null || AutoHoleMine.mc.player == null || AutoHoleMine.mc.player.isDead) {
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
            if (instantPos.equals(new BlockPos(AutoHoleMine.mc.player.posX, AutoHoleMine.mc.player.posY + 2.0, AutoHoleMine.mc.player.posZ))) {
                return;
            }
            if (instantPos.equals(new BlockPos(AutoHoleMine.mc.player.posX, AutoHoleMine.mc.player.posY - 1.0, AutoHoleMine.mc.player.posZ))) {
                return;
            }
            if (AutoHoleMine.mc.world.getBlockState(instantPos).getBlock() == Blocks.WEB) {
                return;
            }
        }
        final EntityPlayer target = PlayerUtil.getNearestPlayer(8.0);
        if (target == null) {
            return;
        }
        final BlockPos feet = new BlockPos(target.posX, target.posY + 0.2, target.posZ);
        double breakRange = 0.0;
        BlockPos doublePos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            doublePos = PacketMine.INSTANCE.doublePos;
            breakRange = PacketMine.INSTANCE.breakRange.getValue();
        }
        BlockPos pos = null;
        for (final BlockPos side : this.side) {
            final BlockPos surroundPos = feet.add(side);
            final BlockPos crystalPos = surroundPos.add(side);
            if (BlockUtil.isAir(surroundPos)) {
                if (BlockUtil.isAir(surroundPos.up())) {
                    return;
                }
                if (BlockUtil.isAirBlock(crystalPos) && BlockUtil.isAirBlock(crystalPos.up())) {
                    if (!this.breakTrap.getValue()) {
                        return;
                    }
                    pos = surroundPos.up();
                }
            }
        }
        if (pos != null) {
            this.surroundMine(pos);
            return;
        }
        final List<BlockPos> posList = new ArrayList<BlockPos>();
        for (final BlockPos side2 : this.side) {
            final BlockPos surroundPos2 = feet.add(side2);
            final BlockPos crystalPos2 = surroundPos2.add(side2);
            if (BlockUtil.isAirBlock(crystalPos2) && BlockUtil.isAirBlock(crystalPos2.up())) {
                if (this.checkMine(surroundPos2, breakRange)) {
                    posList.add(surroundPos2);
                }
            }
            else if (BlockUtil.isAir(surroundPos2) && BlockUtil.isAirBlock(crystalPos2.up())) {
                if (this.checkMine(crystalPos2, breakRange)) {
                    posList.add(crystalPos2);
                }
            }
            else if (BlockUtil.isAir(surroundPos2) && BlockUtil.isAirBlock(crystalPos2) && this.checkMine(crystalPos2.up(), breakRange)) {
                posList.add(crystalPos2.up());
            }
        }
        if (!posList.isEmpty()) {
            this.surroundMine(posList.stream().min(Comparator.comparingDouble(this::getDistance)).orElse(null));
            return;
        }
        if (this.doubleMine.getValue()) {
            final List<DoubleBreak> breakList = new ArrayList<DoubleBreak>();
            for (final BlockPos side3 : this.side) {
                final BlockPos surroundPos3 = feet.add(side3);
                final BlockPos crystalPos3 = surroundPos3.add(side3);
                if (!BlockUtil.isAir(surroundPos3) && !BlockUtil.isAirBlock(crystalPos3) && BlockUtil.isAirBlock(crystalPos3.up())) {
                    if (this.checkMine(surroundPos3, breakRange) && this.checkMine(crystalPos3, breakRange)) {
                        breakList.add(new DoubleBreak(surroundPos3, crystalPos3));
                    }
                }
                else if (!BlockUtil.isAir(surroundPos3) && !BlockUtil.isAirBlock(crystalPos3.up()) && BlockUtil.isAirBlock(crystalPos3)) {
                    if (this.checkMine(surroundPos3, breakRange) && this.checkMine(crystalPos3.up(), breakRange)) {
                        breakList.add(new DoubleBreak(surroundPos3, crystalPos3.up()));
                    }
                }
                else if (BlockUtil.isAir(surroundPos3) && !BlockUtil.isAirBlock(crystalPos3) && !BlockUtil.isAirBlock(crystalPos3.up()) && this.checkMine(crystalPos3, breakRange) && this.checkMine(crystalPos3.up(), breakRange)) {
                    breakList.add(new DoubleBreak(crystalPos3, crystalPos3.up()));
                }
            }
            if (breakList.isEmpty()) {
                for (final BlockPos side3 : this.side) {
                    final BlockPos surroundPos3 = feet.add(side3);
                    final BlockPos crystalPos3 = surroundPos3.add(side3);
                    if (this.checkMine(surroundPos3, breakRange) && this.checkMine(crystalPos3, breakRange) && this.checkMine(crystalPos3.up(), breakRange)) {
                        breakList.add(new DoubleBreak(crystalPos3, crystalPos3.up()));
                    }
                }
            }
            if (breakList.isEmpty()) {
                for (final BlockPos side3 : this.side) {
                    final BlockPos surroundPos3 = feet.add(side3);
                    final BlockPos crystalPos3 = surroundPos3.add(side3);
                    if (!BlockUtil.isAirBlock(crystalPos3) && !BlockUtil.isAirBlock(crystalPos3.up()) && !this.checkMine(crystalPos3) && !this.checkMine(crystalPos3.up()) && this.checkMine(surroundPos3, breakRange) && this.checkMine(surroundPos3.up(), breakRange)) {
                        breakList.add(new DoubleBreak(surroundPos3, surroundPos3.up()));
                    }
                }
            }
            if (!breakList.isEmpty()) {
                final DoubleBreak doubleBreak = breakList.stream().min(Comparator.comparingDouble(DoubleBreak::maxRange)).orElse(null);
                this.surroundMine(doubleBreak.doublePos);
                if (doublePos == null) {
                    this.surroundMine(doubleBreak.packetPos);
                }
                return;
            }
        }
        else {
            for (final BlockPos side2 : this.side) {
                final BlockPos surroundPos2 = feet.add(side2);
                final BlockPos crystalPos2 = surroundPos2.add(side2);
                if (!BlockUtil.isAir(surroundPos2) && this.checkMine(surroundPos2, breakRange)) {
                    if ((BlockUtil.isAirBlock(crystalPos2) && this.checkMine(crystalPos2, breakRange)) || (BlockUtil.isAirBlock(crystalPos2.up()) && this.checkMine(crystalPos2.up(), breakRange))) {
                        posList.add(surroundPos2);
                    }
                }
                else if (!BlockUtil.isAirBlock(crystalPos2) && this.checkMine(crystalPos2, breakRange)) {
                    if ((BlockUtil.isAir(surroundPos2) && this.checkMine(surroundPos2, breakRange)) || (BlockUtil.isAirBlock(crystalPos2.up()) && this.checkMine(crystalPos2.up(), breakRange))) {
                        posList.add(crystalPos2);
                    }
                }
                else if (!BlockUtil.isAirBlock(crystalPos2.up()) && this.checkMine(crystalPos2.up(), breakRange) && ((BlockUtil.isAir(surroundPos2) && this.checkMine(surroundPos2, breakRange)) || (BlockUtil.isAirBlock(crystalPos2) && this.checkMine(crystalPos2, breakRange)))) {
                    posList.add(crystalPos2.up());
                }
            }
            if (posList.isEmpty()) {
                for (final BlockPos side2 : this.side) {
                    final BlockPos surroundPos2 = feet.add(side2);
                    final BlockPos crystalPos2 = surroundPos2.add(side2);
                    if (this.checkMine(surroundPos2, breakRange) && this.checkMine(crystalPos2, breakRange) && this.checkMine(crystalPos2.up(), breakRange)) {
                        posList.add(crystalPos2.up());
                    }
                }
            }
            if (!posList.isEmpty()) {
                this.surroundMine(posList.stream().min(Comparator.comparingDouble(this::getDistance)).orElse(null));
                return;
            }
        }
        boolean hole = true;
        for (final BlockPos offset : this.side) {
            if (BlockUtil.isAir(feet.add(offset)) && BlockUtil.isAir(feet.add(offset).up())) {
                hole = false;
            }
        }
        if (!hole) {
            return;
        }
        for (final BlockPos side3 : this.side) {
            final BlockPos surroundPos3 = feet.add(side3);
            if (this.checkMine(surroundPos3, breakRange)) {
                posList.add(surroundPos3);
            }
        }
        if (!posList.isEmpty()) {
            this.surroundMine(posList.stream().min(Comparator.comparingDouble(this::getDistance)).orElse(null));
        }
    }
    
    private boolean checkMine(final BlockPos pos) {
        return !BlockUtil.isAir(pos) && BlockUtil.getBlock(pos).blockHardness >= 0.0f && this.can(pos);
    }
    
    private boolean checkMine(final BlockPos pos, final double range) {
        return !BlockUtil.isAir(pos) && BlockUtil.getBlock(pos).blockHardness >= 0.0f && this.can(pos) && this.getDistance(pos) <= range;
    }
    
    private boolean can(final BlockPos pos) {
        return (!this.ignore.getValue() || AutoHoleMine.mc.world.getBlockState(pos).getBlock() != Blocks.BED) && (!this.ignorePiston.getValue() || AutoHoleMine.mc.world.getBlockState(pos).getBlock() != Blocks.PISTON_HEAD) && (!this.ignoreWeb.getValue() || AutoHoleMine.mc.world.getBlockState(pos).getBlock() != Blocks.WEB) && (this.fire.getValue() || AutoHoleMine.mc.world.getBlockState(pos).getBlock() != Blocks.FIRE) && (this.sand.getValue() || (AutoHoleMine.mc.world.getBlockState(pos).getBlock() != Blocks.SAND && AutoHoleMine.mc.world.getBlockState(pos).getBlock() != Blocks.GRAVEL && AutoHoleMine.mc.world.getBlockState(pos).getBlock() != Blocks.ANVIL && !(AutoHoleMine.mc.world.getBlockState(pos).getBlock() instanceof BlockConcretePowder)));
    }
    
    private void surroundMine(final BlockPos pos) {
        if (pos == null || !this.checkMine(pos)) {
            return;
        }
        this.working = true;
        BlockPos instantPos;
        BlockPos doublePos = instantPos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
            doublePos = PacketMine.INSTANCE.doublePos;
        }
        if (instantPos != null && instantPos.equals(pos)) {
            return;
        }
        if (doublePos != null && doublePos.equals(pos)) {
            return;
        }
        AutoHoleMine.mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos));
    }
    
    private double getDistance(final BlockPos pos) {
        return AutoHoleMine.mc.player.getDistance(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
    }
    
    class DoubleBreak
    {
        BlockPos packetPos;
        BlockPos doublePos;
        
        public DoubleBreak(final BlockPos packetPos, final BlockPos doublePos) {
            this.packetPos = packetPos;
            this.doublePos = doublePos;
        }
        
        public double maxRange() {
            final double packetRange = AutoHoleMine.this.getDistance(this.packetPos);
            final double doubleRange = AutoHoleMine.this.getDistance(this.doublePos);
            return Math.max(packetRange, doubleRange);
        }
    }
}
