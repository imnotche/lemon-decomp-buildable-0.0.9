// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.BlockObsidian;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.block.BlockConcretePowder;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockAnvil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoConcrete", category = Category.Dev)
public class AutoConcrete extends Module
{
    DoubleSetting range;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting packetSwitch;
    BooleanSetting air;
    BooleanSetting disable;
    IntegerSetting delay;
    DoubleSetting maxTargetSpeed;
    int waited;
    BlockPos[] sides;
    
    public AutoConcrete() {
        this.range = this.registerDouble("Range", 5.5, 0.0, 10.0);
        this.packet = this.registerBoolean("Packet Place", true);
        this.swing = this.registerBoolean("Swing", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.air = this.registerBoolean("Air Check", true);
        this.disable = this.registerBoolean("Disable", true);
        this.delay = this.registerInteger("Delay", 5, 0, 100, () -> !this.disable.getValue());
        this.maxTargetSpeed = this.registerDouble("Max Target Speed", 10.0, 0.0, 50.0);
        this.sides = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1) };
    }
    
    private void switchTo(final int slot, final Runnable runnable) {
        final int oldslot = AutoConcrete.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (slot < 9) {
            final boolean packetSwitch = this.packetSwitch.getValue();
            if (packetSwitch) {
                AutoConcrete.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AutoConcrete.mc.player.inventory.currentItem = slot;
            }
            runnable.run();
            if (packetSwitch) {
                AutoConcrete.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                AutoConcrete.mc.player.inventory.currentItem = oldslot;
            }
        }
    }
    
    public void onEnable() {
        this.waited = 100;
    }
    
    @Override
    public void onUpdate() {
        if (AutoConcrete.mc.world == null || AutoConcrete.mc.player == null || AutoConcrete.mc.player.isDead) {
            if (this.disable.getValue()) {
                this.disable();
            }
            return;
        }
        if (this.waited++ < this.delay.getValue()) {
            return;
        }
        this.waited = 0;
        int slot = BurrowUtil.findHotbarBlock(BlockAnvil.class);
        if (slot == -1) {
            slot = BurrowUtil.findHotbarBlock(BlockConcretePowder.class);
            if (slot == -1) {
                return;
            }
        }
        final EntityPlayer player = PlayerUtil.getNearestPlayer(this.range.getValue());
        if (LemonClient.speedUtil.getPlayerSpeed(player) > this.maxTargetSpeed.getValue()) {
            return;
        }
        if (player == null) {
            if (this.disable.getValue()) {
                this.disable();
            }
            return;
        }
        final BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        if (!BlockUtil.airBlocks.contains(AutoConcrete.mc.world.getBlockState(pos).getBlock()) && this.air.getValue()) {
            if (this.disable.getValue()) {
                this.disable();
            }
            return;
        }
        final BlockPos placePos = pos.up(2);
        if (this.intersectsWithEntity(placePos)) {
            return;
        }
        if (BurrowUtil.getFirstFacing(placePos) == null) {
            final int obby = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (obby == -1) {
                return;
            }
            boolean helped = false;
            for (final BlockPos side : this.sides) {
                final BlockPos helpingBlock = placePos.add(side);
                if (!this.intersectsWithEntity(helpingBlock)) {
                    if (BurrowUtil.getFirstFacing(helpingBlock) != null) {
                        this.switchTo(obby, () -> BurrowUtil.placeBlock(helpingBlock, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
                        helped = true;
                        break;
                    }
                    if (!this.intersectsWithEntity(helpingBlock.down())) {
                        if (BurrowUtil.getFirstFacing(helpingBlock.down()) != null) {
                            this.switchTo(obby, () -> {
                                BurrowUtil.placeBlock(helpingBlock.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                                BurrowUtil.placeBlock(helpingBlock, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                            });
                            helped = true;
                            break;
                        }
                        if (!this.intersectsWithEntity(helpingBlock.down(2)) && BurrowUtil.getFirstFacing(helpingBlock.down(2)) != null) {
                            this.switchTo(obby, () -> {
                                BurrowUtil.placeBlock(helpingBlock.down(2), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                                BurrowUtil.placeBlock(helpingBlock.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                                BurrowUtil.placeBlock(helpingBlock, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                            });
                            helped = true;
                            break;
                        }
                    }
                }
            }
            if (!helped) {
                return;
            }
        }
        this.switchTo(slot, () -> BurrowUtil.placeBlock(placePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
        if (this.disable.getValue()) {
            this.disable();
        }
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoConcrete.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
}
