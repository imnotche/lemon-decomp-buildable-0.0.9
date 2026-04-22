// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import com.lemonclient.client.LemonClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.Comparator;
import com.lemonclient.api.util.world.BlockUtil;
import java.util.Iterator;
import java.util.Collection;
import com.lemonclient.api.util.world.EntityUtil;
import java.util.ArrayList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockEnderChest;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "BetterTrap", category = Category.Dev)
public class AutoTrap extends Module
{
    DoubleSetting range;
    IntegerSetting delay;
    IntegerSetting retryDelay;
    IntegerSetting blocksPerPlace;
    BooleanSetting chest;
    BooleanSetting helpBlocks;
    BooleanSetting only;
    BooleanSetting strict;
    BooleanSetting rotate;
    BooleanSetting raytrace;
    BooleanSetting antiScaffold;
    BooleanSetting antiStep;
    BooleanSetting noGhost;
    BooleanSetting swing;
    BooleanSetting check;
    BooleanSetting packet;
    private final Timing timer;
    private final Map<BlockPos, Integer> retries;
    private final Timing retryTimer;
    public EntityPlayer target;
    private boolean didPlace;
    private int lastHotbarSlot;
    private int placements;
    List<BlockPos> posList;
    
    public AutoTrap() {
        this.range = this.registerDouble("Range", 5.0, 0.0, 10.0);
        this.delay = this.registerInteger("Delay", 50, 0, 500);
        this.retryDelay = this.registerInteger("RetryDelay", 50, 0, 500);
        this.blocksPerPlace = this.registerInteger("BlocksPerTick", 8, 1, 30);
        this.chest = this.registerBoolean("EnderChest", true);
        this.helpBlocks = this.registerBoolean("HelpBlocks", false);
        this.only = this.registerBoolean("OnlyUntrapped", true);
        this.strict = this.registerBoolean("Strict", true);
        this.rotate = this.registerBoolean("Rotate", true);
        this.raytrace = this.registerBoolean("Raytrace", false);
        this.antiScaffold = this.registerBoolean("AntiScaffold", false);
        this.antiStep = this.registerBoolean("AntiStep", false);
        this.noGhost = this.registerBoolean("Packet", false);
        this.swing = this.registerBoolean("Swing", false);
        this.check = this.registerBoolean("SwitchCheck", false);
        this.packet = this.registerBoolean("PacketSwitch", false);
        this.timer = new Timing();
        this.retries = new HashMap<BlockPos, Integer>();
        this.retryTimer = new Timing();
        this.didPlace = false;
        this.placements = 0;
    }
    
    public void onEnable() {
        if (AutoTrap.mc.world == null || AutoTrap.mc.player == null || AutoTrap.mc.player.isDead) {
            return;
        }
        this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        this.retries.clear();
    }
    
    @Override
    public void onTick() {
        if (AutoTrap.mc.world == null || AutoTrap.mc.player == null || AutoTrap.mc.player.isDead) {
            return;
        }
        this.doTrap();
    }
    
    private void doTrap() {
        if (this.check()) {
            return;
        }
        this.doStaticTrap();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void doStaticTrap() {
        final int obbySlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        final int eChestSlot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
        final int slot = this.chest.getValue() ? eChestSlot : ((obbySlot == -1) ? eChestSlot : obbySlot);
        if (slot == -1) {
            return;
        }
        final int originalSlot = AutoTrap.mc.player.inventory.currentItem;
        final Vec3d[] sides = { new Vec3d(0.3, 0.5, 0.3), new Vec3d(-0.3, 0.5, 0.3), new Vec3d(0.3, 0.5, -0.3), new Vec3d(-0.3, 0.5, -0.3) };
        final List<Vec3d> placeTargets = new ArrayList<Vec3d>();
        for (final Vec3d vec3d : sides) {
            placeTargets.addAll(EntityUtil.targets(this.target.getPositionVector().add(vec3d), this.antiScaffold.getValue(), this.antiStep.getValue(), false, false, false, this.raytrace.getValue()));
        }
        this.posList = this.placeList(placeTargets, this.target);
        if (!this.posList.isEmpty()) {
            this.switchTo(slot);
            for (final BlockPos pos : this.posList) {
                this.placeBlock(pos);
            }
            this.switchTo(originalSlot);
        }
    }
    
    private List<BlockPos> placeList(final List<Vec3d> list, final EntityPlayer target) {
        list.sort((vec3d, vec3d2) -> Double.compare(AutoTrap.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoTrap.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        final List<BlockPos> posList = new ArrayList<BlockPos>();
        for (final Vec3d vec3d3 : list) {
            final BlockPos position = new BlockPos(vec3d3);
            if (!this.intersectsWithEntity(position)) {
                if (!BlockUtil.isAir(position)) {
                    continue;
                }
                final int placeability = BlockUtil.isPositionPlaceable(position, this.raytrace.getValue());
                if (placeability == 1 && (this.retries.get(position) == null || this.retries.get(position) < 4)) {
                    posList.add(position);
                    this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                    this.retryTimer.reset();
                }
                else {
                    if (placeability != 3 && this.helpBlocks.getValue() && position.getY() == Math.round(target.posY) + 1L) {
                        posList.add(position.down());
                    }
                    posList.add(position);
                }
            }
        }
        posList.sort(Comparator.comparingDouble(pos -> pos.y));
        return posList;
    }
    
    private void switchTo(final int slot) {
        if (slot > -1 && slot < 9 && (!this.check.getValue() || AutoTrap.mc.player.inventory.currentItem != slot)) {
            if (this.packet.getValue()) {
                AutoTrap.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AutoTrap.mc.player.inventory.currentItem = slot;
                AutoTrap.mc.playerController.updateController();
            }
        }
    }
    
    private boolean check() {
        this.didPlace = false;
        this.placements = 0;
        final int obbySlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        final int eChestSlot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
        final int slot = this.chest.getValue() ? eChestSlot : ((obbySlot == -1) ? eChestSlot : obbySlot);
        if (this.retryTimer.passedMs(this.retryDelay.getValue())) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (slot == -1) {
            return true;
        }
        if (AutoTrap.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoTrap.mc.player.inventory.currentItem != obbySlot) {
            this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
        }
        this.target = this.getTarget(this.range.getValue(), this.only.getValue());
        return this.target == null || !this.timer.passedMs(this.delay.getValue());
    }
    
    private EntityPlayer getTarget(final double range, final boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (final EntityPlayer player : AutoTrap.mc.world.playerEntities) {
            if (EntityUtil.isPlayerValid(player, (float)range) && (!trapped || !EntityUtil.isTrapped(player, this.antiScaffold.getValue(), this.antiStep.getValue(), false, false, false))) {
                if (LemonClient.speedUtil.getPlayerSpeed(player) > 15.0) {
                    continue;
                }
                if (target == null) {
                    target = player;
                    distance = AutoTrap.mc.player.getDistanceSq(player);
                }
                else {
                    if (AutoTrap.mc.player.getDistanceSq(player) >= distance) {
                        continue;
                    }
                    target = player;
                    distance = AutoTrap.mc.player.getDistanceSq(player);
                }
            }
        }
        return target;
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoTrap.mc.world.loadedEntityList) {
            if (!entity.isDead && !(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityExpBottle)) {
                if (entity instanceof EntityArrow) {
                    continue;
                }
                if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerPlace.getValue()) {
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.strict.getValue(), this.raytrace.getValue(), this.swing.getValue());
            this.didPlace = true;
            ++this.placements;
        }
    }
}
