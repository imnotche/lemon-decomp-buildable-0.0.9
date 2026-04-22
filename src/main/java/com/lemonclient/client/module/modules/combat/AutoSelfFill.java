// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockTrapDoor;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.util.EnumHand;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockSlab;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.DeathEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoSelfFill", category = Category.Combat)
public class AutoSelfFill extends Module
{
    IntegerSetting delay;
    BooleanSetting rotate;
    BooleanSetting packet;
    BooleanSetting packetSwitch;
    BooleanSetting swing;
    BooleanSetting obsidian;
    BooleanSetting echest;
    BooleanSetting web;
    BooleanSetting skull;
    BooleanSetting plate;
    BooleanSetting upPlate;
    BooleanSetting trapdoor;
    int new_slot;
    int waited;
    boolean door;
    boolean block;
    @EventHandler
    private final Listener<DeathEvent> deathEventListener;
    
    public AutoSelfFill() {
        this.delay = this.registerInteger("Delay", 10, 0, 50);
        this.rotate = this.registerBoolean("Rotate", true);
        this.packet = this.registerBoolean("Packet Place", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.swing = this.registerBoolean("Swing", true);
        this.obsidian = this.registerBoolean("Obsidian", true);
        this.echest = this.registerBoolean("Ender Chest", true);
        this.web = this.registerBoolean("Web", true);
        this.skull = this.registerBoolean("Skull", true);
        this.plate = this.registerBoolean("Slab", true);
        this.upPlate = this.registerBoolean("Up Slab", true);
        this.trapdoor = this.registerBoolean("Trapdoor", true);
        this.new_slot = -1;
        this.deathEventListener = new Listener<DeathEvent>(event -> {
            if (event.player == AutoSelfFill.mc.player) {
                this.disable();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (this.waited++ < this.delay.getValue()) {
            return;
        }
        this.waited = 0;
        if (BlockUtil.isAir(PlayerUtil.getPlayerPos()) && AutoSelfFill.mc.player.onGround && this.intersectsWithEntity(PlayerUtil.getPlayerPos())) {
            this.placeBlock();
        }
    }
    
    public void placeBlock() {
        this.new_slot = this.find_in_hotbar();
        if (this.new_slot == -1) {
            return;
        }
        InventoryUtil.run(this.new_slot, this.packetSwitch.getValue(), () -> {
            if (this.door) {
                this.placeTrapdoor();
            }
            else if (this.upPlate.getValue() && this.new_slot == BurrowUtil.findHotbarBlock(BlockSlab.class)) {
                this.burrowUp();
            }
            else if (this.block) {
                this.burrow();
            }
            else {
                BurrowUtil.placeBlock(PlayerUtil.getPlayerPos(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
            }
        });
    }
    
    private int find_in_hotbar() {
        final boolean b = false;
        this.block = b;
        this.door = b;
        int newHand = -1;
        if (this.trapdoor.getValue()) {
            newHand = BurrowUtil.findHotbarBlock(BlockTrapDoor.class);
            if (newHand != -1) {
                this.door = true;
            }
        }
        if (newHand == -1 && this.skull.getValue()) {
            newHand = InventoryUtil.findSkullSlot();
        }
        if (newHand == -1 && this.web.getValue()) {
            newHand = BurrowUtil.findHotbarBlock(BlockWeb.class);
        }
        if (newHand == -1 && this.plate.getValue()) {
            newHand = BurrowUtil.findHotbarBlock(BlockSlab.class);
        }
        if (newHand == -1 && this.obsidian.getValue()) {
            newHand = BurrowUtil.findHotbarBlock(BlockObsidian.class);
            if (newHand != -1) {
                this.block = true;
            }
        }
        if (newHand == -1 && this.echest.getValue()) {
            newHand = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
            if (newHand != -1) {
                this.block = true;
            }
        }
        return newHand;
    }
    
    private void placeTrapdoor() {
        final BlockPos originalPos = PlayerUtil.getPlayerPos();
        final EnumFacing facing = BurrowUtil.getTrapdoorFacing(originalPos);
        if (facing == null) {
            return;
        }
        final BlockPos neighbour = originalPos.offset(facing);
        final EnumFacing opposite = facing.getOpposite();
        final double x = AutoSelfFill.mc.player.posX;
        final double y = (int)AutoSelfFill.mc.player.posY;
        final double z = AutoSelfFill.mc.player.posZ;
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + 0.20000000298023224, z, AutoSelfFill.mc.player.onGround));
        BurrowUtil.rightClickBlock(neighbour, opposite, new Vec3d(0.5, 0.8, 0.5), this.packet.getValue(), this.swing.getValue());
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, AutoSelfFill.mc.player.onGround));
    }
    
    private void burrow() {
        final BlockPos originalPos = new BlockPos(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY, AutoSelfFill.mc.player.posZ);
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 0.42, AutoSelfFill.mc.player.posZ, true));
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 0.75, AutoSelfFill.mc.player.posZ, true));
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 1.01, AutoSelfFill.mc.player.posZ, true));
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 1.16, AutoSelfFill.mc.player.posZ, true));
        BurrowUtil.placeBlock(originalPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 1.01, AutoSelfFill.mc.player.posZ, false));
    }
    
    private void burrowUp() {
        final BlockPos originalPos = PlayerUtil.getPlayerPos();
        BlockPos neighbour;
        EnumFacing opposite;
        if (!AutoSelfFill.mc.world.isAirBlock(originalPos.south())) {
            neighbour = originalPos.offset(EnumFacing.SOUTH);
            opposite = EnumFacing.SOUTH.getOpposite();
        }
        else if (!AutoSelfFill.mc.world.isAirBlock(originalPos.north())) {
            neighbour = originalPos.offset(EnumFacing.NORTH);
            opposite = EnumFacing.NORTH.getOpposite();
        }
        else if (!AutoSelfFill.mc.world.isAirBlock(originalPos.east())) {
            neighbour = originalPos.offset(EnumFacing.EAST);
            opposite = EnumFacing.EAST.getOpposite();
        }
        else {
            if (AutoSelfFill.mc.world.isAirBlock(originalPos.west())) {
                return;
            }
            neighbour = originalPos.offset(EnumFacing.WEST);
            opposite = EnumFacing.WEST.getOpposite();
        }
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 0.42, AutoSelfFill.mc.player.posZ, true));
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 0.75, AutoSelfFill.mc.player.posZ, true));
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 1.01, AutoSelfFill.mc.player.posZ, true));
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 1.16, AutoSelfFill.mc.player.posZ, true));
        BurrowUtil.rightClickBlock(neighbour, opposite, new Vec3d(0.5, 0.8, 0.5), this.packet.getValue(), this.swing.getValue());
        AutoSelfFill.mc.player.connection.sendPacket(new CPacketPlayer.Position(AutoSelfFill.mc.player.posX, AutoSelfFill.mc.player.posY + 1.01, AutoSelfFill.mc.player.posZ, false));
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : AutoSelfFill.mc.world.loadedEntityList) {
            if (entity instanceof EntityItem) {
                continue;
            }
            if (entity == AutoSelfFill.mc.player) {
                continue;
            }
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }
        return false;
    }
}
