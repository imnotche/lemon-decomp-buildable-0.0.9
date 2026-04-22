// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import java.util.Collection;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.misc.CrystalUtil;
import net.minecraft.block.BlockEnderChest;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import java.util.function.Predicate;
import net.minecraft.util.MovementInputFromOptions;
import java.util.ArrayList;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.InputUpdateEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.List;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Surround", category = Category.Combat)
public class Surround extends Module
{
    ModeSetting time;
    BooleanSetting once;
    BooleanSetting echest;
    BooleanSetting floor;
    IntegerSetting delay;
    IntegerSetting range;
    IntegerSetting bpt;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting packetSwitch;
    BooleanSetting forceBase;
    BooleanSetting breakCrystal;
    BooleanSetting packetBreak;
    BooleanSetting antiWeakness;
    BooleanSetting weakBypass;
    BooleanSetting silent;
    List<EntityEnderCrystal> crystals;
    List<BlockPos> surround;
    List<BlockPos> hasEntity;
    List<BlockPos> posList;
    List<BlockPos> floorPos;
    int placed;
    int waited;
    int slot;
    double y;
    BlockPos[] sides;
    BlockPos[] neighbour;
    @EventHandler
    private final Listener<InputUpdateEvent> inputUpdateEventListener;
    
    public Surround() {
        this.time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Fast"), "Tick");
        this.once = this.registerBoolean("Once", true);
        this.echest = this.registerBoolean("Ender Chest", true);
        this.floor = this.registerBoolean("Floor", true);
        this.delay = this.registerInteger("Delay", 0, 0, 20);
        this.range = this.registerInteger("Range", 5, 0, 10);
        this.bpt = this.registerInteger("BlocksPerTick", 4, 0, 20);
        this.rotate = this.registerBoolean("Rotate", false);
        this.packet = this.registerBoolean("Packet Place", false);
        this.swing = this.registerBoolean("Swing", false);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.forceBase = this.registerBoolean("Force Base", false);
        this.breakCrystal = this.registerBoolean("Break Crystal", false);
        this.packetBreak = this.registerBoolean("Packet Break", false, () -> this.breakCrystal.getValue());
        this.antiWeakness = this.registerBoolean("Anti Weakness", false, () -> this.breakCrystal.getValue());
        this.weakBypass = this.registerBoolean("Bypass Switch", false, () -> this.breakCrystal.getValue());
        this.silent = this.registerBoolean("Silent Switch", false, () -> !this.weakBypass.getValue() && this.breakCrystal.getValue());
        this.crystals = new ArrayList<EntityEnderCrystal>();
        this.surround = new ArrayList<BlockPos>();
        this.hasEntity = new ArrayList<BlockPos>();
        this.posList = new ArrayList<BlockPos>();
        this.floorPos = new ArrayList<BlockPos>();
        this.sides = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1) };
        this.neighbour = new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 1, 0) };
        this.inputUpdateEventListener = new Listener<InputUpdateEvent>(event -> {
            if (Surround.mc.player == null || Surround.mc.world == null) {
                return;
            }
            if (event.getMovementInput() instanceof MovementInputFromOptions) {
                if (event.getMovementInput().jump) {
                    this.disable();
                }
                if (event.getMovementInput().forwardKeyDown || event.getMovementInput().backKeyDown || event.getMovementInput().leftKeyDown || event.getMovementInput().rightKeyDown) {
                    final double posY = Surround.mc.player.posY - this.y;
                    if (posY * posY > 0.25) {
                        this.disable();
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
        if (Surround.mc.world == null || Surround.mc.player == null || Surround.mc.player.isDead) {
            this.disable();
            return;
        }
        this.y = Surround.mc.player.posY;
    }
    
    @Override
    public void onUpdate() {
        if (this.time.getValue().equals("onUpdate")) {
            this.doSurround();
        }
    }
    
    @Override
    public void onTick() {
        if (this.time.getValue().equals("Tick")) {
            this.doSurround();
        }
    }
    
    @Override
    public void fast() {
        if (this.time.getValue().equals("Fast")) {
            this.doSurround();
        }
    }
    
    private void doSurround() {
        if (Surround.mc.world == null || Surround.mc.player == null || Surround.mc.player.isDead) {
            this.disable();
            return;
        }
        this.slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (this.slot == -1 && this.echest.getValue()) {
            this.slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
        }
        if (this.slot == -1) {
            return;
        }
        if (this.waited++ < this.delay.getValue()) {
            return;
        }
        final int n = 0;
        this.placed = n;
        this.waited = n;
        this.calc();
        if (this.breakCrystal.getValue() && !this.crystals.isEmpty()) {
            Entity crystal = null;
            final Iterator<EntityEnderCrystal> iterator = this.crystals.iterator();
            if (iterator.hasNext()) {
                final EntityEnderCrystal enderCrystal = (EntityEnderCrystal)(crystal = iterator.next());
            }
            if (crystal != null) {
                CrystalUtil.breakCrystal(crystal, this.packetBreak.getValue(), this.swing.getValue(), this.packetSwitch.getValue(), this.silent.getValue(), this.antiWeakness.getValue(), this.weakBypass.getValue());
            }
        }
        if (this.floor.getValue()) {
            for (final BlockPos pos : this.floorPos) {
                this.surround.add(pos.down());
            }
        }
        if (this.surround.isEmpty()) {
            return;
        }
        for (final BlockPos pos : this.surround) {
            if (this.placed >= this.bpt.getValue()) {
                break;
            }
            if (!Surround.mc.world.isAirBlock(pos) && Surround.mc.world.getBlockState(pos).getBlock() != Blocks.FIRE && !(Surround.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) {
                continue;
            }
            EnumFacing face = BurrowUtil.getFirstFacing(pos);
            if (face == null || this.forceBase.getValue()) {
                boolean canPlace = false;
                for (final BlockPos side : this.neighbour) {
                    final BlockPos blockPos = pos.add(side);
                    if (!this.intersectsWithEntity(blockPos)) {
                        if (BlockUtil.hasNeighbour(blockPos)) {
                            this.placeBlock(blockPos, BurrowUtil.getFirstFacing(blockPos));
                            canPlace = true;
                            break;
                        }
                    }
                }
                if (!canPlace) {
                    continue;
                }
                face = BurrowUtil.getFirstFacing(pos);
            }
            this.placeBlock(pos, face);
        }
        if (this.once.getValue()) {
            this.disable();
        }
    }
    
    private void placeBlock(final BlockPos pos, final EnumFacing side) {
        if (this.placed >= this.bpt.getValue()) {
            return;
        }
        if (this.intersectsWithEntity(pos)) {
            return;
        }
        if (side == null) {
            return;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if ((BlockUtil.blackList.contains(Surround.mc.world.getBlockState(neighbour).getBlock()) || BlockUtil.shulkerList.contains(Surround.mc.world.getBlockState(neighbour).getBlock())) && !Surround.mc.player.isSneaking()) {
            Surround.mc.player.connection.sendPacket(new CPacketEntityAction(Surround.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            Surround.mc.player.setSneaking(true);
        }
        if (this.rotate.getValue()) {
            BurrowUtil.faceVector(hitVec, true);
        }
        InventoryUtil.run(this.slot, this.packetSwitch.getValue(), () -> BurrowUtil.rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite, this.packet.getValue(), this.swing.getValue()));
        ++this.placed;
    }
    
    private void calc() {
        this.crystals = new ArrayList<EntityEnderCrystal>();
        this.surround = new ArrayList<BlockPos>();
        this.hasEntity = new ArrayList<BlockPos>();
        this.posList = new ArrayList<BlockPos>();
        this.floorPos = new ArrayList<BlockPos>();
        final BlockPos playerPos = PlayerUtil.getPlayerPos();
        this.addPos(playerPos);
        if (playerPos.y != (int)Surround.mc.player.posY) {
            this.addPos(PlayerUtil.getPlayerFloorPos());
        }
        if (!this.hasEntity.isEmpty()) {
            this.entityCalc();
        }
    }
    
    private void entityCalc() {
        (this.posList = new ArrayList<BlockPos>()).addAll(this.hasEntity);
        this.hasEntity = new ArrayList<BlockPos>();
        for (final BlockPos pos : this.posList) {
            this.addPos(pos);
        }
        this.hasEntity.removeIf(blockPos -> blockPos == null || this.floorPos.contains(blockPos) || Surround.mc.player.getDistanceSq(blockPos) > this.range.getValue() * this.range.getValue());
        this.surround.removeIf(blockPos -> blockPos == null || Surround.mc.player.getDistanceSq(blockPos) > this.range.getValue() * this.range.getValue());
        if (!this.hasEntity.isEmpty()) {
            this.entityCalc();
        }
    }
    
    private void addPos(final BlockPos pos) {
        if (this.floorPos.contains(pos)) {
            return;
        }
        for (final BlockPos side : this.sides) {
            final BlockPos blockPos = pos.add(side);
            if (this.intersectsWithEntity(blockPos)) {
                this.hasEntity.add(blockPos);
            }
            else {
                this.surround.add(blockPos);
            }
        }
        this.floorPos.add(pos);
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : Surround.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (!new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                continue;
            }
            if (entity instanceof EntityEnderCrystal) {
                this.crystals.add((EntityEnderCrystal)entity);
            }
            else {
                if (entity instanceof EntityPlayer) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
}
