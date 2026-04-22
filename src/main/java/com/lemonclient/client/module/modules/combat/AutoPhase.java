// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.block.BlockObsidian;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.Packet;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockTrapDoor;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.player.PhaseUtil;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import java.util.List;
import com.lemonclient.api.util.misc.Timing;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoPhase", category = Category.Combat)
public class AutoPhase extends Module
{
    ModeSetting mode;
    ModeSetting bound;
    BooleanSetting twoBeePvP;
    BooleanSetting update;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting mine;
    BooleanSetting burrow;
    BooleanSetting doubleBurrow;
    IntegerSetting entity;
    BooleanSetting ignoreCrystal;
    IntegerSetting checkDelay;
    BlockPos originalPos;
    boolean down;
    Timing timing;
    Timing timer;
    int tpid;
    List<Block> blockList;
    BlockPos[] sides;
    BlockPos[] height;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    
    public AutoPhase() {
        this.mode = this.registerMode("Mode", Arrays.asList("5b", "Jp"), "5b");
        this.bound = this.registerMode("Bounds", PhaseUtil.bound, "Min", () -> this.mode.getValue().equals("5b"));
        this.twoBeePvP = this.registerBoolean("2b2tpvp", false, () -> this.mode.getValue().equals("5b"));
        this.update = this.registerBoolean("Update Pos", false, () -> this.mode.getValue().equals("5b"));
        this.packet = this.registerBoolean("Packet Place", true, () -> this.mode.getValue().equals("Jp"));
        this.swing = this.registerBoolean("Swing", true, () -> this.mode.getValue().equals("Jp"));
        this.mine = this.registerBoolean("Mine", true, () -> this.mode.getValue().equals("Jp"));
        this.burrow = this.registerBoolean("Try Burrow", true, () -> this.mode.getValue().equals("Jp"));
        this.doubleBurrow = this.registerBoolean("Double", true, () -> this.mode.getValue().equals("Jp") && this.burrow.getValue());
        this.entity = this.registerInteger("Entity Time", 5, 0, 10, () -> this.mode.getValue().equals("Jp"));
        this.ignoreCrystal = this.registerBoolean("Ignore Crystal", true, () -> this.mode.getValue().equals("Jp"));
        this.checkDelay = this.registerInteger("Check Time", 50, 0, 500, () -> this.mode.getValue().equals("Jp"));
        this.timing = new Timing();
        this.timer = new Timing();
        this.tpid = 0;
        this.blockList = Arrays.asList(Blocks.BEDROCK, Blocks.OBSIDIAN, Blocks.ENDER_CHEST, Blocks.ANVIL);
        this.sides = new BlockPos[] { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0) };
        this.height = new BlockPos[] { new BlockPos(0, 0, 0), new BlockPos(0, 1, 0) };
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                this.tpid = ((SPacketPlayerPosLook)event.getPacket()).teleportId;
            }
        }, new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketPlayer.PositionRotation || event.getPacket() instanceof CPacketPlayer.Position) {
                ++this.tpid;
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        if (this.mode.getValue().equals("Jp")) {
            this.down = true;
            this.originalPos = PlayerUtil.getPlayerPos();
            this.originalPos = new BlockPos(this.originalPos.x, this.originalPos.y + 0.2, this.originalPos.z);
            if (BurrowUtil.findHotbarBlock(BlockTrapDoor.class) == -1 || !AutoPhase.mc.world.isAirBlock(this.originalPos)) {
                this.disable();
                return;
            }
            AutoPhase.mc.player.setPosition(AutoPhase.mc.player.posX, (int)AutoPhase.mc.player.posY, AutoPhase.mc.player.posZ);
            this.timing.reset();
            this.timer.reset();
            this.down = false;
        }
    }
    
    public void onDisable() {
        if (this.mode.getValue().equals("Jp") && ModuleManager.isModuleEnabled(PacketMine.class)) {
            PacketMine.INSTANCE.lastBlock = null;
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.mode.getValue().equals("Jp")) {
            this.trapdoor();
        }
        else {
            this.packetFly();
        }
    }
    
    void packetFly() {
        final double[] clip = MotionUtil.forward(0.0624);
        if (AutoPhase.mc.player.onGround) {
            this.tp(0.0, -0.0624, 0.0, false);
        }
        else {
            this.tp(clip[0], 0.0, clip[1], true);
        }
        this.disable();
    }
    
    void tp(final double x, final double y, final double z, final boolean onGround) {
        final double[] dir = MotionUtil.forward(-0.0312);
        if (this.twoBeePvP.getValue()) {
            AutoPhase.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoPhase.mc.player.posX + dir[0], AutoPhase.mc.player.posY, AutoPhase.mc.player.posZ + dir[1], onGround));
        }
        AutoPhase.mc.player.connection.sendPacket(new CPacketPlayer.Position((this.twoBeePvP.getValue() ? (x / 2.0) : x) + AutoPhase.mc.player.posX, y + AutoPhase.mc.player.posY, (this.twoBeePvP.getValue() ? (z / 2.0) : z) + AutoPhase.mc.player.posZ, onGround));
        AutoPhase.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.tpid - 1));
        AutoPhase.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.tpid));
        AutoPhase.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.tpid + 1));
        PhaseUtil.doBounds(this.bound.getValue(), true);
        if (this.update.getValue()) {
            AutoPhase.mc.player.setPosition(x, y, z);
        }
    }
    
    private void trapdoor() {
        if (AutoPhase.mc.world == null || AutoPhase.mc.player == null || AutoPhase.mc.player.isDead || this.originalPos == null) {
            this.disable();
            return;
        }
        if (!this.down) {
            if (BurrowUtil.findHotbarBlock(BlockTrapDoor.class) == -1) {
                this.disable();
                return;
            }
            if (this.intersectsWithEntity(this.originalPos) && this.timer.passedS(this.entity.getValue())) {
                this.disable();
                return;
            }
            final EnumFacing facing = BurrowUtil.getTrapdoorFacing(this.originalPos);
            BlockPos burrowPos = null;
            for (final BlockPos side : this.sides) {
                final BlockPos blockPos = PlayerUtil.getPlayerPos().add(side);
                if (BlockUtil.getBlock(blockPos) == Blocks.BEDROCK || BlockUtil.getBlock(blockPos) == Blocks.OBSIDIAN) {
                    burrowPos = blockPos;
                    break;
                }
            }
            final int obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (facing == null || (burrowPos == null && this.burrow.getValue())) {
                if (this.burrow.getValue()) {
                    boolean placed = false;
                    if (obsidian != -1) {
                        for (final BlockPos side2 : this.sides) {
                            final BlockPos blockPos2 = PlayerUtil.getPlayerPos().add(side2);
                            if (!this.intersectsWithEntity(blockPos2) && BlockUtil.hasNeighbour(blockPos2)) {
                                AutoPhase.mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidian));
                                BurrowUtil.placeBlock(blockPos2, EnumHand.MAIN_HAND, false, false, false, false);
                                if (this.doubleBurrow.getValue()) {
                                    BurrowUtil.placeBlock(blockPos2.up(), EnumHand.MAIN_HAND, false, false, false, false);
                                }
                                placed = true;
                                break;
                            }
                        }
                    }
                    if (!placed) {
                        this.disable();
                    }
                }
                else {
                    this.disable();
                }
                return;
            }
            if (this.burrow.getValue() && this.doubleBurrow.getValue() && BlockUtil.isAir(burrowPos.up())) {
                AutoPhase.mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidian));
                BurrowUtil.placeBlock(burrowPos.up(), EnumHand.MAIN_HAND, false, false, false, false);
            }
            final BlockPos neighbour = this.originalPos.offset(facing);
            final EnumFacing opposite = facing.getOpposite();
            final double x = AutoPhase.mc.player.posX;
            final double y = AutoPhase.mc.player.posY;
            final double z = AutoPhase.mc.player.posZ;
            AutoPhase.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + 0.20000000298023224, z, AutoPhase.mc.player.onGround));
            AutoPhase.mc.player.connection.sendPacket(new CPacketHeldItemChange(BurrowUtil.findHotbarBlock(BlockTrapDoor.class)));
            boolean sneak = false;
            if ((BlockUtil.blackList.contains(AutoPhase.mc.world.getBlockState(neighbour).getBlock()) || BlockUtil.shulkerList.contains(AutoPhase.mc.world.getBlockState(neighbour).getBlock())) && !AutoPhase.mc.player.isSneaking()) {
                AutoPhase.mc.player.connection.sendPacket(new CPacketEntityAction(AutoPhase.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                AutoPhase.mc.player.setSneaking(true);
                sneak = true;
            }
            rightClickBlock(neighbour, opposite, new Vec3d(0.5, 0.8, 0.5), this.packet.getValue(), this.swing.getValue());
            AutoPhase.mc.player.connection.sendPacket(new CPacketHeldItemChange(AutoPhase.mc.player.inventory.currentItem));
            AutoPhase.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, AutoPhase.mc.player.onGround));
            if (sneak) {
                AutoPhase.mc.player.connection.sendPacket(new CPacketEntityAction(AutoPhase.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                AutoPhase.mc.player.setSneaking(false);
            }
            if (this.burrow.getValue()) {
                if (burrowPos == null) {
                    return;
                }
                AutoPhase.mc.player.setPosition(burrowPos.x + 0.5, burrowPos.y, burrowPos.z + 0.5);
                this.disable();
            }
            else {
                int bedrocks = 0;
                int blocks = 0;
                double xAdd = 0.0;
                double zAdd = 0.0;
                for (final BlockPos side3 : this.sides) {
                    for (final BlockPos add : this.sides) {
                        if (!this.isPos2(this.originalPos, this.originalPos.add(side3).add(add))) {
                            if (!this.isPos2(side3, add)) {
                                int bedrock = 0;
                                int block = 0;
                                final BlockPos sidePos = this.originalPos.add(side3);
                                final BlockPos addPos = this.originalPos.add(add);
                                final BlockPos addSide = this.originalPos.add(side3).add(add);
                                for (final BlockPos high : this.height) {
                                    final Block sideState = AutoPhase.mc.world.getBlockState(sidePos.add(high)).getBlock();
                                    final Block addState = AutoPhase.mc.world.getBlockState(addPos.add(high)).getBlock();
                                    final Block addSideState = AutoPhase.mc.world.getBlockState(addSide.add(high)).getBlock();
                                    if (this.blockList.contains(sideState)) {
                                        block += 3;
                                    }
                                    if (sideState == Blocks.BEDROCK) {
                                        bedrock += 3;
                                    }
                                    if (this.blockList.contains(addState)) {
                                        block += 3;
                                    }
                                    if (addState == Blocks.BEDROCK) {
                                        bedrock += 3;
                                    }
                                    if (this.blockList.contains(addSideState)) {
                                        ++block;
                                    }
                                    if (addSideState == Blocks.BEDROCK) {
                                        ++bedrock;
                                    }
                                }
                                boolean shouldSet = false;
                                if (block > blocks) {
                                    shouldSet = true;
                                }
                                else if (block == blocks && bedrock > bedrocks) {
                                    shouldSet = true;
                                }
                                if (shouldSet) {
                                    bedrocks = bedrock;
                                    blocks = block;
                                    xAdd = this.getAdd(side3.x + add.x);
                                    zAdd = this.getAdd(side3.z + add.z);
                                }
                            }
                        }
                    }
                }
                AutoPhase.mc.player.setPosition(this.originalPos.getX() + xAdd, this.originalPos.getY(), this.originalPos.getZ() + zAdd);
                AutoPhase.mc.player.motionX = 0.0;
                AutoPhase.mc.player.motionZ = 0.0;
                if (AutoPhase.mc.player.posX == this.originalPos.getX() + xAdd && AutoPhase.mc.player.posZ == this.originalPos.getZ() + zAdd && !AutoPhase.mc.world.isAirBlock(this.originalPos) && this.timing.passedMs(this.checkDelay.getValue())) {
                    this.down = true;
                }
            }
        }
        if (this.down) {
            this.timing.reset();
            AutoPhase.mc.player.motionX = 0.0;
            AutoPhase.mc.player.motionZ = 0.0;
            if (this.mine.getValue()) {
                AutoPhase.mc.playerController.onPlayerDamageBlock(this.originalPos, EnumFacing.UP);
            }
            else {
                this.disable();
            }
            if (AutoPhase.mc.world.isAirBlock(this.originalPos)) {
                this.disable();
            }
        }
    }
    
    private double getAdd(final int pos) {
        if (pos == 1) {
            return 0.99999999;
        }
        return 0.0;
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    public static void rightClickBlock(final BlockPos pos, final EnumFacing facing, final Vec3d hVec, final boolean packet, final boolean swing) {
        final Vec3d hitVec = new Vec3d(pos).add(hVec).add(new Vec3d(facing.getDirectionVec()).scale(0.5));
        if (packet) {
            rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
        }
        else {
            AutoPhase.mc.playerController.processRightClickBlock(AutoPhase.mc.player, AutoPhase.mc.world, pos, facing, hitVec, EnumHand.MAIN_HAND);
        }
        if (swing) {
            AutoPhase.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction) {
        final float f = (float)(vec.x - pos.getX());
        final float f2 = (float)(vec.y - pos.getY());
        final float f3 = (float)(vec.z - pos.getZ());
        AutoPhase.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoPhase.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (entity instanceof EntityEnderCrystal && this.ignoreCrystal.getValue()) {
                continue;
            }
            if (entity == AutoPhase.mc.player) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
}
