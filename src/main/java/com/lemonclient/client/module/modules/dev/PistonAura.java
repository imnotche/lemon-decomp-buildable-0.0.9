// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.event.events.RenderEvent;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.properties.IProperty;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import java.util.Comparator;
import net.minecraft.util.math.AxisAlignedBB;
import com.lemonclient.api.util.misc.MathUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockPistonBase;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import com.lemonclient.api.util.misc.CrystalUtil;
import net.minecraft.block.Block;
import com.lemonclient.client.LemonClient;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.Iterator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.Entity;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.event.Phase;
import java.util.function.Predicate;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketVehicleMove;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.Arrays;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec2f;
import com.lemonclient.api.util.misc.Timing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "PistonAura", category = Category.Dev)
public class PistonAura extends Module
{
    public static PistonAura INSTANCE;
    public boolean autoCrystal;
    ModeSetting page;
    IntegerSetting maxTarget;
    DoubleSetting range;
    IntegerSetting maxY;
    IntegerSetting delay;
    IntegerSetting baseDelay;
    IntegerSetting startBreakDelay;
    IntegerSetting breakDelay;
    BooleanSetting floor;
    BooleanSetting alwaysCalc;
    BooleanSetting pistonCheck;
    BooleanSetting entityCheck;
    BooleanSetting base;
    BooleanSetting push;
    BooleanSetting crystal;
    BooleanSetting fire;
    BooleanSetting different;
    IntegerSetting maxPos;
    ModeSetting redstone;
    BooleanSetting packetPlace;
    BooleanSetting packet;
    BooleanSetting packetBreak;
    BooleanSetting antiWeakness;
    BooleanSetting swingArm;
    BooleanSetting silentSwitch;
    BooleanSetting packetSwitch;
    BooleanSetting crystalBypass;
    BooleanSetting force;
    BooleanSetting strict;
    BooleanSetting forceRotate;
    BooleanSetting rotate;
    BooleanSetting pistonRotate;
    BooleanSetting raytrace;
    BooleanSetting baseRaytrace;
    DoubleSetting forceRange;
    BooleanSetting pauseEat;
    BooleanSetting pause1;
    DoubleSetting maxSelfSpeed;
    DoubleSetting maxTargetSpeed;
    BooleanSetting render;
    BooleanSetting fireRender;
    BooleanSetting box;
    BooleanSetting outline;
    BooleanSetting hud;
    public static EntityPlayer target;
    public BlockPos targetPos;
    public BlockPos pistonPos;
    public BlockPos crystalPos;
    public BlockPos redStonePos;
    public BlockPos firePos;
    public BlockPos lastTargetPos;
    public int pistonSlot;
    public int crystalSlot;
    public int redStoneSlot;
    public int obbySlot;
    public int flintSlot;
    public Timing timer;
    public Timing baseTimer;
    public Timing startBreakTimer;
    public Timing breakTimer;
    public boolean preparedSpace;
    public boolean placedPiston;
    public boolean placedCrystal;
    public boolean placedFire;
    public boolean placedRedstone;
    public boolean brokeCrystal;
    int oldSlot;
    boolean useBlock;
    boolean boom;
    boolean burrowed;
    boolean moving;
    boolean first;
    Vec2f rotation;
    BlockPos[] saveArray;
    Vec3d[] sides;
    BlockPos[] offsets;
    BlockPos[] fireList;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> listener;
    
    public PistonAura() {
        this.page = this.registerMode("Page", Arrays.asList("Calc", "General", "Render"), "Calc");
        this.maxTarget = this.registerInteger("Max Target", 1, 1, 10, () -> this.page.getValue().equals("Calc"));
        this.range = this.registerDouble("Range", 6.0, 0.0, 10.0, () -> this.page.getValue().equals("Calc"));
        this.maxY = this.registerInteger("MaxY", 3, 1, 5, () -> this.page.getValue().equals("Calc"));
        this.delay = this.registerInteger("Delay", 20, 0, 100, () -> this.page.getValue().equals("Calc"));
        this.baseDelay = this.registerInteger("Base Delay", 0, 0, 100, () -> this.page.getValue().equals("Calc"));
        this.startBreakDelay = this.registerInteger("Start Break Delay", 0, 0, 100, () -> this.page.getValue().equals("Calc"));
        this.breakDelay = this.registerInteger("Break Delay", 0, 0, 100, () -> this.page.getValue().equals("Calc"));
        this.floor = this.registerBoolean("Floor", false, () -> this.page.getValue().equals("Calc"));
        this.alwaysCalc = this.registerBoolean("Loop Calc", false, () -> this.page.getValue().equals("Calc"));
        this.pistonCheck = this.registerBoolean("Piston Check", false, () -> this.page.getValue().equals("Calc"));
        this.entityCheck = this.registerBoolean("Crystal Check", false, () -> this.page.getValue().equals("Calc"));
        this.base = this.registerBoolean("Base", true, () -> this.page.getValue().equals("Calc"));
        this.push = this.registerBoolean("Push To Block", false, () -> this.page.getValue().equals("Calc"));
        this.crystal = this.registerBoolean("Crystal Detect", false, () -> this.page.getValue().equals("Calc"));
        this.fire = this.registerBoolean("Fire", true, () -> this.page.getValue().equals("Calc"));
        this.different = this.registerBoolean("Different Pos", false, () -> this.page.getValue().equals("Calc"));
        this.maxPos = this.registerInteger("Max Pos", 10, 1, 25, () -> this.different.getValue() && this.page.getValue().equals("Calc"));
        this.redstone = this.registerMode("Redstone", Arrays.asList("Block", "Torch", "Both"), "Block", () -> this.page.getValue().equals("General"));
        this.packetPlace = this.registerBoolean("Packet Place", true, () -> this.page.getValue().equals("General"));
        this.packet = this.registerBoolean("Packet Crystal", true, () -> this.page.getValue().equals("General"));
        this.packetBreak = this.registerBoolean("Packet Break", true, () -> this.page.getValue().equals("General"));
        this.antiWeakness = this.registerBoolean("Anti Weakness", false, () -> this.page.getValue().equals("General"));
        this.swingArm = this.registerBoolean("Swing Arm", true, () -> this.page.getValue().equals("General"));
        this.silentSwitch = this.registerBoolean("Switch Back", true, () -> this.page.getValue().equals("General"));
        this.packetSwitch = this.registerBoolean("Packet Switch", true, () -> this.page.getValue().equals("General"));
        this.crystalBypass = this.registerBoolean("Crystal Bypass", true, () -> this.page.getValue().equals("General"));
        this.force = this.registerBoolean("Force Bypass", false, () -> this.crystalBypass.getValue() && this.page.getValue().equals("General"));
        this.strict = this.registerBoolean("Strict", true, () -> this.page.getValue().equals("General"));
        this.forceRotate = this.registerBoolean("Piston ForceRotate", false, () -> this.page.getValue().equals("General"));
        this.rotate = this.registerBoolean("Rotate", true, () -> this.page.getValue().equals("General"));
        this.pistonRotate = this.registerBoolean("Piston Rotate", true, () -> this.rotate.getValue() && this.page.getValue().equals("General"));
        this.raytrace = this.registerBoolean("RayTrace", true, () -> this.page.getValue().equals("General"));
        this.baseRaytrace = this.registerBoolean("Base RayTrace", true, () -> this.base.getValue() && this.raytrace.getValue() && this.page.getValue().equals("General"));
        this.forceRange = this.registerDouble("Force Range", 3.0, 0.0, 6.0, () -> this.raytrace.getValue() && this.page.getValue().equals("General"));
        this.pauseEat = this.registerBoolean("Pause When Eating", true, () -> this.page.getValue().equals("General"));
        this.pause1 = this.registerBoolean("Pause When Burrow", true, () -> this.page.getValue().equals("General"));
        this.maxSelfSpeed = this.registerDouble("Max Self Speed", 10.0, 0.0, 50.0, () -> this.page.getValue().equals("General"));
        this.maxTargetSpeed = this.registerDouble("Max Target Speed", 10.0, 0.0, 50.0, () -> this.page.getValue().equals("General"));
        this.render = this.registerBoolean("Render", false, () -> this.page.getValue().equals("Render"));
        this.fireRender = this.registerBoolean("Fire Render", false, () -> this.render.getValue() && this.page.getValue().equals("Render"));
        this.box = this.registerBoolean("Box", false, () -> this.render.getValue() && this.page.getValue().equals("Render"));
        this.outline = this.registerBoolean("Outline", false, () -> this.render.getValue() && this.page.getValue().equals("Render"));
        this.hud = this.registerBoolean("HUD", false, () -> this.page.getValue().equals("Render"));
        this.timer = new Timing();
        this.baseTimer = new Timing();
        this.startBreakTimer = new Timing();
        this.breakTimer = new Timing();
        this.saveArray = new BlockPos[25];
        this.sides = new Vec3d[] { new Vec3d(0.24, 0.0, 0.24), new Vec3d(-0.24, 0.0, 0.24), new Vec3d(0.24, 0.0, -0.24), new Vec3d(-0.24, 0.0, -0.24) };
        this.offsets = new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        this.fireList = new BlockPos[] { new BlockPos(0, 1, 0), new BlockPos(1, 1, 1), new BlockPos(1, 1, 0), new BlockPos(1, 1, -1), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1), new BlockPos(-1, 1, 1), new BlockPos(-1, 1, 0), new BlockPos(-1, 1, -1), new BlockPos(1, 0, 1), new BlockPos(1, 0, 0), new BlockPos(1, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, 0), new BlockPos(-1, 0, -1), new BlockPos(0, 0, 0) };
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (this.rotation != null) {
                if (event.getPacket() instanceof CPacketPlayer.Rotation) {
                    ((CPacketPlayer.Rotation)event.getPacket()).yaw = this.rotation.x;
                    ((CPacketPlayer.Rotation)event.getPacket()).pitch = 0.0f;
                }
                if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                    ((CPacketPlayer.PositionRotation)event.getPacket()).yaw = this.rotation.x;
                    ((CPacketPlayer.PositionRotation)event.getPacket()).pitch = 0.0f;
                }
                if (event.getPacket() instanceof CPacketVehicleMove) {
                    ((AccessorCPacketVehicleMove)event.getPacket()).setYaw(this.rotation.x);
                }
            }
        }, new Predicate[0]);
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (this.rotation == null || event.getPhase() != Phase.PRE) {
            }
            else {
                final PlayerPacket packet = new PlayerPacket(this, new Vec2f(this.rotation.x, 0.0f));
                PlayerPacketManager.INSTANCE.addPacket(packet);
            }
        }, new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (PistonAura.mc.world == null || PistonAura.mc.player == null || this.crystalPos == null) {
            }
            else {
                if (event.getPacket() instanceof SPacketSoundEffect) {
                    final SPacketSoundEffect packet2 = (SPacketSoundEffect)event.getPacket();
                    if (packet2.getCategory() == SoundCategory.BLOCKS && packet2.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && this.crystalPos.distanceSq(packet2.getX(), packet2.getY(), packet2.getZ()) <= 9.0) {
                        this.boom = true;
                    }
                }
            }
        }, new Predicate[0]);
        this.listener = new Listener<PacketEvent.Receive>(event -> {
            if (PistonAura.mc.world == null || PistonAura.mc.player == null || PistonAura.mc.player.isDead) {
            }
            else {
                if (event.getPacket() instanceof SPacketSoundEffect) {
                    final SPacketSoundEffect packet3 = (SPacketSoundEffect)event.getPacket();
                    if (packet3.getCategory() == SoundCategory.BLOCKS && packet3.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                        for (final Entity crystal : new ArrayList<Entity>(PistonAura.mc.world.loadedEntityList)) {
                            if (crystal instanceof EntityEnderCrystal && crystal.getDistance(packet3.getX(), packet3.getY(), packet3.getZ()) <= this.range.getValue() + 5.0) {
                                crystal.setDead();
                            }
                        }
                    }
                }
            }
        }, new Predicate[0]);
        PistonAura.INSTANCE = this;
    }
    
    public void onEnable() {
        this.saveArray = new BlockPos[25];
        this.first = true;
        this.reset();
    }
    
    @Override
    public void onTick() {
        if (this.autoCrystal) {
            this.reset();
            return;
        }
        this.doPA();
    }
    
    public void doPA() {
        this.moving = false;
        this.burrowed = false;
        final BlockPos originalPos = PlayerUtil.getPlayerPos();
        final Block block = BlockUtil.getBlock(originalPos);
        if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST) {
            this.burrowed = true;
        }
        if (this.pause1.getValue() && this.burrowed) {
            return;
        }
        if (this.pauseEat.getValue() && EntityUtil.isEating()) {
            return;
        }
        if (LemonClient.speedUtil.getPlayerSpeed(PistonAura.mc.player) > this.maxSelfSpeed.getValue()) {
            return;
        }
        this._doPA();
    }
    
    public void _doPA() {
        if (!this.forceRotate.getValue()) {
            this.rotation = null;
        }
        if (PistonAura.mc.world == null || PistonAura.mc.player == null || PistonAura.mc.player.isDead) {
            this.reset();
            return;
        }
        try {
            if (!this.findMaterials()) {
                this.reset();
                return;
            }
            if (this.alwaysCalc.getValue() || this.boom || PistonAura.target == null || !EntityUtil.isAlive(PistonAura.target)) {
                final PistonAuraPos pos = this.findSpace();
                if (pos == null) {
                    this.first = true;
                    PistonAura.target = null;
                    final BlockPos targetPos = null;
                    this.crystalPos = targetPos;
                    this.firePos = targetPos;
                    this.redStonePos = targetPos;
                    this.pistonPos = targetPos;
                    this.targetPos = targetPos;
                    this.rotation = null;
                    return;
                }
                PistonAura.target = pos.target;
                this.targetPos = pos.targetPos;
                this.pistonPos = pos.piston;
                this.redStonePos = pos.redstone;
                this.crystalPos = pos.crystal;
                this.firePos = pos.fire;
            }
            if (this.targetPos == null || this.pistonPos == null || this.redStonePos == null || this.crystalPos == null) {
                if (this.breakTimer.passedDms(this.breakDelay.getValue()) && this.lastTargetPos != null) {
                    if (this.packetBreak.getValue()) {
                        CrystalUtil.breakCrystalPacket(this.lastTargetPos, this.swingArm.getValue());
                    }
                    else {
                        CrystalUtil.breakCrystal(this.lastTargetPos, this.swingArm.getValue());
                    }
                    this.breakTimer.reset();
                }
                this.reset();
                return;
            }
            if (PlayerUtil.getDistanceI(this.pistonPos) > this.range.getValue() || PlayerUtil.getDistanceI(this.redStonePos) > this.range.getValue() || PlayerUtil.getDistanceI(this.crystalPos) > this.range.getValue()) {
                this.lastTargetPos = null;
                this.reset();
                return;
            }
            AutoEz.INSTANCE.addTargetedPlayer(PistonAura.target.getName());
            this.lastTargetPos = new BlockPos(this.targetPos.getX(), this.crystalPos.getY() + 2, this.targetPos.getZ());
            this.oldSlot = PistonAura.mc.player.inventory.currentItem;
            final BlockPos offset = new BlockPos(this.crystalPos.getX() - this.targetPos.getX(), 0, this.crystalPos.getZ() - this.targetPos.getZ());
            final BlockPos headPos = this.pistonPos.add(offset.getX() * -1, 0, offset.getZ() * -1);
            final Block block = BlockUtil.getBlock(headPos);
            if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST || this.checkPos(headPos)) {
                this.reset();
                return;
            }
            this.placedPiston = (BlockUtil.getBlock(this.pistonPos) instanceof BlockPistonBase);
            this.placedRedstone = this.hasRedstone(this.pistonPos);
            this.placedCrystal = (this.getCrystal(this.crystalPos.up()) != null && this.getCrystal(new BlockPos(this.targetPos.getX(), this.crystalPos.getY() + 2, this.targetPos.getZ())) != null);
            this.placedFire = (BlockUtil.getBlock(this.firePos) instanceof BlockFire);
            if (this.flintSlot == -1 || BlockUtil.isBlockUnSolid(this.firePos.down()) || BlockUtil.canOpen(this.firePos.down())) {
                this.placedFire = true;
            }
            if (!this.placedCrystal && (!BlockUtil.isAirBlock(this.crystalPos.up()) || !BlockUtil.isAirBlock(this.crystalPos.up(2)))) {
                this.reset();
                return;
            }
            if (!this.placedPiston && (this.intersectsWithEntity(this.pistonPos) || !BlockUtil.canReplace(this.pistonPos))) {
                this.reset();
                return;
            }
            if (!this.placedRedstone && (this.intersectsWithEntity(this.redStonePos) || !BlockUtil.canReplace(this.redStonePos))) {
                this.reset();
                return;
            }
            final float[] angle = MathUtil.calcAngle(new Vec3d(this.crystalPos), new Vec3d(this.targetPos));
            this.rotation = new Vec2f(angle[0] + 180.0f, angle[1]);
            if (!this.preparedSpace) {
                if (!(this.preparedSpace = (this.canPlace(this.pistonPos) || this.canPlace(this.redStonePos)))) {
                    if (!this.base.getValue()) {
                        this.preparedSpace = true;
                    }
                    else if (this.baseTimer.passedDms(this.baseDelay.getValue())) {
                        this.baseTimer.reset();
                        this.preparedSpace = this.prepareSpace();
                    }
                }
                this.timer.reset();
            }
            if (this.preparedSpace && this.first) {
                if (!this.forceRotate.getValue()) {
                    this.timer.setMs(1000000000L);
                }
                this.first = false;
            }
            boolean finish = this.placedPiston && this.placedCrystal && this.placedRedstone;
            if (this.timer.passedDms(this.delay.getValue()) && !finish) {
                this.timer.reset();
                if (!this.placedPiston && !this.canPlace(this.pistonPos) && this.canPlace(this.redStonePos)) {
                    this.placeRedstone(this.preparedSpace && !this.placedRedstone);
                }
                this.placePiston(this.preparedSpace && !this.placedPiston);
                this.placeCrystal(this.placedPiston && !this.placedCrystal);
                if (this.placedCrystal && !this.placedFire) {
                    this.setItem(this.flintSlot);
                    if (this.placeBlock(this.firePos, this.packetPlace.getValue())) {
                        this.placedFire = true;
                    }
                }
                this.placeRedstone(this.placedFire && !this.placedRedstone);
                finish = (this.placedPiston && this.placedCrystal && this.placedFire && this.placedRedstone);
            }
            if (finish) {
                final Entity crystal = (Entity)PistonAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.crystalPos.up(2))).stream().filter(e -> e instanceof EntityEnderCrystal).min(Comparator.comparing(e -> this.getDistance(PistonAura.target, e))).orElse(null);
                if (this.startBreakTimer.passedDms(this.startBreakDelay.getValue()) && this.breakTimer.passedDms(this.breakDelay.getValue()) && crystal != null) {
                    this.breakTimer.reset();
                    if (this.antiWeakness.getValue() && PistonAura.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                        int newSlot = -1;
                        for (int i = 0; i < 9; ++i) {
                            final ItemStack stack = PistonAura.mc.player.inventory.getStackInSlot(i);
                            if (stack != ItemStack.EMPTY) {
                                if (stack.getItem() instanceof ItemSword) {
                                    newSlot = i;
                                    break;
                                }
                                if (stack.getItem() instanceof ItemTool) {
                                    newSlot = i;
                                }
                            }
                        }
                        if (newSlot != -1) {
                            this.setItem(newSlot);
                        }
                    }
                    if (this.packetBreak.getValue()) {
                        PistonAura.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    }
                    else {
                        PistonAura.mc.playerController.attackEntity(PistonAura.mc.player, crystal);
                    }
                    if (this.swingArm.getValue()) {
                        PistonAura.mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }
            }
            this.restoreItem();
        }
        catch (final Exception ex) {}
    }
    
    private void placePiston(final boolean work) {
        if (!work) {
            return;
        }
        this.setItem(this.pistonSlot);
        PistonAura.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(this.rotation.x, this.rotation.y, true));
        this.placedPiston = this.placeBlock(this.pistonPos, this.packetPlace.getValue());
        this.startBreakTimer.reset();
        this.breakTimer.reset();
    }
    
    private void placeCrystal(final boolean work) {
        if (!work) {
            return;
        }
        final EnumHand hand = (this.crystalSlot != 999) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        if (this.crystalBypass.getValue() && (this.crystalSlot >= 9 || this.force.getValue()) && hand == EnumHand.MAIN_HAND) {
            int slot = this.crystalSlot;
            if (slot < 9) {
                slot += 36;
            }
            PistonAura.mc.player.connection.sendPacket(new CPacketClickWindow(0, slot, PistonAura.mc.player.inventory.currentItem, ClickType.SWAP, ItemStack.EMPTY, PistonAura.mc.player.inventoryContainer.getNextTransactionID(PistonAura.mc.player.inventory)));
            this.placedCrystal = CrystalUtil.placeCrystal(this.crystalPos, hand, this.packet.getValue(), this.rotate.getValue(), this.swingArm.getValue());
            PistonAura.mc.player.connection.sendPacket(new CPacketClickWindow(0, slot, PistonAura.mc.player.inventory.currentItem, ClickType.SWAP, Items.END_CRYSTAL.getDefaultInstance(), PistonAura.mc.player.inventoryContainer.getNextTransactionID(PistonAura.mc.player.inventory)));
        }
        else {
            this.setItem(this.crystalSlot);
            this.placedCrystal = CrystalUtil.placeCrystal(this.crystalPos, hand, this.packet.getValue(), this.rotate.getValue(), this.swingArm.getValue());
        }
        this.startBreakTimer.reset();
        this.breakTimer.reset();
    }
    
    private void placeRedstone(final boolean work) {
        if (!work) {
            return;
        }
        this.setItem(this.redStoneSlot);
        this.placedRedstone = BlockUtil.placeBlockBoolean(this.redStonePos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), this.strict.getValue(), this.needRaytrace(this.redStonePos), this.swingArm.getValue());
        this.startBreakTimer.reset();
        this.breakTimer.reset();
    }
    
    public boolean prepareSpace() {
        BlockPos piston = this.pistonPos.add(0, -1, 0);
        if (this.isPos2(piston, this.redStonePos)) {
            piston = piston.down();
        }
        final BlockPos redstone = this.redStonePos.add(0, -1, 0);
        if (!this.canPlace(this.pistonPos)) {
            if (this.intersectsWithEntity(this.pistonPos)) {
                this.reset();
            }
            else {
                final BlockPos offset = new BlockPos(this.crystalPos.getX() - this.targetPos.getX(), 0, this.crystalPos.getZ() - this.targetPos.getZ());
                final BlockPos crystalOffset = this.crystalPos.add(offset);
                final BlockPos crystalOffset2 = crystalOffset.add(offset);
                this.setItem(this.obbySlot);
                if (this.canPlace(piston) && BlockUtil.canReplace(piston) && !this.isPos2(piston, this.redStonePos)) {
                    if (this.intersectsWithEntity(piston)) {
                        this.reset();
                    }
                    else {
                        BlockUtil.placeBlock(piston, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), this.strict.getValue(), this.raytrace.getValue() && this.baseRaytrace.getValue(), this.swingArm.getValue());
                    }
                }
                else if (this.canPlace(crystalOffset2) && BlockUtil.canReplace(crystalOffset2) && !this.isPos2(crystalOffset2, this.redStonePos)) {
                    if (this.intersectsWithEntity(crystalOffset2)) {
                        this.reset();
                    }
                    else {
                        BlockUtil.placeBlock(crystalOffset2, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), this.strict.getValue(), this.raytrace.getValue() && this.baseRaytrace.getValue(), this.swingArm.getValue());
                    }
                }
                else if (this.canPlace(crystalOffset) && BlockUtil.canReplace(crystalOffset) && !this.isPos2(crystalOffset, this.redStonePos)) {
                    if (this.intersectsWithEntity(crystalOffset)) {
                        this.reset();
                    }
                    else {
                        BlockUtil.placeBlock(crystalOffset, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), this.strict.getValue(), this.raytrace.getValue() && this.baseRaytrace.getValue(), this.swingArm.getValue());
                    }
                }
                else {
                    this.reset();
                }
            }
            return false;
        }
        if ((!this.canPlace(this.redStonePos) || (!this.useBlock && this.redStonePos.getY() == this.pistonPos.getY())) && this.canPlace(redstone) && !this.isPos2(redstone, this.pistonPos)) {
            if (this.intersectsWithEntity(redstone)) {
                this.reset();
            }
            else {
                this.setItem(this.obbySlot);
                BlockUtil.placeBlock(redstone, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), this.strict.getValue(), this.raytrace.getValue() && this.baseRaytrace.getValue(), this.swingArm.getValue());
            }
            return false;
        }
        return true;
    }
    
    public PistonAuraPos findSpace() {
        final List<PistonAuraPos> list = new ArrayList<PistonAuraPos>();
        for (final EntityPlayer target : PlayerUtil.getNearPlayers(this.range.getValue() + 4.0, this.maxTarget.getValue())) {
            if (LemonClient.speedUtil.getPlayerSpeed(target) > this.maxTargetSpeed.getValue()) {
                continue;
            }
            if ((int)(target.posY + 0.5) >= 255) {
                continue;
            }
            final List<PistonAuraPos> sideList = new ArrayList<PistonAuraPos>();
            for (final Vec3d vec3d : this.sides) {
                final BlockPos targetPos = new BlockPos(target.posX + vec3d.x, target.posY + 0.5, target.posZ + vec3d.z);
                BlockPos cPos = null;
                for (final Entity entity : PistonAura.mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {
                        cPos = new BlockPos(entity.posX, entity.posY - 1.0, entity.posZ);
                        final int x = Math.abs(cPos.getX() - targetPos.getX());
                        final int y = cPos.y - targetPos.y;
                        final int z = Math.abs(cPos.getZ() - targetPos.getZ());
                        if (x <= 1 && y <= 5 && y >= 0 && z <= 1) {
                            break;
                        }
                        cPos = null;
                    }
                }
                final BlockPos[] offsets = { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
                boolean calc = false;
                final List<PistonAuraPos> posList = new ArrayList<PistonAuraPos>();
                for (int y = this.floor.getValue() ? -1 : 0; y <= this.maxY.getValue(); ++y) {
                    boolean cantPlace = false;
                    boolean block = false;
                    for (int high = y + 1; high >= 0; --high) {
                        final BlockPos pos = targetPos.up(high);
                        if (isResistant(BlockUtil.getState(pos))) {
                            if (high < y + 1) {
                                cantPlace = true;
                            }
                            else if (!this.push.getValue()) {
                                cantPlace = true;
                            }
                            else {
                                block = true;
                            }
                        }
                    }
                    if (!cantPlace) {
                        for (final BlockPos side : offsets) {
                            if (!this.crystal.getValue()) {
                                cPos = null;
                            }
                            BlockPos offset = (cPos == null) ? side : new BlockPos(cPos.getX() - targetPos.getX(), 0, cPos.getZ() - targetPos.getZ());
                            if (cPos != null && this.isPos2(new BlockPos(-offset.getX(), 0, -offset.getZ()), side)) {
                                cPos = null;
                            }
                            Label_1268: {
                                if (cPos == null) {
                                    offset = side;
                                }
                                else if (calc) {
                                    break Label_1268;
                                }
                                final BlockPos crystalPos = (cPos == null) ? targetPos.add(offset.getX(), y, offset.getZ()) : cPos;
                                if (crystalPos.y < 255) {
                                    if (cPos == null) {
                                        if (BlockUtil.getBlock(crystalPos) != Blocks.OBSIDIAN && BlockUtil.getBlock(crystalPos) != Blocks.BEDROCK) {
                                            break Label_1268;
                                        }
                                        if (!PistonAura.mc.world.isAirBlock(crystalPos.up())) {
                                            break Label_1268;
                                        }
                                        if (!PistonAura.mc.world.isAirBlock(crystalPos.up(2))) {
                                            break Label_1268;
                                        }
                                        if (PlayerUtil.getDistanceI(crystalPos) > this.range.getValue()) {
                                            break Label_1268;
                                        }
                                        if (!PistonAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(crystalPos.up())).isEmpty()) {
                                            break Label_1268;
                                        }
                                        if (!PistonAura.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(crystalPos.up(2))).isEmpty()) {
                                            break Label_1268;
                                        }
                                    }
                                    final BlockPos normal = crystalPos.add(offset);
                                    final BlockPos oneBlock = normal.add(offset);
                                    final BlockPos side2 = normal.add(offset.getZ(), 0, offset.getX());
                                    final BlockPos side3 = normal.add(offset.getZ() * -1, 0, offset.getX() * -1);
                                    final BlockPos side4 = oneBlock.add(offset.getZ(), 0, offset.getX());
                                    final BlockPos side5 = oneBlock.add(offset.getZ() * -1, 0, offset.getX() * -1);
                                    final BlockPos side6 = crystalPos.add(offset.getZ(), 0, offset.getX());
                                    final BlockPos side7 = crystalPos.add(offset.getZ() * -1, 0, offset.getX() * -1);
                                    final List<BlockPos> pistons = new ArrayList<BlockPos>();
                                    this.add(pistons, normal);
                                    this.add(pistons, oneBlock);
                                    this.add(pistons, side2);
                                    this.add(pistons, side3);
                                    this.add(pistons, side4);
                                    this.add(pistons, side5);
                                    this.add(pistons, side6);
                                    this.add(pistons, side7);
                                    final BlockPos finalOffset = offset;
                                    pistons.removeIf(p -> {
                                        if (this.flintSlot != -1 && !this.getFirePos(targetPos, crystalPos, finalOffset)) {
                                            return true;
                                        }
                                        else if (!this.different.getValue()) {
                                            return false;
                                        }
                                        else {
                                            boolean same = false;
                                            final BlockPos[] saveArray = this.saveArray;
                                            final int length3 = saveArray.length;
                                            int k = 0;
                                            while (k < length3) {
                                                final BlockPos savePos = saveArray[k];
                                                if (this.isPos2(savePos, p)) {
                                                    same = true;
                                                    break;
                                                }
                                                else {
                                                    ++k;
                                                }
                                            }
                                            return same;
                                        }
                                    });
                                    if (!pistons.isEmpty()) {
                                        final List<BlockPos> pistonList = pistons.stream().filter(pistonPos -> {
                                            final BlockPos headPos = pistonPos.add(finalOffset.getX() * -1, 0, finalOffset.getZ() * -1);
                                            final Block headBlock = BlockUtil.getBlock(headPos);
                                            if (headBlock == Blocks.BEDROCK || headBlock == Blocks.OBSIDIAN || headBlock == Blocks.ENDER_CHEST || headBlock == Blocks.PISTON_HEAD || this.checkPos(headPos)) {
                                                return false;
                                            }
                                            else {
                                                final boolean isPiston = BlockUtil.getBlock(pistonPos) instanceof BlockPistonBase;
                                                if (!isPiston) {
                                                    if (!this.canPlace(pistonPos)) {
                                                        return false;
                                                    }
                                                    else if (PistonAura.mc.player.getDistance(pistonPos.getX() + 0.5, pistonPos.getY() + 0.5, pistonPos.getZ() + 0.5) > this.range.getValue()) {
                                                        return false;
                                                    }
                                                    else {
                                                        final double feetY = PistonAura.mc.player.posY;
                                                        if ((PlayerUtil.getDistanceI(pistonPos) < 0.8 + pistonPos.getY() - feetY && pistonPos.getY() > feetY + 1.0) || (PlayerUtil.getDistanceI(pistonPos) < 1.8 + feetY - pistonPos.getY() && pistonPos.getY() < feetY)) {
                                                            return false;
                                                        }
                                                    }
                                                }
                                                else if (this.pistonCheck.getValue() && (this.hasRedstone(pistonPos) || !this.isFacing(pistonPos, headPos))) {
                                                    return false;
                                                }
                                                final BlockPos redstonePos = this.getRedStonePos(crystalPos, pistonPos, finalOffset);
                                                if (redstonePos == null) {
                                                    return false;
                                                }
                                                else if (this.flintSlot != -1 && this.getFirePos(targetPos, crystalPos, pistonPos, redstonePos, finalOffset) == null) {
                                                    return false;
                                                }
                                                else {
                                                    final boolean b = false;
                                                    if (!isPiston) {
                                                        if (!BlockUtil.canPlaceWithoutBase(pistonPos, this.strict.getValue(), this.needRaytrace(pistonPos), this.canPlace(redstonePos) || (this.obbySlot != -1 && (this.base.getValue() || this.canPlace(redstonePos.down()))))) {
                                                            return b;
                                                        }
                                                    }
                                                    return b;
                                                }
                                            }
                                        }).collect(Collectors.toList());
                                        if (pistonList.isEmpty()) {
                                            pistonList.addAll(pistons);
                                        }
                                        final BlockPos piston = pistonList.stream().min(Comparator.comparingInt(this::blockLevel)).orElse(null);
                                        final PistonAuraPos pos2 = new PistonAuraPos(crystalPos, piston, this.getRedStonePos(crystalPos, piston, offset), offset, target, targetPos, block);
                                        posList.add(pos2);
                                        if (cPos != null) {
                                            calc = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                final List<PistonAuraPos> paList = posList.stream().filter(p -> !p.block || p.offset.z == 1).collect(Collectors.toList());
                if (paList.isEmpty()) {
                    paList.addAll(posList);
                }
                final PistonAuraPos best = paList.stream().min(Comparator.comparingDouble(PistonAuraPos::range)).orElse(null);
                if (best != null) {
                    sideList.add(best);
                }
            }
            if (sideList.isEmpty()) {
                continue;
            }
            list.add(sideList.stream().min(Comparator.comparingDouble(PistonAuraPos::range)).orElse(null));
        }
        final PistonAuraPos best2 = list.stream().min(Comparator.comparingDouble(PistonAuraPos::range)).orElse(null);
        if (best2 == null) {
            this.saveArray = new BlockPos[25];
            return null;
        }
        return best2;
    }
    
    public boolean isFacing(final BlockPos pos, final BlockPos facingPos) {
        final ImmutableMap<IProperty<?>, Comparable<?>> properties = PistonAura.mc.world.getBlockState(pos).getProperties();
        for (final IProperty<?> prop : properties.keySet()) {
            if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation"))) {
                final BlockPos pushPos = pos.offset((EnumFacing)properties.get(prop));
                return this.isPos2(facingPos, pushPos);
            }
        }
        return false;
    }
    
    public static boolean isResistant(final IBlockState blockState) {
        return blockState.getMaterial() != Material.AIR && !BlockUtil.isAir(blockState.getBlock()) && !(blockState instanceof BlockLiquid) && blockState.getBlock().blockResistance >= 19.7;
    }
    
    public boolean getFirePos(final BlockPos targetPos, final BlockPos crystalPos, final BlockPos offset) {
        final BlockPos origin = new BlockPos(targetPos.getX(), crystalPos.getY() + 1, targetPos.getZ());
        final BlockPos pos1 = targetPos.add(-offset.x, 0, -offset.z);
        final BlockPos pos2 = pos1.add(offset.z, 0, offset.x);
        final BlockPos pos3 = pos1.add(-offset.z, 0, -offset.x);
        final BlockPos pos4 = targetPos.add(-offset.x, 1, -offset.z);
        final BlockPos pos5 = pos4.add(offset.z, 0, offset.x);
        final BlockPos pos6 = pos4.add(-offset.z, 0, -offset.x);
        final BlockPos[] posList = { pos1, pos2, pos3, pos4, pos5, pos6 };
        boolean air = true;
        for (final BlockPos pos7 : posList) {
            if (!BlockUtil.isAir(pos7)) {
                final AxisAlignedBB box = BlockUtil.getBoundingBox(pos7);
                if (box != null) {
                    final double x = box.maxX - box.minX;
                    final double z = box.maxZ - box.minZ;
                    if (x * x == offset.x * offset.x || z * z == offset.z * offset.z) {
                        air = false;
                        break;
                    }
                }
            }
        }
        for (final BlockPos firePos : this.fireList) {
            final BlockPos pos8 = origin.add(firePos);
            Label_0442: {
                if (!air) {
                    if (targetPos.add(-offset.x, 0, 0).getX() == pos8.getX()) {
                        break Label_0442;
                    }
                    if (targetPos.add(0, 0, -offset.z).getZ() == pos8.getZ()) {
                        break Label_0442;
                    }
                }
                if (BlockUtil.getBlock(pos8) == Blocks.FIRE) {
                    return true;
                }
                if (this.canPlaceFire(pos8) && PistonAura.mc.world.isAirBlock(pos8) && !BlockUtil.isBlockUnSolid(pos8.down()) && !BlockUtil.canOpen(pos8.down())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public BlockPos getFirePos(final BlockPos targetPos, final BlockPos crystalPos, final BlockPos pistonPos, final BlockPos redstonePos, final BlockPos offset) {
        final BlockPos origin = new BlockPos(targetPos.getX(), crystalPos.getY() + 1, targetPos.getZ());
        final BlockPos pos1 = targetPos.add(-offset.x, 0, -offset.z);
        final BlockPos pos2 = pos1.add(offset.z, 0, offset.x);
        final BlockPos pos3 = pos1.add(-offset.z, 0, -offset.x);
        final BlockPos pos4 = targetPos.add(-offset.x, 1, -offset.z);
        final BlockPos pos5 = pos4.add(offset.z, 0, offset.x);
        final BlockPos pos6 = pos4.add(-offset.z, 0, -offset.x);
        final BlockPos[] posList = { pos1, pos2, pos3, pos4, pos5, pos6 };
        boolean air = true;
        for (final BlockPos pos7 : posList) {
            if (!BlockUtil.isAir(pos7)) {
                final AxisAlignedBB box = BlockUtil.getBoundingBox(pos7);
                if (box != null) {
                    final double x = box.maxX - box.minX;
                    final double z = box.maxZ - box.minZ;
                    if (x * x == offset.x * offset.x || z * z == offset.z * offset.z) {
                        air = false;
                        break;
                    }
                }
            }
        }
        final List<BlockPos> list = new ArrayList<BlockPos>();
        for (final BlockPos firePos : this.fireList) {
            final BlockPos pos8 = origin.add(firePos);
            Label_0551: {
                if (!this.isPos2(pistonPos, pos8) && !this.isPos2(pistonPos.up(), pos8)) {
                    if (!this.isPos2(redstonePos, pos8)) {
                        if (!this.isPos2(pistonPos, pos8.add(offset))) {
                            if (!this.isPos2(pistonPos.up(), pos8.add(offset))) {
                                if (!air) {
                                    if (targetPos.add(-offset.x, 0, 0).getX() == pos8.getX()) {
                                        break Label_0551;
                                    }
                                    if (targetPos.add(0, 0, -offset.z).getZ() == pos8.getZ()) {
                                        break Label_0551;
                                    }
                                }
                                if (BlockUtil.getBlock(pos8) == Blocks.FIRE) {
                                    return pos8;
                                }
                                if (this.canPlaceFire(pos8) && PistonAura.mc.world.isAirBlock(pos8) && !BlockUtil.isBlockUnSolid(pos8.down()) && !BlockUtil.canOpen(pos8.down())) {
                                    list.add(pos8);
                                }
                            }
                        }
                    }
                }
            }
        }
        return list.stream().sorted(this::fireLevel).min(Comparator.comparingDouble(PlayerUtil::getDistanceI)).orElse(null);
    }
    
    public BlockPos getRedStonePos(final BlockPos crystalPos, final BlockPos pistonPos, final BlockPos offset) {
        final BlockPos pos = this.hasRedstoneBlock(pistonPos);
        if (pos != null) {
            return pos;
        }
        final List<BlockPos> redstone = new ArrayList<BlockPos>();
        if (this.useBlock) {
            for (final EnumFacing facing : EnumFacing.VALUES) {
                final BlockPos redstonePos = pistonPos.offset(facing);
                if (BlockUtil.canReplace(redstonePos)) {
                    if (PistonAura.mc.player.getDistance(redstonePos.getX() + 0.5, redstonePos.getY() + 0.5, redstonePos.getZ() + 0.5) <= this.range.getValue()) {
                        boolean can = true;
                        for (final BlockPos blockPos : this.fireList) {
                            if (this.isPos2(crystalPos.up().add(blockPos), redstonePos)) {
                                can = false;
                                break;
                            }
                        }
                        if (can && BlockUtil.canPlaceWithoutBase(redstonePos, this.strict.getValue(), this.needRaytrace(redstonePos), this.base.getValue())) {
                            redstone.add(redstonePos);
                        }
                    }
                }
            }
        }
        else {
            final BlockPos pistonPush = pistonPos.add(offset.getX() * -1, 0, offset.getZ() * -1);
            for (final BlockPos add : this.offsets) {
                for (int i = 0; i < 2; ++i) {
                    final BlockPos torch = pistonPos.down(i).add(add);
                    if (BlockUtil.canReplace(torch)) {
                        if (torch.getX() != crystalPos.getX() || torch.getZ() != crystalPos.getZ()) {
                            if (torch.getX() != pistonPush.getX() || torch.getZ() != pistonPush.getZ()) {
                                if (i != 1 || !BlockUtil.isBlockUnSolid(torch.up())) {
                                    if (PistonAura.mc.player.getDistance(torch.getX() + 0.5, torch.getY() + 0.5, torch.getZ() + 0.5) <= this.range.getValue()) {
                                        if (BlockUtil.isAir(torch.down()) || !BlockUtil.isBlockUnSolid(torch.down())) {
                                            if (BlockUtil.canPlaceWithoutBase(torch, this.strict.getValue(), this.needRaytrace(torch), this.base.getValue(), pistonPos)) {
                                                redstone.add(torch);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        redstone.removeIf(p -> p.y > 255);
        return redstone.stream().min(Comparator.comparingInt(this::blockLevel)).orElse(null);
    }
    
    public boolean hasRedstone(final BlockPos pos) {
        return this.hasRedstoneBlock(pos) != null;
    }
    
    public BlockPos hasRedstoneBlock(final BlockPos pos) {
        if (BlockUtil.getBlock(pos.up()) == Blocks.REDSTONE_BLOCK) {
            return pos.up();
        }
        for (final BlockPos offset : this.offsets) {
            for (int i = 0; i < 2; ++i) {
                final BlockPos blockPos = pos.add(offset).down(i);
                final Block block = BlockUtil.getBlock(blockPos);
                if (i == 0) {
                    if (block == Blocks.REDSTONE_TORCH || block == Blocks.REDSTONE_BLOCK) {
                        return blockPos;
                    }
                }
                else if (block == Blocks.REDSTONE_TORCH && !BlockUtil.isBlockUnSolid(blockPos.up())) {
                    return blockPos;
                }
            }
        }
        return null;
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    private double getDistance(final EntityPlayer player, final Entity entity) {
        final double x = player.posX - entity.posX;
        final double z = player.posZ - entity.posZ;
        return Math.sqrt(x * x + z * z);
    }
    
    private boolean canPlace(final BlockPos pos) {
        return BlockUtil.getFirstFacing(pos, this.strict.getValue(), this.needRaytrace(pos)) != null && !this.intersectsWithEntity(pos);
    }
    
    private boolean canPlaceFire(final BlockPos pos) {
        return BlockUtil.getFirstFacing(pos, this.strict.getValue(), this.needRaytrace(pos)) != null;
    }
    
    private Entity getCrystal(final BlockPos pos) {
        for (final Entity entity : PistonAura.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return entity;
            }
        }
        return null;
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : PistonAura.mc.world.loadedEntityList) {
            if (!entity.isDead && !(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityExpBottle)) {
                if (entity instanceof EntityArrow) {
                    continue;
                }
                if (!this.entityCheck.getValue() && entity instanceof EntityEnderCrystal) {
                    continue;
                }
                if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public void add(final List<BlockPos> pistons, final BlockPos pos) {
        pistons.add(pos.add(0, 1, 0));
        pistons.add(pos.add(0, 2, 0));
    }
    
    public static int findHotbarBlock(final Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = PistonAura.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() == blockIn) {
                return i;
            }
        }
        return -1;
    }
    
    private int getItemHotbar() {
        for (int i = 0; i < (this.crystalBypass.getValue() ? 36 : 9); ++i) {
            final Item item = PistonAura.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) == Item.getIdFromItem(Items.END_CRYSTAL)) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean findMaterials() {
        this.pistonSlot = findHotbarBlock(Blocks.PISTON);
        this.obbySlot = findHotbarBlock(Blocks.OBSIDIAN);
        this.crystalSlot = this.getItemHotbar();
        this.flintSlot = (this.fire.getValue() ? BurrowUtil.findHotbarBlock(Items.FLINT_AND_STEEL.getClass()) : -1);
        if (this.pistonSlot == -1) {
            this.pistonSlot = findHotbarBlock(Blocks.STICKY_PISTON);
        }
        if (PistonAura.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            this.crystalSlot = 999;
        }
        final int block = findHotbarBlock(Blocks.REDSTONE_BLOCK);
        final int torch = findHotbarBlock(Blocks.REDSTONE_TORCH);
        if (this.redstone.getValue().equals("Block")) {
            this.redStoneSlot = block;
        }
        if (this.redstone.getValue().equals("Torch")) {
            this.redStoneSlot = torch;
        }
        if (this.redstone.getValue().equals("Both")) {
            if (block != -1) {
                this.redStoneSlot = block;
            }
            else {
                this.redStoneSlot = torch;
            }
        }
        this.useBlock = (this.redStoneSlot == block);
        return this.pistonSlot != -1 && this.crystalSlot != -1 && this.redStoneSlot != -1;
    }
    
    private void reset() {
        for (int i = this.saveArray.length - 1; i > 0; --i) {
            this.saveArray[i] = this.saveArray[i - 1];
        }
        if (this.pistonPos != null) {
            this.saveArray[0] = this.pistonPos;
        }
        for (int i = 0; i < this.saveArray.length; ++i) {
            if (i >= this.maxPos.getValue()) {
                this.saveArray[i] = null;
            }
        }
        if (!this.different.getValue()) {
            this.saveArray = new BlockPos[25];
        }
        PistonAura.target = null;
        this.targetPos = null;
        this.rotation = null;
        this.pistonPos = null;
        this.crystalPos = null;
        this.redStonePos = null;
        this.firePos = null;
        this.pistonSlot = -1;
        this.crystalSlot = -1;
        this.redStoneSlot = -1;
        this.obbySlot = -1;
        this.baseTimer = new Timing();
        this.timer = new Timing();
        this.startBreakTimer = new Timing();
        this.breakTimer = new Timing();
        this.preparedSpace = false;
        this.placedPiston = false;
        this.placedCrystal = false;
        this.placedRedstone = false;
        this.brokeCrystal = false;
        this.boom = false;
    }
    
    public boolean checkPos(final BlockPos pos) {
        final BlockPos myPos = PlayerUtil.getPlayerPos();
        return pos.getX() == myPos.getX() && pos.getZ() == myPos.getZ() && (myPos.getY() == pos.getY() || myPos.getY() + 1 == pos.getY());
    }
    
    public void setItem(final int slot) {
        if (slot == 999) {
            return;
        }
        this.normalSwitch(slot);
    }
    
    private void normalSwitch(final int slot) {
        if (this.packetSwitch.getValue()) {
            PistonAura.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        }
        else {
            PistonAura.mc.player.inventory.currentItem = slot;
            PistonAura.mc.playerController.updateController();
        }
    }
    
    public void restoreItem() {
        if (this.silentSwitch.getValue()) {
            if (this.packetSwitch.getValue()) {
                PistonAura.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldSlot));
            }
            else {
                PistonAura.mc.player.inventory.currentItem = this.oldSlot;
                PistonAura.mc.playerController.updateController();
            }
        }
    }
    
    private boolean placeBlock(final BlockPos pos, final boolean packet) {
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        final EnumFacing side = BlockUtil.getFirstFacing(pos, this.strict.getValue(), this.needRaytrace(pos));
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        boolean sneak = false;
        if (!ColorMain.INSTANCE.sneaking && BlockUtil.blackList.contains(BlockUtil.getBlock(neighbour))) {
            PistonAura.mc.player.connection.sendPacket(new CPacketEntityAction(PistonAura.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            PistonAura.mc.player.setSneaking(true);
            sneak = true;
        }
        if (packet) {
            rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite);
        }
        else {
            PistonAura.mc.playerController.processRightClickBlock(PistonAura.mc.player, PistonAura.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        }
        if (this.swingArm.getValue()) {
            PistonAura.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (this.rotate.getValue() && this.pistonRotate.getValue()) {
            BlockUtil.faceVector(hitVec);
        }
        if (sneak) {
            PistonAura.mc.player.connection.sendPacket(new CPacketEntityAction(PistonAura.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            PistonAura.mc.player.setSneaking(false);
        }
        return true;
    }
    
    public static void rightClickBlock(final BlockPos pos, final Vec3d vec, final EnumHand hand, final EnumFacing direction) {
        final float f = (float)(vec.x - pos.getX());
        final float f2 = (float)(vec.y - pos.getY());
        final float f3 = (float)(vec.z - pos.getZ());
        PistonAura.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
    }
    
    private int fireLevel(final BlockPos pos, final BlockPos targetPos) {
        return (pos.getX() == targetPos.getX() || pos.getZ() == targetPos.getZ()) ? 1 : 0;
    }
    
    private int blockLevel(final BlockPos pos) {
        return pos.getY() * 10000;
    }
    
    private boolean needRaytrace(final BlockPos pos) {
        return PistonAura.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > this.forceRange.getValue() && this.raytrace.getValue();
    }
    
    public boolean canPistonCrystal(final BlockPos crystalPos, final BlockPos targetPos) {
        final BlockPos offset = new BlockPos(targetPos.x - crystalPos.x, crystalPos.y, targetPos.z - crystalPos.z);
        final BlockPos normal = crystalPos.add(offset);
        final BlockPos oneBlock = normal.add(offset);
        final BlockPos side0 = normal.add(offset.getZ(), 0, offset.getX());
        final BlockPos side2 = normal.add(offset.getZ() * -1, 0, offset.getX() * -1);
        final BlockPos side3 = oneBlock.add(offset.getZ(), 0, offset.getX());
        final BlockPos side4 = oneBlock.add(offset.getZ() * -1, 0, offset.getX() * -1);
        final BlockPos side5 = crystalPos.add(offset.getZ(), 0, offset.getX());
        final BlockPos side6 = crystalPos.add(offset.getZ() * -1, 0, offset.getX() * -1);
        final List<BlockPos> pistons = new ArrayList<BlockPos>();
        this.add(pistons, normal);
        this.add(pistons, oneBlock);
        this.add(pistons, side0);
        this.add(pistons, side2);
        this.add(pistons, side3);
        this.add(pistons, side4);
        this.add(pistons, side5);
        this.add(pistons, side6);
        pistons.removeIf(p -> {
            if (!this.different.getValue()) {
                return false;
            }
            else {
                boolean same = false;
                final BlockPos[] saveArray = this.saveArray;
                final int length = saveArray.length;
                int i = 0;
                while (i < length) {
                    final BlockPos savePos = saveArray[i];
                    if (this.isPos2(savePos, p)) {
                        same = true;
                        break;
                    }
                    else {
                        ++i;
                    }
                }
                return same;
            }
        });
        if (pistons.isEmpty()) {
            return false;
        }
        final List<BlockPos> pistonList = pistons.stream().filter(pistonPos -> {
            final BlockPos headPos = pistonPos.add(offset.getX() * -1, 0, offset.getZ() * -1);
            final Block headBlock = BlockUtil.getBlock(headPos);
            if (headBlock == Blocks.BEDROCK || headBlock == Blocks.OBSIDIAN || headBlock == Blocks.ENDER_CHEST || headBlock == Blocks.PISTON_HEAD || this.checkPos(headPos)) {
                return false;
            }
            else {
                final boolean isPiston = BlockUtil.getBlock(pistonPos) instanceof BlockPistonBase;
                if (!isPiston) {
                    if (!this.canPlace(pistonPos)) {
                        return false;
                    }
                    else if (PistonAura.mc.player.getDistance(pistonPos.getX() + 0.5, pistonPos.getY() + 0.5, pistonPos.getZ() + 0.5) > this.range.getValue()) {
                        return false;
                    }
                    else {
                        final double feetY = PistonAura.mc.player.posY;
                        if ((PlayerUtil.getDistanceI(pistonPos) < 0.8 + pistonPos.getY() - feetY && pistonPos.getY() > feetY + 1.0) || (PlayerUtil.getDistanceI(pistonPos) < 1.8 + feetY - pistonPos.getY() && pistonPos.getY() < feetY)) {
                            return false;
                        }
                    }
                }
                else if (this.pistonCheck.getValue() && (this.hasRedstone(pistonPos) || !this.isFacing(pistonPos, headPos))) {
                    return false;
                }
                final BlockPos redstonePos = this.getRedStonePos(crystalPos, pistonPos, offset);
                if (redstonePos == null) {
                    return false;
                }
                else if (this.flintSlot != -1 && this.getFirePos(targetPos, crystalPos, pistonPos, redstonePos, offset) == null) {
                    return false;
                }
                else {
                    final boolean b = false;
                    if (!isPiston) {
                        if (!BlockUtil.canPlaceWithoutBase(pistonPos, this.strict.getValue(), this.needRaytrace(pistonPos), this.canPlace(redstonePos) || (this.obbySlot != -1 && (this.base.getValue() || this.canPlace(redstonePos.down()))))) {
                            return b;
                        }
                    }
                    return b;
                }
            }
        }).collect(Collectors.toList());
        return !pistonList.isEmpty();
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (PistonAura.mc.world == null || PistonAura.mc.player == null) {
            return;
        }
        if (this.render.getValue()) {
            if (this.firePos != null && this.fireRender.getValue()) {
                this.drawBoxMain(this.firePos.x, this.firePos.y, this.firePos.z, 255, 160, 0);
            }
            if (this.pistonPos != null && this.crystalPos != null && this.redStonePos != null) {
                this.drawBoxMain(this.pistonPos.x, this.pistonPos.y, this.pistonPos.z, 255, 255, 150);
                this.drawBoxMain(this.crystalPos.x, this.crystalPos.y, this.crystalPos.z, 255, 255, 255);
                this.drawBoxMain(this.redStonePos.x, this.redStonePos.y, this.redStonePos.z, 225, 50, 50);
            }
        }
    }
    
    void drawBoxMain(final double x, final double y, final double z, final int r, final int g, final int b) {
        final AxisAlignedBB box = this.getBox(x, y, z);
        if (this.box.getValue()) {
            RenderUtil.drawBox(box, false, 1.0, new GSColor(r, g, b, 25), 63);
        }
        if (this.outline.getValue()) {
            RenderUtil.drawBoundingBox(box, 1.0, new GSColor(r, g, b, 255));
        }
    }
    
    AxisAlignedBB getBox(final double x, final double y, final double z) {
        final double maxX = x + 1.0;
        final double maxZ = z + 1.0;
        return new AxisAlignedBB(x, y, z, maxX, y + 1.0, maxZ);
    }
    
    @Override
    public String getHudInfo() {
        return (this.hud.getValue() && PistonAura.target != null) ? ("[" + ChatFormatting.WHITE + PistonAura.target.getName() + ChatFormatting.GRAY + "]") : "";
    }
    
    static {
        PistonAura.target = null;
    }
    
    public class PistonAuraPos
    {
        public BlockPos targetPos;
        public BlockPos crystal;
        public BlockPos piston;
        public BlockPos redstone;
        public BlockPos fire;
        public BlockPos offset;
        EntityPlayer target;
        boolean block;
        
        public PistonAuraPos(final BlockPos crystal, final BlockPos piston, final BlockPos redstone, final BlockPos offset, final EntityPlayer target, final BlockPos targetPos, final boolean block) {
            this.crystal = crystal;
            this.piston = piston;
            this.redstone = redstone;
            this.offset = offset;
            this.targetPos = targetPos;
            this.target = target;
            this.block = block;
            this.fire = PistonAura.this.getFirePos(targetPos, crystal, piston, redstone, offset);
        }
        
        public double range() {
            final double crystalRange = PlayerUtil.getDistanceL(this.crystal);
            final double pistonRange = PlayerUtil.getDistanceL(this.piston);
            return Math.max(pistonRange, crystalRange);
        }
    }
}
