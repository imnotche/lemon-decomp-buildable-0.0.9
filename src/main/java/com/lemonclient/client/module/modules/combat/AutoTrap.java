// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.misc.MathUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import java.util.Collection;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import java.util.Iterator;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import com.lemonclient.api.util.world.HoleUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import java.util.ArrayList;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoTrap", category = Category.Combat)
public class AutoTrap extends Module
{
    IntegerSetting delay;
    IntegerSetting range;
    IntegerSetting bpt;
    BooleanSetting top;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BooleanSetting detect;
    BooleanSetting self;
    BooleanSetting bed;
    BooleanSetting pause;
    int ob;
    int waited;
    int placed;
    BlockPos trapPos;
    BlockPos player;
    List<BlockPos> posList;
    BlockPos[] sides;
    BlockPos[] blocks;
    BlockPos breakPos;
    private int place;
    @EventHandler
    private final Listener<PacketEvent.PostSend> listener;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public AutoTrap() {
        this.delay = this.registerInteger("Delay", 0, 0, 20);
        this.range = this.registerInteger("Range", 5, 0, 10);
        this.bpt = this.registerInteger("BlocksPerTick", 4, 0, 20);
        this.top = this.registerBoolean("Top+", false);
        this.rotate = this.registerBoolean("Rotate", false);
        this.packet = this.registerBoolean("Packet Place", false);
        this.swing = this.registerBoolean("Swing", false);
        this.packetSwitch = this.registerBoolean("Packet Switch", false);
        this.detect = this.registerBoolean("Detect Break", false);
        this.self = this.registerBoolean("Self Break", false, () -> this.detect.getValue());
        this.bed = this.registerBoolean("Bedrock", false, () -> this.detect.getValue());
        this.pause = this.registerBoolean("BedrockHole", true);
        this.posList = new ArrayList<BlockPos>();
        this.sides = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1) };
        this.blocks = new BlockPos[] { new BlockPos(0, 1, 0), new BlockPos(0, 2, 0) };
        this.listener = new Listener<PacketEvent.PostSend>(event -> {
            if (this.player == null || !this.self.getValue()) {
            }
            else {
                if (event.getPacket() instanceof CPacketPlayerDigging) {
                    final CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
                    if (packet.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                        final BlockPos ab = packet.getPosition();
                        this.breakPos = packet.getPosition();
                        if (ab.equals(this.player.add(0, 1, 0))) {
                            this.place = 17;
                        }
                        if (ab.equals(this.player.add(1, 1, 0))) {
                            this.place = 18;
                        }
                        if (ab.equals(this.player.add(-1, 1, 0))) {
                            this.place = 19;
                        }
                        if (ab.equals(this.player.add(0, 1, 1))) {
                            this.place = 20;
                        }
                        if (ab.equals(this.player.add(0, 1, -1))) {
                            this.place = 21;
                        }
                        if (ab.equals(this.player.add(0, 2, 0))) {
                            this.place = 22;
                        }
                        if (ab.equals(this.player.add(1, 0, 0))) {
                            this.place = 1;
                        }
                        if (ab.equals(this.player.add(-1, 0, 0))) {
                            this.place = 2;
                        }
                        if (ab.equals(this.player.add(0, 0, 1))) {
                            this.place = 3;
                        }
                        if (ab.equals(this.player.add(0, 0, -1))) {
                            this.place = 4;
                        }
                        if (ab.equals(this.player.add(2, 0, 0))) {
                            this.place = 5;
                        }
                        if (ab.equals(this.player.add(-2, 0, 0))) {
                            this.place = 6;
                        }
                        if (ab.equals(this.player.add(0, 0, 2))) {
                            this.place = 7;
                        }
                        if (ab.equals(this.player.add(0, 0, -2))) {
                            this.place = 8;
                        }
                        if (ab.equals(this.player.add(1, 1, 0))) {
                            this.place = 9;
                        }
                        if (ab.equals(this.player.add(-1, 1, 0))) {
                            this.place = 10;
                        }
                        if (ab.equals(this.player.add(0, 1, 1))) {
                            this.place = 11;
                        }
                        if (ab.equals(this.player.add(0, 1, -1))) {
                            this.place = 12;
                        }
                        if (ab.equals(this.player.add(1, 0, 1))) {
                            this.place = 13;
                        }
                        if (ab.equals(this.player.add(1, 0, -1))) {
                            this.place = 14;
                        }
                        if (ab.equals(this.player.add(-1, 0, 1))) {
                            this.place = 15;
                        }
                        if (ab.equals(this.player.add(-1, 0, -1))) {
                            this.place = 16;
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (AutoTrap.mc.world != null && AutoTrap.mc.player != null && this.player != null) {
                if (event.getPacket() instanceof SPacketBlockBreakAnim) {
                    final SPacketBlockBreakAnim packet2 = (SPacketBlockBreakAnim)event.getPacket();
                    final BlockPos ab2 = packet2.getPosition();
                    this.breakPos = packet2.getPosition();
                    if (ab2.equals(this.player.add(0, 1, 0))) {
                        this.place = 17;
                    }
                    if (ab2.equals(this.player.add(1, 0, 0))) {
                        this.place = 1;
                    }
                    if (ab2.equals(this.player.add(-1, 0, 0))) {
                        this.place = 2;
                    }
                    if (ab2.equals(this.player.add(0, 0, 1))) {
                        this.place = 3;
                    }
                    if (ab2.equals(this.player.add(0, 0, -1))) {
                        this.place = 4;
                    }
                    if (ab2.equals(this.player.add(2, 0, 0))) {
                        this.place = 5;
                    }
                    if (ab2.equals(this.player.add(-2, 0, 0))) {
                        this.place = 6;
                    }
                    if (ab2.equals(this.player.add(0, 0, 2))) {
                        this.place = 7;
                    }
                    if (ab2.equals(this.player.add(0, 0, -2))) {
                        this.place = 8;
                    }
                    if (ab2.equals(this.player.add(1, 1, 0))) {
                        this.place = 9;
                    }
                    if (ab2.equals(this.player.add(-1, 1, 0))) {
                        this.place = 10;
                    }
                    if (ab2.equals(this.player.add(0, 1, 1))) {
                        this.place = 11;
                    }
                    if (ab2.equals(this.player.add(0, 1, -1))) {
                        this.place = 12;
                    }
                    if (ab2.equals(this.player.add(1, 0, 1))) {
                        this.place = 13;
                    }
                    if (ab2.equals(this.player.add(1, 0, -1))) {
                        this.place = 14;
                    }
                    if (ab2.equals(this.player.add(-1, 0, 1))) {
                        this.place = 15;
                    }
                    if (ab2.equals(this.player.add(-1, 0, -1))) {
                        this.place = 16;
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public static boolean isPlayerInHole(final EntityPlayer target) {
        final BlockPos blockPos = getLocalPlayerPosFloored(target);
        final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(blockPos, true, true, false);
        final HoleUtil.HoleType holeType = holeInfo.getType();
        return holeType == HoleUtil.HoleType.SINGLE;
    }
    
    public static BlockPos getLocalPlayerPosFloored(final EntityPlayer target) {
        return new BlockPos(target.getPositionVector());
    }
    
    @Override
    public void onUpdate() {
        if (AutoTrap.mc.world == null || AutoTrap.mc.player == null || AutoTrap.mc.player.isDead) {
            this.trapPos = null;
            this.posList.clear();
            return;
        }
        this.placed = 0;
        if (this.delay.getValue() > 0) {
            if (this.waited++ < this.delay.getValue()) {
                return;
            }
            this.waited = 0;
        }
        if (BurrowUtil.findHotbarBlock(BlockObsidian.class) == -1) {
            return;
        }
        final EntityPlayer target = PlayerUtil.getNearestPlayer(this.range.getValue());
        if (target == null) {
            return;
        }
        BlockPos pos = null;
        if (AutoTrap.mc.player.getDistance(target) > this.range.getValue() || !isPlayerInHole(target)) {
            this.posList.clear();
        }
        else {
            pos = EntityUtil.getEntityPos(target);
            this.addBlock(pos);
        }
        this.ob = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (this.ob == -1) {
            return;
        }
        this.posList.removeIf(blockPos -> !BlockUtil.isAir(blockPos) || this.intersectsWithEntity(blockPos));
        if (!this.posList.isEmpty()) {
            InventoryUtil.run(this.ob, this.packetSwitch.getValue(), () -> {
                for (final BlockPos block : this.posList) {
                    if (this.placed > this.bpt.getValue()) {
                        break;
                    }
                    else {
                        BurrowUtil.placeBlock(block, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                        ++this.placed;
                    }
                }
            });
        }
        this.antiCity(this.player = EntityUtil.getEntityPos(target).up());
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoTrap.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private void addBlock(final BlockPos pos) {
        if (BurrowUtil.findHotbarBlock(BlockObsidian.class) == -1) {
            return;
        }
        final List<BlockPos> blocklist = new ArrayList<BlockPos>();
        blocklist.add(pos.add(0, 2, 0));
        if (this.top.getValue()) {
            blocklist.add(pos.add(0, 3, 0));
        }
        int obby = 0;
        for (final BlockPos side : this.sides) {
            if (AutoTrap.mc.world.getBlockState(pos.add(side)).getBlock() != Blocks.BEDROCK || this.bed.getValue()) {
                for (final BlockPos blockPos : this.blocks) {
                    blocklist.add(pos.add(side).add(blockPos));
                }
                ++obby;
            }
        }
        if (obby == 0 && !this.pause.getValue()) {
            return;
        }
        this.posList.addAll(blocklist);
    }
    
    private boolean noHard(final Block block) {
        return block != Blocks.BEDROCK || this.bed.getValue();
    }
    
    public void antiCity(BlockPos pos) {
        final int obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (obsidian == -1) {
            return;
        }
        if (pos == null) {
            return;
        }
        pos = new BlockPos(pos.x, pos.y + 0.2, pos.z);
        final List<BlockPos> list = new ArrayList<BlockPos>();
        if (this.breakPos != null) {
            if ((this.breakPos.equals(pos.add(1, 0, 0)) || this.breakPos.equals(pos.add(1, 1, 0))) && this.isAir(pos.add(1, 0, 0)) && this.isAir(pos.add(1, 1, 0))) {
                if (this.breakPos.equals(pos.add(1, 0, 0))) {
                    list.add(pos.add(1, 1, 0));
                }
                else {
                    list.add(pos.add(1, 0, 0));
                }
            }
            if ((this.breakPos.equals(pos.add(-1, 0, 0)) || this.breakPos.equals(pos.add(-1, 1, 0))) && this.isAir(pos.add(-1, 0, 0)) && this.isAir(pos.add(-1, 1, 0))) {
                if (this.breakPos.equals(pos.add(-1, 0, 0))) {
                    list.add(pos.add(-1, 1, 0));
                }
                else {
                    list.add(pos.add(-1, 0, 0));
                }
            }
            if ((this.breakPos.equals(pos.add(0, 0, 1)) || this.breakPos.equals(pos.add(0, 1, 1))) && this.isAir(pos.add(0, 0, 1)) && this.isAir(pos.add(0, 1, 1))) {
                if (this.breakPos.equals(pos.add(0, 0, 1))) {
                    list.add(pos.add(0, 1, 1));
                }
                else {
                    list.add(pos.add(0, 0, 1));
                }
            }
            if ((this.breakPos.equals(pos.add(0, 0, -1)) || this.breakPos.equals(pos.add(0, 1, -1))) && this.isAir(pos.add(0, 0, -1)) && this.isAir(pos.add(0, 1, -1))) {
                if (this.breakPos.equals(pos.add(0, 0, -1))) {
                    list.add(pos.add(0, 1, -1));
                }
                else {
                    list.add(pos.add(0, 0, -1));
                }
            }
        }
        if (this.noHard(this.getBlock(pos.add(1, 0, 0)).getBlock())) {
            if (this.place == 1) {
                list.add(pos.add(2, 0, 0));
                list.add(pos.add(1, 0, 1));
                list.add(pos.add(1, 0, -1));
                list.add(pos.add(1, 1, 0));
            }
            if (this.place == 5 || this.place == 9 || this.place == 13 || this.place == 14) {
                list.add(pos.add(1, 0, 0));
            }
        }
        if (this.noHard(this.getBlock(pos.add(-1, 0, 0)).getBlock())) {
            if (this.place == 2) {
                list.add(pos.add(-2, 0, 0));
                list.add(pos.add(-1, 0, 1));
                list.add(pos.add(-1, 0, -1));
                list.add(pos.add(-1, 1, 0));
            }
            if (this.place == 6 || this.place == 10 || this.place == 15 || this.place == 16) {
                list.add(pos.add(-1, 0, 0));
            }
        }
        if (this.noHard(this.getBlock(pos.add(0, 0, 1)).getBlock())) {
            if (this.place == 3) {
                list.add(pos.add(0, 0, 2));
                list.add(pos.add(1, 0, 1));
                list.add(pos.add(-1, 0, 1));
                list.add(pos.add(0, 1, 1));
            }
            if (this.place == 7 || this.place == 11 || this.place == 13 || this.place == 15) {
                list.add(pos.add(0, 0, 1));
            }
        }
        if (this.noHard(this.getBlock(pos.add(0, 0, -1)).getBlock())) {
            if (this.place == 4) {
                list.add(pos.add(0, 0, -2));
                list.add(pos.add(1, 0, -1));
                list.add(pos.add(-1, 0, -1));
                list.add(pos.add(0, 1, -1));
            }
            if (this.place == 8 || this.place == 12 || this.place == 14 || this.place == 16) {
                list.add(pos.add(0, 0, -1));
            }
        }
        if (this.noHard(this.getBlock(pos.add(0, 1, 0)).getBlock())) {
            if (this.place == 17) {
                list.add(pos.add(0, 2, 0));
                list.add(pos.add(0, 1, -1));
                list.add(pos.add(0, 1, 1));
                list.add(pos.add(1, 1, 0));
                list.add(pos.add(-1, 1, 0));
            }
            if (this.place == 9 || this.place == 10 || this.place == 11 || this.place == 12 || this.place > 17) {
                list.add(pos.add(0, 1, 0));
            }
        }
        this.place = 0;
        list.removeIf(p -> PlayerCheck(p) || !this.CanPlace(p));
        if (!list.isEmpty()) {
            InventoryUtil.run(obsidian, this.packetSwitch.getValue(), () -> {
                for (final BlockPos blockPos : list) {
                    if (this.placed >= this.bpt.getValue()) {
                        break;
                    }
                    else {
                        BurrowUtil.placeBlock(blockPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                        ++this.placed;
                    }
                }
            });
        }
    }
    
    private IBlockState getBlock(final BlockPos block) {
        if (block == null) {
            return null;
        }
        return AutoTrap.mc.world.getBlockState(block);
    }
    
    public boolean CanPlace(final BlockPos block) {
        for (final EnumFacing face : EnumFacing.VALUES) {
            if (isReplaceable(block) && !BlockUtil.airBlocks.contains(this.getBlock(block.offset(face))) && AutoTrap.mc.player.getDistanceSq(block) <= MathUtil.square(5.0)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isReplaceable(final BlockPos pos) {
        return BlockUtil.getState(pos).getMaterial().isReplaceable();
    }
    
    private boolean isAir(final BlockPos block) {
        return AutoTrap.mc.world.getBlockState(block).getBlock() == Blocks.AIR;
    }
    
    public static boolean PlayerCheck(final BlockPos pos) {
        for (final Entity entity : AutoTrap.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityPlayer) {
                return true;
            }
        }
        return false;
    }
}
