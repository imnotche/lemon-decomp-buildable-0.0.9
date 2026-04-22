// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.util.world.combat.CrystalUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.Entity;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.properties.IProperty;
import java.util.Iterator;
import net.minecraft.block.BlockPistonBase;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.init.Blocks;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.util.math.Vec3d;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.PlacementUtil;
import com.lemonclient.api.util.player.SpoofRotationUtil;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Blocker", category = Category.Combat)
public class Blocker extends Module
{
    ModeSetting time;
    ModeSetting breakType;
    BooleanSetting packet;
    BooleanSetting swing;
    BooleanSetting rotate;
    BooleanSetting packetSwitch;
    BooleanSetting anvilBlocker;
    BooleanSetting fallingBlocks;
    BooleanSetting trap;
    ModeSetting fallingMode;
    BooleanSetting pistonBlocker;
    BooleanSetting pistonBlockerNew;
    BooleanSetting antiFacePlace;
    ModeSetting blockPlaced;
    IntegerSetting BlocksPerTick;
    IntegerSetting tickDelay;
    DoubleSetting range;
    DoubleSetting yrange;
    List<BlockPos> pistonList;
    private int delayTimeTicks;
    BlockPos[] sides;
    
    public Blocker() {
        this.time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Both", "Fast"), "Tick");
        this.breakType = this.registerMode("Type", Arrays.asList("Vanilla", "Packet"), "Vanilla");
        this.packet = this.registerBoolean("Packet Place", false);
        this.swing = this.registerBoolean("Swing", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.anvilBlocker = this.registerBoolean("Anvil", true);
        this.fallingBlocks = this.registerBoolean("Block FallingBlocks", true);
        this.trap = this.registerBoolean("Trap", true, () -> this.fallingBlocks.getValue());
        this.fallingMode = this.registerMode("Block Mode", Arrays.asList("Break", "Torch", "Skull"), "Break", () -> this.fallingBlocks.getValue());
        this.pistonBlocker = this.registerBoolean("Break Piston", true);
        this.pistonBlockerNew = this.registerBoolean("Block Piston", true);
        this.antiFacePlace = this.registerBoolean("Shift AntiFacePlace", true);
        this.blockPlaced = this.registerMode("Block Place", Arrays.asList("Pressure", "String"), "String", () -> this.antiFacePlace.getValue());
        this.BlocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 10);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.range = this.registerDouble("Range", 5.0, 0.0, 10.0);
        this.yrange = this.registerDouble("YRange", 5.0, 0.0, 10.0);
        this.pistonList = new ArrayList<BlockPos>();
        this.delayTimeTicks = 0;
        this.sides = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
    }
    
    public void onEnable() {
        this.pistonList = new ArrayList<BlockPos>();
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
    }
    
    public void onDisable() {
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();
    }
    
    @Override
    public void onUpdate() {
        if (this.time.getValue().equals("onUpdate") || this.time.getValue().equals("Both")) {
            this.block();
        }
    }
    
    @Override
    public void onTick() {
        if (this.time.getValue().equals("Tick") || this.time.getValue().equals("Both")) {
            this.block();
        }
    }
    
    @Override
    public void fast() {
        if (this.time.getValue().equals("Fast")) {
            this.block();
        }
    }
    
    private void block() {
        if (Blocker.mc.player == null || Blocker.mc.world == null || Blocker.mc.player.isDead) {
            this.pistonList.clear();
            return;
        }
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
        }
        else {
            SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
            this.delayTimeTicks = 0;
            if (this.anvilBlocker.getValue()) {
                this.blockAnvil();
            }
            if (this.fallingBlocks.getValue()) {
                this.blockFallingBlocks();
            }
            if (this.pistonBlocker.getValue()) {
                this.blockPiston();
            }
            if (this.pistonBlockerNew.getValue()) {
                this.blockPA();
            }
            if (this.antiFacePlace.getValue() && Blocker.mc.gameSettings.keyBindSneak.isPressed()) {
                this.antiFacePlace();
            }
        }
    }
    
    private List<BlockPos> posList() {
        return EntityUtil.getSphere(PlayerUtil.getPlayerPos(), this.range.getValue(), this.yrange.getValue(), false, false, 0);
    }
    
    private void antiFacePlace() {
        int blocksPlaced = 0;
        for (final Vec3d surround : new Vec3d[] { new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0) }) {
            final BlockPos pos = new BlockPos(Blocker.mc.player.posX + surround.x, Blocker.mc.player.posY, Blocker.mc.player.posZ + surround.z);
            final Block temp;
            if ((temp = BlockUtil.getBlock(pos)) instanceof BlockObsidian || temp == Blocks.BEDROCK) {
                if (blocksPlaced++ == 0) {
                    InventoryUtil.getHotBarPressure(this.blockPlaced.getValue());
                }
                PlacementUtil.placeItem(new BlockPos(pos.getX(), pos.getY() + surround.y, pos.getZ()), EnumHand.MAIN_HAND, this.rotate.getValue(), Items.STRING.getClass());
                if (blocksPlaced == this.BlocksPerTick.getValue()) {
                    return;
                }
            }
        }
    }
    
    private void blockPA() {
        final int slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (slot == -1) {
            return;
        }
        for (final BlockPos pos : this.posList()) {
            if (!this.pistonList.contains(pos) && (Blocker.mc.world.getBlockState(pos).getBlock() instanceof BlockPistonBase || Blocker.mc.world.getBlockState(pos).getBlock() == Blocks.PISTON || Blocker.mc.world.getBlockState(pos).getBlock() == Blocks.STICKY_PISTON)) {
                this.pistonList.add(pos);
            }
        }
        this.pistonList.removeIf(blockPos -> Blocker.mc.player.getDistanceSq(blockPos) > this.range.getValue() * this.range.getValue());
        if (!this.pistonList.isEmpty()) {
            InventoryUtil.run(slot, this.packetSwitch.getValue(), () -> {
                for (final BlockPos pos2 : this.pistonList) {
                    final BlockPos head = this.getHeadPos(pos2);
                    if (!BlockUtil.canReplace(pos2) && !BlockUtil.canReplace(head)) {
                        continue;
                    }
                    else {
                        BurrowUtil.placeBlock(pos2, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                        BurrowUtil.placeBlock(head, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                    }
                }
            });
        }
        this.pistonList.removeIf(blockPos -> Blocker.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN);
    }
    
    public BlockPos getHeadPos(final BlockPos pos) {
        final ImmutableMap<IProperty<?>, Comparable<?>> properties = Blocker.mc.world.getBlockState(pos).getProperties();
        for (final IProperty<?> prop : properties.keySet()) {
            if (prop.getValueClass() == EnumFacing.class && (prop.getName().equals("facing") || prop.getName().equals("rotation"))) {
                final BlockPos pushPos = pos.offset((EnumFacing)properties.get(prop));
                for (final BlockPos side : this.sides) {
                    if (this.isPos2(pos.add(side), pushPos)) {
                        return pos.add(side);
                    }
                }
            }
        }
        return null;
    }
    
    private boolean isPos2(final BlockPos pos1, final BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.x == pos2.x && pos1.y == pos2.y && pos1.z == pos2.z;
    }
    
    private void blockAnvil() {
        for (final Entity t : Blocker.mc.world.loadedEntityList) {
            if (t instanceof EntityFallingBlock) {
                final Block ex = ((EntityFallingBlock)t).fallTile.getBlock();
                if (!(ex instanceof BlockAnvil) || (int)t.posX != (int)Blocker.mc.player.posX || (int)t.posZ != (int)Blocker.mc.player.posZ || !(BlockUtil.getBlock(Blocker.mc.player.posX, Blocker.mc.player.posY + 2.0, Blocker.mc.player.posZ) instanceof BlockAir)) {
                    continue;
                }
                this.placeBlock(new BlockPos(Blocker.mc.player.posX, Blocker.mc.player.posY + 2.0, Blocker.mc.player.posZ));
            }
        }
    }
    
    private void blockFallingBlocks() {
        for (final Entity t : Blocker.mc.world.loadedEntityList) {
            if (t instanceof EntityFallingBlock) {
                final Block ex = ((EntityFallingBlock)t).fallTile.getBlock();
                if (ex instanceof BlockAnvil || (int)t.posX != (int)Blocker.mc.player.posX || (int)t.posZ != (int)Blocker.mc.player.posZ || (int)t.posY <= (int)Blocker.mc.player.posY) {
                    continue;
                }
                if (this.trap.getValue()) {
                    this.placeBlock(new BlockPos(Blocker.mc.player.posX, Blocker.mc.player.posY + 2.0, Blocker.mc.player.posZ));
                }
                int slot = -1;
                final String s = this.fallingMode.getValue();
                switch (s) {
                    case "Torch": {
                        slot = BurrowUtil.findHotbarBlock(BlockRedstoneTorch.class);
                        break;
                    }
                    case "Skull": {
                        slot = InventoryUtil.findSkullSlot();
                        break;
                    }
                }
                if (slot != -1) {
                    InventoryUtil.run(slot, this.packetSwitch.getValue(), () -> BurrowUtil.placeBlock(PlayerUtil.getPlayerPos(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue()));
                }
                else {
                    Blocker.mc.playerController.onPlayerDamageBlock(PlayerUtil.getPlayerPos(), EnumFacing.UP);
                }
            }
        }
    }
    
    private void blockPiston() {
        for (final Entity t : Blocker.mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && t.posX >= Blocker.mc.player.posX - 1.5 && t.posX <= Blocker.mc.player.posX + 1.5 && t.posZ >= Blocker.mc.player.posZ - 1.5 && t.posZ <= Blocker.mc.player.posZ + 1.5) {
                for (int i = -2; i < 3; ++i) {
                    for (int j = -2; j < 3; ++j) {
                        if ((i == 0 || j == 0) && BlockUtil.getBlock(t.posX + i, t.posY, t.posZ + j) instanceof BlockPistonBase) {
                            this.breakCrystalPiston(t);
                        }
                    }
                }
            }
        }
    }
    
    private void placeBlock(final BlockPos pos) {
        if (!Blocker.mc.world.isAirBlock(pos)) {
            return;
        }
        final int obsidianSlot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        if (obsidianSlot == -1) {
            return;
        }
        InventoryUtil.run(obsidianSlot, this.packetSwitch.getValue(), () -> {
            boolean isNull = true;
            if (BurrowUtil.getFirstFacing(pos) == null) {
                final BlockPos[] sides = this.sides;
                final int length = sides.length;
                int i = 0;
                while (i < length) {
                    final BlockPos side = sides[i];
                    final BlockPos added = pos.add(side);
                    if (!this.intersectsWithEntity(added) && BurrowUtil.getFirstFacing(added) != null) {
                        BurrowUtil.placeBlock(added, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                        isNull = false;
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            else {
                isNull = false;
            }
            if (!isNull) {
                BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
            }
        });
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : Blocker.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
    
    private void breakCrystalPiston(final Entity crystal) {
        if (this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, Blocker.mc.player);
        }
        if (this.breakType.getValue().equals("Vanilla")) {
            CrystalUtil.breakCrystal(crystal, this.swing.getValue());
        }
        else {
            CrystalUtil.breakCrystalPacket(crystal, this.swing.getValue());
        }
        if (this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.resetRotation();
        }
    }
}
