// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.init.Blocks;
import java.util.List;
import java.util.function.Function;
import java.util.Comparator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.item.ItemBed;
import net.minecraft.init.Items;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.misc.Timing;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "SelfBed", category = Category.Dev, priority = 999)
public class SelfBed extends Module
{
    ModeSetting page;
    BooleanSetting packetPlace;
    BooleanSetting placeSwing;
    BooleanSetting breakSwing;
    BooleanSetting packetSwing;
    BooleanSetting highVersion;
    BooleanSetting autoSwitch;
    BooleanSetting update;
    BooleanSetting silentSwitch;
    BooleanSetting packetSwitch;
    IntegerSetting calcDelay;
    IntegerSetting placeDelay;
    IntegerSetting breakDelay;
    DoubleSetting range;
    DoubleSetting yRange;
    ModeSetting handMode;
    DoubleSetting maxDmg;
    BooleanSetting antiSuicide;
    BlockPos headPos;
    BlockPos basePos;
    float damage;
    float selfDamage;
    String face;
    Timing basetiming;
    Timing calctiming;
    Timing placetiming;
    Timing breaktiming;
    EnumHand hand;
    int slot;
    Vec2f rotation;
    int nowSlot;
    @EventHandler
    private final Listener<PacketEvent.Send> postSendListener;
    
    public SelfBed() {
        this.page = this.registerMode("Page", Arrays.asList("General", "Calc"), "General");
        this.packetPlace = this.registerBoolean("Packet Place", true, () -> this.page.getValue().equals("General"));
        this.placeSwing = this.registerBoolean("Place Swing", true, () -> this.page.getValue().equals("General"));
        this.breakSwing = this.registerBoolean("Break Swing", true, () -> this.page.getValue().equals("General"));
        this.packetSwing = this.registerBoolean("Packet Swing", true, () -> this.page.getValue().equals("General"));
        this.highVersion = this.registerBoolean("1.13", true, () -> this.page.getValue().equals("General"));
        this.autoSwitch = this.registerBoolean("Auto Switch", true, () -> this.page.getValue().equals("General"));
        this.update = this.registerBoolean("Update", true, () -> this.page.getValue().equals("General"));
        this.silentSwitch = this.registerBoolean("Switch Back", true, () -> this.page.getValue().equals("General") && this.autoSwitch.getValue());
        this.packetSwitch = this.registerBoolean("Packet Switch", true, () -> this.page.getValue().equals("General"));
        this.calcDelay = this.registerInteger("Calc Delay", 0, 0, 1000, () -> this.page.getValue().equals("Calc"));
        this.placeDelay = this.registerInteger("Place Delay", 0, 0, 1000, () -> this.page.getValue().equals("Calc"));
        this.breakDelay = this.registerInteger("Break Delay", 0, 0, 1000, () -> this.page.getValue().equals("Calc"));
        this.range = this.registerDouble("Place Range", 5.0, 0.0, 10.0, () -> this.page.getValue().equals("Calc"));
        this.yRange = this.registerDouble("Y Range", 2.5, 0.0, 10.0, () -> this.page.getValue().equals("Calc"));
        this.handMode = this.registerMode("Hand", Arrays.asList("Main", "Off", "Auto"), "Auto", () -> this.page.getValue().equals("Calc"));
        this.maxDmg = this.registerDouble("Max Self Dmg", 10.0, 0.0, 20.0, () -> this.page.getValue().equals("Calc"));
        this.antiSuicide = this.registerBoolean("Anti Suicide", true, () -> this.page.getValue().equals("Calc"));
        this.basetiming = new Timing();
        this.calctiming = new Timing();
        this.placetiming = new Timing();
        this.breaktiming = new Timing();
        this.postSendListener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketHeldItemChange) {
                this.nowSlot = ((CPacketHeldItemChange)event.getPacket()).getSlotId();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (SelfBed.mc.player == null || SelfBed.mc.world == null || EntityUtil.isDead(SelfBed.mc.player) || this.inNether()) {
            final BlockPos blockPos = null;
            this.basePos = blockPos;
            this.headPos = blockPos;
            final float n = 0.0f;
            this.selfDamage = n;
            this.damage = n;
            this.rotation = null;
            return;
        }
        this.calc();
    }
    
    @Override
    public void fast() {
        if (SelfBed.mc.player == null || SelfBed.mc.world == null || EntityUtil.isDead(SelfBed.mc.player) || this.inNether()) {
            return;
        }
        if (SelfBed.mc.player.movementInput.moveForward == 0.0f && SelfBed.mc.player.movementInput.moveStrafe == 0.0f) {
            return;
        }
        this.bedaura();
    }
    
    private void bedaura() {
        if (this.headPos == null || this.basePos == null) {
            return;
        }
        if (this.isBed(this.headPos) || this.isBed(this.basePos)) {
            this.breakBed();
        }
        this.place();
        this.breakBed();
    }
    
    private void calc() {
        if (this.calctiming.passedMs(this.calcDelay.getValue())) {
            this.calctiming.reset();
            final BlockPos blockPos = null;
            this.basePos = blockPos;
            this.headPos = blockPos;
            final float n = 0.0f;
            this.selfDamage = n;
            this.damage = n;
            this.rotation = null;
            if (SelfBed.mc.player.movementInput.moveForward == 0.0f && SelfBed.mc.player.movementInput.moveStrafe == 0.0f) {
                return;
            }
            final boolean offhand = !this.handMode.getValue().equals("Main") && SelfBed.mc.player.getHeldItemOffhand().getItem() == Items.BED;
            if (!offhand && !this.handMode.getValue().equals("Off")) {
                this.slot = BurrowUtil.findHotbarBlock(ItemBed.class);
                if (this.slot == -1) {
                    return;
                }
            }
            this.hand = (offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            BlockPos bedPos = this.findBlocksExcluding();
            if (bedPos == null) {
                return;
            }
            this.headPos = bedPos;
            if (SelfBed.mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH)) {
                this.face = "SOUTH";
                this.rotation = new Vec2f(0.0f, 90.0f);
                bedPos = new BlockPos(this.headPos.x, this.headPos.y, this.headPos.z - 1);
            }
            else if (SelfBed.mc.player.getHorizontalFacing().equals(EnumFacing.WEST)) {
                this.face = "WEST";
                this.rotation = new Vec2f(90.0f, 90.0f);
                bedPos = new BlockPos(this.headPos.x + 1, this.headPos.y, this.headPos.z);
            }
            else if (SelfBed.mc.player.getHorizontalFacing().equals(EnumFacing.NORTH)) {
                this.face = "NORTH";
                this.rotation = new Vec2f(180.0f, 90.0f);
                bedPos = new BlockPos(this.headPos.x, this.headPos.y, this.headPos.z + 1);
            }
            else {
                this.face = "EAST";
                this.rotation = new Vec2f(-90.0f, 90.0f);
                bedPos = new BlockPos(this.headPos.x - 1, this.headPos.y, this.headPos.z);
            }
            if (!this.block(bedPos, true)) {
                final BlockPos blockPos2 = null;
                this.basePos = blockPos2;
                this.headPos = blockPos2;
                final float n2 = 0.0f;
                this.selfDamage = n2;
                this.damage = n2;
                this.rotation = null;
                return;
            }
            this.headPos = this.headPos.up();
            this.basePos = bedPos.up();
        }
    }
    
    private void place() {
        if (this.placetiming.passedMs(this.placeDelay.getValue())) {
            final BlockPos neighbour = this.basePos.down();
            final EnumFacing opposite = EnumFacing.DOWN.getOpposite();
            final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
            boolean sneak = false;
            if (BlockUtil.blackList.contains(SelfBed.mc.world.getBlockState(neighbour).getBlock()) && !SelfBed.mc.player.isSneaking()) {
                SelfBed.mc.player.connection.sendPacket(new CPacketEntityAction(SelfBed.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                sneak = true;
            }
            this.run(() -> BurrowUtil.rightClickBlock(neighbour, hitVec, this.hand, opposite, this.packetPlace.getValue()), this.slot);
            if (this.placeSwing.getValue()) {
                this.swing(this.hand);
            }
            if (sneak) {
                SelfBed.mc.player.connection.sendPacket(new CPacketEntityAction(SelfBed.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.placetiming.reset();
        }
    }
    
    private void run(final Runnable runnable, final int slot) {
        if (this.hand == EnumHand.OFF_HAND) {
            runnable.run();
            return;
        }
        final int oldSlot = SelfBed.mc.player.inventory.currentItem;
        if (slot != oldSlot) {
            if (this.autoSwitch.getValue()) {
                this.switchTo(slot);
                if (this.nowSlot == slot || SelfBed.mc.player.getHeldItemMainhand().getItem() == Items.BED) {
                    runnable.run();
                }
                if (this.silentSwitch.getValue()) {
                    this.switchTo(oldSlot);
                }
            }
        }
        else {
            runnable.run();
        }
    }
    
    private void breakBed() {
        if (this.breaktiming.passedMs(this.breakDelay.getValue())) {
            final EnumFacing side = EnumFacing.UP;
            if (ModuleManager.getModule(ColorMain.class).sneaking) {
                SelfBed.mc.player.connection.sendPacket(new CPacketEntityAction(SelfBed.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            final Vec3d facing = this.getHitVecOffset(side);
            if (this.isBed(this.headPos) && !this.isBed(this.basePos)) {
                SelfBed.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.headPos, side, this.hand, (float)facing.x, (float)facing.y, (float)facing.z));
            }
            else {
                SelfBed.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.basePos, side, this.hand, (float)facing.x, (float)facing.y, (float)facing.z));
            }
            if (this.breakSwing.getValue()) {
                this.swing(this.hand);
            }
            this.breaktiming.reset();
        }
    }
    
    private BlockPos findBlocksExcluding() {
        final double x = SelfBed.mc.player.prevPosX;
        final double z = SelfBed.mc.player.prevPosZ;
        final double dX = SelfBed.mc.player.posX - x;
        final double dZ = SelfBed.mc.player.posZ - z;
        final List<BlockPos> posList = new ArrayList<BlockPos>();
        for (final int y : new int[] { -3, -2, -1, 0, 1, 2 }) {
            posList.addAll(EntityUtil.getSphere(PlayerUtil.getEyesPos(), this.range.getValue() + 1.0, 1.0, false, false, y).stream().filter(p -> (SelfBed.mc.player.posX - x) * (SelfBed.mc.player.posX - p.x) > 0.0 && (SelfBed.mc.player.posZ - z) * (SelfBed.mc.player.posZ - p.z) > 0.0).filter(this::canPlaceBed).filter(p -> (x - p.x) * dX >= 0.0 && (z - p.z) * dZ >= 0.0).filter(p -> {
                final double dmg = DamageUtil.calculateDamage(SelfBed.mc.player, SelfBed.mc.player.getPositionVector(), SelfBed.mc.player.getEntityBoundingBox(), p.x + 0.5, p.y + 1.5625, p.z + 0.5, 5.0f, "Bed");
                return dmg <= this.maxDmg.getValue() && (!this.antiSuicide.getValue() || dmg <= EntityUtil.getHealth(SelfBed.mc.player) + 1.0f);
            }).collect(Collectors.toList()));
        }
        final BlockPos pos = posList.stream().min(Comparator.comparingDouble(PlayerUtil::getDistanceI)).orElse(null);
        return pos;
    }
    
    private boolean canPlaceBed(final BlockPos blockPos) {
        if (!this.block(blockPos, false)) {
            return false;
        }
        final BlockPos pos = blockPos.offset(SelfBed.mc.player.getHorizontalFacing(), -1);
        return this.block(pos, true) && this.inRange(pos.up());
    }
    
    private boolean block(final BlockPos pos, final boolean rangeCheck) {
        return this.space(pos.up()) && !BlockUtil.canReplace(pos) && (this.highVersion.getValue() || this.solid(pos)) && (!rangeCheck || this.inRange(pos.up()));
    }
    
    private boolean isBed(final BlockPos pos) {
        final Block block = SelfBed.mc.world.getBlockState(pos).getBlock();
        return block == Blocks.BED || block instanceof BlockBed;
    }
    
    private boolean space(final BlockPos pos) {
        return SelfBed.mc.world.isAirBlock(pos) || SelfBed.mc.world.getBlockState(pos).getBlock() == Blocks.BED;
    }
    
    private boolean solid(final BlockPos pos) {
        return !BlockUtil.isBlockUnSolid(pos) && !(SelfBed.mc.world.getBlockState(pos).getBlock() instanceof BlockBed) && SelfBed.mc.world.getBlockState(pos).isSideSolid(SelfBed.mc.world, pos, EnumFacing.UP);
    }
    
    private boolean inRange(final BlockPos pos) {
        final double x = pos.x - SelfBed.mc.player.posX;
        final double z = pos.z - SelfBed.mc.player.posZ;
        final double y = pos.y - PlayerUtil.getEyesPos().y;
        final double add = Math.sqrt(y * y) / 2.0;
        return x * x + z * z <= (this.range.getValue() - add) * (this.range.getValue() - add) && y * y <= this.yRange.getValue() * this.yRange.getValue();
    }
    
    private Vec3d getHitVecOffset(final EnumFacing face) {
        final Vec3i vec = face.getDirectionVec();
        return new Vec3d(vec.x * 0.5f + 0.5f, vec.y * 0.5f + 0.5f, vec.z * 0.5f + 0.5f);
    }
    
    private void switchTo(final int slot) {
        if (slot > -1 && slot < 9) {
            if (this.packetSwitch.getValue()) {
                SelfBed.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                SelfBed.mc.player.inventory.currentItem = slot;
            }
            if (this.update.getValue()) {
                SelfBed.mc.playerController.updateController();
            }
        }
    }
    
    private void swing(final EnumHand hand) {
        if (this.packetSwing.getValue()) {
            SelfBed.mc.player.connection.sendPacket(new CPacketAnimation(hand));
        }
        else {
            SelfBed.mc.player.swingArm(hand);
        }
    }
    
    private boolean inNether() {
        return SelfBed.mc.player.dimension == 0;
    }
    
    public void onEnable() {
        this.calctiming.reset();
        this.basetiming.reset();
        this.placetiming.reset();
        this.breaktiming.reset();
    }
}
