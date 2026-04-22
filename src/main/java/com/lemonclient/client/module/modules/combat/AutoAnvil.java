// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.List;
import java.util.Collection;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.BlockAir;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.entity.item.EntityItem;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.util.EnumFacing;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.PacketMine;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.BlockAnvil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.Arrays;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoAnvil", category = Category.Combat)
public class AutoAnvil extends Module
{
    ModeSetting anvilMode;
    ModeSetting target;
    BooleanSetting rotate;
    BooleanSetting packetSwitch;
    BooleanSetting packetPlace;
    BooleanSetting swing;
    DoubleSetting enemyRange;
    DoubleSetting decrease;
    IntegerSetting tickDelay;
    IntegerSetting blocksPerTick;
    IntegerSetting hDistance;
    IntegerSetting minH;
    IntegerSetting maxH;
    private boolean noMaterials;
    private boolean enoughSpace;
    private boolean blockUp;
    private int[] slot_mat;
    private double[] enemyCoords;
    int[][] model;
    private int blocksPlaced;
    private int delayTimeTicks;
    private int offsetSteps;
    private BlockPos base;
    private EntityPlayer aimTarget;
    private static ArrayList<Vec3d> to_place;
    
    public AutoAnvil() {
        this.anvilMode = this.registerMode("Mode", Arrays.asList("Pick", "Feet", "None"), "Pick");
        this.target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
        this.rotate = this.registerBoolean("Rotate", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", false);
        this.packetPlace = this.registerBoolean("Packet Place", false);
        this.swing = this.registerBoolean("Swing", false);
        this.enemyRange = this.registerDouble("Range", 5.9, 0.0, 6.0);
        this.decrease = this.registerDouble("Decrease", 2.0, 0.0, 6.0);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.hDistance = this.registerInteger("H Distance", 7, 1, 10);
        this.minH = this.registerInteger("Min H", 3, 1, 10);
        this.maxH = this.registerInteger("Max H", 3, 1, 10);
        this.noMaterials = false;
        this.enoughSpace = true;
        this.blockUp = false;
        this.slot_mat = new int[] { -1, -1, -1 };
        this.model = new int[][] { { 1, 1, 1 }, { -1, 1, -1 }, { -1, 1, 1 }, { 1, 1, -1 } };
        this.blocksPlaced = 0;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
    }
    
    public void onEnable() {
        this.blocksPlaced = 0;
        this.blockUp = false;
        this.slot_mat = new int[] { -1, -1, -1 };
        AutoAnvil.to_place = new ArrayList<Vec3d>();
        if (AutoAnvil.mc.player == null) {
            this.disable();
        }
    }
    
    public void onDisable() {
        if (AutoAnvil.mc.player == null) {
            return;
        }
        if (this.noMaterials) {
            this.setDisabledMessage("No Materials Detected... AutoAnvil turned OFF!");
        }
        else if (!this.enoughSpace) {
            this.setDisabledMessage("Not enough space... AutoAnvil turned OFF!");
        }
        else if (this.blockUp) {
            this.setDisabledMessage("Enemy head blocked.. AutoAnvil turned OFF!");
        }
        this.noMaterials = false;
    }
    
    @Override
    public void onUpdate() {
        if (AutoAnvil.mc.player == null) {
            this.disable();
            return;
        }
        if (this.target.getValue().equals("Nearest")) {
            this.aimTarget = PlayerUtil.getNearestPlayer(this.enemyRange.getValue());
        }
        else if (this.target.getValue().equals("Looking")) {
            this.aimTarget = PlayerUtil.findLookingPlayer(this.enemyRange.getValue());
        }
        if (this.aimTarget == null || AutoAnvil.mc.player.isDead) {
            this.disable();
            return;
        }
        if (this.getMaterialsSlot()) {
            this.enemyCoords = new double[] { this.aimTarget.posX, this.aimTarget.posY, this.aimTarget.posZ };
            this.enoughSpace = this.createStructure();
        }
        else {
            this.noMaterials = true;
        }
        if (this.noMaterials || !this.enoughSpace || this.blockUp) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (!BlockUtil.isAir(new BlockPos(this.enemyCoords[0], this.enemyCoords[1] + 2.0, this.enemyCoords[2])) && !(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + 2.0, this.enemyCoords[2]) instanceof BlockAnvil)) {
            this.blockUp = true;
        }
        this.blocksPlaced = 0;
        while (this.blocksPlaced <= this.blocksPerTick.getValue()) {
            final int maxSteps = AutoAnvil.to_place.size();
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(AutoAnvil.to_place.get(this.offsetSteps));
            final BlockPos targetPos = new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean tryPlacing = true;
            if (this.offsetSteps > 0 && this.offsetSteps < AutoAnvil.to_place.size() - 1) {
                for (final Entity entity : AutoAnvil.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        tryPlacing = false;
                        break;
                    }
                }
            }
            if (tryPlacing && this.placeBlock(targetPos, this.offsetSteps)) {
                ++this.blocksPlaced;
            }
            ++this.offsetSteps;
        }
        BlockPos instantPos = null;
        if (ModuleManager.isModuleEnabled(PacketMine.class)) {
            instantPos = PacketMine.INSTANCE.packetPos;
        }
        if (this.anvilMode.getValue().equalsIgnoreCase("Pick") && (instantPos == null || !instantPos.equals(new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2])))) {
            AutoAnvil.mc.playerController.onPlayerDamageBlock(new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]), EnumFacing.UP);
        }
    }
    
    private boolean placeBlock(final BlockPos pos, final int step) {
        if (this.intersectsWithEntity(pos)) {
            return false;
        }
        if (!BlockUtil.canReplace(pos)) {
            return false;
        }
        final int utilSlot = (step == 0 && this.anvilMode.getValue().equalsIgnoreCase("feet")) ? 2 : ((step >= AutoAnvil.to_place.size() - 1) ? 1 : 0);
        if (utilSlot == 0 && BlockUtil.canBeClicked(this.base)) {
            return false;
        }
        final int slot = this.slot_mat[utilSlot];
        int oldslot = AutoAnvil.mc.player.inventory.currentItem;
        if (AutoAnvil.mc.player.inventory.getStackInSlot(slot) != ItemStack.EMPTY) {
            if (oldslot != slot) {
                if (this.packetSwitch.getValue()) {
                    AutoAnvil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                }
                else {
                    AutoAnvil.mc.player.inventory.currentItem = slot;
                }
            }
            else {
                oldslot = -1;
            }
            BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packetPlace.getValue(), false, this.swing.getValue());
            if (oldslot != -1) {
                if (this.packetSwitch.getValue()) {
                    AutoAnvil.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
                }
                else {
                    AutoAnvil.mc.player.inventory.currentItem = oldslot;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoAnvil.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean getMaterialsSlot() {
        final boolean feet = this.anvilMode.getValue().equalsIgnoreCase("Feet");
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoAnvil.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        this.slot_mat[0] = i;
                    }
                    else if (block instanceof BlockAnvil) {
                        this.slot_mat[1] = i;
                    }
                    else if (feet && (block instanceof BlockPressurePlate || block instanceof BlockButton)) {
                        this.slot_mat[2] = i;
                    }
                }
            }
        }
        int count = 0;
        for (final int val : this.slot_mat) {
            if (val != -1) {
                ++count;
            }
        }
        return count - (feet ? 1 : 0) == 2;
    }
    
    private boolean createStructure() {
        AutoAnvil.to_place = new ArrayList<Vec3d>();
        if (this.anvilMode.getValue().equalsIgnoreCase("feet")) {
            AutoAnvil.to_place.add(new Vec3d(0.0, 0.0, 0.0));
        }
        int hDistanceMod = this.hDistance.getValue();
        for (double distEnemy = AutoAnvil.mc.player.getDistance(this.aimTarget); distEnemy > this.decrease.getValue(); distEnemy -= this.decrease.getValue()) {
            --hDistanceMod;
        }
        hDistanceMod += (int)(AutoAnvil.mc.player.posY - this.aimTarget.posY);
        double min_found = Double.MAX_VALUE;
        int cor = -1;
        int i = 0;
        final BlockPos[] array;
        final BlockPos[] posList = array = new BlockPos[] { new BlockPos(this.enemyCoords[0] + 1.0, this.enemyCoords[1], this.enemyCoords[2] + 1.0), new BlockPos(this.enemyCoords[0] - 1.0, this.enemyCoords[1], this.enemyCoords[2] - 1.0), new BlockPos(this.enemyCoords[0] - 1.0, this.enemyCoords[1], this.enemyCoords[2] + 1.0), new BlockPos(this.enemyCoords[0] + 1.0, this.enemyCoords[1], this.enemyCoords[2] - 1.0) };
        for (final BlockPos pos : array) {
            boolean breakOut = false;
            for (int h = 0; h <= this.minH.getValue(); ++h) {
                if (BlockUtil.checkEntity(pos.up(h))) {
                    breakOut = true;
                    ++i;
                    break;
                }
            }
            if (!breakOut) {
                final double distance_now = AutoAnvil.mc.player.getDistanceSq(pos);
                if (distance_now < min_found) {
                    min_found = distance_now;
                    cor = i;
                }
                ++i;
            }
        }
        if (cor == -1) {
            return false;
        }
        final List<Vec3d> baseList = new ArrayList<Vec3d>();
        baseList.add(new Vec3d(this.model[cor][0], this.model[cor][1] - 1, this.model[cor][2]));
        baseList.add(new Vec3d(this.model[cor][0], this.model[cor][1], this.model[cor][2]));
        while (true) {
            for (int incr = 1; incr != this.maxH.getValue(); ++incr) {
                if (!(BlockUtil.getBlock(this.enemyCoords[0], this.enemyCoords[1] + incr, this.enemyCoords[2]) instanceof BlockAir) || incr >= hDistanceMod) {
                    final boolean possible = incr >= this.minH.getValue() && incr <= this.maxH.getValue();
                    final BlockPos targetPos = new BlockPos(this.enemyCoords[0], this.enemyCoords[1], this.enemyCoords[2]);
                    final double x = AutoAnvil.mc.player.getDistanceSq(new BlockPos(targetPos).add(this.model[cor][0], 0, 0));
                    final double z = AutoAnvil.mc.player.getDistanceSq(new BlockPos(targetPos).add(0, 0, this.model[cor][2]));
                    Vec3d base = new Vec3d(this.model[cor][0], this.model[cor][1] + incr - 1, 0.0);
                    if (x > z) {
                        base = new Vec3d(0.0, this.model[cor][1] + incr - 1, this.model[cor][2]);
                    }
                    this.base = targetPos.add(base.x, base.y, base.z);
                    AutoAnvil.to_place.add(base);
                    final double yRef = base.y;
                    if (BurrowUtil.getFirstFacing(targetPos.add(0.0, yRef, 0.0)) == null) {
                        AutoAnvil.to_place.addAll(baseList);
                    }
                    AutoAnvil.to_place.add(new Vec3d(0.0, yRef, 0.0));
                    return possible;
                }
                baseList.add(new Vec3d(this.model[cor][0], this.model[cor][1] + incr, this.model[cor][2]));
            }
            continue;
        }
    }
    
    static {
        AutoAnvil.to_place = new ArrayList<Vec3d>();
    }
}
