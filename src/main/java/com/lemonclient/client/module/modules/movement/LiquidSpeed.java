// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.init.MobEffects;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.world.TimerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "LiquidSpeed", category = Category.Movement)
public class LiquidSpeed extends Module
{
    DoubleSetting timerVal;
    DoubleSetting XZWater;
    DoubleSetting upWater;
    DoubleSetting downWater;
    DoubleSetting XZBoostWater;
    DoubleSetting yBoostWater;
    DoubleSetting XZLava;
    DoubleSetting upLava;
    DoubleSetting downLava;
    DoubleSetting XZBoostLava;
    DoubleSetting yBoostLava;
    DoubleSetting jitter;
    BooleanSetting groundIgnore;
    Vec3d[] sides;
    double moveSpeed;
    double motionY;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener;
    
    public LiquidSpeed() {
        this.timerVal = this.registerDouble("Timer Speed", 1.0, 1.0, 2.0);
        this.XZWater = this.registerDouble("XZ Water", 5.75, 0.01, 8.0);
        this.upWater = this.registerDouble("Y+ Water", 2.69, 0.01, 8.0);
        this.downWater = this.registerDouble("Y- Water", 0.8, 0.01, 8.0);
        this.XZBoostWater = this.registerDouble("XZ Boost Water", 6.0, 1.0, 8.0);
        this.yBoostWater = this.registerDouble("Y Boost Water", 2.9, 0.1, 8.0);
        this.XZLava = this.registerDouble("XZ Lava", 3.8, 0.01, 8.0);
        this.upLava = this.registerDouble("Y+ Lava", 2.69, 0.01, 8.0);
        this.downLava = this.registerDouble("Y- Lava", 4.22, 0.01, 8.0);
        this.XZBoostLava = this.registerDouble("XZ Boost Lava", 4.0, 1.0, 8.0);
        this.yBoostLava = this.registerDouble("Y Boost Lava", 2.0, 0.1, 8.0);
        this.jitter = this.registerDouble("Jitter", 1.0, 1.0, 20.0);
        this.groundIgnore = this.registerBoolean("Ground Ignore", true);
        this.sides = new Vec3d[] { new Vec3d(0.3, 0.0, 0.3), new Vec3d(0.3, 0.0, -0.3), new Vec3d(-0.3, 0.0, 0.3), new Vec3d(-0.3, 0.0, -0.3) };
        this.moveSpeed = 0.0;
        this.motionY = 0.0;
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                this.reset();
            }
        }, new Predicate[0]);
        this.playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
            if (LiquidSpeed.mc.player != null && LiquidSpeed.mc.world != null) {
                if (LiquidSpeed.mc.player.isInWater() || LiquidSpeed.mc.player.isInLava()) {
                    if (this.groundIgnore.getValue() || !LiquidSpeed.mc.player.onGround) {
                        if (LiquidSpeed.mc.player.isInWater()) {
                            this.waterSwim(event);
                        }
                        else if (LiquidSpeed.mc.player.isInLava()) {
                            this.lavaSwim(event);
                        }
                        else {
                            this.reset();
                        }
                    }
                    else {
                        this.stopMotion(event);
                        this.reset();
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public void onDisable() {
        this.reset();
    }
    
    private boolean intersect(final BlockPos pos) {
        final AxisAlignedBB box = BlockUtil.getBoundingBox(pos);
        return box != null && LiquidSpeed.mc.player.boundingBox.intersects(box);
    }
    
    private boolean inLiquid(final Material material) {
        final Vec3d vec = LiquidSpeed.mc.player.getPositionVector();
        for (final Vec3d side : this.sides) {
            final BlockPos blockPos = new BlockPos(vec.add(side));
            if (this.intersect(blockPos)) {
                final IBlockState blockState = BlockUtil.getState(blockPos);
                if (!(blockState instanceof BlockLiquid)) {
                    return false;
                }
                if (((BlockLiquid)blockState).material != material) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void lavaSwim(final PlayerMoveEvent moveEvent) {
        this.ySwim(moveEvent, this.yBoostLava.getValue(), this.upLava.getValue(), this.downLava.getValue());
        final boolean jump = LiquidSpeed.mc.player.movementInput.jump;
        final boolean sneak = LiquidSpeed.mc.player.movementInput.sneak;
        if ((!jump || !sneak) && (jump || sneak)) {
            TimerUtils.setTimerSpeed(this.timerVal.getValue().floatValue());
        }
        else {
            TimerUtils.setTimerSpeed(1.0f);
        }
        if (LiquidSpeed.mc.player.movementInput.moveForward != 0.0f || LiquidSpeed.mc.player.movementInput.moveStrafe != 0.0f) {
            final double yaw = MotionUtil.calcMoveYaw();
            this.moveSpeed = Math.min(Math.max(this.moveSpeed * this.XZBoostLava.getValue(), 0.05), this.XZLava.getValue() / 20.0);
            moveEvent.setX(-Math.sin(yaw) * this.moveSpeed);
            moveEvent.setZ(Math.cos(yaw) * this.moveSpeed);
        }
        else {
            this.stopMotion(moveEvent);
        }
    }
    
    private void waterSwim(final PlayerMoveEvent moveEvent) {
        this.ySwim(moveEvent, this.yBoostWater.getValue(), this.upWater.getValue(), this.downWater.getValue() * 20.0);
        final boolean jump = LiquidSpeed.mc.player.movementInput.jump;
        final boolean sneak = LiquidSpeed.mc.player.movementInput.sneak;
        if ((!jump || !sneak) && (jump || sneak)) {
            TimerUtils.setTimerSpeed(this.timerVal.getValue().floatValue());
        }
        else {
            TimerUtils.setTimerSpeed(1.0f);
        }
        if (LiquidSpeed.mc.player.movementInput.moveForward != 0.0f || LiquidSpeed.mc.player.movementInput.moveStrafe != 0.0f) {
            final double yaw = MotionUtil.calcMoveYaw();
            final double multiplier = this.applySpeedPotionEffects();
            this.moveSpeed = Math.min(Math.max(this.moveSpeed * this.XZBoostWater.getValue(), 0.075), this.XZWater.getValue() / 20.0);
            if (LiquidSpeed.mc.player.movementInput.sneak && !LiquidSpeed.mc.player.movementInput.jump) {
                final double downMotion = LiquidSpeed.mc.player.motionY * 0.25;
                this.moveSpeed = Math.min(this.moveSpeed, Math.max(this.moveSpeed + downMotion, 0.0));
            }
            this.moveSpeed *= multiplier;
            moveEvent.setX(-Math.sin(yaw) * this.moveSpeed);
            moveEvent.setZ(Math.cos(yaw) * this.moveSpeed);
        }
        else {
            this.stopMotion(moveEvent);
        }
    }
    
    private double applySpeedPotionEffects() {
        double result = 1.0;
        if (LiquidSpeed.mc.player.getActivePotionEffect(MobEffects.SPEED) != null) {
            result += (LiquidSpeed.mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1.0) * 0.2;
        }
        if (LiquidSpeed.mc.player.getActivePotionEffect(MobEffects.SLOWNESS) != null) {
            result -= (LiquidSpeed.mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() + 1.0) * 0.15;
        }
        return result;
    }
    
    private void ySwim(final PlayerMoveEvent moveEvent, final double vBoost, final double upSpeed, final double downSpeed) {
        final boolean jump = LiquidSpeed.mc.player.movementInput.jump;
        final boolean sneak = LiquidSpeed.mc.player.movementInput.sneak;
        this.motionY = Math.pow(0.1, this.jitter.getValue());
        if (!jump || !sneak) {
            if (jump) {
                this.motionY = Math.min(this.motionY + vBoost / 20.0, upSpeed / 20.0);
            }
            if (sneak) {
                this.motionY = Math.max(this.motionY - vBoost / 20.0, -downSpeed / 20.0);
            }
        }
        moveEvent.setY(this.motionY);
    }
    
    private void stopMotion(final PlayerMoveEvent event) {
        event.setX(0.0);
        event.setZ(0.0);
        this.moveSpeed = 0.0;
    }
    
    private void reset() {
        this.moveSpeed = 0.0;
        this.motionY = 0.0;
    }
}
