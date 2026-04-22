// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketAnimation;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.inventory.ClickType;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import com.lemonclient.api.util.world.BlockUtil;
import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import java.util.Comparator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.function.Predicate;
import java.util.ArrayList;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.DeathEvent;
import me.zero.alpine.listener.Listener;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoShulker", category = Category.Dev)
public class AutoShulker extends Module
{
    BooleanSetting once;
    IntegerSetting counts;
    BooleanSetting disable;
    DoubleSetting range;
    DoubleSetting yRange;
    DoubleSetting targetRange;
    IntegerSetting tickDelay;
    IntegerSetting openDelay;
    BooleanSetting inventory;
    IntegerSetting Slot;
    BooleanSetting packetPlace;
    BooleanSetting placeSwing;
    BooleanSetting packetSwing;
    BooleanSetting packetSwitch;
    private int delayTimeTicks;
    BlockPos playerPos;
    ShulkerPos blockAim;
    List<BlockPos> list;
    int slot;
    boolean swapped;
    int tick;
    @EventHandler
    private final Listener<DeathEvent> deathEventListener;
    
    public AutoShulker() {
        this.once = this.registerBoolean("Once", false);
        this.counts = this.registerInteger("EmptySlots", 6, 1, 36, () -> !this.once.getValue());
        this.disable = this.registerBoolean("Disable After Death", true, () -> !this.once.getValue());
        this.range = this.registerDouble("Range", 5.0, 0.0, 10.0);
        this.yRange = this.registerDouble("YRange", 5.0, 0.0, 10.0);
        this.targetRange = this.registerDouble("Target Range", 8.0, 0.0, 16.0);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.openDelay = this.registerInteger("Open Delay", 5, 0, 10);
        this.inventory = this.registerBoolean("Inventory", true);
        this.Slot = this.registerInteger("Slot", 1, 1, 9);
        this.packetPlace = this.registerBoolean("Packet Place", true);
        this.placeSwing = this.registerBoolean("Place Swing", true);
        this.packetSwing = this.registerBoolean("Packet Swing", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.list = new ArrayList<BlockPos>();
        this.swapped = false;
        this.tick = 0;
        this.deathEventListener = new Listener<DeathEvent>(event -> {
            if (event.player == AutoShulker.mc.player && this.disable.getValue()) {
                this.disable();
            }
        }, new Predicate[0]);
    }
    
    private void switchTo(final int slot, final Runnable runnable) {
        final int oldslot = AutoShulker.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (slot < 9) {
            final boolean packetSwitch = this.packetSwitch.getValue();
            if (packetSwitch) {
                AutoShulker.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AutoShulker.mc.player.inventory.currentItem = slot;
            }
            runnable.run();
            if (packetSwitch) {
                AutoShulker.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                AutoShulker.mc.player.inventory.currentItem = oldslot;
            }
        }
    }
    
    private int getShulkerSlot() {
        for (int i = 0; i < AutoShulker.mc.player.inventory.mainInventory.size(); ++i) {
            if (AutoShulker.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)AutoShulker.mc.player.inventory.getStackInSlot(i).getItem()).getBlock() instanceof BlockShulkerBox) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    private void initValues() {
        final List<BlockPos> blocks = EntityUtil.getSphere(PlayerUtil.getEyesPos(), this.range.getValue() + 1.0, this.yRange.getValue() + 1.0, false, true, 0);
        blocks.removeIf(p -> ColorMain.INSTANCE.breakList.contains(p) || this.list.contains(p));
        final List<ShulkerPos> posList = new ArrayList<ShulkerPos>();
        blocks.forEach(pos -> {
            final EnumFacing facing = this.getFacing(pos);
            if (facing == null) {
            }
            else {
                final BlockPos neighbour = pos.offset(facing);
                final EnumFacing opposite = facing.getOpposite();
                final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
                if (this.inRange(hitVec)) {
                    posList.add(new ShulkerPos(pos, facing, neighbour, opposite, hitVec));
                }
            }
        });
        final EntityPlayer target = PlayerUtil.getNearestPlayer(12.0);
        if (target == null) {
            this.blockAim = posList.stream().min(Comparator.comparing(p -> p.getRange(AutoShulker.mc.player))).orElse(null);
        }
        else {
            this.blockAim = posList.stream().max(Comparator.comparing(p -> this.getWeight(p, target))).orElse(null);
        }
        if (this.blockAim == null) {
            return;
        }
        this.list.add(this.blockAim.pos);
    }
    
    private double getWeight(final ShulkerPos pos, final EntityPlayer target) {
        double range = pos.getRange(target);
        if (range >= this.targetRange.getValue()) {
            final int y = 256 - pos.pos.getY();
            range += y * 100;
        }
        return range;
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoShulker.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private EnumFacing getFacing(final BlockPos pos) {
        if (this.intersectsWithEntity(pos) || (!BlockUtil.canReplace(pos) && !(BlockUtil.getBlock(pos) instanceof BlockShulkerBox))) {
            return null;
        }
        for (final EnumFacing facing : EnumFacing.VALUES) {
            if (BlockUtil.canBeClicked(pos.offset(facing))) {
                if (BlockUtil.airBlocks.contains(AutoShulker.mc.world.getBlockState(pos.offset(facing, -1)).getBlock())) {
                    return facing;
                }
            }
        }
        return null;
    }
    
    private boolean inRange(final Vec3d vec) {
        final double x = vec.x - AutoShulker.mc.player.posX;
        final double z = vec.z - AutoShulker.mc.player.posZ;
        final double y = vec.y - PlayerUtil.getEyesPos().y;
        final double add = Math.sqrt(y * y) / 2.0;
        return x * x + z * z <= (this.range.getValue() - add) * (this.range.getValue() - add) && y * y <= this.yRange.getValue() * this.yRange.getValue();
    }
    
    private boolean inRange(final BlockPos pos) {
        final double x = pos.x + 0.5 - AutoShulker.mc.player.posX;
        final double z = pos.z + 0.5 - AutoShulker.mc.player.posZ;
        final double y = pos.y + 0.5 - PlayerUtil.getEyesPos().y;
        final double add = Math.sqrt(y * y) / 2.0;
        return x * x + z * z <= (this.range.getValue() - add) * (this.range.getValue() - add) && y * y <= this.yRange.getValue() * this.yRange.getValue();
    }
    
    @Override
    public void onUpdate() {
        if (AutoShulker.mc.player == null) {
            return;
        }
        if (this.tick++ >= this.openDelay.getValue()) {
            if (this.blockAim != null && !BlockUtil.isAir(this.blockAim.pos) && !BlockUtil.canReplace(this.blockAim.pos)) {
                this.openBlock();
            }
            this.tick = 0;
        }
        if (AutoShulker.mc.currentScreen instanceof GuiShulkerBox) {
            if (this.once.getValue()) {
                this.disable();
            }
            this.blockAim = null;
            return;
        }
        if (this.delayTimeTicks++ < this.tickDelay.getValue()) {
            return;
        }
        this.delayTimeTicks = 0;
        if ((this.slot = this.getShulkerSlot()) == -1) {
            return;
        }
        if (this.once.getValue() || InventoryUtil.getEmptyCounts() >= this.counts.getValue()) {
            if (this.blockAim == null) {
                this.initValues();
            }
        }
        else {
            this.checkPos();
        }
        if (this.blockAim == null) {
            if (this.once.getValue()) {
                this.disable();
            }
            return;
        }
        if (!this.inRange(this.blockAim.pos)) {
            this.blockAim = null;
            return;
        }
        if (this.slot > 8 && !this.swapped) {
            if (!this.inventory.getValue()) {
                return;
            }
            AutoShulker.mc.playerController.windowClick(0, this.slot, this.Slot.getValue(), ClickType.SWAP, AutoShulker.mc.player);
            AutoShulker.mc.playerController.updateController();
            this.swapped = true;
            if (this.tickDelay.getValue() != 0) {
                return;
            }
        }
        if (BlockUtil.isAir(this.blockAim.pos) || BlockUtil.canReplace(this.blockAim.pos)) {
            this.switchTo(this.slot, () -> {
                boolean sneak = false;
                if (BlockUtil.blackList.contains(AutoShulker.mc.world.getBlockState(this.blockAim.neighbour).getBlock()) && !AutoShulker.mc.player.isSneaking()) {
                    AutoShulker.mc.player.connection.sendPacket(new CPacketEntityAction(AutoShulker.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    sneak = true;
                }
                BurrowUtil.rightClickBlock(this.blockAim.neighbour, this.blockAim.vec, EnumHand.MAIN_HAND, this.blockAim.opposite, this.packetPlace.getValue());
                if (sneak) {
                    AutoShulker.mc.player.connection.sendPacket(new CPacketEntityAction(AutoShulker.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (this.placeSwing.getValue()) {
                    this.swing();
                }
                this.tick = 0;
            });
            if (this.tickDelay.getValue() == 0) {
                this.openBlock();
            }
        }
        else {
            this.openBlock();
        }
    }
    
    private void checkPos() {
        if (!this.isPos2(PlayerUtil.getPlayerPos(), this.playerPos)) {
            this.list = new ArrayList<BlockPos>();
            this.playerPos = PlayerUtil.getPlayerPos();
        }
    }
    
    private void swing() {
        if (this.packetSwing.getValue()) {
            AutoShulker.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        else {
            AutoShulker.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    
    private void openBlock() {
        final EnumFacing side = EnumFacing.getDirectionFromEntityLiving(this.blockAim.pos, AutoShulker.mc.player);
        final BlockPos neighbour = this.blockAim.pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        AutoShulker.mc.player.connection.sendPacket(new CPacketEntityAction(AutoShulker.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        AutoShulker.mc.playerController.processRightClickBlock(AutoShulker.mc.player, AutoShulker.mc.world, this.blockAim.pos, opposite, hitVec, EnumHand.MAIN_HAND);
    }
    
    public void onEnable() {
        this.blockAim = null;
        this.checkPos();
    }
    
    static class ShulkerPos
    {
        BlockPos pos;
        EnumFacing facing;
        Vec3d vec;
        BlockPos neighbour;
        EnumFacing opposite;
        
        public ShulkerPos(final BlockPos pos, final EnumFacing facing, final BlockPos neighbour, final EnumFacing opposite, final Vec3d vec3d) {
            this.pos = pos;
            this.facing = facing;
            this.neighbour = neighbour;
            this.opposite = opposite;
            this.vec = vec3d;
        }
        
        public double getRange(final EntityPlayer player) {
            return player.getDistance(this.pos.x + 0.5, this.pos.y + 0.5, this.pos.z + 0.5);
        }
    }
}
