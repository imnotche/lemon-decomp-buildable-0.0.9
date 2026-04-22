// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.init.MobEffects;
import java.util.List;
import java.util.Comparator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.RotationUtil;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import java.math.RoundingMode;
import java.math.BigDecimal;
import org.lwjgl.input.Keyboard;
import com.lemonclient.api.util.misc.KeyBoardClass;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.TimerUtils;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import com.lemonclient.api.event.events.StepEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.event.events.MotionUpdateEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Speed+", category = Category.Movement, priority = 999)
public class SpeedPlus extends Module
{
    public static SpeedPlus INSTANCE;
    BooleanSetting damageBoost;
    public BooleanSetting sum;
    BooleanSetting longJump;
    IntegerSetting lagCoolDown;
    IntegerSetting jumpStage;
    BooleanSetting motionJump;
    BooleanSetting randomBoost;
    BooleanSetting lavaBoost;
    BooleanSetting SpeedInWater;
    BooleanSetting strict;
    BooleanSetting strictBoost;
    BooleanSetting useTimer;
    BooleanSetting jump;
    BooleanSetting stepCheck;
    BooleanSetting bindCheck;
    StringSetting bind;
    DoubleSetting minStepHeight;
    DoubleSetting maxStepHeight;
    BooleanSetting test;
    Timing lagBackCoolDown;
    Timing rdBoostTimer;
    boolean lagDetected;
    boolean inCoolDown;
    boolean checkCoolDown;
    boolean warn;
    boolean checkStep;
    int readyStage;
    int stage;
    int level;
    double boostSpeed;
    double lastDist;
    double moveSpeed;
    double stepHigh;
    float boostFactor;
    long detectionTime;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<MotionUpdateEvent> motionUpdateEventListener;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener;
    @EventHandler
    private final Listener<StepEvent> stepEventListener;
    
    public SpeedPlus() {
        this.damageBoost = this.registerBoolean("DamageBoost", true);
        this.sum = this.registerBoolean("Sum", false, () -> this.damageBoost.getValue());
        this.longJump = this.registerBoolean("TryLongJump", false);
        this.lagCoolDown = this.registerInteger("LagCoolDown", 2200, 0, 8000, () -> this.longJump.getValue());
        this.jumpStage = this.registerInteger("JumpStage", 6, 1, 20, () -> this.longJump.getValue());
        this.motionJump = this.registerBoolean("MotionJump", false, () -> this.longJump.getValue());
        this.randomBoost = this.registerBoolean("RandomBoost", false);
        this.lavaBoost = this.registerBoolean("LavaBoost", true);
        this.SpeedInWater = this.registerBoolean("SpeedInWater", true);
        this.strict = this.registerBoolean("Strict", false);
        this.strictBoost = this.registerBoolean("StrictBoost", false, () -> this.damageBoost.getValue());
        this.useTimer = this.registerBoolean("UseTimer", true);
        this.jump = this.registerBoolean("Jump", true);
        this.stepCheck = this.registerBoolean("Step Check", true);
        this.bindCheck = this.registerBoolean("Use Bind", false, () -> this.stepCheck.getValue());
        this.bind = this.registerString("Step Check Bind", "", () -> this.stepCheck.getValue() && this.bindCheck.getValue());
        this.minStepHeight = this.registerDouble("Min Step Height", 1.0, 0.0, 10.0, () -> this.stepCheck.getValue());
        this.maxStepHeight = this.registerDouble("Max Step Height", 2.5, 0.0, 10.0, () -> this.stepCheck.getValue());
        this.test = this.registerBoolean("Test Mode", false, () -> this.stepCheck.getValue());
        this.lagBackCoolDown = new Timing();
        this.rdBoostTimer = new Timing();
        this.stage = 1;
        this.level = 1;
        this.boostFactor = 6.0f;
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (SpeedPlus.mc.world == null || SpeedPlus.mc.player == null || SpeedPlus.mc.player.isDead) {
            }
            else {
                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    this.lastDist = 0.0;
                    this.moveSpeed = this.applySpeedPotionEffects();
                    this.stage = 2;
                    this.detectionTime = System.currentTimeMillis();
                    this.lagDetected = true;
                    this.rdBoostTimer.reset();
                    this.boostFactor = 8.0f;
                    if (this.longJump.getValue()) {
                        this.readyStage = 0;
                        this.inCoolDown = true;
                        if (!this.checkCoolDown) {
                            this.lagBackCoolDown.reset();
                            this.checkCoolDown = true;
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.motionUpdateEventListener = new Listener<MotionUpdateEvent>(event -> {
            if (SpeedPlus.mc.world == null || SpeedPlus.mc.player == null || SpeedPlus.mc.player.isDead) {
            }
            else {
                try {
                    if (this.lagBackCoolDown.passedMs(this.lagCoolDown.getValue())) {
                        this.checkCoolDown = false;
                        this.inCoolDown = false;
                        this.lagBackCoolDown.reset();
                    }
                    if (System.currentTimeMillis() - this.detectionTime > 3182L) {
                        this.lagDetected = false;
                    }
                    if (this.useTimer.getValue()) {
                        TimerUtils.setTickLength(45.955883f);
                    }
                    if (event.stage == 1) {
                        this.lastDist = Math.sqrt((SpeedPlus.mc.player.posX - SpeedPlus.mc.player.prevPosX) * (SpeedPlus.mc.player.posX - SpeedPlus.mc.player.prevPosX) + (SpeedPlus.mc.player.posZ - SpeedPlus.mc.player.prevPosZ) * (SpeedPlus.mc.player.posZ - SpeedPlus.mc.player.prevPosZ));
                    }
                }
                catch (final NumberFormatException ex) {}
            }
        }, new Predicate[0]);
        this.playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
            if (SpeedPlus.mc.world == null || SpeedPlus.mc.player == null || SpeedPlus.mc.player.isDead) {
            }
            else if (SpeedPlus.mc.player.movementInput.moveForward == 0.0f && SpeedPlus.mc.player.movementInput.moveStrafe == 0.0f) {
                event.setX(0.0);
                event.setZ(0.0);
                event.setSpeed(0.0);
            }
            else {
                if (this.checkStep && this.test.getValue()) {
                    final double yaw = this.calcMoveYaw(SpeedPlus.mc.player.rotationYaw, SpeedPlus.mc.player.movementInput.moveForward, SpeedPlus.mc.player.movementInput.moveStrafe);
                    final double dirX = -Math.sin(yaw);
                    final double dirZ = Math.cos(yaw);
                    final double dist = this.calcBlockDistAhead(dirX * 6.0, dirZ * 6.0);
                    final double stepHeight = this.test.getValue() ? this.calcStepHeight(dist, dirX, dirZ) : this.stepHigh;
                    final double multiplier = this.applySpeedPotionEffects();
                    if (stepHeight <= this.maxStepHeight.getValue()) {
                        if (dist < 3.0 * multiplier && stepHeight > this.minStepHeight.getValue() * 2.0) {
                            return;
                        }
                        else if (dist < 1.4 * multiplier && stepHeight > this.minStepHeight.getValue()) {
                            return;
                        }
                    }
                }
                if (!this.SpeedInWater.getValue() && this.shouldReturn()) {
                }
                else {
                    if (SpeedPlus.mc.player.onGround) {
                        this.level = 2;
                    }
                    if (round(SpeedPlus.mc.player.posY - (int)SpeedPlus.mc.player.posY, 3) == round(0.138, 3) && this.jump.getValue()) {
                        final EntityPlayerSP player = SpeedPlus.mc.player;
                        player.motionY -= 0.07;
                        event.setY(event.getY() - 0.08316090325960147);
                        final EntityPlayerSP player2 = SpeedPlus.mc.player;
                        player2.posY -= 0.08316090325960147;
                    }
                    if (this.level != 1) {
                        if (this.level == 2) {
                            this.level = 3;
                            if (!SpeedPlus.mc.player.isInLava() && SpeedPlus.mc.player.onGround && this.jump.getValue()) {
                                final EntityPlayerSP player3 = SpeedPlus.mc.player;
                                this.applyJumpBoostPotionEffects();
                                final double motionY = 0;
                                event.setY(player3.motionY = motionY);
                            }
                            if (this.strict.getValue() || SpeedPlus.mc.player.isSneaking()) {
                                this.moveSpeed *= 1.433;
                            }
                            else {
                                this.moveSpeed *= 1.64847275;
                            }
                        }
                        else if (this.level == 3) {
                            this.level = 4;
                            this.moveSpeed = this.lastDist - 0.6553 * (this.lastDist - this.applySpeedPotionEffects() + 0.04);
                        }
                        else {
                            if (SpeedPlus.mc.player.onGround && (!SpeedPlus.mc.world.getCollisionBoxes(SpeedPlus.mc.player, SpeedPlus.mc.player.boundingBox.offset(0.0, SpeedPlus.mc.player.motionY, 0.0)).isEmpty() || SpeedPlus.mc.player.collidedVertically)) {
                                this.level = 1;
                            }
                            this.moveSpeed = this.lastDist - this.lastDist / 201.0;
                        }
                    }
                    else {
                        this.level = 2;
                        this.moveSpeed = 1.418 * this.applySpeedPotionEffects();
                    }
                    if (this.damageBoost.getValue() && ColorMain.INSTANCE.velocityBoost != 0.0) {
                        if (this.longJump.getValue()) {
                            ++this.readyStage;
                        }
                        this.boostSpeed = ColorMain.INSTANCE.velocityBoost;
                        this.moveSpeed += this.boostSpeed;
                        if (this.strictBoost.getValue()) {
                            this.moveSpeed = Math.max((this.moveSpeed + 0.10000000149011612) / 1.5, this.applySpeedPotionEffects());
                        }
                        ColorMain.INSTANCE.velocityBoost = 0.0;
                    }
                    if (this.randomBoost.getValue() && this.rdBoostTimer.passedMs(3500L) && !this.lagDetected && MotionUtil.moving(SpeedPlus.mc.player) && SpeedPlus.mc.player.onGround) {
                        this.moveSpeed += this.moveSpeed / this.boostFactor;
                        this.boostFactor = 6.0f;
                        this.rdBoostTimer.reset();
                    }
                    if (this.longJump.getValue() && this.readyStage >= this.jumpStage.getValue() && !this.inCoolDown) {
                        if (!this.motionJump.getValue()) {
                            this.moveSpeed *= this.jumpStage.getValue() / 10.0f;
                        }
                        else {
                            motionJump();
                            final EntityPlayerSP player4 = SpeedPlus.mc.player;
                            player4.motionY *= 1.02;
                            final EntityPlayerSP player5 = SpeedPlus.mc.player;
                            player5.motionY *= 1.13;
                            final EntityPlayerSP player6 = SpeedPlus.mc.player;
                            player6.motionY *= 1.27;
                            this.moveSpeed += Math.abs(this.moveSpeed - this.boostSpeed);
                        }
                        this.readyStage = 0;
                    }
                    this.moveSpeed = Math.max(this.moveSpeed, this.applySpeedPotionEffects());
                    if (!this.shouldReturn()) {
                        event.setSpeed(this.moveSpeed);
                    }
                    else if (this.lavaBoost.getValue() && SpeedPlus.mc.player.isInLava()) {
                        event.setX(event.getX() * 3.1);
                        event.setZ(event.getZ() * 3.1);
                        if (SpeedPlus.mc.gameSettings.keyBindJump.isKeyDown()) {
                            event.setY(event.getY() * 3.0);
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.stepEventListener = new Listener<StepEvent>(event -> this.stepHigh = event.getBB().minY - SpeedPlus.mc.player.posY, new Predicate[0]);
    }
    
    @Override
    public void onTick() {
        this.checkStep = false;
        if (this.stepCheck.getValue()) {
            if (this.bindCheck.getValue()) {
                if (this.bind.getText().isEmpty() || !Keyboard.isKeyDown(KeyBoardClass.getKeyFromChar(this.bind.getText().charAt(0)))) {
                    this.checkStep = !this.checkStep;
                }
            }
            else {
                this.checkStep = true;
            }
        }
    }
    
    public static double round(final double n, final int n2) {
        if (n2 < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(n).setScale(n2, RoundingMode.HALF_UP).doubleValue();
    }
    
    public void onEnable() {
        if (SpeedPlus.mc.player == null) {
            this.disable();
            return;
        }
        this.boostSpeed = 0.0;
        this.lagBackCoolDown.reset();
        this.readyStage = 0;
        this.warn = false;
        this.moveSpeed = this.applySpeedPotionEffects();
    }
    
    public static void motionJump() {
        if (!SpeedPlus.mc.player.collidedVertically) {
            if (SpeedPlus.mc.player.motionY == -0.07190068807140403) {
                final EntityPlayerSP player = SpeedPlus.mc.player;
                player.motionY *= 0.3499999940395355;
            }
            else if (SpeedPlus.mc.player.motionY == -0.10306193759436909) {
                final EntityPlayerSP player2 = SpeedPlus.mc.player;
                player2.motionY *= 0.550000011920929;
            }
            else if (SpeedPlus.mc.player.motionY == -0.13395038817442878) {
                final EntityPlayerSP player3 = SpeedPlus.mc.player;
                player3.motionY *= 0.6700000166893005;
            }
            else if (SpeedPlus.mc.player.motionY == -0.16635183030382) {
                final EntityPlayerSP player4 = SpeedPlus.mc.player;
                player4.motionY *= 0.6899999976158142;
            }
            else if (SpeedPlus.mc.player.motionY == -0.19088711097794803) {
                final EntityPlayerSP player5 = SpeedPlus.mc.player;
                player5.motionY *= 0.7099999785423279;
            }
            else if (SpeedPlus.mc.player.motionY == -0.21121925191528862) {
                final EntityPlayerSP player6 = SpeedPlus.mc.player;
                player6.motionY *= 0.20000000298023224;
            }
            else if (SpeedPlus.mc.player.motionY == -0.11979897632390576) {
                final EntityPlayerSP player7 = SpeedPlus.mc.player;
                player7.motionY *= 0.9300000071525574;
            }
            else if (SpeedPlus.mc.player.motionY == -0.18758479151225355) {
                final EntityPlayerSP player8 = SpeedPlus.mc.player;
                player8.motionY *= 0.7200000286102295;
            }
            else if (SpeedPlus.mc.player.motionY == -0.21075983825251726) {
                final EntityPlayerSP player9 = SpeedPlus.mc.player;
                player9.motionY *= 0.7599999904632568;
            }
            if (SpeedPlus.mc.player.motionY < -0.2 && SpeedPlus.mc.player.motionY > -0.24) {
                final EntityPlayerSP player10 = SpeedPlus.mc.player;
                player10.motionY *= 0.7;
            }
            if (SpeedPlus.mc.player.motionY < -0.25 && SpeedPlus.mc.player.motionY > -0.32) {
                final EntityPlayerSP player11 = SpeedPlus.mc.player;
                player11.motionY *= 0.8;
            }
            if (SpeedPlus.mc.player.motionY < -0.35 && SpeedPlus.mc.player.motionY > -0.8) {
                final EntityPlayerSP player12 = SpeedPlus.mc.player;
                player12.motionY *= 0.98;
            }
            if (SpeedPlus.mc.player.motionY < -0.8 && SpeedPlus.mc.player.motionY > -1.6) {
                final EntityPlayerSP player13 = SpeedPlus.mc.player;
                player13.motionY *= 0.99;
            }
        }
    }
    
    public boolean shouldReturn() {
        return SpeedPlus.mc.player.isInLava() || SpeedPlus.mc.player.isInWater() || SpeedPlus.mc.player.isInWeb;
    }
    
    public void onDisable() {
        this.moveSpeed = 0.0;
        this.stage = 2;
        if (SpeedPlus.mc.player != null) {
            SpeedPlus.mc.player.stepHeight = 0.6f;
            TimerUtils.setTickLength(50.0f);
        }
    }
    
    private double calcBlockDistAhead(final double offsetX, final double offsetZ) {
        if (SpeedPlus.mc.player.collidedHorizontally) {
            return 0.0;
        }
        final AxisAlignedBB box = SpeedPlus.mc.player.boundingBox;
        final double x = (offsetX > 0.0) ? box.maxX : box.minX;
        final double z = (offsetX > 0.0) ? box.maxZ : box.minZ;
        return Math.min(this.rayTraceDist(new Vec3d(x, box.minY + 0.6, z), offsetX, offsetZ), this.rayTraceDist(new Vec3d(x, box.maxY + 0.6, z), offsetX, offsetZ));
    }
    
    private double rayTraceDist(final Vec3d start, final double offsetX, final double offsetZ) {
        final RayTraceResult result = SpeedPlus.mc.world.rayTraceBlocks(start, start.add(offsetX, 0.0, offsetZ), false, true, false);
        if (result != null && result.hitVec != null) {
            final double x = start.x - result.hitVec.x;
            final double z = start.z - result.hitVec.z;
            return Math.sqrt(Math.pow(x, 2.0) + Math.pow(z, 2.0));
        }
        return 999.0;
    }
    
    private double calcMoveYaw(final float yaw, final float moveForward, final float moveStrafe) {
        final double moveYaw = (moveForward == 0.0f && moveStrafe == 0.0f) ? 0.0 : (Math.toDegrees(Math.atan2(moveForward, moveStrafe)) - 90.0);
        return Math.toRadians(RotationUtil.normalizeAngle(yaw + moveYaw));
    }
    
    private double calcStepHeight(final double dist, final double motionX, final double motionZ) {
        final BlockPos pos = PlayerUtil.getPlayerPos();
        if (SpeedPlus.mc.world.getBlockState(pos).getCollisionBoundingBox(SpeedPlus.mc.world, pos) != null) {
            return 0.0;
        }
        final double i = (double)Math.max(Math.round(dist), 1L);
        double minStepHeight = Double.MAX_VALUE;
        final double x = motionX * i;
        final double z = motionZ * i;
        minStepHeight = this.checkBox(minStepHeight, x, 0.0);
        minStepHeight = this.checkBox(minStepHeight, 0.0, z);
        return (minStepHeight == Double.MAX_VALUE) ? 0.0 : minStepHeight;
    }
    
    private double checkBox(final double minStepHeight, final double offsetX, final double offsetZ) {
        final AxisAlignedBB box = SpeedPlus.mc.player.boundingBox.offset(offsetX, 0.0, offsetZ);
        if (!SpeedPlus.mc.world.collidesWithAnyBlock(box)) {
            return minStepHeight;
        }
        double stepHeight = minStepHeight;
        for (final double y : new double[] { 0.605, 1.005, 1.505, 2.005, 2.505 }) {
            if (y > minStepHeight) {
                break;
            }
            final AxisAlignedBB stepBox = new AxisAlignedBB(box.minX, box.minY + y - 0.5, box.minZ, box.maxX, box.minY + y, box.maxZ);
            final List<AxisAlignedBB> boxList = SpeedPlus.mc.world.getCollisionBoxes(null, stepBox);
            final AxisAlignedBB maxHeight = boxList.stream().max(Comparator.comparing(bb -> bb.maxY)).orElse(null);
            if (maxHeight != null) {
                final double maxStepHeight = maxHeight.maxY - SpeedPlus.mc.player.posY;
                if (!SpeedPlus.mc.world.collidesWithAnyBlock(box.offset(0.0, maxStepHeight, 0.0))) {
                    stepHeight = maxStepHeight;
                    break;
                }
            }
        }
        return stepHeight;
    }
    
    private double applySpeedPotionEffects() {
        double result = 0.2873;
        if (SpeedPlus.mc.player.getActivePotionEffect(MobEffects.SPEED) != null) {
            result += 0.2873 * (SpeedPlus.mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1.0) * 0.2;
        }
        if (SpeedPlus.mc.player.getActivePotionEffect(MobEffects.SLOWNESS) != null) {
            result -= 0.2873 * (SpeedPlus.mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() + 1.0) * 0.15;
        }
        return result;
    }
    
    private double applyJumpBoostPotionEffects() {
        double result = 0.4;
        if (SpeedPlus.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST) != null) {
            result += (SpeedPlus.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
        }
        return result;
    }
    
    static {
        SpeedPlus.INSTANCE = new SpeedPlus();
    }
}
