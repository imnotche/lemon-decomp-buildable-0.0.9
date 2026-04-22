// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.misc.MathUtil;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import java.util.List;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.util.EnumHand;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import java.util.ArrayList;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import com.lemonclient.client.LemonClient;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
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

@Module.Declaration(name = "AntiCity", category = Category.Combat)
public class AntiCity extends Module
{
    ModeSetting time;
    IntegerSetting bpt;
    BooleanSetting self;
    BooleanSetting smart;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BlockPos breakPos;
    private int placeID;
    @EventHandler
    private final Listener<PacketEvent.PostSend> sendListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    int placed;
    
    public AntiCity() {
        this.time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Fast"), "Tick");
        this.bpt = this.registerInteger("Blocks Per Tick", 4, 0, 20);
        this.self = this.registerBoolean("Self", false);
        this.smart = this.registerBoolean("Smart", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.packet = this.registerBoolean("Packet", true);
        this.swing = this.registerBoolean("Swing", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.sendListener = new Listener<PacketEvent.PostSend>(event -> {
            if (AntiCity.mc.world == null || AntiCity.mc.player == null) {
            }
            else if (!this.self.getValue()) {
            }
            else {
                if (event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging)event.getPacket()).getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                    final CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
                    final BlockPos ab = packet.getPosition();
                    this.breakPos = packet.getPosition();
                    final BlockPos player = EntityUtil.getPlayerPos(AntiCity.mc.player);
                    if (ab.equals(player.add(1, 0, 0))) {
                        this.placeID = 1;
                    }
                    if (ab.equals(player.add(-1, 0, 0))) {
                        this.placeID = 2;
                    }
                    if (ab.equals(player.add(0, 0, 1))) {
                        this.placeID = 3;
                    }
                    if (ab.equals(player.add(0, 0, -1))) {
                        this.placeID = 4;
                    }
                    if (ab.equals(player.add(2, 0, 0))) {
                        this.placeID = 5;
                    }
                    if (ab.equals(player.add(-2, 0, 0))) {
                        this.placeID = 6;
                    }
                    if (ab.equals(player.add(0, 0, 2))) {
                        this.placeID = 7;
                    }
                    if (ab.equals(player.add(0, 0, -2))) {
                        this.placeID = 8;
                    }
                    if (ab.equals(player.add(1, 1, 0))) {
                        this.placeID = 9;
                    }
                    if (ab.equals(player.add(-1, 1, 0))) {
                        this.placeID = 10;
                    }
                    if (ab.equals(player.add(0, 1, 1))) {
                        this.placeID = 11;
                    }
                    if (ab.equals(player.add(0, 1, -1))) {
                        this.placeID = 12;
                    }
                    if (ab.equals(player.add(1, 0, 1))) {
                        this.placeID = 13;
                    }
                    if (ab.equals(player.add(1, 0, -1))) {
                        this.placeID = 14;
                    }
                    if (ab.equals(player.add(-1, 0, 1))) {
                        this.placeID = 15;
                    }
                    if (ab.equals(player.add(-1, 0, -1))) {
                        this.placeID = 16;
                    }
                }
            }
        }, new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (AntiCity.mc.world != null && AntiCity.mc.player != null) {
                if (event.getPacket() instanceof SPacketBlockBreakAnim) {
                    final SPacketBlockBreakAnim packet2 = (SPacketBlockBreakAnim)event.getPacket();
                    final BlockPos ab2 = packet2.getPosition();
                    this.breakPos = packet2.getPosition();
                    final BlockPos player2 = EntityUtil.getPlayerPos(AntiCity.mc.player);
                    if (ab2.equals(player2.add(1, 0, 0))) {
                        this.placeID = 1;
                    }
                    if (ab2.equals(player2.add(-1, 0, 0))) {
                        this.placeID = 2;
                    }
                    if (ab2.equals(player2.add(0, 0, 1))) {
                        this.placeID = 3;
                    }
                    if (ab2.equals(player2.add(0, 0, -1))) {
                        this.placeID = 4;
                    }
                    if (ab2.equals(player2.add(2, 0, 0))) {
                        this.placeID = 5;
                    }
                    if (ab2.equals(player2.add(-2, 0, 0))) {
                        this.placeID = 6;
                    }
                    if (ab2.equals(player2.add(0, 0, 2))) {
                        this.placeID = 7;
                    }
                    if (ab2.equals(player2.add(0, 0, -2))) {
                        this.placeID = 8;
                    }
                    if (ab2.equals(player2.add(1, 1, 0))) {
                        this.placeID = 9;
                    }
                    if (ab2.equals(player2.add(-1, 1, 0))) {
                        this.placeID = 10;
                    }
                    if (ab2.equals(player2.add(0, 1, 1))) {
                        this.placeID = 11;
                    }
                    if (ab2.equals(player2.add(0, 1, -1))) {
                        this.placeID = 12;
                    }
                    if (ab2.equals(player2.add(1, 0, 1))) {
                        this.placeID = 13;
                    }
                    if (ab2.equals(player2.add(1, 0, -1))) {
                        this.placeID = 14;
                    }
                    if (ab2.equals(player2.add(-1, 0, 1))) {
                        this.placeID = 15;
                    }
                    if (ab2.equals(player2.add(-1, 0, -1))) {
                        this.placeID = 16;
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public static boolean noHard(final Block block) {
        return block != Blocks.BEDROCK;
    }
    
    @Override
    public void onUpdate() {
        if (this.time.getValue().equals("onUpdate")) {
            this.antiCity();
        }
        this.placed = 0;
    }
    
    @Override
    public void onTick() {
        if (this.time.getValue().equals("Tick")) {
            this.antiCity();
        }
    }
    
    @Override
    public void fast() {
        if (this.time.getValue().equals("Fast")) {
            this.antiCity();
        }
    }
    
    public void antiCity() {
        if (AntiCity.mc.world == null || AntiCity.mc.player == null || AntiCity.mc.player.isDead) {
            return;
        }
        if (LemonClient.speedUtil.getPlayerSpeed(AntiCity.mc.player) >= 15.0) {
            return;
        }
        final int obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (obsidian == -1) {
            return;
        }
        BlockPos pos = EntityUtil.getPlayerPos(AntiCity.mc.player);
        if (pos == null) {
            return;
        }
        pos = new BlockPos(pos.x, pos.y + 0.2, pos.z);
        final List<BlockPos> placeList = new ArrayList<BlockPos>();
        if (this.breakPos != null) {
            if ((this.breakPos.equals(pos.add(1, 0, 0)) || this.breakPos.equals(pos.add(1, 1, 0))) && this.isAir(pos.add(1, 0, 0)) && this.isAir(pos.add(1, 1, 0))) {
                if (this.breakPos.equals(pos.add(1, 0, 0))) {
                    placeList.add(pos.add(1, 1, 0));
                }
                else {
                    placeList.add(pos.add(1, 0, 0));
                }
            }
            if ((this.breakPos.equals(pos.add(-1, 0, 0)) || this.breakPos.equals(pos.add(-1, 1, 0))) && this.isAir(pos.add(-1, 0, 0)) && this.isAir(pos.add(-1, 1, 0))) {
                if (this.breakPos.equals(pos.add(-1, 0, 0))) {
                    placeList.add(pos.add(-1, 1, 0));
                }
                else {
                    placeList.add(pos.add(-1, 0, 0));
                }
            }
            if ((this.breakPos.equals(pos.add(0, 0, 1)) || this.breakPos.equals(pos.add(0, 1, 1))) && this.isAir(pos.add(0, 0, 1)) && this.isAir(pos.add(0, 1, 1))) {
                if (this.breakPos.equals(pos.add(0, 0, 1))) {
                    placeList.add(pos.add(0, 1, 1));
                }
                else {
                    placeList.add(pos.add(0, 0, 1));
                }
            }
            if ((this.breakPos.equals(pos.add(0, 0, -1)) || this.breakPos.equals(pos.add(0, 1, -1))) && this.isAir(pos.add(0, 0, -1)) && this.isAir(pos.add(0, 1, -1))) {
                if (this.breakPos.equals(pos.add(0, 0, -1))) {
                    placeList.add(pos.add(0, 1, -1));
                }
                else {
                    placeList.add(pos.add(0, 0, -1));
                }
            }
        }
        if (noHard(this.getBlock(pos.add(1, 0, 0)).getBlock())) {
            if (this.placeID == 1) {
                placeList.add(pos.add(2, 0, 0));
                placeList.add(pos.add(1, 0, 1));
                placeList.add(pos.add(1, 0, -1));
                placeList.add(pos.add(1, 1, 0));
                if (EntityCheck(pos.add(2, 0, 0))) {
                    placeList.add(pos.add(3, 0, 0));
                    placeList.add(pos.add(3, 1, 0));
                }
            }
            if (this.placeID == 5) {
                placeList.add(pos.add(1, 0, 0));
                placeList.add(pos.add(2, 1, 0));
                placeList.add(pos.add(3, 0, 0));
            }
            if (this.placeID == 9) {
                placeList.add(pos.add(1, 0, 0));
                placeList.add(pos.add(2, 1, 0));
            }
            if (this.placeID == 13 || this.placeID == 14) {
                placeList.add(pos.add(1, 0, 0));
            }
        }
        if (noHard(this.getBlock(pos.add(-1, 0, 0)).getBlock())) {
            if (this.placeID == 2) {
                placeList.add(pos.add(-2, 0, 0));
                placeList.add(pos.add(-1, 0, 1));
                placeList.add(pos.add(-1, 0, -1));
                placeList.add(pos.add(-1, 1, 0));
                if (EntityCheck(pos.add(-2, 0, 0))) {
                    placeList.add(pos.add(-3, 0, 0));
                    placeList.add(pos.add(-3, 1, 0));
                }
            }
            if (this.placeID == 6) {
                placeList.add(pos.add(-1, 0, 0));
                placeList.add(pos.add(-2, 1, 0));
                placeList.add(pos.add(-3, 0, 0));
            }
            if (this.placeID == 10) {
                placeList.add(pos.add(-1, 0, 0));
                placeList.add(pos.add(-2, 1, 0));
            }
            if (this.placeID == 15 || this.placeID == 16) {
                placeList.add(pos.add(-1, 0, 0));
            }
        }
        if (noHard(this.getBlock(pos.add(0, 0, 1)).getBlock())) {
            if (this.placeID == 3) {
                placeList.add(pos.add(0, 0, 2));
                placeList.add(pos.add(1, 0, 1));
                placeList.add(pos.add(-1, 0, 1));
                placeList.add(pos.add(0, 1, 1));
                if (EntityCheck(pos.add(0, 0, 2))) {
                    placeList.add(pos.add(0, 0, 3));
                    placeList.add(pos.add(0, 1, 3));
                }
            }
            if (this.placeID == 7) {
                placeList.add(pos.add(0, 0, 1));
                placeList.add(pos.add(0, 1, 2));
                placeList.add(pos.add(0, 0, 3));
            }
            if (this.placeID == 11) {
                placeList.add(pos.add(0, 0, 1));
                placeList.add(pos.add(0, 1, 2));
            }
            if (this.placeID == 13 || this.placeID == 15) {
                placeList.add(pos.add(0, 0, 1));
            }
        }
        if (noHard(this.getBlock(pos.add(0, 0, -1)).getBlock())) {
            if (this.placeID == 4) {
                placeList.add(pos.add(0, 0, -2));
                placeList.add(pos.add(1, 0, -1));
                placeList.add(pos.add(-1, 0, -1));
                placeList.add(pos.add(0, 1, -1));
                if (EntityCheck(pos.add(0, 0, -2))) {
                    placeList.add(pos.add(0, 0, -3));
                    placeList.add(pos.add(0, 1, -3));
                }
            }
            if (this.placeID == 8) {
                placeList.add(pos.add(0, 0, -1));
                placeList.add(pos.add(0, 1, -2));
                placeList.add(pos.add(0, 0, -3));
            }
            if (this.placeID == 12) {
                placeList.add(pos.add(0, 0, -1));
                placeList.add(pos.add(0, 1, -2));
            }
            if (this.placeID == 14 || this.placeID == 16) {
                placeList.add(pos.add(0, 0, -1));
            }
        }
        this.placeID = 0;
        BlockPos instantPos;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
        }
        else {
            instantPos = null;
        }
        placeList.removeIf(blockPos -> PlayerCheck(blockPos) || !this.CanPlace(blockPos) || (this.smart.getValue() && this.isPos2(blockPos, instantPos)) || EntityCheck(blockPos));
        if (!placeList.isEmpty()) {
            InventoryUtil.run(obsidian, this.packetSwitch.getValue(), () -> {
                for (final BlockPos blockPos2 : placeList) {
                    if (this.placed >= this.bpt.getValue()) {
                        break;
                    }
                    else {
                        BurrowUtil.placeBlock(blockPos2, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                        ++this.placed;
                    }
                }
            });
        }
    }
    
    public static boolean EntityCheck(final BlockPos pos) {
        for (final Entity entity : AntiCity.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem)) {
                if (entity instanceof EntityXPOrb) {
                    continue;
                }
                if (entity != null) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private IBlockState getBlock(final BlockPos block) {
        if (block == null) {
            return null;
        }
        return AntiCity.mc.world.getBlockState(block);
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    public boolean CanPlace(final BlockPos block) {
        for (final EnumFacing face : EnumFacing.VALUES) {
            if (isReplaceable(block) && !BlockUtil.airBlocks.contains(this.getBlock(block.offset(face))) && AntiCity.mc.player.getDistanceSq(block) <= MathUtil.square(5.0)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isReplaceable(final BlockPos pos) {
        return BlockUtil.getState(pos).getMaterial().isReplaceable();
    }
    
    private boolean isAir(final BlockPos block) {
        return AntiCity.mc.world.getBlockState(block).getBlock() == Blocks.AIR;
    }
    
    public static boolean PlayerCheck(final BlockPos pos) {
        for (final Entity entity : AntiCity.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityPlayer) {
                return true;
            }
        }
        return false;
    }
}
