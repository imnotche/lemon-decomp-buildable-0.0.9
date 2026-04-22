// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.entity.item.EntityEnderCrystal;
import com.lemonclient.api.event.events.RenderEvent;
import net.minecraft.block.BlockWeb;
import net.minecraft.util.EnumHand;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.util.math.Vec3i;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import com.lemonclient.api.util.misc.MathUtil;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Comparator;
import com.lemonclient.api.util.misc.CrystalUtil;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.player.PredictUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.Collection;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.api.util.world.EntityUtil;
import java.util.Iterator;
import net.minecraft.util.EnumFacing;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import java.util.ArrayList;
import com.lemonclient.api.util.render.GSColor;
import java.util.Arrays;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.misc.Timing;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "HoleFill", category = Category.Combat, priority = 999)
public class HoleFill extends Module
{
    BooleanSetting test;
    ModeSetting page;
    IntegerSetting maxTarget;
    IntegerSetting tickAdd;
    IntegerSetting maxTick;
    BooleanSetting calculateYPredict;
    IntegerSetting startDecrease;
    IntegerSetting exponentStartDecrease;
    IntegerSetting decreaseY;
    IntegerSetting exponentDecreaseY;
    BooleanSetting splitXZ;
    BooleanSetting manualOutHole;
    BooleanSetting aboveHoleManual;
    BooleanSetting stairPredict;
    IntegerSetting nStair;
    DoubleSetting speedActivationStair;
    IntegerSetting delay;
    BooleanSetting upPlate;
    BooleanSetting selfFill;
    BooleanSetting mine;
    BooleanSetting selfTrap;
    BooleanSetting yCheck;
    BooleanSetting web;
    BooleanSetting above;
    BooleanSetting raytraceCheck;
    BooleanSetting holeCheck;
    IntegerSetting placeDelay;
    IntegerSetting bpc;
    DoubleSetting range;
    DoubleSetting yRange;
    DoubleSetting fillRange;
    DoubleSetting fillYRange;
    DoubleSetting safety;
    BooleanSetting rotate;
    BooleanSetting strict;
    BooleanSetting raytrace;
    BooleanSetting onGround;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BooleanSetting render;
    BooleanSetting box;
    BooleanSetting outline;
    IntegerSetting width;
    ColorSetting color;
    IntegerSetting alpha;
    IntegerSetting outAlpha;
    BooleanSetting animate;
    IntegerSetting time;
    BooleanSetting hObby;
    BooleanSetting hEChest;
    BooleanSetting hWeb;
    BooleanSetting hSlab;
    BooleanSetting hSkull;
    BooleanSetting hTrap;
    BooleanSetting sObby;
    BooleanSetting sEChest;
    BooleanSetting sWeb;
    BooleanSetting sSlab;
    BooleanSetting sSkull;
    BooleanSetting sTrap;
    ModeSetting jumpMode;
    ModeSetting rubberBand;
    managerClassRenderBlocks managerRenderBlocks;
    List<BlockPos> posList;
    Timing timer;
    Timing placeTimer;
    boolean trapdoor;
    boolean mined;
    boolean self;
    boolean placedSelf;
    int placed;
    int slot;
    BlockPos[] sides;
    Vec3d[] add;
    
    public HoleFill() {
        this.test = this.registerBoolean("Test", false);
        this.page = this.registerMode("Page", Arrays.asList("Target", "Place", "HoleFill", "SelfFill", "Render"), "Target");
        this.maxTarget = this.registerInteger("Max Target", 10, 1, 50, () -> this.page.getValue().equals("Target"));
        this.tickAdd = this.registerInteger("Tick Add", 8, 1, 30, () -> this.page.getValue().equals("Target"));
        this.maxTick = this.registerInteger("Max Tick", 8, 0, 30, () -> this.page.getValue().equals("Target"));
        this.calculateYPredict = this.registerBoolean("Calculate Y Predict", true, () -> this.page.getValue().equals("Target"));
        this.startDecrease = this.registerInteger("Start Decrease", 39, 0, 200, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Target"));
        this.exponentStartDecrease = this.registerInteger("Exponent Start", 2, 1, 5, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Target"));
        this.decreaseY = this.registerInteger("Decrease Y", 2, 1, 5, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Target"));
        this.exponentDecreaseY = this.registerInteger("Exponent Decrease Y", 1, 1, 3, () -> this.calculateYPredict.getValue() && this.page.getValue().equals("Target"));
        this.splitXZ = this.registerBoolean("Split XZ", true, () -> this.page.getValue().equals("Target"));
        this.manualOutHole = this.registerBoolean("Manual Out Hole", false, () -> this.page.getValue().equals("Target"));
        this.aboveHoleManual = this.registerBoolean("Above Hole Manual", false, () -> this.manualOutHole.getValue() && this.page.getValue().equals("Target"));
        this.stairPredict = this.registerBoolean("Stair Predict", false, () -> this.page.getValue().equals("Target"));
        this.nStair = this.registerInteger("N Stair", 2, 1, 4, () -> this.stairPredict.getValue() && this.page.getValue().equals("Target"));
        this.speedActivationStair = this.registerDouble("Speed Activation Stair", 0.3, 0.0, 1.0, () -> this.stairPredict.getValue() && this.page.getValue().equals("Target"));
        this.delay = this.registerInteger("Calc Delay", 0, 0, 1000, () -> this.page.getValue().equals("Place"));
        this.upPlate = this.registerBoolean("Up Slab", true, () -> this.page.getValue().equals("Place"));
        this.selfFill = this.registerBoolean("Self Fill", true, () -> this.page.getValue().equals("Place"));
        this.mine = this.registerBoolean("Mine SelfFill", true, () -> this.page.getValue().equals("Place") && this.selfFill.getValue());
        this.selfTrap = this.registerBoolean("Self Trap", true, () -> this.page.getValue().equals("Place"));
        this.yCheck = this.registerBoolean("Y Check", true, () -> this.page.getValue().equals("Place"));
        this.web = this.registerBoolean("Web", true, () -> this.page.getValue().equals("Place") && this.yCheck.getValue());
        this.above = this.registerBoolean("Above", true, () -> this.page.getValue().equals("Place") && this.yCheck.getValue());
        this.raytraceCheck = this.registerBoolean("Raytrace Check", true, () -> this.page.getValue().equals("Place"));
        this.holeCheck = this.registerBoolean("InHole Check", true, () -> this.page.getValue().equals("Place"));
        this.placeDelay = this.registerInteger("Place Delay", 50, 0, 1000, () -> this.page.getValue().equals("Place"));
        this.bpc = this.registerInteger("Block pre Tick", 6, 1, 20, () -> this.page.getValue().equals("Place"));
        this.range = this.registerDouble("Range", 6.0, 0.0, 10.0, () -> this.page.getValue().equals("Place"));
        this.yRange = this.registerDouble("Y Range", 2.5, 0.0, 6.0, () -> this.page.getValue().equals("Place"));
        this.fillRange = this.registerDouble("Fill Range", 3.0, 0.0, 6.0, () -> this.page.getValue().equals("Place"));
        this.fillYRange = this.registerDouble("Fill YRange", 3.0, 0.0, 10.0, () -> this.page.getValue().equals("Place"));
        this.safety = this.registerDouble("Safety Range", 3.0, 0.0, 6.0, () -> this.page.getValue().equals("Place"));
        this.rotate = this.registerBoolean("Rotate", false, () -> this.page.getValue().equals("Place"));
        this.strict = this.registerBoolean("Strict", false, () -> this.page.getValue().equals("Place"));
        this.raytrace = this.registerBoolean("RayTrace", false, () -> this.page.getValue().equals("Place"));
        this.onGround = this.registerBoolean("OnGround", false, () -> this.page.getValue().equals("Place"));
        this.packet = this.registerBoolean("Packet Place", false, () -> this.page.getValue().equals("Place"));
        this.swing = this.registerBoolean("Swing", false, () -> this.page.getValue().equals("Place"));
        this.packetSwitch = this.registerBoolean("Packet Switch", true, () -> this.page.getValue().equals("Place"));
        this.render = this.registerBoolean("Render", false, () -> this.page.getValue().equals("Render"));
        this.box = this.registerBoolean("Box", true, () -> this.page.getValue().equals("Render") && this.render.getValue());
        this.outline = this.registerBoolean("Outline", true, () -> this.page.getValue().equals("Render") && this.render.getValue());
        this.width = this.registerInteger("Width", 1, 1, 5, () -> this.page.getValue().equals("Render") && this.render.getValue() && this.outline.getValue());
        this.color = this.registerColor("Color", new GSColor(255, 0, 0), () -> this.page.getValue().equals("Render") && this.render.getValue());
        this.alpha = this.registerInteger("Alpha", 75, 0, 255, () -> this.page.getValue().equals("Render") && this.render.getValue() && this.box.getValue());
        this.outAlpha = this.registerInteger("Outline Alpha", 125, 0, 255, () -> this.page.getValue().equals("Render") && this.render.getValue() && this.outline.getValue());
        this.animate = this.registerBoolean("Animate", true, () -> this.page.getValue().equals("Render") && this.render.getValue());
        this.time = this.registerInteger("Life Time", 500, 0, 1000, () -> this.page.getValue().equals("Render") && this.render.getValue());
        this.hObby = this.registerBoolean("H-Obby", true, () -> this.page.getValue().equals("HoleFill"));
        this.hEChest = this.registerBoolean("H-EChest", true, () -> this.page.getValue().equals("HoleFill"));
        this.hWeb = this.registerBoolean("H-Web", true, () -> this.page.getValue().equals("HoleFill"));
        this.hSlab = this.registerBoolean("H-Slab", true, () -> this.page.getValue().equals("HoleFill"));
        this.hSkull = this.registerBoolean("H-Skull", true, () -> this.page.getValue().equals("HoleFill"));
        this.hTrap = this.registerBoolean("H-Trapdoor", true, () -> this.page.getValue().equals("HoleFill"));
        this.sObby = this.registerBoolean("S-Obby", true, () -> this.page.getValue().equals("SelfFill"));
        this.sEChest = this.registerBoolean("S-EChest", true, () -> this.page.getValue().equals("SelfFill"));
        this.sWeb = this.registerBoolean("S-Web", true, () -> this.page.getValue().equals("SelfFill"));
        this.sSlab = this.registerBoolean("S-Slab", true, () -> this.page.getValue().equals("SelfFill"));
        this.sSkull = this.registerBoolean("S-Skull", true, () -> this.page.getValue().equals("SelfFill"));
        this.sTrap = this.registerBoolean("S-Trapdoor", true, () -> this.page.getValue().equals("SelfFill"));
        this.jumpMode = this.registerMode("JumpMode", Arrays.asList("Normal", "Future", "Strict"), "Normal", () -> this.page.getValue().equals("SelfFill"));
        this.rubberBand = this.registerMode("RubberBand", Arrays.asList("Cn", "Strict", "Future", "FutureStrict", "Troll", "Void", "Auto", "Test", "Custom"), "Cn", () -> this.page.getValue().equals("SelfFill"));
        this.managerRenderBlocks = new managerClassRenderBlocks();
        this.posList = new ArrayList<BlockPos>();
        this.timer = new Timing();
        this.placeTimer = new Timing();
        this.sides = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
        this.add = new Vec3d[] { new Vec3d(0.1, 0.0, 0.1), new Vec3d(-0.1, 0.0, 0.1), new Vec3d(-0.1, 0.0, -0.1), new Vec3d(0.1, 0.0, -0.1) };
    }
    
    @Override
    public void onTick() {
        if (HoleFill.mc.world == null || HoleFill.mc.player == null) {
            return;
        }
        if (this.timer.passedMs(this.delay.getValue())) {
            this.posList = this.calc();
            this.timer.reset();
        }
    }
    
    @Override
    public void fast() {
        if (HoleFill.mc.world == null || HoleFill.mc.player == null || (!HoleFill.mc.player.onGround && this.onGround.getValue())) {
            return;
        }
        if (this.placeTimer.passedMs(this.placeDelay.getValue()) && !this.posList.isEmpty()) {
            InventoryUtil.run(this.slot = this.findRightBlock(false), this.packetSwitch.getValue(), () -> {
                for (final BlockPos pos : this.posList) {
                    if (this.placed >= this.bpc.getValue()) {
                        break;
                    }
                    else {
                        this.placeBlock(pos);
                    }
                }
            });
            this.placeTimer.reset();
        }
        if (this.mine.getValue() && !this.self && this.placedSelf) {
            final boolean air = BlockUtil.isAir(PlayerUtil.getPlayerPos());
            if (this.mined) {
                if (air) {
                    if (ModuleManager.isModuleEnabled(PacketMine.class)) {
                        PacketMine.INSTANCE.lastBlock = null;
                    }
                    this.mined = false;
                    this.placedSelf = false;
                }
            }
            else if (!air) {
                HoleFill.mc.playerController.onPlayerDamageBlock(PlayerUtil.getPlayerPos(), EnumFacing.UP);
                this.mined = true;
            }
        }
    }
    
    private List<BlockPos> calc() {
        if (HoleFill.mc.world == null || HoleFill.mc.player == null || HoleFill.mc.player.isDead) {
            return new ArrayList<BlockPos>();
        }
        this.placed = 0;
        final List<BlockPos> check = new ArrayList<BlockPos>();
        final List<HoleInfo> holeList = new ArrayList<HoleInfo>();
        final Iterator<BlockPos> iterator = EntityUtil.getSphere(PlayerUtil.getEyesPos(), this.range.getValue() + 1.0, this.yRange.getValue() + 1.0, false, false, 0).iterator();
        BlockPos pos = null;
        List<BlockPos> holePos = null;
        while (iterator.hasNext()) {
            pos = iterator.next();
            if (check.contains(pos)) {
                continue;
            }
            if (!BlockUtil.canReplace(pos) || DamageUtil.isResistantMine(pos.up())) {
                continue;
            }
            if (DamageUtil.isResistantMine(pos.up(2))) {
                continue;
            }
            final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, false, true, false);
            final HoleUtil.HoleType holeType = holeInfo.getType();
            if (holeType == HoleUtil.HoleType.NONE) {
                continue;
            }
            final AxisAlignedBB box = holeInfo.getCentre();
            final Vec3d center = box.getCenter();
            holePos = new ArrayList<BlockPos>();
            for (final Vec3d add : this.add) {
                final BlockPos hole = new BlockPos(center.x + add.x, center.y, center.z + add.z);
                if (!holePos.contains(hole)) {
                    holePos.add(hole);
                }
            }
            check.addAll(holePos);
            boolean recall = false;
            for (final BlockPos block : holePos) {
                final boolean selfFilling = this.isPlayer(block);
                if (selfFilling) {
                    if (this.selfTrap.getValue()) {
                        break;
                    }
                    if (!this.selfFill.getValue() || this.findRightBlock(true) == -1) {
                        recall = true;
                        break;
                    }
                }
                if (ColorMain.INSTANCE.breakList.contains(block)) {
                    recall = true;
                    break;
                }
            }
            if (recall) {
                continue;
            }
            holeList.add(new HoleInfo(holePos, box));
        }
        final List<BlockPos> holePos2 = new ArrayList<BlockPos>();
        EntityPlayer player = null;
        final List<EntityPlayer> targets = PlayerUtil.getNearPlayers(this.range.getValue() + this.fillRange.getValue(), this.maxTarget.getMax()).stream().filter(targetPlayer -> !this.holeCheck.getValue() || !HoleUtil.isInHole(targetPlayer, false, false, false)).collect(Collectors.toList());
        if (this.test.getValue()) {
            targets.add(HoleFill.mc.player);
        }
        final List<EntityPlayer> listPlayer = new ArrayList<EntityPlayer>();
        final Iterator<EntityPlayer> iterator3 = targets.iterator();
        while (iterator3.hasNext()) {
            player = iterator3.next();
            for (int tick = 0; tick <= this.maxTick.getValue() + this.tickAdd.getValue(); tick += this.tickAdd.getValue()) {
                if (tick >= this.maxTick.getValue()) {
                    tick = this.maxTick.getValue();
                }
                listPlayer.add(PredictUtil.predictPlayer(player, new PredictUtil.PredictSettings(tick, this.calculateYPredict.getValue(), this.startDecrease.getValue(), this.exponentStartDecrease.getValue(), this.decreaseY.getValue(), this.exponentDecreaseY.getValue(), this.splitXZ.getValue(), this.manualOutHole.getValue(), this.aboveHoleManual.getValue(), this.stairPredict.getValue(), this.nStair.getValue(), this.speedActivationStair.getValue())));
                if (tick == this.maxTick.getValue()) {
                    break;
                }
            }
        }
        boolean fill = false;
        final AxisAlignedBB selfBox = HoleFill.mc.player.getEntityBoundingBox();
        for (final HoleInfo hole2 : holeList) {
            for (final EntityPlayer target : listPlayer) {
                final AxisAlignedBB targetBox = target.boundingBox;
                if (!targetBox.intersects(hole2.checkBox)) {
                    continue;
                }
                if (hole2.box.intersects(targetBox)) {
                    break;
                }
                final double y = hole2.box.minY + 1.0;
                if (this.yCheck.getValue() && (int)(target.posY + 0.5) != y) {
                    if (target.posY < y) {
                        if (this.web.getValue() && target.isInWeb) {
                            continue;
                        }
                        boolean cancel = false;
                        for (int value = (int)y - 1 - (int)target.posY; value > 0; --value) {
                            boolean recall2 = false;
                            for (final BlockPos blockPos : hole2.posList) {
                                final BlockPos pos2 = blockPos.down(value);
                                if (DamageUtil.isResistantMine(pos2)) {
                                    cancel = true;
                                    recall2 = true;
                                    break;
                                }
                            }
                            if (recall2) {
                                break;
                            }
                        }
                        if (cancel) {
                            continue;
                        }
                    }
                    else if (this.above.getValue()) {
                        boolean cancel = false;
                        for (int value = (int)target.posY - (int)y; value > 0; --value) {
                            boolean recall2 = false;
                            for (final BlockPos blockPos : hole2.posList) {
                                final BlockPos pos2 = blockPos.up(value);
                                if (DamageUtil.isResistantMine(pos2)) {
                                    cancel = true;
                                    recall2 = true;
                                    break;
                                }
                            }
                            if (recall2) {
                                break;
                            }
                        }
                        if (cancel) {
                            continue;
                        }
                    }
                }
                if (this.raytraceCheck.getValue() && !CrystalUtil.calculateRaytrace(target, hole2.box.getCenter())) {
                    continue;
                }
                if (!fill && selfBox.intersects(hole2.box)) {
                    fill = true;
                }
                holePos2.addAll(hole2.posList);
                break;
            }
        }
        this.self = fill;
        final boolean inHole = HoleUtil.isInHole(HoleFill.mc.player, false, true, false);
        holePos2.sort(Comparator.comparing(p -> p.y));
        List<BlockPos> finalHolePos = holePos;
        holePos2.removeIf(candidatePos -> {
            if (!this.checkPlaceRange(candidatePos) || DamageUtil.isResistantMine(candidatePos.up()) || DamageUtil.isResistantMine(candidatePos.up(2))) {
                return true;
            }
            else {
                if (!inHole) {
                    if (MathUtil.isIntersect(selfBox.grow(this.safety.getValue()), new AxisAlignedBB(candidatePos))) {
                        return true;
                    }
                }
                return finalHolePos.contains(candidatePos.up());
            }
        });
        return holePos2;
    }
    
    private boolean checkPlaceRange(final BlockPos pos) {
        final BlockPos playerPos = new BlockPos(Math.floor(HoleFill.mc.player.posX), Math.floor(HoleFill.mc.player.posY), Math.floor(HoleFill.mc.player.posZ));
        final double x = playerPos.x - (pos.x + 0.5);
        final double y = playerPos.y - (pos.y + 0.5);
        final double z = playerPos.z - (pos.z + 0.5);
        return x * x <= this.range.getValue() * this.range.getValue() && y * y <= this.yRange.getValue() * this.yRange.getValue() && z * z <= this.range.getValue() * this.range.getValue();
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : HoleFill.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                if (entity instanceof EntityExpBottle) {
                    continue;
                }
                if (MathUtil.isIntersect(new AxisAlignedBB(pos), entity.getEntityBoundingBox())) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private boolean isPlayer(final BlockPos pos) {
        for (final EntityPlayer entity : HoleFill.mc.world.playerEntities) {
            if (entity != HoleFill.mc.player) {
                continue;
            }
            if (MathUtil.isIntersect(new AxisAlignedBB(pos), entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private void placeBlock(final BlockPos pos) {
        if (pos == null) {
            return;
        }
        final boolean selfFilling = this.isPlayer(pos);
        if (selfFilling && this.selfTrap.getValue()) {
            final int obby = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (obby != -1) {
                InventoryUtil.run(obby, this.packetSwitch.getValue(), () -> {
                    final BlockPos ori = pos.up();
                    if (BurrowUtil.getFirstFacing(pos.up(2)) == null) {
                        BlockPos e = null;
                        boolean isNull = true;
                        final BlockPos[] sides = this.sides;
                        final int length = sides.length;
                        int i = 0;
                        while (i < length) {
                            final BlockPos side2 = sides[i];
                            final BlockPos added = ori.up().add(side2);
                            if (!this.intersectsWithEntity(added) && BurrowUtil.getFirstFacing(added) != null) {
                                e = added;
                                isNull = false;
                                break;
                            }
                            else {
                                ++i;
                            }
                        }
                        if (isNull) {
                            final BlockPos[] sides2 = this.sides;
                            final int length2 = sides2.length;
                            int j = 0;
                            while (j < length2) {
                                final BlockPos side3 = sides2[j];
                                final BlockPos added2 = ori.add(side3);
                                if (!this.intersectsWithEntity(added2) && !this.intersectsWithEntity(added2.up())) {
                                    this.placeTrapBlock(added2);
                                    e = added2.up();
                                    break;
                                }
                                else {
                                    ++j;
                                }
                            }
                        }
                        this.placeTrapBlock(e);
                    }
                    this.placeTrapBlock(pos.up(2));
                });
                return;
            }
        }
        int fillSlot = -1;
        if (selfFilling) {
            if (!this.selfFill.getValue()) {
                return;
            }
            fillSlot = this.findRightBlock(true);
            if (fillSlot == -1) {
                return;
            }
        }
        else if (this.intersectsWithEntity(pos)) {
            return;
        }
        this.trapdoor = (fillSlot == InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8) || (this.upPlate.getValue() && fillSlot == BurrowUtil.findHotbarBlock(BlockSlab.class)));
        final boolean jump = fillSlot == BurrowUtil.findHotbarBlock(BlockEnderChest.class) || fillSlot == BurrowUtil.findHotbarBlock(BlockObsidian.class);
        final EnumFacing side = this.trapdoor ? BurrowUtil.getTrapdoorFacing(pos) : BlockUtil.getFirstFacing(pos, this.strict.getValue(), this.raytrace.getValue());
        if (side == null) {
            return;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, this.trapdoor ? 0.8 : 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if ((BlockUtil.blackList.contains(HoleFill.mc.world.getBlockState(neighbour).getBlock()) || BlockUtil.shulkerList.contains(HoleFill.mc.world.getBlockState(neighbour).getBlock())) && !HoleFill.mc.player.isSneaking()) {
            HoleFill.mc.player.connection.sendPacket(new CPacketEntityAction(HoleFill.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            HoleFill.mc.player.setSneaking(true);
        }
        if (selfFilling) {
            this.placedSelf = true;
            if (this.trapdoor) {
                final double x = HoleFill.mc.player.posX;
                final double y = (int)HoleFill.mc.player.posY;
                final double z = HoleFill.mc.player.posZ;
                if (fillSlot == InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8)) {
                    HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + 0.20000000298023224, z, HoleFill.mc.player.onGround));
                }
                else {
                    this.jump();
                }
                HoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(fillSlot));
                BurrowUtil.rightClickBlock(neighbour, opposite, new Vec3d(0.5, 0.8, 0.5), true, this.swing.getValue());
                if (fillSlot == InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8)) {
                    HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, HoleFill.mc.player.onGround));
                }
                else {
                    this.rubberBand();
                }
                HoleFill.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.slot));
                return;
            }
            if (jump) {
                this.jump();
            }
        }
        if (this.rotate.getValue()) {
            BurrowUtil.faceVector(hitVec, true);
        }
        InventoryUtil.run(jump ? fillSlot : this.slot, this.packetSwitch.getValue(), () -> BurrowUtil.rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite, this.packet.getValue(), this.swing.getValue()));
        if (selfFilling) {
            this.rubberBand();
        }
        this.managerRenderBlocks.addRender(pos);
        ++this.placed;
    }
    
    public static BlockPos getFlooredPosition(final Entity entity) {
        return new BlockPos(Math.floor(entity.posX), (double)Math.round(entity.posY), Math.floor(entity.posZ));
    }
    
    private void placeTrapBlock(final BlockPos pos) {
        if (ColorMain.INSTANCE.breakList.contains(pos)) {
            return;
        }
        BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
    }
    
    private int findRightBlock(final boolean selfFill) {
        int slot = -1;
        if (selfFill) {
            if (this.sTrap.getValue()) {
                slot = InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8);
            }
            if (this.sSkull.getValue() && slot == -1) {
                slot = InventoryUtil.findSkullSlot();
            }
            if (this.sWeb.getValue() && slot == -1) {
                slot = InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
            }
            if (this.sSlab.getValue() && slot == -1) {
                slot = BurrowUtil.findHotbarBlock(BlockSlab.class);
            }
            if (this.sEChest.getValue() && slot == -1) {
                slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
            }
            if (this.sObby.getValue() && slot == -1) {
                slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            }
        }
        else {
            if (this.hObby.getValue()) {
                slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            }
            if (this.hEChest.getValue() && slot == -1) {
                slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
            }
            if (this.hSlab.getValue() && slot == -1) {
                slot = BurrowUtil.findHotbarBlock(BlockSlab.class);
            }
            if (this.hWeb.getValue() && slot == -1) {
                slot = InventoryUtil.findFirstBlockSlot(BlockWeb.class, 0, 8);
            }
            if (this.hSkull.getValue() && slot == -1) {
                slot = InventoryUtil.findSkullSlot();
            }
            if (this.hTrap.getValue()) {
                slot = InventoryUtil.findFirstBlockSlot(BlockTrapDoor.class, 0, 8);
            }
        }
        return slot;
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        this.managerRenderBlocks.update(this.time.getValue());
        this.managerRenderBlocks.render();
    }
    
    boolean sameBlockPos(final BlockPos first, final BlockPos second) {
        return first != null && second != null && first.getX() == second.getX() && first.getY() == second.getY() && first.getZ() == second.getZ();
    }
    
    public static void back() {
        for (final Entity crystal : HoleFill.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> HoleFill.mc.player.getDistance(e))).collect(Collectors.toList())) {
            if (crystal instanceof EntityEnderCrystal) {
                HoleFill.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                HoleFill.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
            }
        }
    }
    
    private boolean canGoTo(final BlockPos pos) {
        return isAir(pos) && isAir(pos.up());
    }
    
    public static boolean isAir(final Vec3d vec3d) {
        return isAir(new BlockPos(vec3d));
    }
    
    public static boolean isAir(final BlockPos pos) {
        return BlockUtil.canReplace(pos);
    }
    
    public static Vec3d getEyesPos() {
        return new Vec3d(HoleFill.mc.player.posX, HoleFill.mc.player.posY + HoleFill.mc.player.getEyeHeight(), HoleFill.mc.player.posZ);
    }
    
    private void jump() {
        final String s = this.jumpMode.getValue();
        switch (s) {
            case "Normal": {
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.419999986886978, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.7531999805212015, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.001335979112147, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.166109260938214, HoleFill.mc.player.posZ, false));
                break;
            }
            case "Future": {
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.419997486886978, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.7500025, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.999995, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.170005001788139, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.2426050013947485, HoleFill.mc.player.posZ, false));
                break;
            }
            case "Strict": {
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.419998586886978, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.7500014, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.9999972, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.170002801788139, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.170009801788139, HoleFill.mc.player.posZ, false));
                break;
            }
        }
    }
    
    private void rubberBand() {
        final String s = this.rubberBand.getValue();
        switch (s) {
            case "Cn": {
                double distance = 0.0;
                BlockPos bestPos = null;
                for (final BlockPos pos : BlockUtil.getBox(6.0f)) {
                    if (this.canGoTo(pos)) {
                        if (HoleFill.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 3.0) {
                            continue;
                        }
                        if (bestPos != null && HoleFill.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) >= distance) {
                            continue;
                        }
                        bestPos = pos;
                        distance = HoleFill.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    }
                }
                if (bestPos != null) {
                    HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(bestPos.getX() + 0.5, bestPos.getY(), bestPos.getZ() + 0.5, false));
                    break;
                }
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, -7.0, HoleFill.mc.player.posZ, false));
                break;
            }
            case "Future": {
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.242609801394749, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 2.340028003576279, HoleFill.mc.player.posZ, false));
                break;
            }
            case "FutureStrict": {
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.315205001001358, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 1.315205001001358, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 2.485225002789497, HoleFill.mc.player.posZ, false));
                break;
            }
            case "Troll": {
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 3.3400880035762786, HoleFill.mc.player.posZ, false));
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, HoleFill.mc.player.posY - 1.0, HoleFill.mc.player.posZ, false));
                break;
            }
            case "Strict": {
                double distance = 0.0;
                BlockPos bestPos = null;
                for (int i = 0; i < 20; ++i) {
                    final BlockPos pos = new BlockPos(HoleFill.mc.player.posX, HoleFill.mc.player.posY + 0.5 + i, HoleFill.mc.player.posZ);
                    if (this.canGoTo(pos) && HoleFill.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 5.0 && (bestPos == null || HoleFill.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < distance)) {
                        bestPos = pos;
                        distance = HoleFill.mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    }
                }
                if (bestPos != null) {
                    HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(bestPos.getX() + 0.5, bestPos.getY(), bestPos.getZ() + 0.5, false));
                    break;
                }
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, -7.0, HoleFill.mc.player.posZ, false));
                break;
            }
            case "Void": {
                HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleFill.mc.player.posX, -7.0, HoleFill.mc.player.posZ, false));
                break;
            }
            case "Auto": {
                for (int j = -10; j < 10; ++j) {
                    if (j == -1) {
                        j = 4;
                    }
                    if (HoleFill.mc.world.getBlockState(getFlooredPosition(HoleFill.mc.player).add(0, j, 0)).getBlock().equals(Blocks.AIR) && HoleFill.mc.world.getBlockState(getFlooredPosition(HoleFill.mc.player).add(0, j + 1, 0)).getBlock().equals(Blocks.AIR)) {
                        final BlockPos pos2 = getFlooredPosition(HoleFill.mc.player).add(0, j, 0);
                        HoleFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(pos2.getX() + 0.3, pos2.getY(), pos2.getZ() + 0.3, false));
                        break;
                    }
                }
                break;
            }
        }
    }
    
    class managerClassRenderBlocks
    {
        ArrayList<renderBlock> blocks;
        
        managerClassRenderBlocks() {
            this.blocks = new ArrayList<renderBlock>();
        }
        
        void update(final int time) {
            this.blocks.removeIf(e -> System.currentTimeMillis() - e.start > time);
        }
        
        void render() {
            this.blocks.forEach(renderBlock::render);
        }
        
        void addRender(final BlockPos pos) {
            boolean render = true;
            for (final renderBlock block : this.blocks) {
                if (HoleFill.this.sameBlockPos(block.pos, pos)) {
                    render = false;
                    block.resetTime();
                    break;
                }
            }
            if (render) {
                this.blocks.add(new renderBlock(pos));
            }
        }
    }
    
    class renderBlock
    {
        private final BlockPos pos;
        private long start;
        boolean placed;
        
        public renderBlock(final BlockPos pos) {
            this.start = System.currentTimeMillis();
            this.pos = pos;
            this.placed = false;
        }
        
        void resetTime() {
            this.start = System.currentTimeMillis();
        }
        
        void render() {
            if (!this.placed) {
                if (!DamageUtil.isResistantMine(this.pos)) {
                    return;
                }
                this.resetTime();
                this.placed = true;
            }
            AxisAlignedBB alignedBB = new AxisAlignedBB(this.pos);
            if (HoleFill.this.animate.getValue()) {
                alignedBB = alignedBB.grow(this.delta() * this.delta() / 2.0 - 1.0);
            }
            if (HoleFill.this.box.getValue()) {
                RenderUtil.drawBox(alignedBB, true, 1.0, new GSColor(HoleFill.this.color.getColor(), this.returnGradient()), 63);
            }
            if (HoleFill.this.outline.getValue()) {
                RenderUtil.drawBoundingBox(alignedBB, HoleFill.this.width.getValue(), new GSColor(HoleFill.this.color.getColor(), this.returnOutGradient()));
            }
        }
        
        public double delta() {
            final long end = this.start + HoleFill.this.time.getValue();
            double result = (end - System.currentTimeMillis()) / (double)(end - this.start);
            if (result < 0.0) {
                result = 0.0;
            }
            if (result > 1.0) {
                result = 1.0;
            }
            return 1.0 - result;
        }
        
        public int returnGradient() {
            return (int)(HoleFill.this.alpha.getValue() * (1.0 - this.delta()));
        }
        
        public int returnOutGradient() {
            return (int)(HoleFill.this.outAlpha.getValue() * (1.0 - this.delta()));
        }
    }
    
    class HoleInfo
    {
        List<BlockPos> posList;
        AxisAlignedBB checkBox;
        AxisAlignedBB box;
        
        public HoleInfo(final List<BlockPos> posList, final AxisAlignedBB box) {
            this.posList = posList;
            this.box = box;
            this.checkBox = new AxisAlignedBB(box.minX - HoleFill.this.fillRange.getValue(), box.minY, box.minZ - HoleFill.this.fillRange.getValue(), box.maxX + HoleFill.this.fillRange.getValue(), box.maxY + HoleFill.this.fillYRange.getValue(), box.maxZ + HoleFill.this.fillRange.getValue());
        }
    }
}
