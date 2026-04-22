// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import java.util.Objects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.Vec3d;
import java.math.RoundingMode;
import java.math.BigDecimal;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.player.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.client.module.ModuleManager;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.event.events.MotionUpdateEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "TargetStrafe", category = Category.Movement)
public class TargetStrafe extends Module
{
    IntegerSetting range;
    BooleanSetting jump;
    BooleanSetting antiStuck;
    DoubleSetting distanceSetting;
    DoubleSetting maxDistance;
    DoubleSetting turnAmount;
    String pattern;
    Timing lagBackCoolDown;
    Timing boostTimer;
    long detectionTime;
    boolean checkCoolDown;
    double boostSpeed;
    double boostSpeed2;
    double lastDist;
    int level;
    double moveSpeed;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<MotionUpdateEvent> motionUpdateEventListener;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener;
    int direction;
    
    public TargetStrafe() {
        this.range = this.registerInteger("TargetRange", 20, 0, 256);
        this.jump = this.registerBoolean("Jump", true);
        this.antiStuck = this.registerBoolean("AntiStuck", true);
        this.distanceSetting = this.registerDouble("PreferredDistance", 1.0, 0.0, 10.0);
        this.maxDistance = this.registerDouble("MaxDistance", 10.0, 1.0, 32.0);
        this.turnAmount = this.registerDouble("TurnAmount", 5.0, 1.0, 90.0);
        this.pattern = "%.1f";
        this.lagBackCoolDown = new Timing();
        this.boostTimer = new Timing();
        this.checkCoolDown = false;
        this.level = 1;
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (TargetStrafe.mc.world == null || TargetStrafe.mc.player == null || TargetStrafe.mc.player.isDead) {
            }
            else {
                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    this.lastDist = 0.0;
                    this.moveSpeed = Math.min(this.getBaseMoveSpeed(), this.getBaseMoveSpeed());
                    this.detectionTime = System.currentTimeMillis();
                    if (!this.checkCoolDown) {
                        this.lagBackCoolDown.reset();
                        this.checkCoolDown = true;
                    }
                }
                if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == TargetStrafe.mc.player.getEntityId()) {
                    this.boostSpeed = Math.hypot(((SPacketEntityVelocity)event.getPacket()).motionX / 8000.0f, ((SPacketEntityVelocity)event.getPacket()).motionZ / 8000.0f);
                    this.boostSpeed2 = this.boostSpeed;
                }
            }
        }, new Predicate[0]);
        this.motionUpdateEventListener = new Listener<MotionUpdateEvent>(event -> {
            if (TargetStrafe.mc.world == null || TargetStrafe.mc.player == null || TargetStrafe.mc.player.isDead) {
            }
            else if (ModuleManager.getModule(HoleSnap.class).isEnabled()) {
            }
            else {
                try {
                    if (this.lagBackCoolDown.passedMs((long)Double.parseDouble(String.format(this.pattern, 1000.0)))) {
                        this.checkCoolDown = false;
                        this.lagBackCoolDown.reset();
                    }
                    if (event.stage == 1) {
                        this.lastDist = Math.sqrt((TargetStrafe.mc.player.posX - TargetStrafe.mc.player.prevPosX) * (TargetStrafe.mc.player.posX - TargetStrafe.mc.player.prevPosX) + (TargetStrafe.mc.player.posZ - TargetStrafe.mc.player.prevPosZ) * (TargetStrafe.mc.player.posZ - TargetStrafe.mc.player.prevPosZ));
                    }
                }
                catch (final NumberFormatException ex) {}
            }
        }, new Predicate[0]);
        this.playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
            if (TargetStrafe.mc.world == null || TargetStrafe.mc.player == null || TargetStrafe.mc.player.isDead) {
            }
            else {
                final EntityPlayer target = PlayerUtil.getNearestPlayer(this.range.getValue());
                if (target != null) {
                    if (!TargetStrafe.mc.player.isInLava() && !TargetStrafe.mc.player.isInWater() && !TargetStrafe.mc.player.isInWeb) {
                        if (TargetStrafe.mc.player.onGround) {
                            this.level = 2;
                        }
                        if (round(TargetStrafe.mc.player.posY - (int)TargetStrafe.mc.player.posY, 3) == round(0.138, 3) && this.jump.getValue()) {
                            final EntityPlayerSP player3;
                            final EntityPlayerSP player = player3 = TargetStrafe.mc.player;
                            player3.motionY -= 0.07;
                            event.setY(event.getY() - 0.08316090325960147);
                            final EntityPlayerSP player4;
                            final EntityPlayerSP player2 = player4 = TargetStrafe.mc.player;
                            player4.posY -= 0.08316090325960147;
                        }
                        if (this.level != 1 || (TargetStrafe.mc.player.moveForward == 0.0f && TargetStrafe.mc.player.moveStrafing == 0.0f)) {
                            if (this.level == 2) {
                                this.level = 3;
                                if (MotionUtil.moving(TargetStrafe.mc.player)) {
                                    if (!TargetStrafe.mc.player.isInLava() && TargetStrafe.mc.player.onGround && this.jump.getValue()) {
                                        final EntityPlayerSP player5 = TargetStrafe.mc.player;
                                        final double motionY = 0;
                                        event.setY(player5.motionY = motionY);
                                    }
                                    this.moveSpeed *= 1.433;
                                }
                            }
                            else if (this.level == 3) {
                                this.level = 4;
                                this.moveSpeed = this.lastDist - 0.6553 * (this.lastDist - this.getBaseMoveSpeed() + 0.04);
                            }
                            else {
                                if (TargetStrafe.mc.player.onGround && (TargetStrafe.mc.world.getCollisionBoxes(TargetStrafe.mc.player, TargetStrafe.mc.player.boundingBox.offset(0.0, TargetStrafe.mc.player.motionY, 0.0)).size() > 0 || TargetStrafe.mc.player.collidedVertically)) {
                                    this.level = 1;
                                }
                                this.moveSpeed = this.lastDist - this.lastDist / 201.0;
                            }
                        }
                        else {
                            this.level = 2;
                            this.moveSpeed = 1.418 * this.getBaseMoveSpeed();
                        }
                        if (MotionUtil.moving(TargetStrafe.mc.player) && this.boostSpeed2 != 0.0) {
                            if (this.boostTimer.passedMs(1L)) {
                                this.moveSpeed = this.boostSpeed2;
                                this.boostTimer.reset();
                            }
                            this.boostSpeed2 = 0.0;
                        }
                        this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
                        if (TargetStrafe.mc.player.collidedHorizontally && this.antiStuck.getValue()) {
                            this.switchDirection();
                        }
                        this.doStrafeAtSpeed(event, RotationUtil.getRotationTo(target.getPositionVector()).x, target.getPositionVector());
                    }
                }
            }
        }, -100, new Predicate[0]);
        this.direction = 1;
    }
    
    public static double round(final double n, final int n2) {
        if (n2 < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(n).setScale(n2, RoundingMode.HALF_UP).doubleValue();
    }
    
    private void switchDirection() {
        this.direction = -this.direction;
    }
    
    private void doStrafeAtSpeed(final PlayerMoveEvent event, final float rotation, final Vec3d target) {
        float rotationYaw = rotation + 90.0f * this.direction;
        final double disX = TargetStrafe.mc.player.posX - target.x;
        final double disZ = TargetStrafe.mc.player.posZ - target.z;
        final double distance = Math.sqrt(disX * disX + disZ * disZ);
        if (distance < this.maxDistance.getValue()) {
            if (distance > this.distanceSetting.getValue()) {
                rotationYaw -= (float)(this.turnAmount.getValue() * this.direction);
            }
            else if (distance < this.distanceSetting.getValue()) {
                rotationYaw += (float)(this.turnAmount.getValue() * this.direction);
            }
        }
        else {
            rotationYaw = rotation;
        }
        if (this.jump.getValue() && TargetStrafe.mc.player.onGround) {
            TargetStrafe.mc.player.jump();
        }
        event.setX(this.moveSpeed * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
        event.setZ(this.moveSpeed * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
    }
    
    public double getBaseMoveSpeed() {
        double n = 0.2873;
        if (TargetStrafe.mc.player.isPotionActive(MobEffects.SPEED)) {
            n *= 1.0 + 0.2 * (Objects.requireNonNull(TargetStrafe.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        }
        return n;
    }
}
