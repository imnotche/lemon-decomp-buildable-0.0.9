// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.render.RenderUtil;
import net.minecraft.util.math.Vec3i;
import com.lemonclient.api.event.events.RenderEvent;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.lemonclient.client.module.modules.dev.PullCrystal;
import com.lemonclient.client.module.modules.dev.PistonAura;
import com.lemonclient.client.module.modules.dev.OffHand;
import net.minecraft.item.Item;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import com.lemonclient.api.util.misc.CrystalUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import net.minecraft.util.NonNullList;
import com.lemonclient.mixin.mixins.accessor.AccessorCPacketUseEntity;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import java.util.Objects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import java.util.Comparator;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.combat.DamageUtil;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.modules.qwq.AutoEz;
import com.lemonclient.api.util.player.Locks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.math.Vec2f;
import java.util.Iterator;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.world.World;
import java.util.function.Predicate;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.event.Phase;
import com.lemonclient.api.util.render.GSColor;
import java.util.Arrays;
import com.lemonclient.api.event.events.EntityRemovedEvent;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.item.EntityEnderCrystal;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.PredictUtil;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import net.minecraft.network.play.client.CPacketUseEntity;
import java.util.concurrent.CopyOnWriteArrayList;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoCrystal", category = Category.Combat, priority = 999)
public class AutoCrystal extends Module
{
    public static CopyOnWriteArrayList<CPacketUseEntity> packetList;
    public static AutoCrystal INSTANCE;
    ModeSetting page;
    ModeSetting logic;
    IntegerSetting updateDelay;
    BooleanSetting wall;
    BooleanSetting wallAI;
    IntegerSetting enemyRange;
    IntegerSetting maxTarget;
    BooleanSetting highVersion;
    ModeSetting godMode;
    DoubleSetting maxSelfDMG;
    DoubleSetting balance;
    BooleanSetting eat;
    BooleanSetting place;
    BooleanSetting multiPlace;
    BooleanSetting packet;
    IntegerSetting placeDelay;
    DoubleSetting placeRange;
    DoubleSetting placeWallRange;
    DoubleSetting minDamage;
    BooleanSetting forcePlace;
    BooleanSetting crystalCheck;
    BooleanSetting placeAfter;
    BooleanSetting post;
    BooleanSetting placeOnRemove;
    BooleanSetting explode;
    BooleanSetting PacketExplode;
    IntegerSetting hitDelay;
    IntegerSetting PacketExplodeDelay;
    DoubleSetting breakRange;
    DoubleSetting breakWallRange;
    IntegerSetting breakMinDmg;
    BooleanSetting forceBreak;
    BooleanSetting antiWeakness;
    ModeSetting antiWeakMode;
    BooleanSetting PredictHit;
    IntegerSetting PredictHitFactor;
    BooleanSetting rotate;
    BooleanSetting swing;
    BooleanSetting packetSwing;
    BooleanSetting facePlace;
    IntegerSetting BlastHealth;
    IntegerSetting armorCount;
    IntegerSetting armorRate;
    DoubleSetting fpMinDmg;
    BooleanSetting ClientSide;
    BooleanSetting autoSwitch;
    BooleanSetting offhand;
    BooleanSetting switchBack;
    BooleanSetting bypass;
    BooleanSetting packetSwitch;
    BooleanSetting forceUpdate;
    BooleanSetting base;
    IntegerSetting baseDelay;
    IntegerSetting toggleDamage;
    IntegerSetting baseMinDamage;
    DoubleSetting maxSpeed;
    BooleanSetting baseBypass;
    BooleanSetting packetPlace;
    BooleanSetting target;
    BooleanSetting self;
    IntegerSetting tickPredict;
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
    IntegerSetting cooldown;
    BooleanSetting MineDetect;
    public BooleanSetting civ;
    public BooleanSetting rangeCheck;
    BooleanSetting packetOptimize;
    IntegerSetting limit;
    BooleanSetting pause;
    BooleanSetting showBreakDelay;
    BooleanSetting speedDebug;
    ModeSetting mode;
    BooleanSetting showDamage;
    BooleanSetting showSelfDamage;
    BooleanSetting flat;
    IntegerSetting width;
    ColorSetting color;
    IntegerSetting alpha;
    IntegerSetting outAlpha;
    IntegerSetting movingTime;
    IntegerSetting lifeTime;
    BooleanSetting scale;
    PredictUtil.PredictSettings settings;
    Timing PacketExplodeTimer;
    Timing ExplodeTimer;
    Timing UpdateTimer;
    Timing PlaceTimer;
    Timing CalcTimer;
    Timing cooldownTimer;
    EntityEnderCrystal lastCrystal;
    EntityEnderCrystal crystal;
    Vec3d movingPlaceNow;
    BlockPos lastBestPlace;
    PlaceInfo placeInfo;
    boolean ShouldInfoLastBreak;
    boolean afterAttacking;
    boolean canPredictHit;
    boolean calculated;
    boolean canBase;
    long infoBreakTime;
    long lastBreakTime;
    long updateTime;
    long startTime;
    int lastEntityID;
    int placements;
    int StuckTimes;
    int crystals;
    int waited;
    int crystalSlot;
    int crystalId;
    int lastSlot;
    Vec3d lastHitVec;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.PostSend> postSendListener;
    @EventHandler
    private final Listener<PacketEvent.PostReceive> postReceiveListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<EntityRemovedEvent> entityRemovedEventListener;
    boolean tryCalc;
    int c;
    
    public AutoCrystal() {
        this.page = this.registerMode("Page", Arrays.asList("General", "Place", "Break", "Combat", "Switch", "Base", "Predict", "Dev", "Render"), "General");
        this.logic = this.registerMode("Logic", Arrays.asList("PlaceBreak", "BreakPlace"), "BreakPlace", () -> this.page.getValue().equals("General"));
        this.updateDelay = this.registerInteger("CalcDelay", 25, 0, 1000, () -> this.page.getValue().equals("General"));
        this.wall = this.registerBoolean("WallCheck", true, () -> this.page.getValue().equals("General"));
        this.wallAI = this.registerBoolean("WallAI", true, () -> this.wall.getValue() && this.page.getValue().equals("General"));
        this.enemyRange = this.registerInteger("EnemyRange", 7, 1, 16, () -> this.page.getValue().equals("General"));
        this.maxTarget = this.registerInteger("MaxTargets", 1, 1, 10, () -> this.page.getValue().equals("General"));
        this.highVersion = this.registerBoolean("1.13", false, () -> this.page.getValue().equals("General"));
        this.godMode = this.registerMode("SelfDamage", Arrays.asList("Auto", "GodMode", "NoGodMode"), "Auto", () -> this.page.getValue().equals("General"));
        this.maxSelfDMG = this.registerDouble("MaxSelfDmg", 12.0, 0.0, 36.0, () -> !this.godMode.getValue().equals("GodMode") && this.page.getValue().equals("General"));
        this.balance = this.registerDouble("HealthBalance", 1.5, 0.0, 10.0, () -> !this.godMode.getValue().equals("GodMode") && this.page.getValue().equals("General"));
        this.eat = this.registerBoolean("WhileEating", true, () -> this.page.getValue().equals("General"));
        this.place = this.registerBoolean("Place", true, () -> this.page.getValue().equals("Place"));
        this.multiPlace = this.registerBoolean("MultiPlace", false, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.packet = this.registerBoolean("PacketCrystal", true, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.placeDelay = this.registerInteger("PlaceDelay", 50, 0, 1000, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.placeRange = this.registerDouble("PlaceRange", 5.5, 0.0, 6.0, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.placeWallRange = this.registerDouble("PlaceWallRange", 3.0, 0.1, 6.0, () -> this.place.getValue() && this.wall.getValue() && !this.wallAI.getValue() && this.page.getValue().equals("Place"));
        this.minDamage = this.registerDouble("MinDmg", 4.0, 0.0, 36.0, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.forcePlace = this.registerBoolean("OverridePlace", false, () -> !this.godMode.getValue().equals("GodMode") && this.place.getValue() && this.page.getValue().equals("Place"));
        this.crystalCheck = this.registerBoolean("CrystalCheck", false, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.placeAfter = this.registerBoolean("PlaceAfterBreak", true, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.post = this.registerBoolean("Posted", true, () -> this.place.getValue() && this.placeAfter.getValue() && this.page.getValue().equals("Place"));
        this.placeOnRemove = this.registerBoolean("PlaceOnRemove", true, () -> this.place.getValue() && this.page.getValue().equals("Place"));
        this.explode = this.registerBoolean("Break", true, () -> this.page.getValue().equals("Break"));
        this.PacketExplode = this.registerBoolean("PacketExplode", true, () -> this.explode.getValue() && this.page.getValue().equals("Break"));
        this.hitDelay = this.registerInteger("BreakDelay", 50, 0, 1000, () -> this.explode.getValue() && this.page.getValue().equals("Break"));
        this.PacketExplodeDelay = this.registerInteger("PacketExplodeDelay", 45, 0, 500, () -> this.PacketExplode.getValue() && this.page.getValue().equals("Break"));
        this.breakRange = this.registerDouble("BreakRange", 5.5, 0.0, 6.0, () -> this.explode.getValue() && this.page.getValue().equals("Break"));
        this.breakWallRange = this.registerDouble("BreakWallRange", 3.0, 0.1, 6.0, () -> this.explode.getValue() && this.wall.getValue() && !this.wallAI.getValue() && this.page.getValue().equals("Break"));
        this.breakMinDmg = this.registerInteger("BreakMinDmg", 2, 0, 36, () -> this.explode.getValue() && this.page.getValue().equals("Break"));
        this.forceBreak = this.registerBoolean("OverrideBreak", false, () -> !this.godMode.getValue().equals("GodMode") && this.explode.getValue() && this.page.getValue().equals("Break"));
        this.antiWeakness = this.registerBoolean("AntiWeakness", false, () -> this.explode.getValue() && this.page.getValue().equals("Break"));
        this.antiWeakMode = this.registerMode("SwitchMode", Arrays.asList("Normal", "Silent", "Bypass"), "Normal", () -> this.explode.getValue() && this.antiWeakness.getValue() && this.page.getValue().equals("Break"));
        this.PredictHit = this.registerBoolean("PredictHit", false, () -> this.explode.getValue() && this.page.getValue().equals("Break"));
        this.PredictHitFactor = this.registerInteger("PredictHitFactor", 2, 1, 20, () -> this.explode.getValue() && this.PredictHit.getValue() && this.page.getValue().equals("Break"));
        this.rotate = this.registerBoolean("Rotate", true, () -> this.page.getValue().equals("Combat"));
        this.swing = this.registerBoolean("Swing", true, () -> this.page.getValue().equals("Combat"));
        this.packetSwing = this.registerBoolean("PacketSwing", false, () -> this.swing.getValue() && this.page.getValue().equals("Combat"));
        this.facePlace = this.registerBoolean("FacePlace", true, () -> this.page.getValue().equals("Combat"));
        this.BlastHealth = this.registerInteger("BlastHealth", 10, 0, 20, () -> this.facePlace.getValue() && this.page.getValue().equals("Combat"));
        this.armorCount = this.registerInteger("ArmorCount", 1, 0, 64, () -> this.facePlace.getValue() && this.page.getValue().equals("Combat"));
        this.armorRate = this.registerInteger("ArmorDamage", 15, 0, 100, () -> this.facePlace.getValue() && this.armorCount.getValue() > 0 && this.page.getValue().equals("Combat"));
        this.fpMinDmg = this.registerDouble("FpMinDmg", 1.0, 0.0, 36.0, () -> this.facePlace.getValue() && this.page.getValue().equals("Combat"));
        this.ClientSide = this.registerBoolean("ClientSide", false, () -> this.page.getValue().equals("Combat"));
        this.autoSwitch = this.registerBoolean("AutoSwitch", true, () -> this.page.getValue().equals("Switch"));
        this.offhand = this.registerBoolean("Offhand", false, () -> this.autoSwitch.getValue() && this.page.getValue().equals("Switch"));
        this.switchBack = this.registerBoolean("SwitchBack", true, () -> this.autoSwitch.getValue() && !this.offhand.getValue() && this.page.getValue().equals("Switch"));
        this.bypass = this.registerBoolean("Bypass", false, () -> this.autoSwitch.getValue() && !this.offhand.getValue() && this.switchBack.getValue() && this.page.getValue().equals("Switch"));
        this.packetSwitch = this.registerBoolean("PacketSwitch", false, () -> this.autoSwitch.getValue() && !this.offhand.getValue() && this.switchBack.getValue() && this.page.getValue().equals("Switch"));
        this.forceUpdate = this.registerBoolean("ForceUpdate", false, () -> this.autoSwitch.getValue() && !this.offhand.getValue() && this.switchBack.getValue() && this.bypass.getValue() && this.page.getValue().equals("Switch"));
        this.base = this.registerBoolean("Base", false, () -> this.page.getValue().equals("Base"));
        this.baseDelay = this.registerInteger("BaseDelay", 100, 0, 200, () -> this.base.getValue() && this.page.getValue().equals("Base"));
        this.toggleDamage = this.registerInteger("ToggleMaxDmg", 12, 0, 36, () -> this.base.getValue() && this.page.getValue().equals("Base"));
        this.baseMinDamage = this.registerInteger("BaseMinDmg", 6, 0, 36, () -> this.base.getValue() && this.page.getValue().equals("Base"));
        this.maxSpeed = this.registerDouble("MaxSpeed", 10.0, 0.0, 50.0, () -> this.base.getValue() && this.page.getValue().equals("Base"));
        this.baseBypass = this.registerBoolean("BaseBypassSwitch", false, () -> this.base.getValue() && this.page.getValue().equals("Base"));
        this.packetPlace = this.registerBoolean("PacketPlace", false, () -> this.base.getValue() && this.page.getValue().equals("Base"));
        this.target = this.registerBoolean("Target", true, () -> this.page.getValue().equals("Predict"));
        this.self = this.registerBoolean("Self", true, () -> this.page.getValue().equals("Predict"));
        this.tickPredict = this.registerInteger("TickPredict", 8, 0, 30, () -> this.page.getValue().equals("Predict"));
        this.calculateYPredict = this.registerBoolean("CalculateYPredict", true, () -> this.page.getValue().equals("Predict"));
        this.startDecrease = this.registerInteger("StartDecrease", 39, 0, 200, () -> this.page.getValue().equals("Predict") && this.calculateYPredict.getValue());
        this.exponentStartDecrease = this.registerInteger("ExponentStart", 2, 1, 5, () -> this.page.getValue().equals("Predict") && this.calculateYPredict.getValue());
        this.decreaseY = this.registerInteger("DecreaseY", 2, 1, 5, () -> this.page.getValue().equals("Predict") && this.calculateYPredict.getValue());
        this.exponentDecreaseY = this.registerInteger("ExponentDecreaseY", 1, 1, 3, () -> this.page.getValue().equals("Predict") && this.calculateYPredict.getValue());
        this.splitXZ = this.registerBoolean("SplitXZ", true, () -> this.page.getValue().equals("Predict"));
        this.manualOutHole = this.registerBoolean("ManualOutHole", false, () -> this.page.getValue().equals("Predict"));
        this.aboveHoleManual = this.registerBoolean("AboveHoleManual", false, () -> this.page.getValue().equals("Predict") && this.manualOutHole.getValue());
        this.stairPredict = this.registerBoolean("StairPredict", false, () -> this.page.getValue().equals("Predict"));
        this.nStair = this.registerInteger("NStair", 2, 1, 4, () -> this.page.getValue().equals("Predict") && this.stairPredict.getValue());
        this.speedActivationStair = this.registerDouble("SpeedActivationStair", 0.11, 0.0, 1.0, () -> this.page.getValue().equals("Predict") && this.stairPredict.getValue());
        this.cooldown = this.registerInteger("Cooldown", 500, 0, 2000, () -> this.page.getValue().equals("Dev"));
        this.MineDetect = this.registerBoolean("MineDetect", false, () -> this.page.getValue().equals("Dev"));
        this.civ = this.registerBoolean("AllowCiv", false, () -> this.MineDetect.getValue() && this.page.getValue().equals("Dev"));
        this.rangeCheck = this.registerBoolean("RangeCheck", false, () -> this.MineDetect.getValue() && this.page.getValue().equals("Dev"));
        this.packetOptimize = this.registerBoolean("PacketOptimize", true, () -> this.page.getValue().equals("Dev"));
        this.limit = this.registerInteger("Limit", 40, 1, 100, () -> this.packetOptimize.getValue() && this.page.getValue().equals("Dev"));
        this.pause = this.registerBoolean("PausePistonAura", true, () -> this.page.getValue().equals("Dev"));
        this.showBreakDelay = this.registerBoolean("ShowBreakDelay", true, () -> this.page.getValue().equals("Dev"));
        this.speedDebug = this.registerBoolean("SpeedDebug", true, () -> this.page.getValue().equals("Dev"));
        this.mode = this.registerMode("Mode", Arrays.asList("Solid", "Both", "Outline"), "Both", () -> this.page.getValue().equals("Render"));
        this.showDamage = this.registerBoolean("ShowDamage", false, () -> this.page.getValue().equals("Render"));
        this.showSelfDamage = this.registerBoolean("ShowSelfDamage", false, () -> this.showDamage.getValue() && this.page.getValue().equals("Render"));
        this.flat = this.registerBoolean("Flat", false, () -> this.page.getValue().equals("Render"));
        this.width = this.registerInteger("Width", 1, 0, 10, () -> this.page.getValue().equals("Render"));
        this.color = this.registerColor("Color", new GSColor(255, 255, 255), () -> this.page.getValue().equals("Render"));
        this.alpha = this.registerInteger("Alpha", 50, 0, 255, () -> this.page.getValue().equals("Render"));
        this.outAlpha = this.registerInteger("OutlineAlpha", 125, 0, 255, () -> this.page.getValue().equals("Render"));
        this.movingTime = this.registerInteger("MovingTime", 0, 0, 500, () -> this.page.getValue().equals("Render"));
        this.lifeTime = this.registerInteger("FadeTime", 100, 0, 500, () -> this.page.getValue().equals("Render"));
        this.scale = this.registerBoolean("Scale", false, () -> this.page.getValue().equals("Render"));
        this.PacketExplodeTimer = new Timing();
        this.ExplodeTimer = new Timing();
        this.UpdateTimer = new Timing();
        this.PlaceTimer = new Timing();
        this.CalcTimer = new Timing();
        this.cooldownTimer = new Timing();
        this.movingPlaceNow = new Vec3d(-1.0, -1.0, -1.0);
        this.lastBestPlace = null;
        this.ShouldInfoLastBreak = false;
        this.afterAttacking = false;
        this.canPredictHit = false;
        this.infoBreakTime = 0L;
        this.lastBreakTime = 0L;
        this.lastEntityID = -1;
        this.placements = 0;
        this.StuckTimes = 0;
        this.crystals = 0;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() != Phase.PRE || this.lastHitVec == null || !this.rotate.getValue()) {
            }
            else {
                final PlayerPacket packet = new PlayerPacket(this, RotationUtil.getRotationTo(this.lastHitVec));
                PlayerPacketManager.INSTANCE.addPacket(packet);
            }
        }, new Predicate[0]);
        this.postSendListener = new Listener<PacketEvent.PostSend>(event -> {
            if (AutoCrystal.mc.world == null || AutoCrystal.mc.player == null || AutoCrystal.mc.player.isDead) {
            }
            else {
                if (event.getPacket() instanceof CPacketUseEntity && this.placeAfter.getValue() && this.post.getValue() && ((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
                    final Entity attacked = ((CPacketUseEntity)event.getPacket()).getEntityFromWorld(AutoCrystal.mc.world);
                    if (attacked instanceof EntityEnderCrystal) {
                        final long passed = this.PlaceTimer.getTime();
                        this.PlaceTimer.setMs(this.placeDelay.getValue() + 1);
                        this.place(false);
                        this.PlaceTimer.setTime(passed);
                    }
                }
            }
        }, new Predicate[0]);
        this.postReceiveListener = new Listener<PacketEvent.PostReceive>(event -> {
            if (AutoCrystal.mc.world == null || AutoCrystal.mc.player == null || AutoCrystal.mc.player.isDead) {
            }
            else {
                if (event.getPacket() instanceof SPacketSpawnObject) {
                    final SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket();
                    if (this.PredictHit.getValue()) {
                        for (final Entity e : AutoCrystal.mc.world.loadedEntityList) {
                            if ((e instanceof EntityItem || e instanceof EntityArrow || e instanceof EntityEnderPearl || e instanceof EntitySnowball || e instanceof EntityEgg) && e.getDistance(packet2.getX(), packet2.getY(), packet2.getZ()) <= 6.0) {
                                this.lastEntityID = -1;
                                this.canPredictHit = false;
                                event.cancel();
                            }
                        }
                    }
                    if (packet2.getType() == 51) {
                        this.lastEntityID = packet2.getEntityID();
                        if (this.explode.getValue() && this.check()) {
                            final EntityEnderCrystal crystal = (EntityEnderCrystal)AutoCrystal.mc.world.getEntityByID(this.lastEntityID);
                            if (crystal != null && this.PacketExplode.getValue() && this.PacketExplodeTimer.passedMs(this.PacketExplodeDelay.getValue()) && this.canHitCrystal(crystal)) {
                                this.PacketExplode(this.lastEntityID);
                                this.PacketExplodeTimer.reset();
                            }
                        }
                    }
                }
                if (event.getPacket() instanceof SPacketSoundEffect) {
                    final SPacketSoundEffect packet3 = (SPacketSoundEffect)event.getPacket();
                    if (packet3.getSound().equals(SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW) || packet3.getSound().equals(SoundEvents.ENTITY_ITEM_BREAK)) {
                        this.canPredictHit = false;
                    }
                    if (packet3.getSound().equals(SoundEvents.ENTITY_GENERIC_EXPLODE)) {
                        this.ShouldInfoLastBreak = true;
                        ++this.crystals;
                    }
                }
            }
        }, new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (AutoCrystal.mc.world == null || AutoCrystal.mc.player == null || AutoCrystal.mc.player.isDead) {
            }
            else {
                if (this.packetOptimize.getValue() && event.getPacket() instanceof CPacketUseEntity && AutoCrystal.packetList.size() > this.limit.getValue()) {
                    event.cancel();
                    AutoCrystal.packetList.clear();
                }
                if (event.getPacket() instanceof CPacketUseEntity && this.placeAfter.getValue() && !this.post.getValue() && ((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
                    final Entity attacked2 = ((CPacketUseEntity)event.getPacket()).getEntityFromWorld(AutoCrystal.mc.world);
                    if (attacked2 instanceof EntityEnderCrystal) {
                        final long passed2 = this.PlaceTimer.getTime();
                        this.PlaceTimer.setMs(this.placeDelay.getValue() + 1);
                        this.place(false);
                        this.PlaceTimer.setTime(passed2);
                    }
                }
                if (this.rotate.getValue() && this.lastHitVec != null) {
                    final Vec2f vec = RotationUtil.getRotationTo(this.lastHitVec);
                    if (event.getPacket() instanceof CPacketPlayer.Rotation) {
                        ((CPacketPlayer.Rotation)event.getPacket()).yaw = vec.x;
                        ((CPacketPlayer.Rotation)event.getPacket()).pitch = vec.y;
                    }
                    if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
                        ((CPacketPlayer.PositionRotation)event.getPacket()).yaw = vec.x;
                        ((CPacketPlayer.PositionRotation)event.getPacket()).pitch = vec.y;
                    }
                }
                if (event.getPacket() instanceof CPacketHeldItemChange) {
                    final int slot = ((CPacketHeldItemChange)event.getPacket()).getSlotId();
                    if (slot != this.lastSlot) {
                        this.lastSlot = slot;
                        this.cooldownTimer.reset();
                    }
                }
            }
        }, new Predicate[0]);
        this.entityRemovedEventListener = new Listener<EntityRemovedEvent>(event -> {
            if (event.getEntity().entityId == this.crystalId && this.placeOnRemove.getValue()) {
                final long passed3 = this.PlaceTimer.getTime();
                this.PlaceTimer.setMs(this.placeDelay.getValue() + 1);
                this.place(false);
                this.PlaceTimer.setTime(passed3);
            }
        }, new Predicate[0]);
        this.c = 0;
    }
    
    public void windowClick(final int windowId, final int slotId, final int mouseButton, final ClickType type, final EntityPlayer player, final boolean back) {
        final short short1 = player.openContainer.getNextTransactionID(player.inventory);
        ItemStack itemStack = ItemStack.EMPTY;
        if (!this.packetSwitch.getValue()) {
            itemStack = player.openContainer.slotClick(slotId, mouseButton, type, player);
        }
        AutoCrystal.mc.player.connection.sendPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, (back && this.forceUpdate.getValue()) ? Items.END_CRYSTAL.getDefaultInstance() : itemStack, short1));
        AutoCrystal.mc.playerController.updateController();
        AutoCrystal.mc.player.openContainer.detectAndSendChanges();
    }
    
    private void switchToCrystal(int slot, final boolean bypass, final boolean shouldSwitch, final boolean back, final Runnable runnable) {
        final int oldslot = AutoCrystal.mc.player.inventory.currentItem;
        if (!shouldSwitch || slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (bypass) {
            if (slot < 9) {
                slot += 36;
            }
            final int id = AutoCrystal.mc.player.inventoryContainer.windowId;
            final int finalSlot = slot;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> this.windowClick(id, finalSlot, oldslot, ClickType.SWAP, AutoCrystal.mc.player, false));
                runnable.run();
                AutoCrystal.mc.playerController.updateController();
                AutoCrystal.mc.player.openContainer.detectAndSendChanges();
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> this.windowClick(id, finalSlot, oldslot, ClickType.SWAP, AutoCrystal.mc.player, true));
            });
        }
        else if (slot < 9) {
            final boolean packetSwitch = back && this.packetSwitch.getValue();
            if (packetSwitch) {
                AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AutoCrystal.mc.player.inventory.currentItem = slot;
                AutoCrystal.mc.playerController.updateController();
            }
            runnable.run();
            if (back) {
                if (packetSwitch) {
                    AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
                }
                else {
                    AutoCrystal.mc.player.inventory.currentItem = oldslot;
                    AutoCrystal.mc.playerController.updateController();
                }
            }
        }
    }
    
    private void switchTo(int slot, final boolean bypass, final boolean back, final Runnable runnable) {
        final int oldslot = AutoCrystal.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (bypass) {
            if (slot < 9) {
                slot += 36;
            }
            final int id = AutoCrystal.mc.player.inventoryContainer.windowId;
            final int finalSlot = slot;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> this.windowClick(id, finalSlot, oldslot, ClickType.SWAP, AutoCrystal.mc.player, false));
                runnable.run();
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> this.windowClick(id, finalSlot, oldslot, ClickType.SWAP, AutoCrystal.mc.player, false));
            });
        }
        else if (slot < 9) {
            final boolean packetSwitch = back && this.packetSwitch.getValue();
            if (packetSwitch) {
                AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AutoCrystal.mc.player.inventory.currentItem = slot;
                AutoCrystal.mc.playerController.updateController();
            }
            runnable.run();
            if (back) {
                if (packetSwitch) {
                    AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
                }
                else {
                    AutoCrystal.mc.player.inventory.currentItem = oldslot;
                    AutoCrystal.mc.playerController.updateController();
                }
            }
        }
    }
    
    public static double getRange(final Vec3d a, final double x, final double y, final double z) {
        final double xl = a.x - x;
        final double yl = a.y - y;
        final double zl = a.z - z;
        return Math.sqrt(xl * xl + yl * yl + zl * zl);
    }
    
    private boolean check() {
        return this.placeInfo != null && this.placeInfo.target != null && this.placeInfo.target.player != null;
    }
    
    @Override
    public void onTick() {
        if (!this.tryCalc) {
            return;
        }
        if (this.UpdateTimer.passedMs(this.updateDelay.getValue())) {
            if (this.crystalSlot == -1 && AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && (!this.autoSwitch.getValue() || !this.offhand.getValue())) {
                return;
            }
            this.placeInfo = this.Calc();
            if (!this.check()) {
                this.lastBreakTime = System.currentTimeMillis();
                this.switchOffhand(false);
                this.pausePA(false);
                this.lastHitVec = null;
                this.placeInfo = null;
                this.crystal = null;
                return;
            }
            if (this.placeInfo.blockPos == null || this.placeInfo.dmg == 0.0) {
                this.placeInfo.blockPos = null;
                this.placeInfo.dmg = 0.0;
                this.switchOffhand(false);
                this.pausePA(false);
                this.lastHitVec = null;
                this.crystal = null;
            }
            AutoEz.INSTANCE.addTargetedPlayer(this.placeInfo.target.player.getName());
            this.UpdateTimer.reset();
        }
    }
    
    @Override
    public void fast() {
        if (AutoCrystal.mc.world == null || AutoCrystal.mc.player == null || AutoCrystal.mc.player.isDead) {
            return;
        }
        if (this.CalcTimer.passedMs(1000L)) {
            this.CalcTimer.reset();
            this.calculated = true;
        }
        this.crystalSlot = this.getItemHotbar();
        if (this.crystalSlot == -1 && AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && (!this.autoSwitch.getValue() || !this.offhand.getValue())) {
            this.lastBreakTime = System.currentTimeMillis();
            this.placeInfo = null;
            this.switchOffhand(false);
            this.pausePA(false);
            this.lastHitVec = null;
            this.tryCalc = false;
            return;
        }
        this.tryCalc = true;
        if (this.base.getValue()) {
            if (this.waited++ >= this.baseDelay.getValue()) {
                this.canBase = true;
                this.waited = 0;
            }
        }
        else {
            this.canBase = false;
        }
        if (!this.check()) {
            return;
        }
        this.pausePA(this.pause.getValue());
        if ((!this.eat.getValue() && EntityUtil.isEating()) || (this.cooldown.getValue() != 0 && !this.cooldownTimer.passedMs(this.cooldown.getValue()))) {
            this.lastHitVec = null;
            return;
        }
        if (this.logic.getValue().equals("BreakPlace")) {
            this.explode();
            this.place(this.crystalCheck.getValue());
        }
        else {
            this.place(this.crystalCheck.getValue());
            this.explode();
        }
    }
    
    private void place(final boolean check) {
        if (!this.place.getValue()) {
            return;
        }
        if (this.placeInfo == null || this.placeInfo.blockPos == null) {
            this.crystal = null;
            return;
        }
        boolean detected = true;
        for (final Entity entity : AutoCrystal.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            if (this.crystalPlaceBoxIntersectsCrystalBox(this.placeInfo.blockPos, entity)) {
                detected = false;
                this.crystal = (EntityEnderCrystal)entity;
                break;
            }
        }
        if (detected) {
            this.crystal = null;
        }
        final boolean useOffhand = AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        if (AutoCrystal.mc.player.inventory.currentItem != this.crystalSlot && !useOffhand) {
            if (!this.autoSwitch.getValue()) {
                return;
            }
            if (this.offhand.getValue()) {
                this.switchOffhand(true);
                return;
            }
        }
        final Block block = BlockUtil.getBlock(this.placeInfo.blockPos);
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && this.base.getValue()) {
            final int obby = BurrowUtil.findBlock(BlockObsidian.class, this.findInventory());
            if (obby == -1) {
                return;
            }
            this.switchTo(obby, this.baseBypass.getValue(), true, () -> BurrowUtil.placeBlock(this.placeInfo.blockPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), false, this.swing.getValue()));
            this.canBase = false;
        }
        if (this.PlaceTimer.passedMs(this.placeDelay.getValue()) && (detected || !check)) {
            final EnumHand hand = useOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            final EnumFacing facing = (this.placeInfo.blockPos.getY() == 255) ? EnumFacing.DOWN : EnumFacing.UP;
            final Vec3d add = new Vec3d(0.5, (facing == EnumFacing.UP) ? 1 : 0, 0.5);
            final Vec3d vec = new Vec3d(this.placeInfo.blockPos.x, this.placeInfo.blockPos.y, this.placeInfo.blockPos.z).add(add);
            this.lastHitVec = vec;
            this.switchToCrystal(this.crystalSlot, this.findInventory(), !useOffhand && this.autoSwitch.getValue(), this.switchBack.getValue(), () -> {
                if (this.packet.getValue()) {
                    AutoCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.placeInfo.blockPos, facing, hand, (float)add.x, (float)add.y, (float)add.z));
                }
                else {
                    AutoCrystal.mc.playerController.processRightClickBlock(AutoCrystal.mc.player, AutoCrystal.mc.world, this.placeInfo.blockPos, facing, vec, hand);
                }
            });
            if (this.swing.getValue()) {
                if (this.packetSwing.getValue()) {
                    AutoCrystal.mc.player.connection.sendPacket(new CPacketAnimation(hand));
                }
                else {
                    AutoCrystal.mc.player.swingArm(hand);
                }
            }
            ++this.placements;
            this.PlaceTimer.reset();
        }
        if (this.PredictHit.getValue() && DamageUtil.calculateCrystalDamage(this.placeInfo.target.player, this.placeInfo.target.position, this.placeInfo.target.boundingBox, this.placeInfo.blockPos.x + 0.5, this.placeInfo.blockPos.y + 1, this.placeInfo.blockPos.z + 0.5) > this.breakMinDmg.getValue()) {
            try {
                if (!this.canPredictHit) {
                    this.PlaceTimer.reset();
                    return;
                }
                if (AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount() > this.maxSelfDMG.getValue() && this.lastEntityID != -1 && this.lastCrystal != null && this.canPredictHit) {
                    for (int i = 0; i < this.PredictHitFactor.getValue(); ++i) {
                        this.PacketExplode(this.lastEntityID + i + 2);
                    }
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    public List<EntityPlayer> getTargets() {
        return PlayerUtil.getNearPlayers(this.enemyRange.getValue(), this.maxTarget.getValue());
    }
    
    public PlaceInfo Calc() {
        PlaceInfo best = new PlaceInfo(new PlayerInfo(PlayerUtil.getNearestPlayer(this.enemyRange.getValue())), null, 0.0, 0.0);
        List<BlockPos> default_blocks;
        if (this.wall.getValue() && this.wallAI.getValue()) {
            double TempRange = this.placeRange.getValue();
            final double temp2 = TempRange - this.StuckTimes * 0.5;
            if (this.StuckTimes > 0) {
                TempRange = this.placeRange.getValue();
                if (temp2 > this.placeWallRange.getValue()) {
                    TempRange = temp2;
                }
                else if (this.placeWallRange.getValue() < this.placeRange.getValue()) {
                    TempRange = 3.0;
                }
            }
            default_blocks = this.renditions(TempRange);
        }
        else {
            default_blocks = this.renditions(this.placeRange.getValue());
        }
        this.settings = new PredictUtil.PredictSettings(this.tickPredict.getValue(), this.calculateYPredict.getValue(), this.startDecrease.getValue(), this.exponentStartDecrease.getValue(), this.decreaseY.getValue(), this.exponentDecreaseY.getValue(), this.splitXZ.getValue(), this.manualOutHole.getValue(), this.aboveHoleManual.getValue(), this.stairPredict.getValue(), this.nStair.getValue(), this.speedActivationStair.getValue());
        EntityPlayer player = AutoCrystal.mc.player;
        if (this.self.getValue()) {
            player = PredictUtil.predictPlayer(player, this.settings);
        }
        final PlayerInfo self = new PlayerInfo(AutoCrystal.mc.player, player.getPositionVector(), player.getEntityBoundingBox());
        boolean calcBase = true;
        for (final EntityPlayer origin : this.getTargets()) {
            EntityPlayer target = origin;
            if (this.target.getValue()) {
                target = PredictUtil.predictPlayer(target, this.settings);
            }
            final PlayerInfo targetPlayer = new PlayerInfo(origin, target.getPositionVector(), target.getEntityBoundingBox());
            this.canPredictHit = (((!this.PredictHit.getValue() || !targetPlayer.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) && !targetPlayer.player.getHeldItemOffhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) || !ModuleManager.getModule("AutoMend").isEnabled());
            for (final BlockPos blockPos : default_blocks) {
                final boolean shouldBase = AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN;
                if (shouldBase) {
                    if (!this.canBase || !calcBase || blockPos.y >= (int)(targetPlayer.player.posY + 0.5) || BurrowUtil.findHotbarBlock(BlockObsidian.class) == -1) {
                        continue;
                    }
                    if (LemonClient.speedUtil.getPlayerSpeed(targetPlayer.player) > this.maxSpeed.getValue()) {
                        continue;
                    }
                }
                final double dmg = this.MineDetect.getValue() ? DamageUtil.calculateCrystalDamageMine(targetPlayer.player, targetPlayer.position, targetPlayer.boundingBox, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5) : ((double)DamageUtil.calculateCrystalDamage(targetPlayer.player, targetPlayer.position, targetPlayer.boundingBox, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5));
                if (dmg != 0.0) {
                    if (dmg < best.dmg) {
                        continue;
                    }
                    if (shouldBase) {
                        if ((int)dmg == (int)best.dmg) {
                            continue;
                        }
                        if (dmg < this.baseMinDamage.getValue()) {
                            continue;
                        }
                    }
                    else if (dmg >= this.toggleDamage.getValue()) {
                        calcBase = false;
                    }
                    double selfDmg = 0.0;
                    if (this.godMode.getValue().equals("NoGodMode") || (this.godMode.getValue().equals("Auto") && !AutoCrystal.mc.player.isCreative())) {
                        selfDmg = DamageUtil.calculateCrystalDamage(self.player, self.position, self.boundingBox, blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);
                    }
                    if (selfDmg != 0.0 && (selfDmg + this.balance.getValue() >= self.health || selfDmg + this.balance.getValue() > this.maxSelfDMG.getValue())) {
                        if (!this.forcePlace.getValue()) {
                            continue;
                        }
                        if (dmg <= targetPlayer.health) {
                            continue;
                        }
                    }
                    double minDamage = this.minDamage.getValue();
                    if (this.canFacePlace(targetPlayer)) {
                        minDamage = this.fpMinDmg.getValue();
                    }
                    if (dmg < minDamage) {
                        continue;
                    }
                    best = new PlaceInfo(targetPlayer, blockPos, dmg, selfDmg);
                }
            }
        }
        return best;
    }
    
    public void explode() {
        if (!this.explode.getValue()) {
            return;
        }
        final EntityEnderCrystal crystal = (this.crystal == null) ? AutoCrystal.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && this.canHitCrystal((EntityEnderCrystal)e)).map(e -> (EntityEnderCrystal)e).min(Comparator.comparing(e -> AutoCrystal.mc.player.getDistance(e))).orElse(null) : this.crystal;
        if (crystal != null) {
            this.lastCrystal = crystal;
            if (this.StuckTimes > 0) {
                this.StuckTimes = 0;
            }
            this.lastHitVec = new Vec3d(crystal.posX, crystal.posY, crystal.posZ);
            this.ExplodeCrystal(this.lastCrystal);
            if (this.lastBreakTime == 0L) {
                this.lastBreakTime = System.currentTimeMillis();
            }
            this.afterAttacking = true;
        }
        else {
            this.lastBreakTime = System.currentTimeMillis();
            this.afterAttacking = false;
            ++this.StuckTimes;
        }
    }
    
    public void ExplodeCrystal(final Entity crystal) {
        if (crystal != null && this.ExplodeTimer.passedMs(this.hitDelay.getValue()) && AutoCrystal.mc.getConnection() != null) {
            this.PacketExplode(crystal.getEntityId());
            this.ExplodeTimer.reset();
            if (this.ClientSide.getValue()) {
                for (final Entity o : AutoCrystal.mc.world.getLoadedEntityList()) {
                    if (o instanceof EntityEnderCrystal && o.getDistance(o.posX, o.posY, o.posZ) <= 6.0) {
                        o.setDead();
                    }
                }
                AutoCrystal.mc.world.removeAllEntities();
            }
            if (this.multiPlace.getValue() && this.placements >= 3) {
                this.placements = 0;
                this.afterAttacking = true;
            }
        }
    }
    
    public void PacketExplode(final int i) {
        if (this.check() && ((this.lastCrystal != null && this.canHitCrystal(this.lastCrystal)) || AutoCrystal.mc.world.getEntityByID(i) == null)) {
            this.crystalId = i;
            try {
                int slot = -1;
                if (this.antiWeakness.getValue() && AutoCrystal.mc.player.isPotionActive(MobEffects.WEAKNESS) && (!AutoCrystal.mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(AutoCrystal.mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                    for (int b = 0; b < (this.findInventory() ? 36 : 9); ++b) {
                        final ItemStack stack = AutoCrystal.mc.player.inventory.getStackInSlot(b);
                        if (stack != ItemStack.EMPTY) {
                            if (stack.getItem() instanceof ItemSword) {
                                slot = b;
                                break;
                            }
                            if (stack.getItem() instanceof ItemTool) {
                                slot = b;
                            }
                        }
                    }
                }
                this.switchTo(slot, this.antiWeakMode.getValue().equals("Bypass"), !this.antiWeakMode.getValue().equals("Normal"), () -> {
                    final CPacketUseEntity crystal = new CPacketUseEntity();
                    setEntityId(crystal, i);
                    setAction(crystal, CPacketUseEntity.Action.ATTACK);
                    AutoCrystal.mc.player.connection.sendPacket(crystal);
                    if (this.packetOptimize.getValue()) {
                        AutoCrystal.packetList.add(crystal);
                    }
                });
                final EnumHand hand = (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                if (this.swing.getValue()) {
                    if (this.packetSwing.getValue()) {
                        AutoCrystal.mc.player.connection.sendPacket(new CPacketAnimation(hand));
                    }
                    else {
                        AutoCrystal.mc.player.swingArm(hand);
                    }
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    public static void setEntityId(final CPacketUseEntity packet, final int entityId) {
        ((AccessorCPacketUseEntity)packet).setId(entityId);
    }
    
    public static void setAction(final CPacketUseEntity packet, final CPacketUseEntity.Action action) {
        ((AccessorCPacketUseEntity)packet).setAction(action);
    }
    
    public CrystalInfo getBestDmg(final EntityEnderCrystal crystal) {
        CrystalInfo best = new CrystalInfo(crystal, null, 0.0);
        for (final EntityPlayer entityPlayer : this.getTargets()) {
            final EntityPlayer player = this.target.getValue() ? PredictUtil.predictPlayer(entityPlayer, this.settings) : entityPlayer;
            final PlayerInfo target = new PlayerInfo(entityPlayer, player.getPositionVector(), player.getEntityBoundingBox());
            final double dmg = DamageUtil.calculateCrystalDamage(target.player, target.position, target.boundingBox, crystal.posX, crystal.posY, crystal.posZ);
            if (dmg != 0.0) {
                final CrystalInfo get = new CrystalInfo(crystal, target, dmg);
                if (dmg >= target.health) {
                    return get;
                }
                if (dmg <= best.damage) {
                    continue;
                }
                best = get;
            }
        }
        return best;
    }
    
    public List<BlockPos> renditions(final double range) {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(EntityUtil.getSphere(PlayerUtil.getEyesPos(), range, range, false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }
    
    public boolean canPlaceCrystal(final BlockPos blockPos) {
        if (PlayerUtil.getDistanceI(blockPos) > this.placeRange.getValue()) {
            return false;
        }
        if (this.wall.getValue() && PlayerUtil.getDistanceI(blockPos) > this.placeWallRange.getValue() && !CrystalUtil.calculateRaytrace(blockPos)) {
            return false;
        }
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        if (!BlockUtil.isAirBlock(boost)) {
            return false;
        }
        if (!this.highVersion.getValue() && !BlockUtil.isAirBlock(boost2)) {
            return false;
        }
        if (AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && AutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            if (!this.canBase || this.base.getValue()) {
                return false;
            }
            if (!BlockUtil.isAirBlock(blockPos) || this.intersectsWithEntity(blockPos)) {
                return false;
            }
            if (BurrowUtil.getFirstFacing(blockPos) == null) {
                return false;
            }
        }
        boolean recall = false;
        for (final Entity entity : AutoCrystal.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) {
                continue;
            }
            if (this.crystalPlaceBoxIntersectsCrystalBox(blockPos, entity)) {
                recall = true;
                break;
            }
        }
        for (final Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
            if (entity instanceof EntityEnderCrystal) {
                continue;
            }
            if (recall) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
                    continue;
                }
                if (entity instanceof EntityExpBottle) {
                    continue;
                }
            }
            return false;
        }
        if (!this.highVersion.getValue()) {
            for (final Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                if (recall) {
                    if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
                        continue;
                    }
                    if (entity instanceof EntityExpBottle) {
                        continue;
                    }
                }
                return false;
            }
        }
        if (this.afterAttacking && this.lastCrystal != null) {
            for (final Entity entity : AutoCrystal.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    continue;
                }
                final EntityEnderCrystal enderCrystal = (EntityEnderCrystal)entity;
                if (Math.abs(enderCrystal.posY - (blockPos.getY() + 1)) >= 2.0) {
                    continue;
                }
                final double d2 = this.lastCrystal.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);
                if (d2 <= 6.0) {
                    continue;
                }
                if (getRange(enderCrystal.getPositionVector(), blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5) >= 2.0) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    
    public boolean canHitCrystal(final EntityEnderCrystal crystal) {
        if (crystal == null) {
            return false;
        }
        if (AutoCrystal.mc.player.getDistance(crystal) > this.breakRange.getValue()) {
            return false;
        }
        if (this.wall.getValue() && AutoCrystal.mc.player.getDistance(crystal) > this.breakWallRange.getValue() && !CrystalUtil.calculateRaytrace(crystal)) {
            return false;
        }
        if (crystal == this.crystal && DamageUtil.calculateCrystalDamage(this.placeInfo.target.player, this.placeInfo.target.position, this.placeInfo.target.boundingBox, this.placeInfo.blockPos.x + 0.5, this.placeInfo.blockPos.y + 1, this.placeInfo.blockPos.z + 0.5) >= this.breakMinDmg.getValue()) {
            return true;
        }
        final float healthSelf = AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount();
        float selfDamage = 0.0f;
        if (this.godMode.getValue().equals("NoGodMode") || (this.godMode.getValue().equals("Auto") && !AutoCrystal.mc.player.isCreative())) {
            final EntityPlayer player = this.self.getValue() ? PredictUtil.predictPlayer(AutoCrystal.mc.player, this.settings) : AutoCrystal.mc.player;
            final PlayerInfo self = new PlayerInfo(AutoCrystal.mc.player, player.getPositionVector(), player.getEntityBoundingBox());
            selfDamage = DamageUtil.calculateCrystalDamage(self.player, self.position, self.boundingBox, crystal.posX, crystal.posY, crystal.posZ);
        }
        final CrystalInfo bestTarget = this.getBestDmg(crystal);
        if (bestTarget.player == null) {
            return false;
        }
        if (selfDamage != 0.0f && (selfDamage + this.balance.getValue() >= healthSelf || selfDamage + this.balance.getValue() > this.maxSelfDMG.getValue())) {
            return this.forceBreak.getValue() && bestTarget.player.health <= bestTarget.damage;
        }
        double minDamage = this.breakMinDmg.getValue();
        if (this.canFacePlace(bestTarget.player)) {
            minDamage = this.fpMinDmg.getValue();
        }
        return bestTarget.damage >= minDamage;
    }
    
    public boolean canFacePlace(final PlayerInfo target) {
        if (target == null || target.player == null || !this.facePlace.getValue()) {
            return false;
        }
        if (target.health < this.BlastHealth.getValue()) {
            return true;
        }
        for (final ItemStack itemStack : target.player.getArmorInventoryList()) {
            if (itemStack.isEmpty()) {
                continue;
            }
            if (itemStack.getCount() > this.armorCount.getValue()) {
                continue;
            }
            final float dmg = (itemStack.getMaxDamage() - (float)itemStack.getItemDamage()) / itemStack.getMaxDamage();
            if (dmg < this.armorRate.getValue() / 100.0f) {
                return true;
            }
        }
        return false;
    }
    
    private int getItemHotbar() {
        for (int i = 0; i < (this.findInventory() ? 36 : 9); ++i) {
            final Item item = AutoCrystal.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) == Item.getIdFromItem(Items.END_CRYSTAL)) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean findInventory() {
        return this.bypass.getValue() && this.switchBack.getValue();
    }
    
    private void switchOffhand(final boolean value) {
        if (ModuleManager.isModuleEnabled(OffHand.class)) {
            OffHand.INSTANCE.autoCrystal = value;
        }
    }
    
    private void pausePA(final boolean value) {
        if (ModuleManager.isModuleEnabled(PistonAura.class)) {
            PistonAura.INSTANCE.autoCrystal = value;
        }
        if (ModuleManager.isModuleEnabled(PullCrystal.class)) {
            PullCrystal.INSTANCE.autoCrystal = value;
        }
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoCrystal.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                if (entity instanceof EntityExpBottle) {
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
    
    private boolean crystalPlaceBoxIntersectsCrystalBox(final BlockPos placePos, final Entity entity) {
        return entity.boundingBox.intersects(new AxisAlignedBB(placePos.x - 0.5, placePos.y, placePos.z - 0.5, placePos.x + 1.5, placePos.y + (this.highVersion.getValue() ? 1 : 2), placePos.z + 1.5));
    }
    
    public void onEnable() {
        this.lastBreakTime = System.currentTimeMillis();
        this.lastEntityID = -1;
        final int n = 0;
        this.c = n;
        this.crystals = n;
        this.updateTime = System.currentTimeMillis();
        this.startTime = System.currentTimeMillis();
        this.ShouldInfoLastBreak = false;
        this.afterAttacking = false;
        this.canPredictHit = true;
        this.PlaceTimer.reset();
        this.ExplodeTimer.reset();
        this.PacketExplodeTimer.reset();
        this.UpdateTimer.reset();
        this.CalcTimer.reset();
        AutoCrystal.packetList.clear();
        this.lastSlot = AutoCrystal.mc.player.inventory.currentItem;
        this.lastHitVec = null;
        this.placeInfo = null;
        this.movingPlaceNow = new Vec3d(-1.0, -1.0, -1.0);
    }
    
    public void onDisable() {
        this.switchOffhand(false);
        this.pausePA(false);
        this.lastHitVec = null;
        this.StuckTimes = 0;
        AutoCrystal.packetList.clear();
    }
    
    @Override
    public String getHudInfo() {
        if (!this.check()) {
            return "";
        }
        if (this.ShouldInfoLastBreak) {
            this.infoBreakTime = System.currentTimeMillis() - this.lastBreakTime;
            this.lastBreakTime = 0L;
            this.ShouldInfoLastBreak = false;
        }
        if (this.calculated) {
            this.c = this.crystals;
            this.calculated = false;
            this.crystals = 0;
        }
        final String text = "[" + ChatFormatting.WHITE + this.placeInfo.target.player.getName() + (this.showBreakDelay.getValue() ? (", " + this.infoBreakTime + "ms") : "") + (this.speedDebug.getValue() ? (", " + this.c + "c/s") : "") + ChatFormatting.GRAY + "]";
        return text;
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (AutoCrystal.mc.world == null || AutoCrystal.mc.player == null) {
            return;
        }
        final BlockPos placing = (this.placeInfo == null) ? null : this.placeInfo.blockPos;
        if (placing != this.lastBestPlace) {
            if (placing != null && this.lastBestPlace == null) {
                this.movingPlaceNow = new Vec3d(this.placeInfo.blockPos.getX(), this.placeInfo.blockPos.getY(), this.placeInfo.blockPos.getZ());
            }
            this.updateTime = System.currentTimeMillis();
            if (placing == null) {
                this.startTime = System.currentTimeMillis();
            }
            else if (this.lastBestPlace == null) {
                this.startTime = System.currentTimeMillis();
            }
            this.lastBestPlace = placing;
        }
        if (this.lastBestPlace != null) {
            if (this.movingPlaceNow.x == -1.0 && this.movingPlaceNow.y == -1.0 && this.movingPlaceNow.z == -1.0) {
                this.movingPlaceNow = new Vec3d((float)this.lastBestPlace.getX(), (float)this.lastBestPlace.getY(), (float)this.lastBestPlace.getZ());
            }
            if (this.movingTime.getValue() == 0) {
                this.movingPlaceNow = new Vec3d(this.lastBestPlace);
            }
            else {
                this.movingPlaceNow = new Vec3d(this.movingPlaceNow.x + (this.lastBestPlace.getX() - this.movingPlaceNow.x) * this.toDelta(this.updateTime, this.movingTime.getValue()), this.movingPlaceNow.y + (this.lastBestPlace.getY() - this.movingPlaceNow.y) * this.toDelta(this.updateTime, this.movingTime.getValue()), this.movingPlaceNow.z + (this.lastBestPlace.getZ() - this.movingPlaceNow.z) * this.toDelta(this.updateTime, this.movingTime.getValue()));
            }
        }
        if (this.movingPlaceNow.x != -1.0 || this.movingPlaceNow.y != -1.0 || this.movingPlaceNow.z != -1.0) {
            this.drawBoxMain(this.movingPlaceNow.x, this.movingPlaceNow.y, this.movingPlaceNow.z);
        }
    }
    
    AxisAlignedBB getBox(final double x, final double y, final double z) {
        final double maxX = x + 1.0;
        final double maxZ = z + 1.0;
        return new AxisAlignedBB(x, y, z, maxX, y + 1.0, maxZ);
    }
    
    float toDelta(final long start, final float length) {
        float value = this.toDelta(start) / length;
        if (value > 1.0f) {
            value = 1.0f;
        }
        if (value < 0.0f) {
            value = 0.0f;
        }
        return value;
    }
    
    long toDelta(final long start) {
        return System.currentTimeMillis() - start;
    }
    
    void drawBoxMain(final double x, final double y, final double z) {
        AxisAlignedBB box = this.getBox(x, y, z);
        float size;
        if (!this.check() || this.placeInfo.blockPos == null) {
            size = 1.0f - this.toDelta(this.startTime, this.lifeTime.getValue());
        }
        else {
            size = this.toDelta(this.startTime, this.lifeTime.getValue());
        }
        if (this.scale.getValue()) {
            box = box.grow((1.0f - size) * (1.0f - size) / 2.0f - 1.0f);
        }
        if (this.flat.getValue()) {
            box = new AxisAlignedBB(box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ);
        }
        final int alpha = (int)(this.alpha.getValue() * size);
        final int outAlpha = (int)(this.outAlpha.getValue() * size);
        final String s = this.mode.getValue();
        switch (s) {
            case "Outline": {
                RenderUtil.drawBoundingBox(box, this.width.getValue(), new GSColor(this.color.getValue(), outAlpha));
                break;
            }
            case "Solid": {
                RenderUtil.drawBox(box, true, this.flat.getValue() ? 0.0 : 1.0, new GSColor(this.color.getValue(), alpha), 63);
                break;
            }
            case "Both": {
                RenderUtil.drawBox(box, true, this.flat.getValue() ? 0.0 : 1.0, new GSColor(this.color.getValue(), alpha), 63);
                RenderUtil.drawBoundingBox(box, this.width.getValue(), new GSColor(this.color.getValue(), outAlpha));
                break;
            }
        }
        if (this.showDamage.getValue() && this.check() && this.placeInfo.blockPos != null) {
            box = this.getBox(x, y, z);
            String[] damageText = { String.format("%.1f", this.placeInfo.dmg) };
            if (this.showSelfDamage.getValue()) {
                damageText = new String[] { String.format("%.1f", this.placeInfo.dmg) + "/" + String.format("%.1f", this.placeInfo.selfDmg) };
            }
            RenderUtil.drawNametag(box.minX + 0.5, box.minY + 0.5, box.minZ + 0.5, damageText, new GSColor(255, 255, 255), 1, 0.02666666666666667, 0.0);
        }
    }
    
    static {
        AutoCrystal.packetList = new CopyOnWriteArrayList<CPacketUseEntity>();
        AutoCrystal.INSTANCE = new AutoCrystal();
    }
    
    public static class PlaceInfo
    {
        public BlockPos blockPos;
        public PlayerInfo target;
        public double dmg;
        public double selfDmg;
        
        public PlaceInfo(final PlayerInfo target, final BlockPos block, final double dmg, final double selfDmg) {
            this.blockPos = block;
            this.target = target;
            this.dmg = dmg;
            this.selfDmg = selfDmg;
        }
    }
    
    public static class PlayerInfo
    {
        EntityPlayer player;
        Vec3d position;
        AxisAlignedBB boundingBox;
        double health;
        
        public PlayerInfo(final EntityPlayer player) {
            this.player = player;
            if (player != null) {
                this.position = player.getPositionVector();
                this.boundingBox = player.getEntityBoundingBox();
                this.health = player.getHealth() + player.getAbsorptionAmount();
            }
        }
        
        public PlayerInfo(final EntityPlayer player, final Vec3d position, final AxisAlignedBB boundingBox) {
            this.player = player;
            this.position = position;
            this.boundingBox = boundingBox;
            this.health = player.getHealth() + player.getAbsorptionAmount();
        }
    }
    
    public static class CrystalInfo
    {
        EntityEnderCrystal crystal;
        PlayerInfo player;
        double damage;
        
        public CrystalInfo(final EntityEnderCrystal crystal, final PlayerInfo player, final double damage) {
            this.crystal = crystal;
            this.player = player;
            this.damage = damage;
        }
    }
}
