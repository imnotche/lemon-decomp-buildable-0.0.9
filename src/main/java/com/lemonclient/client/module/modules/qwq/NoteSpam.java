// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.qwq;

import java.util.Iterator;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.Arrays;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "NoteSpam", category = Category.qwq)
public class NoteSpam extends Module
{
    ModeSetting timeMode;
    DoubleSetting range;
    IntegerSetting max;
    
    public NoteSpam() {
        this.timeMode = this.registerMode("Time Mode", Arrays.asList("onUpdate", "Tick", "Fast"), "Fast");
        this.range = this.registerDouble("Range", 5.5, 1.0, 10.0);
        this.max = this.registerInteger("MaxBlocks", 30, 1, 150);
    }
    
    @Override
    public void onUpdate() {
        if (this.timeMode.getValue().equalsIgnoreCase("onUpdate")) {
            this.doNoteSpam();
        }
    }
    
    @Override
    public void onTick() {
        if (this.timeMode.getValue().equalsIgnoreCase("Tick")) {
            this.doNoteSpam();
        }
    }
    
    @Override
    public void fast() {
        if (this.timeMode.getValue().equalsIgnoreCase("Fast")) {
            this.doNoteSpam();
        }
    }
    
    private void doNoteSpam() {
        if (NoteSpam.mc.world == null || NoteSpam.mc.player == null || NoteSpam.mc.player.isDead) {
            return;
        }
        int counter = 0;
        final List<BlockPos> posList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), this.range.getValue(), this.range.getValue(), false, true, 0);
        for (final BlockPos b : posList) {
            if (BlockUtil.getBlock(b) == Blocks.NOTEBLOCK) {
                NoteSpam.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, b, EnumFacing.UP));
                if (++counter > this.max.getValue()) {
                    return;
                }
                continue;
            }
        }
    }
}
