// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.List;
import com.lemonclient.api.util.world.HoleUtil;
import net.minecraft.util.NonNullList;
import java.util.Comparator;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.block.BlockLiquid;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.player.RotationUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.TimerUtils;
import java.util.function.Predicate;
import net.minecraft.util.MovementInputFromOptions;
import java.util.Arrays;
import com.lemonclient.api.event.events.StepEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.InputUpdateEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoChase", category = Category.Dev, priority = 120)
public class AutoChase extends Module
{
    IntegerSetting targetRange;
    IntegerSetting fixedRange;
    IntegerSetting cancelRange;
    IntegerSetting downRange;
    IntegerSetting upRange;
    DoubleSetting hRange;
    DoubleSetting timer;
    DoubleSetting speed;
    BooleanSetting step;
    ModeSetting mode;
    ModeSetting height;
    ModeSetting vHeight;
    BooleanSetting abnormal;
    IntegerSetting centerSpeed;
    BooleanSetting only;
    BooleanSetting single;
    BooleanSetting twoBlocks;
    BooleanSetting custom;
    BooleanSetting four;
    BooleanSetting near;
    BooleanSetting disable;
    BooleanSetting hud;
    private int stuckTicks;
    BlockPos originPos;
    BlockPos startPos;
    boolean isActive;
    boolean wasInHole;
    boolean slowDown;
    double playerSpeed;
    EntityPlayer target;
    @EventHandler
    private final Listener<InputUpdateEvent> inputUpdateEventListener;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveListener;
    double[] pointFiveToOne;
    double[] one;
    double[] oneFive;
    double[] oneSixTwoFive;
    double[] oneEightSevenFive;
    double[] two;
    double[] twoFive;
    double[] threeStep;
    double[] fourStep;
    double[] betaShared;
    double[] betaTwo;
    double[] betaTwoFive;
    @EventHandler
    private final Listener<StepEvent> stepEventListener;
    
    public AutoChase() {
        this.targetRange = this.registerInteger("Target Range", 16, 0, 256);
        this.fixedRange = this.registerInteger("Fixed Target Range", 16, 0, 256);
        this.cancelRange = this.registerInteger("Cancel Range", 6, 0, 16);
        this.downRange = this.registerInteger("Down Range", 5, 0, 8);
        this.upRange = this.registerInteger("Up Range", 1, 0, 8);
        this.hRange = this.registerDouble("H Range", 4.0, 1.0, 8.0);
        this.timer = this.registerDouble("Timer", 2.0, 1.0, 50.0);
        this.speed = this.registerDouble("Speed", 2.0, 0.0, 10.0);
        this.step = this.registerBoolean("Step", true);
        this.mode = this.registerMode("Mode", Arrays.asList("NCP", "Vanilla"), "NCP", () -> this.step.getValue());
        this.height = this.registerMode("NCP Height", Arrays.asList("1", "1.5", "2", "2.5", "3", "4"), "2.5", () -> this.mode.getValue().equalsIgnoreCase("NCP") && this.step.getValue());
        this.vHeight = this.registerMode("Vanilla Height", Arrays.asList("1", "1.5", "2", "2.5", "3", "4"), "2.5", () -> this.mode.getValue().equalsIgnoreCase("Vanilla") && this.step.getValue());
        this.abnormal = this.registerBoolean("Abnormal", false, () -> !this.mode.getValue().equalsIgnoreCase("Vanilla") && this.step.getValue());
        this.centerSpeed = this.registerInteger("Center Speed", 2, 10, 1);
        this.only = this.registerBoolean("Only 1x1", true);
        this.single = this.registerBoolean("Single Hole", true, () -> !this.only.getValue());
        this.twoBlocks = this.registerBoolean("Double Hole", true, () -> !this.only.getValue());
        this.custom = this.registerBoolean("Custom Hole", true, () -> !this.only.getValue());
        this.four = this.registerBoolean("Four Blocks", true, () -> !this.only.getValue());
        this.near = this.registerBoolean("Near Target", true);
        this.disable = this.registerBoolean("Disable", true);
        this.hud = this.registerBoolean("Hud", true);
        this.stuckTicks = 0;
        this.inputUpdateEventListener = new Listener<InputUpdateEvent>(event -> {
            if (event.getMovementInput() instanceof MovementInputFromOptions && this.isActive) {
                event.getMovementInput().jump = false;
                event.getMovementInput().sneak = false;
                event.getMovementInput().forwardKeyDown = false;
                event.getMovementInput().backKeyDown = false;
                event.getMovementInput().leftKeyDown = false;
                event.getMovementInput().rightKeyDown = false;
                event.getMovementInput().moveForward = 0.0f;
                event.getMovementInput().moveStrafe = 0.0f;
            }
        }, new Predicate[0]);
        this.playerMoveListener = new Listener<PlayerMoveEvent>(event -> {
            this.isActive = false;
            TimerUtils.setTickLength(50.0f);
            if (!AutoChase.mc.player.isEntityAlive() || AutoChase.mc.player.isElytraFlying() || AutoChase.mc.player.capabilities.isFlying) {
            }
            else {
                final double currentSpeed = Math.hypot(AutoChase.mc.player.motionX, AutoChase.mc.player.motionZ);
                if (currentSpeed <= 0.05) {
                    this.originPos = PlayerUtil.getPlayerPos();
                }
                this.target = this.getNearestPlayer(this.target);
                if (this.target == null) {
                }
                else {
                    final double range = AutoChase.mc.player.getDistance(this.target);
                    final boolean inRange = range <= this.cancelRange.getValue();
                    if (this.shouldDisable(currentSpeed, inRange)) {
                        if (this.disable.getValue()) {
                            this.disable();
                        }
                    }
                    else {
                        final BlockPos hole = this.findHoles(this.target, inRange);
                        if (hole != null) {
                            final double x = hole.getX() + 0.5;
                            final double y = hole.getY();
                            final double z = hole.getZ() + 0.5;
                            if (this.checkYRange((int)AutoChase.mc.player.posY, this.originPos.y)) {
                                final Vec3d playerPos = AutoChase.mc.player.getPositionVector();
                                final double yawRad = Math.toRadians(RotationUtil.getRotationTo(playerPos, new Vec3d(x, y, z)).x);
                                final double dist = Math.hypot(x - playerPos.x, z - playerPos.z);
                                if (AutoChase.mc.player.onGround) {
                                    this.playerSpeed = MotionUtil.getBaseMoveSpeed() * ((EntityUtil.isColliding(0.0, -0.5, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.91 : this.speed.getValue());
                                    this.slowDown = true;
                                }
                                final double speed = Math.min(dist, this.playerSpeed);
                                AutoChase.mc.player.motionX = 0.0;
                                AutoChase.mc.player.motionZ = 0.0;
                                event.setX(-Math.sin(yawRad) * speed);
                                event.setZ(Math.cos(yawRad) * speed);
                                if (speed != 0.0 && (-Math.sin(yawRad) != 0.0 || Math.cos(yawRad) != 0.0)) {
                                    TimerUtils.setTickLength((float)(50.0 / this.timer.getValue()));
                                    this.isActive = true;
                                }
                            }
                        }
                        if (AutoChase.mc.player.collidedHorizontally && hole == null) {
                            ++this.stuckTicks;
                        }
                        else {
                            this.stuckTicks = 0;
                        }
                    }
                }
            }
        }, new Predicate[0]);
        this.pointFiveToOne = new double[] { 0.41999998688698 };
        this.one = new double[] { 0.41999998688698, 0.7531999805212 };
        this.oneFive = new double[] { 0.42, 0.753, 1.001, 1.084, 1.006 };
        this.oneSixTwoFive = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372 };
        this.oneEightSevenFive = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652 };
        this.two = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869 };
        this.twoFive = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };
        this.threeStep = new double[] { 0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43 };
        this.fourStep = new double[] { 0.42, 0.75, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43, 2.78, 2.63, 2.51, 2.9, 3.21, 3.45, 3.43 };
        this.betaShared = new double[] { 0.419999986887, 0.7531999805212, 1.0013359791121, 1.1661092609382, 1.249187078744682, 1.176759275064238 };
        this.betaTwo = new double[] { 1.596759261951216, 1.929959255585439 };
        this.betaTwoFive = new double[] { 1.596759261951216, 1.929959255585439, 2.178095254176385, 2.3428685360024515, 2.425946353808919 };
        this.stepEventListener = new Listener<StepEvent>(event -> {
            if (this.canStep()) {
                final double step = event.getBB().minY - AutoChase.mc.player.posY;
                if (!this.mode.getValue().equalsIgnoreCase("Vanilla")) {
                    if (this.mode.getValue().equalsIgnoreCase("NCP")) {
                        if (step == 0.625 && this.abnormal.getValue()) {
                            this.sendOffsets(this.pointFiveToOne);
                        }
                        else if (step == 1.0 || ((step == 0.875 || step == 1.0625 || step == 0.9375) && this.abnormal.getValue())) {
                            this.sendOffsets(this.one);
                        }
                        else if (step == 1.5) {
                            this.sendOffsets(this.oneFive);
                        }
                        else if (step == 1.875 && this.abnormal.getValue()) {
                            this.sendOffsets(this.oneEightSevenFive);
                        }
                        else if (step == 1.625 && this.abnormal.getValue()) {
                            this.sendOffsets(this.oneSixTwoFive);
                        }
                        else if (step == 2.0) {
                            this.sendOffsets(this.two);
                        }
                        else if (step == 2.5) {
                            this.sendOffsets(this.twoFive);
                        }
                        else if (step == 3.0) {
                            this.sendOffsets(this.threeStep);
                        }
                        else if (step == 4.0) {
                            this.sendOffsets(this.fourStep);
                        }
                        else {
                            event.cancel();
                        }
                    }
                    else if (this.mode.getValue().equalsIgnoreCase("Beta")) {
                        if (step == 1.5) {
                            this.sendOffsets(this.betaShared);
                        }
                        else if (step == 2.0) {
                            this.sendOffsets(this.betaShared);
                            this.sendOffsets(this.betaTwo);
                        }
                        else if (step == 2.5) {
                            this.sendOffsets(this.betaShared);
                            this.sendOffsets(this.betaTwoFive);
                        }
                        else if (step == 3.0) {
                            this.sendOffsets(this.betaShared);
                            this.sendOffsets(this.threeStep);
                        }
                        else if (step == 4.0) {
                            this.sendOffsets(this.betaShared);
                            this.sendOffsets(this.fourStep);
                        }
                        else {
                            event.cancel();
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    private EntityPlayer getNearestPlayer(final EntityPlayer target) {
        if (target != null && AutoChase.mc.player.getDistance(target) <= this.fixedRange.getValue() && !EntityUtil.basicChecksEntity(target)) {
            return target;
        }
        return AutoChase.mc.world.playerEntities.stream().filter(p -> AutoChase.mc.player.getDistance(p) <= this.targetRange.getValue()).filter(p -> AutoChase.mc.player.entityId != p.entityId).filter(p -> !EntityUtil.basicChecksEntity(p)).min(Comparator.comparing(p -> AutoChase.mc.player.getDistance(p))).orElse(null);
    }
    
    public void onEnable() {
        this.wasInHole = false;
        final BlockPos playerPos = PlayerUtil.getPlayerPos();
        this.originPos = playerPos;
        this.startPos = playerPos;
    }
    
    @Override
    public void onUpdate() {
        if (AutoChase.mc.world == null || AutoChase.mc.player == null || AutoChase.mc.player.isDead || this.startPos == null) {
            this.disable();
            return;
        }
        if (this.canStep()) {
            AutoChase.mc.player.stepHeight = this.getHeight(this.mode.getValue());
        }
        else {
            if (AutoChase.mc.player.getRidingEntity() != null) {
                AutoChase.mc.player.getRidingEntity().stepHeight = 1.0f;
            }
            AutoChase.mc.player.stepHeight = 0.6f;
        }
        if (this.target == null) {
            this.isActive = false;
        }
    }
    
    public void onDisable() {
        this.isActive = false;
        this.stuckTicks = 0;
        TimerUtils.setTickLength(50.0f);
        if (AutoChase.mc.player != null) {
            if (AutoChase.mc.player.getRidingEntity() != null) {
                AutoChase.mc.player.getRidingEntity().stepHeight = 1.0f;
            }
            AutoChase.mc.player.stepHeight = 0.6f;
        }
    }
    
    private BlockPos findHoles(final EntityPlayer target, final boolean inRange) {
        if (inRange && this.wasInHole) {
            return null;
        }
        this.wasInHole = false;
        final NonNullList<BlockPos> holes = NonNullList.create();
        final List<BlockPos> blockPosList = EntityUtil.getSphere(EntityUtil.getPlayerPos(target), this.hRange.getValue(), 8.0, false, true, 0);
        blockPosList.forEach(pos -> {
            if (!this.checkYRange((int)AutoChase.mc.player.posY, pos.y)) {
            }
            else if (!AutoChase.mc.world.isAirBlock(PlayerUtil.getPlayerPos().up(2)) && (int)AutoChase.mc.player.posY < pos.y) {
            }
            else {
                final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(pos, this.only.getValue(), false, false);
                final HoleUtil.HoleType holeType = holeInfo.getType();
                if (holeType != HoleUtil.HoleType.NONE) {
                    if (this.only.getValue()) {
                        if (holeType != HoleUtil.HoleType.SINGLE) {
                            return;
                        }
                    }
                    else if (!this.single.getValue() && holeType == HoleUtil.HoleType.SINGLE) {
                        return;
                    }
                    else if (!this.twoBlocks.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                        return;
                    }
                    else if (!this.custom.getValue() && holeType == HoleUtil.HoleType.CUSTOM) {
                        return;
                    }
                    else if (!this.four.getValue() && holeType == HoleUtil.HoleType.FOUR) {
                        return;
                    }
                    if (AutoChase.mc.world.isAirBlock(pos) && AutoChase.mc.world.isAirBlock(pos.add(0, 1, 0)) && AutoChase.mc.world.isAirBlock(pos.add(0, 2, 0))) {
                        for (int high = 0; high < AutoChase.mc.player.posY - pos.y; ++high) {
                            if (high != 0) {
                                if (AutoChase.mc.player.posY > pos.y) {
                                    if (!AutoChase.mc.world.isAirBlock(new BlockPos(pos.x, pos.y + high, pos.z))) {
                                        return;
                                    }
                                }
                                if (AutoChase.mc.player.posY < pos.y) {
                                    final BlockPos newPos = new BlockPos(pos.x, pos.y + high, pos.z);
                                    if (AutoChase.mc.world.isAirBlock(newPos) && (AutoChase.mc.world.isAirBlock(newPos.down()) || AutoChase.mc.world.isAirBlock(newPos.up()))) {
                                        return;
                                    }
                                }
                            }
                        }
                        holes.add(pos);
                    }
                }
            }
        });
        return holes.stream().min(Comparator.comparing(p -> this.near.getValue() ? target.getDistance(p.x + 0.5, p.y, p.z + 0.5) : AutoChase.mc.player.getDistance(p.x + 0.5, p.y, p.z + 0.5))).orElse(null);
    }
    
    private boolean shouldDisable(final Double currentSpeed, final boolean inRange) {
        if (this.isActive) {
            return false;
        }
        if (!AutoChase.mc.player.onGround) {
            return false;
        }
        if (this.stuckTicks > 5 && currentSpeed < 0.05) {
            return true;
        }
        final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(new BlockPos(PlayerUtil.getPlayerPos().x, PlayerUtil.getPlayerPos().y + 0.5, PlayerUtil.getPlayerPos().z), false, false, false);
        final HoleUtil.HoleType holeType = holeInfo.getType();
        if (holeType != HoleUtil.HoleType.NONE && inRange) {
            if (this.only.getValue()) {
                if (holeType != HoleUtil.HoleType.SINGLE) {
                    return false;
                }
            }
            else {
                if (!this.single.getValue() && holeType == HoleUtil.HoleType.SINGLE) {
                    return false;
                }
                if (!this.twoBlocks.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                    return false;
                }
                if (!this.custom.getValue() && holeType == HoleUtil.HoleType.CUSTOM) {
                    return false;
                }
                if (!this.four.getValue() && holeType == HoleUtil.HoleType.FOUR) {
                    return false;
                }
            }
            final Vec3d center = this.getCenter(holeInfo.getCentre());
            final double XDiff = Math.abs(center.x - AutoChase.mc.player.posX);
            final double ZDiff = Math.abs(center.z - AutoChase.mc.player.posZ);
            if ((XDiff > 0.3 || ZDiff > 0.3) && !this.wasInHole) {
                final double MotionX = center.x - AutoChase.mc.player.posX;
                final double MotionZ = center.z - AutoChase.mc.player.posZ;
                AutoChase.mc.player.motionX = MotionX / this.centerSpeed.getValue();
                AutoChase.mc.player.motionZ = MotionZ / this.centerSpeed.getValue();
            }
            return this.wasInHole = true;
        }
        return false;
    }
    
    public Vec3d getCenter(final AxisAlignedBB box) {
        final boolean air = AutoChase.mc.world.isAirBlock(new BlockPos(box.minX, box.minY + 1.0, box.minZ));
        return air ? new Vec3d(box.minX + (box.maxX - box.minX) / 2.0, box.minY, box.minZ + (box.maxZ - box.minZ) / 2.0) : new Vec3d(box.maxX - 0.5, box.minY, box.maxZ - 0.5);
    }
    
    private boolean checkYRange(final int playerY, final int holeY) {
        if (playerY >= holeY) {
            return playerY - holeY <= this.downRange.getValue();
        }
        return holeY - playerY <= -this.upRange.getValue();
    }
    
    float getHeight(final String mode) {
        return Float.parseFloat(mode.equals("Vanilla") ? this.vHeight.getValue() : this.height.getValue());
    }
    
    protected boolean canStep() {
        return !AutoChase.mc.player.isInWater() && AutoChase.mc.player.onGround && !AutoChase.mc.player.isOnLadder() && !AutoChase.mc.player.movementInput.jump && AutoChase.mc.player.collidedVertically && AutoChase.mc.player.fallDistance < 0.1 && this.step.getValue() && this.isActive;
    }
    
    void sendOffsets(final double[] offsets) {
        for (final double i : offsets) {
            AutoChase.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoChase.mc.player.posX, AutoChase.mc.player.posY + i + 0.0, AutoChase.mc.player.posZ, false));
        }
    }
    
    @Override
    public String getHudInfo() {
        return this.hud.getValue() ? ("[" + ChatFormatting.WHITE + ((this.target == null) ? "None" : (this.target.getName() + ", " + (this.isActive ? "Chasing" : "Pausing"))) + ChatFormatting.GRAY + "]") : "";
    }
}
