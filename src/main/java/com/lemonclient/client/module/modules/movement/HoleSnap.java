// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.List;
import net.minecraft.util.NonNullList;
import com.lemonclient.client.module.ModuleManager;
import java.util.Comparator;
import com.lemonclient.api.util.world.HoleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.block.BlockLiquid;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.player.RotationUtil;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.util.MovementInputFromOptions;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import java.util.Arrays;
import com.lemonclient.api.event.events.StepEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "HoleSnap", category = Category.Movement, priority = 120)
public class HoleSnap extends Module
{
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
    IntegerSetting timeoutTicks;
    BooleanSetting only;
    BooleanSetting single;
    BooleanSetting twoBlocks;
    BooleanSetting custom;
    BooleanSetting four;
    BooleanSetting near;
    BooleanSetting bedrock;
    BooleanSetting autoPhase;
    private int stuckTicks;
    private int enabledTicks;
    BlockPos originPos;
    BlockPos startPos;
    boolean isActive;
    boolean slowDown;
    double playerSpeed;
    @EventHandler
    private final Listener<PacketEvent.Receive> listener;
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
    @EventHandler
    private final Listener<StepEvent> stepEventListener;
    
    public HoleSnap() {
        this.downRange = this.registerInteger("Down Range", 5, 0, 8);
        this.upRange = this.registerInteger("Up Range", 1, 0, 8);
        this.hRange = this.registerDouble("H Range", 4.0, 1.0, 8.0);
        this.timer = this.registerDouble("Timer", 2.0, 1.0, 50.0);
        this.speed = this.registerDouble("Max Speed", 2.0, 0.0, 10.0);
        this.step = this.registerBoolean("Step", true);
        this.mode = this.registerMode("Mode", Arrays.asList("NCP", "Vanilla"), "NCP", () -> this.step.getValue());
        this.height = this.registerMode("NCP Height", Arrays.asList("1", "1.5", "2", "2.5", "3", "4"), "2.5", () -> this.mode.getValue().equalsIgnoreCase("NCP") && this.step.getValue());
        this.vHeight = this.registerMode("Vanilla Height", Arrays.asList("1", "1.5", "2", "2.5", "3", "4"), "2.5", () -> this.mode.getValue().equalsIgnoreCase("Vanilla") && this.step.getValue());
        this.abnormal = this.registerBoolean("Abnormal", false, () -> !this.mode.getValue().equalsIgnoreCase("Vanilla") && this.step.getValue());
        this.centerSpeed = this.registerInteger("Center Speed", 2, 10, 1);
        this.timeoutTicks = this.registerInteger("Timeout Ticks", 10, 0, 100);
        this.only = this.registerBoolean("Only 1x1", true);
        this.single = this.registerBoolean("Single Hole", true, () -> !this.only.getValue());
        this.twoBlocks = this.registerBoolean("Double Hole", true, () -> !this.only.getValue());
        this.custom = this.registerBoolean("Custom Hole", true, () -> !this.only.getValue());
        this.four = this.registerBoolean("Four Blocks", true, () -> !this.only.getValue());
        this.near = this.registerBoolean("Near Target", true);
        this.bedrock = this.registerBoolean("Preferred Bedrock", true);
        this.autoPhase = this.registerBoolean("AutoPhase OnDisable", true);
        this.stuckTicks = 0;
        this.enabledTicks = 0;
        this.listener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                this.disable();
            }
        }, new Predicate[0]);
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
            if (++this.enabledTicks > this.timeoutTicks.getValue()) {
                this.disable();
            }
            else if (!HoleSnap.mc.player.isEntityAlive() || HoleSnap.mc.player.isElytraFlying() || HoleSnap.mc.player.capabilities.isFlying) {
            }
            else {
                final double currentSpeed = Math.hypot(HoleSnap.mc.player.motionX, HoleSnap.mc.player.motionZ);
                if (currentSpeed <= 0.05) {
                    this.originPos = PlayerUtil.getPlayerPos();
                }
                if (this.shouldDisable(currentSpeed)) {
                    this.disable();
                }
                else {
                    final BlockPos hole = this.findHoles();
                    if (hole != null) {
                        final double x = hole.getX() + 0.5;
                        final double y = hole.getY();
                        final double z = hole.getZ() + 0.5;
                        this.enabledTicks = 0;
                        if (this.checkYRange((int)HoleSnap.mc.player.posY, this.originPos.y) && HoleSnap.mc.player.getDistance(x + 0.5, HoleSnap.mc.player.posY, z + 0.5) <= this.hRange.getValue()) {
                            this.isActive = true;
                            TimerUtils.setTickLength((float)(50.0 / this.timer.getValue()));
                            final Vec3d playerPos = HoleSnap.mc.player.getPositionVector();
                            final double yawRad = Math.toRadians(RotationUtil.getRotationTo(playerPos, new Vec3d(x, y, z)).x);
                            final double dist = Math.hypot(x - playerPos.x, z - playerPos.z);
                            if (HoleSnap.mc.player.onGround) {
                                this.playerSpeed = MotionUtil.getBaseMoveSpeed() * ((EntityUtil.isColliding(0.0, -0.5, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.91 : this.speed.getValue());
                                this.slowDown = true;
                            }
                            final double speed = Math.min(dist, this.playerSpeed);
                            HoleSnap.mc.player.motionX = 0.0;
                            HoleSnap.mc.player.motionZ = 0.0;
                            event.setX(-Math.sin(yawRad) * speed);
                            event.setZ(Math.cos(yawRad) * speed);
                        }
                    }
                    if (HoleSnap.mc.player.collidedHorizontally && hole == null) {
                        ++this.stuckTicks;
                    }
                    else {
                        this.stuckTicks = 0;
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
        this.stepEventListener = new Listener<StepEvent>(event -> {
            if (this.canStep()) {
                final double step = event.getBB().minY - HoleSnap.mc.player.posY;
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
                }
            }
        }, new Predicate[0]);
    }
    
    public static EntityPlayer getNearestPlayer() {
        return HoleSnap.mc.world.playerEntities.stream().filter(p -> HoleSnap.mc.player.getDistance(p) <= 8.0f).filter(p -> HoleSnap.mc.player.entityId != p.entityId).filter(p -> !EntityUtil.basicChecksEntity(p)).filter(p -> HoleUtil.isInHole(p, false, true, false)).min(Comparator.comparing(p -> HoleSnap.mc.player.getDistance(p))).orElse(null);
    }
    
    @Override
    public void onUpdate() {
        if (HoleSnap.mc.world == null || HoleSnap.mc.player == null || HoleSnap.mc.player.isDead || this.startPos == null) {
            this.disable();
            return;
        }
        if (this.step.getValue()) {
            HoleSnap.mc.player.stepHeight = this.getHeight(this.mode.getValue());
        }
    }
    
    public void onEnable() {
        final BlockPos playerPos = PlayerUtil.getPlayerPos();
        this.originPos = playerPos;
        this.startPos = playerPos;
    }
    
    public void onDisable() {
        this.isActive = false;
        final int n = 0;
        this.enabledTicks = n;
        this.stuckTicks = n;
        TimerUtils.setTickLength(50.0f);
        if (HoleSnap.mc.player != null) {
            if (HoleSnap.mc.player.getRidingEntity() != null) {
                HoleSnap.mc.player.getRidingEntity().stepHeight = 1.0f;
            }
            HoleSnap.mc.player.stepHeight = 0.6f;
        }
        if (this.autoPhase.getValue()) {
            ModuleManager.getModule("AutoPhase").enable();
        }
    }
    
    private BlockPos findHoles() {
        final EntityPlayer target = getNearestPlayer();
        final boolean near = this.near.getValue() && target != null;
        final NonNullList<HoleBlock> holes = NonNullList.create();
        final List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), this.hRange.getValue(), 8.0, false, true, 0);
        blockPosList.forEach(pos -> {
            if (!this.checkYRange((int)HoleSnap.mc.player.posY, pos.y)) {
            }
            else if (!HoleSnap.mc.world.isAirBlock(PlayerUtil.getPlayerPos().up(2)) && (int)HoleSnap.mc.player.posY < pos.y) {
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
                    if (HoleSnap.mc.world.isAirBlock(pos) && HoleSnap.mc.world.isAirBlock(pos.add(0, 1, 0)) && HoleSnap.mc.world.isAirBlock(pos.add(0, 2, 0))) {
                        for (int high = 0; high < HoleSnap.mc.player.posY - pos.y; ++high) {
                            if (high != 0) {
                                if (HoleSnap.mc.player.posY > pos.y) {
                                    if (!HoleSnap.mc.world.isAirBlock(new BlockPos(pos.x, pos.y + high, pos.z))) {
                                        return;
                                    }
                                }
                                if (HoleSnap.mc.player.posY < pos.y) {
                                    final BlockPos newPos = new BlockPos(pos.x, pos.y + high, pos.z);
                                    if (HoleSnap.mc.world.isAirBlock(newPos) && (HoleSnap.mc.world.isAirBlock(newPos.down()) || HoleSnap.mc.world.isAirBlock(newPos.up()))) {
                                        return;
                                    }
                                }
                            }
                        }
                        holes.add(new HoleBlock(pos, (near ? target.getDistance(pos.x + 0.5, pos.y, pos.z + 0.5) : HoleSnap.mc.player.getDistance(pos.x + 0.5, pos.y, pos.z + 0.5)) + (this.bedrock.getValue() ? ((holeInfo.getSafety() == HoleUtil.BlockSafety.UNBREAKABLE) ? -100 : 0) : 0)));
                    }
                }
            }
        });
        if (holes.isEmpty()) {
            return null;
        }
        return holes.stream().min(Comparator.comparing(p -> p.value)).orElse(null).pos;
    }
    
    private boolean shouldDisable(final Double currentSpeed) {
        if (!HoleSnap.mc.player.onGround) {
            return false;
        }
        if (this.stuckTicks > 5 && currentSpeed < 0.05) {
            return true;
        }
        final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(new BlockPos(PlayerUtil.getPlayerPos().x, PlayerUtil.getPlayerPos().y + 0.5, PlayerUtil.getPlayerPos().z), false, false, false);
        final HoleUtil.HoleType holeType = holeInfo.getType();
        if (holeType != HoleUtil.HoleType.NONE) {
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
            final double XDiff = Math.abs(center.x - HoleSnap.mc.player.posX);
            final double ZDiff = Math.abs(center.z - HoleSnap.mc.player.posZ);
            if (XDiff > 0.3 || ZDiff > 0.3) {
                final double MotionX = center.x - HoleSnap.mc.player.posX;
                final double MotionZ = center.z - HoleSnap.mc.player.posZ;
                HoleSnap.mc.player.motionX = MotionX / this.centerSpeed.getValue();
                HoleSnap.mc.player.motionZ = MotionZ / this.centerSpeed.getValue();
            }
            return true;
        }
        return false;
    }
    
    public Vec3d getCenter(final AxisAlignedBB box) {
        final boolean air = HoleSnap.mc.world.isAirBlock(new BlockPos(box.minX, box.minY + 1.0, box.minZ));
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
        return !HoleSnap.mc.player.isInWater() && HoleSnap.mc.player.onGround && !HoleSnap.mc.player.isOnLadder() && !HoleSnap.mc.player.movementInput.jump && HoleSnap.mc.player.collidedVertically && HoleSnap.mc.player.fallDistance < 0.1 && this.step.getValue();
    }
    
    void sendOffsets(final double[] offsets) {
        for (final double i : offsets) {
            HoleSnap.mc.player.connection.sendPacket(new CPacketPlayer.Position(HoleSnap.mc.player.posX, HoleSnap.mc.player.posY + i + 0.0, HoleSnap.mc.player.posZ, false));
        }
    }
    
    static class HoleBlock
    {
        public final BlockPos pos;
        public final double value;
        
        public HoleBlock(final BlockPos pos, final double value) {
            this.pos = pos;
            this.value = value;
        }
    }
}
