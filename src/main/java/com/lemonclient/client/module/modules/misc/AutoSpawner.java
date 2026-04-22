// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.BlockAir;
import java.util.List;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.world.EntityUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.init.Items;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.Item;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.Entity;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoSpawner", category = Category.Misc)
public class AutoSpawner extends Module
{
    ModeSetting useMode;
    BooleanSetting party;
    ModeSetting entityMode;
    BooleanSetting nametagWithers;
    DoubleSetting placeRange;
    IntegerSetting delay;
    BooleanSetting rotate;
    BooleanSetting packetSwitch;
    BooleanSetting check;
    BooleanSetting packet;
    BooleanSetting swing;
    private static boolean isSneaking;
    private BlockPos placeTarget;
    private boolean rotationPlaceableX;
    private boolean rotationPlaceableZ;
    private int bodySlot;
    private int headSlot;
    private int buildStage;
    private int delayStep;
    
    public AutoSpawner() {
        this.useMode = this.registerMode("Use Mode", Arrays.asList("Single", "Spam"), "Spam");
        this.party = this.registerBoolean("Wither Party", false);
        this.entityMode = this.registerMode("Entity Mode", Arrays.asList("Snow", "Iron", "Wither"), "Wither");
        this.nametagWithers = this.registerBoolean("Nametag", true);
        this.placeRange = this.registerDouble("Place Range", 3.5, 1.0, 10.0);
        this.delay = this.registerInteger("Delay", 20, 0, 100);
        this.rotate = this.registerBoolean("Rotate", true);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.check = this.registerBoolean("Switch Check", true);
        this.packet = this.registerBoolean("Packet Place", true);
        this.swing = this.registerBoolean("Swing", true);
    }
    
    private void useNameTag() {
        final int originalSlot = AutoSpawner.mc.player.inventory.currentItem;
        for (final Entity w : AutoSpawner.mc.world.getLoadedEntityList()) {
            if (w instanceof EntityWither && w.getDisplayName().getUnformattedText().equalsIgnoreCase("Wither")) {
                final EntityWither wither = (EntityWither)w;
                if (AutoSpawner.mc.player.getDistance(wither) > this.placeRange.getValue()) {
                    continue;
                }
                this.selectNameTags();
                AutoSpawner.mc.playerController.interactWithEntity(AutoSpawner.mc.player, wither, EnumHand.MAIN_HAND);
            }
        }
        this.switchTo(originalSlot);
    }
    
    private void selectNameTags() {
        int tagSlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoSpawner.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (!(stack.getItem() instanceof ItemBlock)) {
                    final Item tag = stack.getItem();
                    if (tag instanceof ItemNameTag) {
                        tagSlot = i;
                    }
                }
            }
        }
        if (tagSlot == -1) {
            return;
        }
        this.switchTo(tagSlot);
    }
    
    private static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (AutoSpawner.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(AutoSpawner.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = AutoSpawner.mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable() && !(blockState.getBlock() instanceof BlockTallGrass) && !(blockState.getBlock() instanceof BlockDeadBush)) {
                    return side;
                }
            }
        }
        return null;
    }
    
    @Override
    protected void onEnable() {
        this.buildStage = 1;
        this.delayStep = 1;
    }
    
    private boolean checkBlocksInHotbar() {
        this.headSlot = -1;
        this.bodySlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoSpawner.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (this.entityMode.getValue().equals("Wither")) {
                    if (stack.getItem() == Items.SKULL && stack.getItemDamage() == 1) {
                        if (AutoSpawner.mc.player.inventory.getStackInSlot(i).stackSize >= 3) {
                            this.headSlot = i;
                        }
                        continue;
                    }
                    else {
                        if (!(stack.getItem() instanceof ItemBlock)) {
                            continue;
                        }
                        final Block block = ((ItemBlock)stack.getItem()).getBlock();
                        if (block instanceof BlockSoulSand && AutoSpawner.mc.player.inventory.getStackInSlot(i).stackSize >= 4) {
                            this.bodySlot = i;
                        }
                    }
                }
                if (this.entityMode.getValue().equals("Iron")) {
                    if (!(stack.getItem() instanceof ItemBlock)) {
                        continue;
                    }
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if ((block == Blocks.LIT_PUMPKIN || block == Blocks.PUMPKIN) && AutoSpawner.mc.player.inventory.getStackInSlot(i).stackSize >= 1) {
                        this.headSlot = i;
                    }
                    if (block == Blocks.IRON_BLOCK && AutoSpawner.mc.player.inventory.getStackInSlot(i).stackSize >= 4) {
                        this.bodySlot = i;
                    }
                }
                if (this.entityMode.getValue().equals("Snow")) {
                    if (stack.getItem() instanceof ItemBlock) {
                        final Block block = ((ItemBlock)stack.getItem()).getBlock();
                        if ((block == Blocks.LIT_PUMPKIN || block == Blocks.PUMPKIN) && AutoSpawner.mc.player.inventory.getStackInSlot(i).stackSize >= 1) {
                            this.headSlot = i;
                        }
                        if (block == Blocks.SNOW && AutoSpawner.mc.player.inventory.getStackInSlot(i).stackSize >= 2) {
                            this.bodySlot = i;
                        }
                    }
                }
            }
        }
        return this.bodySlot != -1 && this.headSlot != -1;
    }
    
    private boolean testStructure() {
        if (this.entityMode.getValue().equals("Wither")) {
            return this.testWitherStructure();
        }
        if (this.entityMode.getValue().equals("Iron")) {
            return this.testIronGolemStructure();
        }
        return this.entityMode.getValue().equals("Snow") && this.testSnowGolemStructure();
    }
    
    private boolean testWitherStructure() {
        boolean noRotationPlaceable = true;
        this.rotationPlaceableX = true;
        this.rotationPlaceableZ = true;
        boolean isShitGrass = false;
        if (AutoSpawner.mc.world.getBlockState(this.placeTarget) == null) {
            return false;
        }
        final Block block = AutoSpawner.mc.world.getBlockState(this.placeTarget).getBlock();
        if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
        }
        if (getPlaceableSide(this.placeTarget.up()) == null) {
            return false;
        }
        for (final BlockPos pos : BodyParts.bodyBase) {
            if (this.placingIsBlocked(this.placeTarget.add(pos))) {
                noRotationPlaceable = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsX) {
            if (this.placingIsBlocked(this.placeTarget.add(pos)) || this.placingIsBlocked(this.placeTarget.add(pos.down()))) {
                this.rotationPlaceableX = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsZ) {
            if (this.placingIsBlocked(this.placeTarget.add(pos)) || this.placingIsBlocked(this.placeTarget.add(pos.down()))) {
                this.rotationPlaceableZ = false;
            }
        }
        for (final BlockPos pos : BodyParts.headsX) {
            if (this.placingIsBlocked(this.placeTarget.add(pos))) {
                this.rotationPlaceableX = false;
            }
        }
        for (final BlockPos pos : BodyParts.headsZ) {
            if (this.placingIsBlocked(this.placeTarget.add(pos))) {
                this.rotationPlaceableZ = false;
            }
        }
        return !isShitGrass && noRotationPlaceable && (this.rotationPlaceableX || this.rotationPlaceableZ);
    }
    
    private boolean testIronGolemStructure() {
        boolean noRotationPlaceable = true;
        this.rotationPlaceableX = true;
        this.rotationPlaceableZ = true;
        boolean isShitGrass = false;
        if (AutoSpawner.mc.world.getBlockState(this.placeTarget) == null) {
            return false;
        }
        final Block block = AutoSpawner.mc.world.getBlockState(this.placeTarget).getBlock();
        if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
        }
        if (getPlaceableSide(this.placeTarget.up()) == null) {
            return false;
        }
        for (final BlockPos pos : BodyParts.bodyBase) {
            if (this.placingIsBlocked(this.placeTarget.add(pos))) {
                noRotationPlaceable = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsX) {
            if (this.placingIsBlocked(this.placeTarget.add(pos)) || this.placingIsBlocked(this.placeTarget.add(pos.down()))) {
                this.rotationPlaceableX = false;
            }
        }
        for (final BlockPos pos : BodyParts.ArmsZ) {
            if (this.placingIsBlocked(this.placeTarget.add(pos)) || this.placingIsBlocked(this.placeTarget.add(pos.down()))) {
                this.rotationPlaceableZ = false;
            }
        }
        for (final BlockPos pos : BodyParts.head) {
            if (this.placingIsBlocked(this.placeTarget.add(pos))) {
                noRotationPlaceable = false;
            }
        }
        return !isShitGrass && noRotationPlaceable && (this.rotationPlaceableX || this.rotationPlaceableZ);
    }
    
    private boolean testSnowGolemStructure() {
        boolean noRotationPlaceable = true;
        boolean isShitGrass = false;
        if (AutoSpawner.mc.world.getBlockState(this.placeTarget) == null) {
            return false;
        }
        final Block block = AutoSpawner.mc.world.getBlockState(this.placeTarget).getBlock();
        if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
        }
        if (getPlaceableSide(this.placeTarget.up()) == null) {
            return false;
        }
        for (final BlockPos pos : BodyParts.bodyBase) {
            if (this.placingIsBlocked(this.placeTarget.add(pos))) {
                noRotationPlaceable = false;
            }
        }
        for (final BlockPos pos : BodyParts.head) {
            if (this.placingIsBlocked(this.placeTarget.add(pos))) {
                noRotationPlaceable = false;
            }
        }
        return !isShitGrass && noRotationPlaceable;
    }
    
    private void switchTo(final int slot) {
        if (slot > -1 && slot < 9 && (!this.check.getValue() || AutoSpawner.mc.player.inventory.currentItem != slot)) {
            if (this.packetSwitch.getValue()) {
                AutoSpawner.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AutoSpawner.mc.player.inventory.currentItem = slot;
            }
            AutoSpawner.mc.playerController.updateController();
        }
    }
    
    @Override
    public void onUpdate() {
        if (AutoSpawner.mc.world == null || AutoSpawner.mc.player == null || AutoSpawner.mc.player.isDead) {
            return;
        }
        if (this.nametagWithers.getValue() && (this.party.getValue() || (!this.party.getValue() && this.entityMode.getValue().equals("Wither")))) {
            this.useNameTag();
        }
        if (this.buildStage == 1) {
            AutoSpawner.isSneaking = false;
            this.rotationPlaceableX = false;
            this.rotationPlaceableZ = false;
            if (this.party.getValue()) {
                this.entityMode.setValue("Wither");
            }
            if (!this.checkBlocksInHotbar()) {
                if (this.useMode.getValue().equals("Single")) {
                    this.disable();
                }
                return;
            }
            final List<BlockPos> blockPosList = EntityUtil.getSphere(AutoSpawner.mc.player.getPosition().down(), this.placeRange.getValue(), this.placeRange.getValue(), false, true, 0);
            boolean noPositionInArea = true;
            for (final BlockPos pos : blockPosList) {
                this.placeTarget = pos.down();
                if (this.testStructure()) {
                    noPositionInArea = false;
                    break;
                }
            }
            if (noPositionInArea) {
                if (this.useMode.getValue().equals("Single")) {
                    this.disable();
                }
                return;
            }
            final int oldslot = AutoSpawner.mc.player.inventory.currentItem;
            this.switchTo(this.bodySlot);
            for (final BlockPos pos2 : BodyParts.bodyBase) {
                BurrowUtil.placeBlock(this.placeTarget.add(pos2), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
            }
            if (this.entityMode.getValue().equals("Wither") || this.entityMode.getValue().equals("Iron")) {
                if (this.rotationPlaceableX) {
                    for (final BlockPos pos2 : BodyParts.ArmsX) {
                        BurrowUtil.placeBlock(this.placeTarget.add(pos2), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                    }
                }
                else if (this.rotationPlaceableZ) {
                    for (final BlockPos pos2 : BodyParts.ArmsZ) {
                        BurrowUtil.placeBlock(this.placeTarget.add(pos2), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                    }
                }
            }
            this.switchTo(oldslot);
            this.buildStage = 2;
        }
        else if (this.buildStage == 2) {
            final int oldslot2 = AutoSpawner.mc.player.inventory.currentItem;
            this.switchTo(this.headSlot);
            if (this.entityMode.getValue().equals("Wither")) {
                if (this.rotationPlaceableX) {
                    for (final BlockPos pos3 : BodyParts.headsX) {
                        BurrowUtil.placeBlock(this.placeTarget.add(pos3), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                    }
                }
                else if (this.rotationPlaceableZ) {
                    for (final BlockPos pos3 : BodyParts.headsZ) {
                        BurrowUtil.placeBlock(this.placeTarget.add(pos3), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                    }
                }
            }
            if (this.entityMode.getValue().equals("Iron") || this.entityMode.getValue().equals("Snow")) {
                for (final BlockPos pos3 : BodyParts.head) {
                    BurrowUtil.placeBlock(this.placeTarget.add(pos3), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, this.swing.getValue());
                }
            }
            if (AutoSpawner.isSneaking) {
                AutoSpawner.mc.player.connection.sendPacket(new CPacketEntityAction(AutoSpawner.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                AutoSpawner.isSneaking = false;
            }
            if (this.useMode.getValue().equals("Single")) {
                this.disable();
            }
            this.switchTo(oldslot2);
            this.buildStage = 3;
        }
        else if (this.buildStage == 3) {
            if (this.delayStep < this.delay.getValue()) {
                ++this.delayStep;
            }
            else {
                this.delayStep = 1;
                this.buildStage = 1;
            }
        }
    }
    
    private boolean placingIsBlocked(final BlockPos pos) {
        final Block block = AutoSpawner.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir)) {
            return true;
        }
        for (final Entity entity : AutoSpawner.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return true;
            }
        }
        return false;
    }
    
    private static class BodyParts
    {
        private static final BlockPos[] bodyBase;
        private static final BlockPos[] ArmsX;
        private static final BlockPos[] ArmsZ;
        private static final BlockPos[] headsX;
        private static final BlockPos[] headsZ;
        private static final BlockPos[] head;
        
        static {
            bodyBase = new BlockPos[] { new BlockPos(0, 1, 0), new BlockPos(0, 2, 0) };
            ArmsX = new BlockPos[] { new BlockPos(-1, 2, 0), new BlockPos(1, 2, 0) };
            ArmsZ = new BlockPos[] { new BlockPos(0, 2, -1), new BlockPos(0, 2, 1) };
            headsX = new BlockPos[] { new BlockPos(0, 3, 0), new BlockPos(-1, 3, 0), new BlockPos(1, 3, 0) };
            headsZ = new BlockPos[] { new BlockPos(0, 3, 0), new BlockPos(0, 3, -1), new BlockPos(0, 3, 1) };
            head = new BlockPos[] { new BlockPos(0, 3, 0) };
        }
    }
}
