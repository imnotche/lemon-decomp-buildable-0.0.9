// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.function.Predicate;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import java.util.ArrayList;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiRegear", category = Category.Combat)
public class AntiRegear extends Module
{
    public static AntiRegear INSTANCE;
    DoubleSetting reach;
    BooleanSetting packet;
    BooleanSetting swing;
    List<BlockPos> selfPlaced;
    public boolean working;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public AntiRegear() {
        this.reach = this.registerDouble("Range", 5.5, 0.0, 10.0);
        this.packet = this.registerBoolean("Packet Break", false);
        this.swing = this.registerBoolean("Swing", true);
        this.selfPlaced = new ArrayList<BlockPos>();
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (AntiRegear.mc.world == null || AntiRegear.mc.player == null || AntiRegear.mc.player.isDead) {
            }
            else {
                if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                    final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                    if (AntiRegear.mc.player.getHeldItem(packet.getHand()).getItem() instanceof ItemShulkerBox) {
                        this.selfPlaced.add(packet.getPos().offset(packet.getDirection()));
                    }
                }
            }
        }, new Predicate[0]);
        AntiRegear.INSTANCE = this;
    }
    
    public void onDisable() {
        this.working = false;
    }
    
    @Override
    public void fast() {
        if (AntiRegear.mc.world == null || AntiRegear.mc.player == null || AntiRegear.mc.player.isDead) {
            this.working = false;
            return;
        }
        final List<BlockPos> sphere = new ArrayList<BlockPos>();
        BlockPos pos = null;
        for (final EntityPlayer target : PlayerUtil.getNearPlayers(16.0, 10)) {
            final Iterator<BlockPos> iterator2 = EntityUtil.getSphere(EntityUtil.getEntityPos(target), 6.5, 6.5, false, false, 0).iterator();
            while (iterator2.hasNext()) {
                pos = iterator2.next();
                if (!this.selfPlaced.contains(pos)) {
                    if (!(AntiRegear.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox)) {
                        continue;
                    }
                    if (AntiRegear.mc.player.getDistance(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) > this.reach.getValue()) {
                        continue;
                    }
                    sphere.add(pos);
                }
            }
        }
        this.working = !sphere.isEmpty();
        final Iterator<BlockPos> iterator3 = sphere.iterator();
        if (iterator3.hasNext()) {
            final BlockPos pos2 = iterator3.next();
            if (this.swing.getValue()) {
                AntiRegear.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            if (this.packet.getValue()) {
                AntiRegear.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos2, EnumFacing.UP));
                AntiRegear.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos2, EnumFacing.UP));
            }
            else {
                AntiRegear.mc.playerController.onPlayerDamageBlock(pos2, EnumFacing.UP);
            }
        }
        this.selfPlaced.removeIf(blockPos -> !(AntiRegear.mc.world.getBlockState(blockPos).getBlock() instanceof BlockShulkerBox));
    }
}
