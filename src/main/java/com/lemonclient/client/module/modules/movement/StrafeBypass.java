// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.MotionUtil;
import net.minecraft.entity.Entity;
import com.lemonclient.api.event.LemonClientEvent;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import com.lemonclient.client.module.ModuleManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import java.util.Arrays;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.event.events.PlayerJumpEvent;
import com.lemonclient.api.event.events.MotionUpdateEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "StrafeBypass", category = Category.Movement, priority = 999)
public class StrafeBypass extends Module
{
    ModeSetting mode;
    BooleanSetting boost;
    BooleanSetting randomBoost;
    BooleanSetting debug;
    public Timing rdBoostTimer;
    public float boostFactor;
    public long detectionTime;
    public boolean lagDetected;
    public double boostSpeed;
    public int stage;
    private double lastDist;
    private double moveSpeed;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<MotionUpdateEvent> motionUpdateEventListener;
    @EventHandler
    private final Listener<PlayerJumpEvent> jumpEventListener;
    @EventHandler
    private final Listener<PlayerMoveEvent> moveEventListener;
    
    public StrafeBypass() {
        this.mode = this.registerMode("Mode", Arrays.asList("Strict", "Normal"), "Normal");
        this.boost = this.registerBoolean("DamageBoost", false);
        this.randomBoost = this.registerBoolean("RandomBoost", false);
        this.debug = this.registerBoolean("Debug", false);
        this.rdBoostTimer = new Timing();
        this.boostFactor = 4.0f;
        this.stage = 1;
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (StrafeBypass.mc.world == null || StrafeBypass.mc.player == null || StrafeBypass.mc.player.isDead) {
            }
            else {
                if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).getEntityID() == StrafeBypass.mc.player.getEntityId() && !ModuleManager.getModule(SpeedPlus.class).isEnabled()) {
                    this.boostSpeed = Math.max(Math.hypot(((SPacketEntityVelocity)event.getPacket()).motionX / 8000.0f, ((SPacketEntityVelocity)event.getPacket()).motionZ / 8000.0f), this.boostSpeed);
                }
                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    this.detectionTime = System.currentTimeMillis();
                    this.lagDetected = true;
                    this.rdBoostTimer.reset();
                    this.boostFactor = 6.0f;
                }
            }
        }, new Predicate[0]);
        this.motionUpdateEventListener = new Listener<MotionUpdateEvent>(event -> {
            if (StrafeBypass.mc.world == null || StrafeBypass.mc.player == null || StrafeBypass.mc.player.isDead) {
            }
            else if (event.getEra() != LemonClientEvent.Era.PRE) {
            }
            else {
                if (System.currentTimeMillis() - this.detectionTime > 3182L) {
                    this.lagDetected = false;
                }
                if (event.stage == 1) {
                    this.lastDist = Math.sqrt((StrafeBypass.mc.player.posX - StrafeBypass.mc.player.prevPosX) * (StrafeBypass.mc.player.posX - StrafeBypass.mc.player.prevPosX) + (StrafeBypass.mc.player.posZ - StrafeBypass.mc.player.prevPosZ) * (StrafeBypass.mc.player.posZ - StrafeBypass.mc.player.prevPosZ));
                }
            }
        }, new Predicate[0]);
        this.jumpEventListener = new Listener<PlayerJumpEvent>(event -> {
            if (StrafeBypass.mc.world == null || StrafeBypass.mc.player == null || StrafeBypass.mc.player.isDead) {
            }
            else {
                if (!StrafeBypass.mc.player.isInWater() && !StrafeBypass.mc.player.isInLava()) {
                    event.cancel();
                }
            }
        }, new Predicate[0]);
        this.moveEventListener = new Listener<PlayerMoveEvent>(event -> {
            if (StrafeBypass.mc.world != null && StrafeBypass.mc.player != null && !StrafeBypass.mc.player.isDead) {
                if (!StrafeBypass.mc.player.isInWater() && !StrafeBypass.mc.player.isInLava()) {
                    if (StrafeBypass.mc.player.movementInput.moveForward == 0.0 && StrafeBypass.mc.player.movementInput.moveStrafe == 0.0) {
                        event.setX(0.0);
                        event.setZ(0.0);
                        event.setSpeed(0.0);
                    }
                    else {
                        if (StrafeBypass.mc.player.onGround) {
                            this.stage = 2;
                        }
                        switch (this.stage) {
                            case 0: {
                                ++this.stage;
                                this.lastDist = 0.0;
                                break;
                            }
                            case 3: {
                                this.moveSpeed = this.lastDist - (this.mode.getValue().equals("Normal") ? 0.6896 : 0.795) * (this.lastDist - this.getBaseMoveSpeed());
                                break;
                            }
                            default: {
                                if ((!StrafeBypass.mc.world.getCollisionBoxes(StrafeBypass.mc.player, StrafeBypass.mc.player.getEntityBoundingBox().offset(0.0, StrafeBypass.mc.player.motionY, 0.0)).isEmpty() || StrafeBypass.mc.player.collidedVertically) && this.stage > 0) {
                                    this.stage = ((StrafeBypass.mc.player.moveForward != 0.0f || StrafeBypass.mc.player.moveStrafing != 0.0f) ? 1 : 0);
                                }
                                this.moveSpeed = this.lastDist - this.lastDist / 159.0;
                                break;
                            }
                        }
                        if (this.boost.getValue() && this.boostSpeed != 0.0 && MotionUtil.moving(StrafeBypass.mc.player)) {
                            this.moveSpeed += this.boostSpeed;
                            this.boostSpeed = 0.0;
                        }
                        if (this.randomBoost.getValue() && this.rdBoostTimer.passedMs(3500L) && !this.lagDetected && MotionUtil.moving(StrafeBypass.mc.player) && StrafeBypass.mc.player.onGround) {
                            this.moveSpeed += this.moveSpeed / this.boostFactor;
                            if (this.debug.getValue()) {
                                MessageBus.sendClientPrefixMessage("RandomBoost", Notification.Type.INFO);
                            }
                            this.boostFactor = 4.0f;
                            this.rdBoostTimer.reset();
                        }
                        if (!StrafeBypass.mc.gameSettings.keyBindJump.isKeyDown() && StrafeBypass.mc.player.onGround) {
                            this.moveSpeed = this.getBaseMoveSpeed();
                        }
                        else {
                            this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
                        }
                        if (StrafeBypass.mc.player.movementInput.moveForward != 0.0 && StrafeBypass.mc.player.movementInput.moveStrafe != 0.0) {
                            final MovementInput movementInput = StrafeBypass.mc.player.movementInput;
                            movementInput.moveForward *= (float)Math.sin(0.7853981633974483);
                            final MovementInput movementInput2 = StrafeBypass.mc.player.movementInput;
                            movementInput2.moveStrafe *= (float)Math.cos(0.7853981633974483);
                        }
                        event.setX((StrafeBypass.mc.player.movementInput.moveForward * this.moveSpeed * -Math.sin(Math.toRadians(StrafeBypass.mc.player.rotationYaw)) + StrafeBypass.mc.player.movementInput.moveStrafe * this.moveSpeed * Math.cos(Math.toRadians(StrafeBypass.mc.player.rotationYaw))) * (this.mode.getValue().equals("Normal") ? 0.993 : 0.99));
                        event.setZ((StrafeBypass.mc.player.movementInput.moveForward * this.moveSpeed * Math.cos(Math.toRadians(StrafeBypass.mc.player.rotationYaw)) - StrafeBypass.mc.player.movementInput.moveStrafe * this.moveSpeed * -Math.sin(Math.toRadians(StrafeBypass.mc.player.rotationYaw))) * (this.mode.getValue().equals("Normal") ? 0.993 : 0.99));
                        ++this.stage;
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public double getBaseMoveSpeed() {
        double result = 0.2873;
        if (StrafeBypass.mc.player.getActivePotionEffect(MobEffects.SPEED) != null) {
            result += 0.2873 * (StrafeBypass.mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1.0) * 0.2;
        }
        if (StrafeBypass.mc.player.getActivePotionEffect(MobEffects.SLOWNESS) != null) {
            result -= 0.2873 * (StrafeBypass.mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() + 1.0) * 0.15;
        }
        return result;
    }
}
