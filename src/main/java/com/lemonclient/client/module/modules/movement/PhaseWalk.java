// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.api.util.misc.MathUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.BlockPos;
import java.util.Arrays;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "PhaseWalk", category = Category.Movement)
public class PhaseWalk extends Module
{
    BooleanSetting phaseCheck;
    ModeSetting noClipMode;
    BooleanSetting fallPacket;
    BooleanSetting sprintPacket;
    BooleanSetting instantWalk;
    BooleanSetting antiVoid;
    BooleanSetting clip;
    IntegerSetting antiVoidHeight;
    DoubleSetting instantWalkSpeed;
    DoubleSetting phaseSpeed;
    BooleanSetting downOnShift;
    BooleanSetting stopMotion;
    IntegerSetting stopMotionDelay;
    int delay;
    
    public PhaseWalk() {
        this.phaseCheck = this.registerBoolean("Only In Block", true);
        this.noClipMode = this.registerMode("NoClipMode", Arrays.asList("Bypass", "NoClip", "None", "Fall"), "NoClip");
        this.fallPacket = this.registerBoolean("Fall Packet", true);
        this.sprintPacket = this.registerBoolean("Sprint Packet", true);
        this.instantWalk = this.registerBoolean("Instant Walk", true);
        this.antiVoid = this.registerBoolean("Anti Void", false);
        this.clip = this.registerBoolean("Clip", true);
        this.antiVoidHeight = this.registerInteger("Anti Void Height", 5, 1, 100);
        this.instantWalkSpeed = this.registerDouble("Instant Speed", 1.8, 0.1, 2.0, () -> this.instantWalk.getValue());
        this.phaseSpeed = this.registerDouble("Phase Walk Speed", 42.4, 0.1, 70.0);
        this.downOnShift = this.registerBoolean("Phase Down When Crouch", true);
        this.stopMotion = this.registerBoolean("Attempt Clips", true);
        this.stopMotionDelay = this.registerInteger("Attempt Clips Delay", 5, 0, 20, () -> this.stopMotion.getValue());
    }
    
    public void onDisable() {
        PhaseWalk.mc.player.noClip = false;
    }
    
    private boolean air(final BlockPos pos) {
        final Block blockState = BlockUtil.getBlock(pos);
        return !BlockUtil.airBlocks.contains(blockState) && blockState != Blocks.WEB;
    }
    
    @Override
    public void onUpdate() {
        ++this.delay;
        final double n = this.phaseSpeed.getValue() / 1000.0;
        final double n2 = this.instantWalkSpeed.getValue() / 10.0;
        final RayTraceResult rayTraceBlocks;
        if (this.antiVoid.getValue() && PhaseWalk.mc.player.posY <= this.antiVoidHeight.getValue() && ((rayTraceBlocks = PhaseWalk.mc.world.rayTraceBlocks(PhaseWalk.mc.player.getPositionVector(), new Vec3d(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ), false, false, false)) == null || rayTraceBlocks.typeOfHit != RayTraceResult.Type.BLOCK)) {
            PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
        }
        if (this.phaseCheck.getValue()) {
            if ((PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) && ((!this.eChestCheck() && this.air(PlayerUtil.getPlayerPos())) || this.air(PlayerUtil.getPlayerPos().up()))) {
                if (PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isPressed() && PhaseWalk.mc.player.isSneaking()) {
                    final double[] motion = this.getMotion(n);
                    if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + motion[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    }
                    else {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    }
                    if (this.noClipMode.getValue().equals("Fall")) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                    }
                    if (this.noClipMode.getValue().equals("NoClip")) {
                        PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                        if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                            final double[] directionSpeed = MathUtil.directionSpeed(0.05999999865889549);
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + directionSpeed[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + directionSpeed[1], PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                    }
                    if (this.noClipMode.getValue().equals("Bypass")) {
                        PhaseWalk.mc.player.noClip = true;
                    }
                    if (this.fallPacket.getValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                    }
                    if (this.sprintPacket.getValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    }
                    if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + motion[1]);
                    }
                    else {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion[1]);
                    }
                    PhaseWalk.mc.player.motionZ = 0.0;
                    PhaseWalk.mc.player.motionY = 0.0;
                    PhaseWalk.mc.player.motionX = 0.0;
                    PhaseWalk.mc.player.noClip = true;
                }
                if (PhaseWalk.mc.player.collidedHorizontally && this.clip.getValue() && !PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() && !PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() && !PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown()) {
                    PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown();
                }
                Label_2567: {
                    if (PhaseWalk.mc.player.collidedHorizontally && this.stopMotion.getValue()) {
                        if (this.delay < this.stopMotionDelay.getValue()) {
                            break Label_2567;
                        }
                    }
                    else if (!PhaseWalk.mc.player.collidedHorizontally) {
                        break Label_2567;
                    }
                    final double[] motion2 = this.getMotion(n);
                    if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion2[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + motion2[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    }
                    else {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion2[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion2[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                    }
                    if (this.noClipMode.getValue().equals("Fall")) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                    }
                    if (this.noClipMode.getValue().equals("NoClip")) {
                        PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                        if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                            final double[] directionSpeed2 = MathUtil.directionSpeed(0.05999999865889549);
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + directionSpeed2[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + directionSpeed2[1], PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                        if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                            PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        }
                    }
                    if (this.noClipMode.getValue().equals("Bypass")) {
                        PhaseWalk.mc.player.noClip = true;
                    }
                    if (this.fallPacket.getValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                    }
                    if (this.sprintPacket.getValue()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    }
                    if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion2[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + motion2[1]);
                    }
                    else {
                        PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion2[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion2[1]);
                    }
                    PhaseWalk.mc.player.motionZ = 0.0;
                    PhaseWalk.mc.player.motionY = 0.0;
                    PhaseWalk.mc.player.motionX = 0.0;
                    PhaseWalk.mc.player.noClip = true;
                    this.delay = 0;
                    return;
                }
                if (this.instantWalk.getValue()) {
                    final double[] directionSpeed3 = MathUtil.directionSpeed(n2);
                    PhaseWalk.mc.player.motionX = directionSpeed3[0];
                    PhaseWalk.mc.player.motionZ = directionSpeed3[1];
                }
            }
        }
        else if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isPressed() && PhaseWalk.mc.player.isSneaking()) {
                final double[] motion3 = this.getMotion(n);
                if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion3[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + motion3[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                }
                else {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion3[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion3[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                }
                if (this.noClipMode.getValue().equals("Fall")) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                }
                if (this.noClipMode.getValue().equals("NoClip")) {
                    PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                    if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                        final double[] directionSpeed4 = MathUtil.directionSpeed(0.05999999865889549);
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + directionSpeed4[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + directionSpeed4[1], PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                }
                if (this.noClipMode.getValue().equals("Bypass")) {
                    PhaseWalk.mc.player.noClip = true;
                }
                if (this.fallPacket.getValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                }
                if (this.sprintPacket.getValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion3[0], PhaseWalk.mc.player.posY - 0.0424, PhaseWalk.mc.player.posZ + motion3[1]);
                }
                else {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion3[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion3[1]);
                }
                PhaseWalk.mc.player.motionZ = 0.0;
                PhaseWalk.mc.player.motionY = 0.0;
                PhaseWalk.mc.player.motionX = 0.0;
                PhaseWalk.mc.player.noClip = true;
            }
            Label_4888: {
                if (PhaseWalk.mc.player.collidedHorizontally && this.stopMotion.getValue()) {
                    if (this.delay < this.stopMotionDelay.getValue()) {
                        break Label_4888;
                    }
                }
                else if (!PhaseWalk.mc.player.collidedHorizontally) {
                    break Label_4888;
                }
                final double[] motion4 = this.getMotion(n);
                if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion4[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + motion4[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                }
                else {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX + motion4[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion4[1], PhaseWalk.mc.player.rotationYaw, PhaseWalk.mc.player.rotationPitch, false));
                }
                if (this.noClipMode.getValue().equals("Fall")) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(PhaseWalk.mc.player.posX, -1300.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.rotationYaw * -5.0f, PhaseWalk.mc.player.rotationPitch * -5.0f, true));
                }
                if (this.noClipMode.getValue().equals("NoClip")) {
                    PhaseWalk.mc.player.setVelocity(0.0, 0.0, 0.0);
                    if (PhaseWalk.mc.gameSettings.keyBindForward.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindBack.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindLeft.isKeyDown() || PhaseWalk.mc.gameSettings.keyBindRight.isKeyDown()) {
                        final double[] directionSpeed5 = MathUtil.directionSpeed(0.05999999865889549);
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX + directionSpeed5[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + directionSpeed5[1], PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY - 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                    if (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown()) {
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, PhaseWalk.mc.player.posY + 0.05999999865889549, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                        PhaseWalk.mc.player.connection.sendPacket(new CPacketPlayer.Position(PhaseWalk.mc.player.posX, 0.0, PhaseWalk.mc.player.posZ, PhaseWalk.mc.player.onGround));
                    }
                }
                if (this.noClipMode.getValue().equals("Bypass")) {
                    PhaseWalk.mc.player.noClip = true;
                }
                if (this.fallPacket.getValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                }
                if (this.sprintPacket.getValue()) {
                    PhaseWalk.mc.player.connection.sendPacket(new CPacketEntityAction(PhaseWalk.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                if (this.downOnShift.getValue() && PhaseWalk.mc.player.collidedVertically && PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion4[0], PhaseWalk.mc.player.posY - 0.1, PhaseWalk.mc.player.posZ + motion4[1]);
                }
                else {
                    PhaseWalk.mc.player.setPosition(PhaseWalk.mc.player.posX + motion4[0], PhaseWalk.mc.player.posY, PhaseWalk.mc.player.posZ + motion4[1]);
                }
                PhaseWalk.mc.player.motionZ = 0.0;
                PhaseWalk.mc.player.motionY = 0.0;
                PhaseWalk.mc.player.motionX = 0.0;
                PhaseWalk.mc.player.noClip = true;
                this.delay = 0;
                return;
            }
            if (this.instantWalk.getValue()) {
                final double[] directionSpeed6 = MathUtil.directionSpeed(n2);
                PhaseWalk.mc.player.motionX = directionSpeed6[0];
                PhaseWalk.mc.player.motionZ = directionSpeed6[1];
            }
        }
    }
    
    private boolean eChestCheck() {
        return String.valueOf(PhaseWalk.mc.player.posY).split("\\.")[1].equals("875") || String.valueOf(PhaseWalk.mc.player.posY).split("\\.")[1].equals("5");
    }
    
    private double[] getMotion(final double n) {
        float moveForward = PhaseWalk.mc.player.movementInput.moveForward;
        float moveStrafe = PhaseWalk.mc.player.movementInput.moveStrafe;
        float n2 = PhaseWalk.mc.player.prevRotationYaw + (PhaseWalk.mc.player.rotationYaw - PhaseWalk.mc.player.prevRotationYaw) * PhaseWalk.mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                n2 += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f) {
                n2 += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        return new double[] { moveForward * n * -Math.sin(Math.toRadians(n2)) + moveStrafe * n * Math.cos(Math.toRadians(n2)), moveForward * n * Math.cos(Math.toRadians(n2)) - moveStrafe * n * -Math.sin(Math.toRadians(n2)) };
    }
}
