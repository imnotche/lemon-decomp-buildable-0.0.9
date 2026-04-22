// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.properties.IProperty;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPiston;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.Block;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.misc.MathUtil;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.client.LemonClient;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.MotionUtil;
import java.util.function.Function;
import java.util.Comparator;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.List;
import net.minecraft.util.math.Vec3i;
import java.util.ArrayList;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockPistonBase;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.util.EnumFacing;
import com.lemonclient.api.util.world.BlockUtil;
import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.event.Phase;
import java.util.Arrays;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec2f;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiHoleCamper", category = Category.Dev, priority = 1000)
public class AntiHoleCamper extends Module
{
    IntegerSetting delay;
    BooleanSetting pause;
    ModeSetting mode;
    IntegerSetting range;
    BooleanSetting look;
    BooleanSetting ground;
    BooleanSetting box;
    BooleanSetting hole;
    BooleanSetting pushCheck;
    BooleanSetting headCheck;
    BooleanSetting breakRedstone;
    BooleanSetting pushedCheck;
    ModeSetting breakBlock;
    BooleanSetting packetPiston;
    BooleanSetting packetRedstone;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting block;
    BooleanSetting packet;
    BooleanSetting packetSwitch;
    BooleanSetting update;
    BooleanSetting force;
    BooleanSetting strict;
    BooleanSetting raytrace;
    DoubleSetting maxSpeed;
    BooleanSetting debug;
    ModeSetting disable;
    IntegerSetting disableDelay;
    private final Timing timer;
    BlockPos beforePlayerPos;
    BlockPos pistonPos;
    BlockPos redstonePos;
    PistonPos pos;
    boolean useBlock;
    boolean disabling;
    int redstoneSlot;
    int pistonSlot;
    int obsiSlot;
    int waited;
    int wait;
    int[] enemyCoordsInt;
    EntityPlayer aimTarget;
    Vec2f rotation;
    Vec3d[] sides;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    
    public AntiHoleCamper() {
        this.delay = this.registerInteger("Delay", 0, 0, 20);
        this.pause = this.registerBoolean("Pause When Move", true);
        this.mode = this.registerMode("Mode", Arrays.asList("Block", "Torch", "Both"), "Block");
        this.range = this.registerInteger("Range", 6, 0, 10);
        this.look = this.registerBoolean("Looking Target", false);
        this.ground = this.registerBoolean("OnGround Check", true);
        this.box = this.registerBoolean("Entity Box", true);
        this.hole = this.registerBoolean("Double Hole Check", false);
        this.pushCheck = this.registerBoolean("Push Check", false);
        this.headCheck = this.registerBoolean("Head Check", false);
        this.breakRedstone = this.registerBoolean("Break Redstone", false);
        this.pushedCheck = this.registerBoolean("Pushed Check", true, () -> this.breakRedstone.getValue());
        this.breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet", () -> this.breakRedstone.getValue());
        this.packetPiston = this.registerBoolean("Packet Place Piston", true);
        this.packetRedstone = this.registerBoolean("Packet Place Redstone", true);
        this.swing = this.registerBoolean("Swing", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.block = this.registerBoolean("Place Block", true);
        this.packet = this.registerBoolean("Packet Place", true, () -> this.block.getValue());
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.update = this.registerBoolean("Update Controller", true);
        this.force = this.registerBoolean("Force Rotate", true);
        this.strict = this.registerBoolean("Strict", true);
        this.raytrace = this.registerBoolean("RayTrace", true);
        this.maxSpeed = this.registerDouble("Max Target Speed", 5.0, 0.0, 50.0);
        this.debug = this.registerBoolean("Debug Msg", true);
        this.disable = this.registerMode("Disable Mode", Arrays.asList("NoDisable", "Check", "AutoDisable"), "AutoDisable");
        this.disableDelay = this.registerInteger("Disable Delay", 0, 0, 50);
        this.timer = new Timing();
        this.pos = null;
        this.aimTarget = null;
        this.sides = new Vec3d[] { new Vec3d(0.25, 0.0, 0.25), new Vec3d(-0.25, 0.0, 0.25), new Vec3d(0.25, 0.0, -0.25), new Vec3d(-0.25, 0.0, -0.25) };
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() != Phase.PRE || this.rotation == null) {
            }
            else {
                final PlayerPacket packet = new PlayerPacket(this, new Vec2f(this.rotation.x, PlayerPacketManager.INSTANCE.getServerSideRotation().y));
                PlayerPacketManager.INSTANCE.addPacket(packet);
            }
        }, new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (this.rotation != null && this.force.getValue()) {
                if (event.getPacket() instanceof CPacketPlayer.Rotation) {
                    ((CPacketPlayer.Rotation)event.getPacket()).yaw = this.rotation.x;
                }
                if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                    ((CPacketPlayer.PositionRotation)event.getPacket()).yaw = this.rotation.x;
                }
            }
        }, new Predicate[0]);
    }
    
    private void switchTo(final int slot, final Runnable runnable) {
        final int oldslot = AntiHoleCamper.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (slot < 9) {
            final boolean packetSwitch = this.packetSwitch.getValue();
            if (packetSwitch) {
                AntiHoleCamper.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AntiHoleCamper.mc.player.inventory.currentItem = slot;
            }
            if (this.update.getValue()) {
                AntiHoleCamper.mc.playerController.updateController();
            }
            runnable.run();
            if (packetSwitch) {
                AntiHoleCamper.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                AntiHoleCamper.mc.player.inventory.currentItem = oldslot;
            }
        }
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AntiHoleCamper.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean airBlock(final BlockPos pos) {
        return BlockUtil.canReplace(pos);
    }
    
    private boolean canPlacePiston(final BlockPos pos, final EnumFacing facing) {
        final BlockPos p = pos.offset(facing);
        final BlockPos push = pos.offset(facing, -1);
        final double feetY = AntiHoleCamper.mc.player.posY;
        return ((!this.intersectsWithEntity(p) && this.airBlock(p) && (PlayerUtil.getDistanceI(p) >= 1.4 + p.getY() - feetY || p.getY() <= feetY + 1.0) && (PlayerUtil.getDistanceI(p) >= 2.4 + feetY - p.getY() || p.getY() >= feetY) && BlockUtil.canPlaceWithoutBase(p, this.strict.getValue(), this.raytrace.getValue(), true)) || (this.isFacing(p, pos) && (AntiHoleCamper.mc.world.getBlockState(p).getBlock() instanceof BlockPistonBase || AntiHoleCamper.mc.world.getBlockState(p).getBlock() == Blocks.PISTON || AntiHoleCamper.mc.world.getBlockState(p).getBlock() == Blocks.STICKY_PISTON))) && (!this.hole.getValue() || this.airBlock(push)) && (!this.pushCheck.getValue() || (this.airBlock(push.up()) && (this.airBlock(push.up(2)) || this.airBlock(push))));
    }
    
    public BlockPos getRedstonePos(final BlockPos pistonPos) {
        final BlockPos pos = this.hasRedstoneBlock(pistonPos);
        if (pos != null) {
            return pos;
        }
        List<BlockPos> redstone = new ArrayList<BlockPos>();
        if (this.useBlock) {
            for (final EnumFacing facing : EnumFacing.VALUES) {
                redstone.add(pistonPos.offset(facing));
            }
        }
        else {
            final BlockPos[] array;
            final BlockPos[] offsets = array = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
            for (final BlockPos offs : array) {
                for (int i = 0; i < 2; ++i) {
                    final BlockPos torch = pistonPos.down(i).add(offs);
                    if (i != 1 || !BlockUtil.isBlockUnSolid(torch.up())) {
                        redstone.add(torch);
                    }
                }
            }
        }
        redstone = redstone.stream().filter(p -> !ColorMain.INSTANCE.breakList.contains(p) && !this.intersectsWithEntity(p) && AntiHoleCamper.mc.player.getDistance(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) <= this.range.getValue()).collect(Collectors.toList());
        if (redstone.isEmpty()) {
            return null;
        }
        final List<BlockPos> hasBase = redstone.stream().filter(p -> BlockUtil.canPlaceWithoutBase(p, this.strict.getValue(), this.raytrace.getValue(), false)).collect(Collectors.toList());
        if (hasBase.isEmpty()) {
            hasBase.addAll(redstone);
        }
        return hasBase.stream().min(Comparator.comparingDouble(PlayerUtil::getDistanceI)).orElse(null);
    }
    
    public void onDisable() {
        if (this.breakRedstone.getValue() && this.redstonePos != null && !this.airBlock(this.redstonePos) && (!this.pushedCheck.getValue() || AntiHoleCamper.mc.world.getBlockState(this.beforePlayerPos).getBlock() == Blocks.PISTON_HEAD || AntiHoleCamper.mc.world.getBlockState(this.beforePlayerPos.up()).getBlock() == Blocks.PISTON_HEAD)) {
            this.doBreak(this.redstonePos);
        }
    }
    
    public void onEnable() {
        this.disabling = false;
    }
    
    @Override
    public void onUpdate() {
        if (AntiHoleCamper.mc.world == null || AntiHoleCamper.mc.player == null || AntiHoleCamper.mc.player.isDead) {
            this.disable();
            return;
        }
        this.rotation = null;
        this.aimTarget = null;
        if (this.breakRedstone.getValue() && this.redstonePos != null && !this.airBlock(this.redstonePos) && (!this.pushedCheck.getValue() || AntiHoleCamper.mc.world.getBlockState(this.beforePlayerPos).getBlock() == Blocks.PISTON_HEAD || AntiHoleCamper.mc.world.getBlockState(this.beforePlayerPos.up()).getBlock() == Blocks.PISTON_HEAD)) {
            this.doBreak(this.redstonePos);
        }
        if (this.disabling && !this.disable.getValue().equals("NoDisable")) {
            if (this.wait++ >= this.disableDelay.getValue()) {
                boolean placed = true;
                if (this.block.getValue()) {
                    if (this.timer.passedMs(1000L)) {
                        this.switchTo(this.obsiSlot, () -> BlockUtil.placeBlock(this.beforePlayerPos, this.rotate.getValue(), this.packet.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue()));
                        this.timer.reset();
                        if (this.disable.getValue().equals("AutoDisable")) {
                            this.disable();
                            return;
                        }
                    }
                    placed = (AntiHoleCamper.mc.world.getBlockState(this.beforePlayerPos).getBlock() == Blocks.OBSIDIAN);
                }
                else if (this.disable.getValue().equals("AutoDisable")) {
                    this.disable();
                }
                if (this.disable.getValue().equals("Check") && placed) {
                    this.disable();
                }
                this.wait = 0;
                return;
            }
        }
        else {
            this.wait = 0;
        }
        if (this.waited++ < this.delay.getValue() || (MotionUtil.isMoving(AntiHoleCamper.mc.player) && this.pause.getValue())) {
            return;
        }
        this.waited = 0;
        final int n = this.obsiSlot - 1;
        this.pistonSlot = n;
        this.redstoneSlot = n;
        if (!this.ready()) {
            if (!this.disable.getValue().equals("NoDisable")) {
                this.disable();
            }
            return;
        }
        if (!this.look.getValue()) {
            this.aimTarget = PlayerUtil.getNearestPlayer(this.range.getValue() + 1.5);
        }
        else {
            this.aimTarget = PlayerUtil.findLookingPlayer(this.range.getValue() + 1.5);
        }
        this.pos = null;
        if (this.aimTarget != null) {
            if (LemonClient.speedUtil.getPlayerSpeed(this.aimTarget) > this.maxSpeed.getValue()) {
                return;
            }
            if (!this.aimTarget.onGround && this.ground.getValue()) {
                return;
            }
            this.beforePlayerPos = new BlockPos(this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ);
            this.enemyCoordsInt = new int[] { (int)this.aimTarget.posX, (int)this.aimTarget.posY, (int)this.aimTarget.posZ };
            if (this.box.getValue()) {
                final List<PistonPos> list = new ArrayList<PistonPos>();
                for (final Vec3d side : this.sides) {
                    final Vec3d vec3d = new Vec3d(this.aimTarget.posX + side.x, this.aimTarget.posY, this.aimTarget.posZ + side.z);
                    final BlockPos blockPos = vec3toBlockPos(vec3d);
                    final PistonPos piston = this.getPos(blockPos, blockPos);
                    if (piston != null) {
                        list.add(piston);
                    }
                }
                this.pos = list.stream().filter(p -> p.getMaxRange() <= this.range.getValue()).min(Comparator.comparingDouble(PistonPos::getMaxRange)).orElse(null);
            }
            else {
                this.pos = this.getPos(this.beforePlayerPos, this.beforePlayerPos);
            }
            if (this.pos == null) {
                if (this.box.getValue()) {
                    final List<PistonPos> list = new ArrayList<PistonPos>();
                    for (final Vec3d side : this.sides) {
                        final Vec3d vec3d = new Vec3d(this.aimTarget.posX + side.x, this.aimTarget.posY, this.aimTarget.posZ + side.z);
                        final BlockPos blockPos = vec3toBlockPos(vec3d);
                        final PistonPos piston = this.getPos(blockPos.up(), blockPos);
                        if (piston != null) {
                            list.add(piston);
                        }
                    }
                    this.pos = list.stream().filter(p -> p.getMaxRange() <= this.range.getValue()).min(Comparator.comparingDouble(PistonPos::getMaxRange)).orElse(null);
                }
                else {
                    this.pos = this.getPos(this.beforePlayerPos.up(), this.beforePlayerPos);
                }
            }
        }
        else {
            if (this.debug.getValue()) {
                MessageBus.sendClientDeleteMessage("Cant find target", Notification.Type.ERROR, "AntiCamp", 7);
            }
            if (!this.disable.getValue().equals("NoDisable")) {
                this.disable();
            }
        }
        if (this.pos != null) {
            if (this.redstonePos != null && !this.useBlock && AntiHoleCamper.mc.world.getBlockState(this.redstonePos.down()).getBlock() == Blocks.AIR) {
                final BlockPos obsiPos = new BlockPos(this.redstonePos.x, this.redstonePos.y - 1, this.redstonePos.z);
                this.switchTo(this.obsiSlot, () -> BlockUtil.placeBlock(obsiPos, this.rotate.getValue(), this.packet.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue()));
            }
            this.pistonPos = this.pos.piston;
            this.redstonePos = this.pos.redstone;
            this.beforePlayerPos = this.pos.calcPos;
            if (BurrowUtil.getFirstFacing(this.redstonePos) == null) {
                this.placePiston(this.pistonPos, this.beforePlayerPos);
                this.placeRedstone(this.redstonePos);
            }
            else {
                this.placeRedstone(this.redstonePos);
                this.placePiston(this.pistonPos, this.beforePlayerPos);
            }
            this.disabling = true;
        }
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    private void placePiston(final BlockPos pistonPos, final BlockPos targetPos) {
        if (!BlockUtil.isAir(pistonPos)) {
            return;
        }
        final float[] angle = MathUtil.calcAngle(new Vec3d(pistonPos.x, 0.0, pistonPos.z), new Vec3d(targetPos.x, 0.0, targetPos.z));
        this.rotation = new Vec2f(angle[0] + 180.0f, angle[1]);
        AntiHoleCamper.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0] + 180.0f, angle[1], true));
        this.switchTo(this.pistonSlot, () -> {
            BlockUtil.placeBlock(pistonPos, false, this.packetPiston.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue());
            if (this.rotate.getValue()) {
                EntityUtil.facePlacePos(pistonPos, this.strict.getValue(), this.raytrace.getValue());
            }
        });
    }
    
    private void placeRedstone(final BlockPos redstonePos) {
        this.switchTo(this.redstoneSlot, () -> BlockUtil.placeBlock(redstonePos, this.rotate.getValue(), this.packetRedstone.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue()));
    }
    
    private PistonPos getPos(final BlockPos calcPos, final BlockPos playerPos) {
        if (AntiHoleCamper.mc.world.getBlockState(calcPos).getBlock() == Blocks.BEDROCK || AntiHoleCamper.mc.world.getBlockState(calcPos).getBlock() == Blocks.OBSIDIAN) {
            return null;
        }
        final List<PistonPos> posList = new ArrayList<PistonPos>();
        if (this.headCheck.getValue() && !this.airBlock(playerPos.up(2))) {
            return null;
        }
        for (final EnumFacing facing : EnumFacing.VALUES) {
            if (facing != EnumFacing.UP) {
                if (facing != EnumFacing.DOWN) {
                    if (this.canPlacePiston(calcPos, facing)) {
                        final BlockPos pistonPos = calcPos.offset(facing);
                        final BlockPos redstonePos = this.getRedstonePos(pistonPos);
                        if (redstonePos != null) {
                            if (BlockUtil.hasNeighbour(redstonePos) || BlockUtil.hasNeighbour(pistonPos)) {
                                posList.add(new PistonPos(pistonPos, redstonePos, calcPos));
                            }
                        }
                    }
                }
            }
        }
        return posList.stream().filter(p -> p.getMaxRange() <= this.range.getValue()).min(Comparator.comparingDouble(PistonPos::getMaxRange)).orElse(null);
    }
    
    public static BlockPos vec3toBlockPos(final Vec3d vec3d) {
        return new BlockPos(Math.floor(vec3d.x), (double)Math.round(vec3d.y), Math.floor(vec3d.z));
    }
    
    private boolean ready() {
        this.pistonSlot = findHotbarBlock(Blocks.PISTON);
        if (this.pistonSlot == -1) {
            this.pistonSlot = findHotbarBlock(Blocks.STICKY_PISTON);
        }
        this.redstoneSlot = (this.mode.getValue().equals("Torch") ? findHotbarBlock(Blocks.REDSTONE_TORCH) : findHotbarBlock(Blocks.REDSTONE_BLOCK));
        if (this.mode.getValue().equals("Both") && this.redstoneSlot == -1) {
            this.redstoneSlot = findHotbarBlock(Blocks.REDSTONE_TORCH);
        }
        this.obsiSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (this.redstoneSlot == -1) {
            if (this.debug.getValue()) {
                MessageBus.sendClientDeleteMessage("Cant find Redstone", Notification.Type.ERROR, "AntiCamp", 7);
            }
            return false;
        }
        this.useBlock = (this.redstoneSlot == findHotbarBlock(Blocks.REDSTONE_BLOCK));
        if ((!this.useBlock || this.block.getValue()) && this.obsiSlot == -1) {
            if (this.debug.getValue()) {
                MessageBus.sendClientDeleteMessage("Cant find Obsidian", Notification.Type.ERROR, "AntiCamp", 7);
            }
            return false;
        }
        if (BurrowUtil.findHotbarBlock(ItemPiston.class) == -1) {
            if (this.debug.getValue()) {
                MessageBus.sendClientDeleteMessage("Cant find Piston", Notification.Type.ERROR, "AntiCamp", 7);
            }
            return false;
        }
        return true;
    }
    
    public static int findHotbarBlock(final Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AntiHoleCamper.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() == blockIn) {
                return i;
            }
        }
        return -1;
    }
    
    private void doBreak(final BlockPos pos) {
        if (this.swing.getValue()) {
            AntiHoleCamper.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (this.breakBlock.getValue().equals("Packet")) {
            AntiHoleCamper.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            AntiHoleCamper.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
        }
        else {
            AntiHoleCamper.mc.playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
        }
    }
    
    public boolean isFacing(final BlockPos pos, final BlockPos facingPos) {
        final ImmutableMap<IProperty<?>, Comparable<?>> properties = AntiHoleCamper.mc.world.getBlockState(pos).getProperties();
        for (final IProperty<?> prop : properties.keySet()) {
            if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation"))) {
                final BlockPos pushPos = pos.offset((EnumFacing)properties.get(prop));
                if (this.isPos2(facingPos, pushPos)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public BlockPos hasRedstoneBlock(final BlockPos pos) {
        final List<BlockPos> redstone = new ArrayList<BlockPos>();
        final BlockPos[] array;
        final BlockPos[] offsets = array = new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        for (final BlockPos redstonePos : array) {
            redstone.add(pos.add(redstonePos));
        }
        if (this.useBlock) {
            redstone.add(pos.add(0, 1, 0));
        }
        return redstone.stream().filter(p -> BlockUtil.getBlock(p) == Blocks.REDSTONE_TORCH || BlockUtil.getBlock(p) == Blocks.REDSTONE_BLOCK).min(Comparator.comparingDouble(PlayerUtil::getDistanceI)).orElse(null);
    }
    
    static class PistonPos
    {
        public BlockPos piston;
        public BlockPos redstone;
        public BlockPos calcPos;
        
        public PistonPos(final BlockPos pistonPos, final BlockPos redstonePos, final BlockPos pos) {
            this.piston = pistonPos;
            this.redstone = redstonePos;
            this.calcPos = pos;
        }
        
        public double getMaxRange() {
            if (this.piston == null || this.redstone == null) {
                return 999999.0;
            }
            return Math.max(PlayerUtil.getDistance(this.piston), PlayerUtil.getDistance(this.redstone));
        }
    }
}
