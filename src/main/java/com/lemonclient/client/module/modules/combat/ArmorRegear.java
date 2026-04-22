// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import java.util.Iterator;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityItem;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.entity.EntityLivingBase;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.Comparator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemBlock;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.item.ItemStack;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ArmorRegear", category = Category.Combat)
public class ArmorRegear extends Module
{
    DoubleSetting range;
    IntegerSetting delay;
    BooleanSetting packetSwitch;
    BooleanSetting packetPlace;
    int waited;
    int actionSlot;
    int slot;
    boolean placed;
    ItemStack shulker;
    ShulkerPos pos;
    
    public ArmorRegear() {
        this.range = this.registerDouble("Range", 5.0, 0.0, 10.0);
        this.delay = this.registerInteger("Delay", 1, 0, 20);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.packetPlace = this.registerBoolean("Packet Place", true);
        this.waited = 0;
    }
    
    private void switchTo(final int slot, final Runnable runnable) {
        final int oldslot = ArmorRegear.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
            return;
        }
        if (slot < 9) {
            final boolean packetSwitch = this.packetSwitch.getValue();
            if (packetSwitch) {
                ArmorRegear.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                ArmorRegear.mc.player.inventory.currentItem = slot;
            }
            runnable.run();
            if (packetSwitch) {
                ArmorRegear.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldslot));
            }
            else {
                ArmorRegear.mc.player.inventory.currentItem = oldslot;
            }
        }
    }
    
    private int getSlot(final ItemStack itemStack) {
        final NonNullList<ItemStack> contentItems = NonNullList.withSize(27, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(itemStack.getTagCompound().getCompoundTag("BlockEntityTag"), contentItems);
        for (int i = 0; i < contentItems.size(); ++i) {
            if (contentItems.get(i).getItem() instanceof ItemArmor && contentItems.get(i).getCount() == 127) {
                return i;
            }
        }
        return -1;
    }
    
    private int getSlot(final ItemStack itemStack, final EntityEquipmentSlot equipmentSlot) {
        final NonNullList<ItemStack> contentItems = NonNullList.withSize(27, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(itemStack.getTagCompound().getCompoundTag("BlockEntityTag"), contentItems);
        for (int i = 0; i < contentItems.size(); ++i) {
            final ItemStack stack = contentItems.get(i);
            if (stack.getItem() instanceof ItemArmor && stack.getCount() == 127 && ((ItemArmor)stack.getItem()).armorType.equals(equipmentSlot)) {
                return i;
            }
        }
        return -1;
    }
    
    public void onEnable() {
        this.actionSlot = 5;
        this.slot = -1;
        this.pos = null;
        this.placed = false;
        this.shulker = null;
        for (int slot = 0; slot < 9; ++slot) {
            final ItemStack itemStack = ArmorRegear.mc.player.inventory.getStackInSlot(slot);
            if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockShulkerBox && this.getSlot(itemStack) != -1) {
                this.slot = slot;
                this.shulker = ArmorRegear.mc.player.inventory.getStackInSlot(slot);
                break;
            }
        }
        if (this.slot != -1) {
            this.pos = this.initValues();
        }
        this.placed = (this.pos == null);
    }
    
    private ShulkerPos initValues() {
        final List<BlockPos> blocks = EntityUtil.getSphere(PlayerUtil.getEyesPos(), this.range.getValue() + 1.0, this.range.getValue() + 1.0, false, true, 0);
        blocks.removeIf(p -> ColorMain.INSTANCE.breakList.contains(p));
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
        ShulkerPos blockAim;
        if (target == null) {
            blockAim = posList.stream().min(Comparator.comparing(p -> p.getRange(ArmorRegear.mc.player))).orElse(null);
        }
        else {
            blockAim = posList.stream().max(Comparator.comparing(p -> this.getWeight(p, target))).orElse(null);
        }
        return blockAim;
    }
    
    @Override
    public void onTick() {
        if (this.actionSlot > 8) {
            this.disable();
            return;
        }
        if (this.waited++ < this.delay.getValue()) {
            return;
        }
        if (ArmorRegear.mc.player.inventoryContainer.inventorySlots.get(this.actionSlot).getStack().getCount() == 127) {
            ++this.actionSlot;
            return;
        }
        this.waited = 0;
        if (ArmorRegear.mc.player.openContainer instanceof ContainerShulkerBox && this.slot != -1) {
            final int armorSlot = this.getSlot(this.shulker, fromSlot(this.actionSlot));
            final ItemStack slotStack = ArmorRegear.mc.player.inventory.getStackInSlot(this.slot);
            if (slotStack.isEmpty() || !(slotStack.getItem() instanceof ItemArmor) || !((ItemArmor)slotStack.getItem()).armorType.equals(fromSlot(this.actionSlot)) || slotStack.getCount() != 127) {
                if (!slotStack.isEmpty()) {
                    ArmorRegear.mc.playerController.windowClick(0, this.slot, 1, ClickType.THROW, ArmorRegear.mc.player);
                }
                final ItemStack armorStack = ArmorRegear.mc.player.openContainer.inventorySlots.get(armorSlot).getStack();
                if (!armorStack.isEmpty()) {
                    ArmorRegear.mc.playerController.windowClick(ArmorRegear.mc.player.openContainer.windowId, armorSlot, this.slot, ClickType.SWAP, ArmorRegear.mc.player);
                }
                else {
                    ArmorRegear.mc.player.closeScreen();
                }
                return;
            }
            ArmorRegear.mc.player.closeScreen();
        }
        final List<InvStack> armors = new ArrayList<InvStack>();
        InvStack candidateStack = null;
        for (int slot = 0; slot < 45; ++slot) {
            if (slot <= 4 || slot >= 9) {
                candidateStack = new InvStack(slot, ArmorRegear.mc.player.inventoryContainer.getSlot(slot).getStack());
                if (candidateStack.stack.getItem() instanceof ItemArmor && candidateStack.stack.getCount() == 127) {
                    armors.add(candidateStack);
                }
            }
        }
        final InvStack stack = armors.stream().filter(armorStack -> ((ItemArmor)armorStack.stack.getItem()).armorType.equals(fromSlot(this.actionSlot))).min(Comparator.comparing(armorStack -> armorStack.slot)).orElse(null);
        if (stack == null) {
            if (!this.placed) {
                this.switchTo(this.slot, () -> {
                    boolean sneak = false;
                    if (BlockUtil.blackList.contains(ArmorRegear.mc.world.getBlockState(this.pos.neighbour).getBlock()) && !ArmorRegear.mc.player.isSneaking()) {
                        ArmorRegear.mc.player.connection.sendPacket(new CPacketEntityAction(ArmorRegear.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                        sneak = true;
                    }
                    BurrowUtil.rightClickBlock(this.pos.neighbour, this.pos.vec, EnumHand.MAIN_HAND, this.pos.opposite, this.packetPlace.getValue());
                    if (sneak) {
                        ArmorRegear.mc.player.connection.sendPacket(new CPacketEntityAction(ArmorRegear.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }
                    this.waited = 0;
                });
                this.placed = true;
            }
            else if (this.pos != null && BlockUtil.getBlock(this.pos.pos) instanceof BlockShulkerBox && this.getSlot(this.shulker, fromSlot(this.actionSlot)) != -1) {
                this.openBlock();
            }
            else {
                this.disable();
            }
        }
        else {
            this.swapStack(stack.slot, this.actionSlot);
            ++this.actionSlot;
        }
    }
    
    private void openBlock() {
        final EnumFacing side = EnumFacing.getDirectionFromEntityLiving(this.pos.pos, ArmorRegear.mc.player);
        final BlockPos neighbour = this.pos.pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        final Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        ArmorRegear.mc.player.connection.sendPacket(new CPacketEntityAction(ArmorRegear.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        ArmorRegear.mc.playerController.processRightClickBlock(ArmorRegear.mc.player, ArmorRegear.mc.world, this.pos.pos, opposite, hitVec, EnumHand.MAIN_HAND);
    }
    
    private void swapStack(final int slotFrom, final int slotTo) {
        ArmorRegear.mc.playerController.windowClick(0, slotTo, 1, ClickType.THROW, ArmorRegear.mc.player);
        final int slot = slotFrom - 36;
        InventoryUtil.run(slot, this.packetSwitch.getValue(), () -> ArmorRegear.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
    }
    
    public static ItemStack get(final int slot) {
        if (slot == -2) {
            return ArmorRegear.mc.player.inventory.getItemStack();
        }
        return ArmorRegear.mc.player.inventoryContainer.getInventory().get(slot);
    }
    
    public static EntityEquipmentSlot fromSlot(final int slot) {
        switch (slot) {
            case 5: {
                return EntityEquipmentSlot.HEAD;
            }
            case 6: {
                return EntityEquipmentSlot.CHEST;
            }
            case 7: {
                return EntityEquipmentSlot.LEGS;
            }
            case 8: {
                return EntityEquipmentSlot.FEET;
            }
            default: {
                return null;
            }
        }
    }
    
    private double getWeight(final ShulkerPos pos, final EntityPlayer target) {
        double range = pos.getRange(target);
        if (range >= 4.0) {
            final int y = 256 - pos.pos.getY();
            range += y * 100;
        }
        return range;
    }
    
    private boolean intersectsWithEntity(final BlockPos pos) {
        for (final Entity entity : ArmorRegear.mc.world.loadedEntityList) {
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
                if (BlockUtil.airBlocks.contains(ArmorRegear.mc.world.getBlockState(pos.offset(facing, -1)).getBlock())) {
                    return facing;
                }
            }
        }
        return null;
    }
    
    private boolean inRange(final Vec3d vec) {
        final double x = vec.x - ArmorRegear.mc.player.posX;
        final double z = vec.z - ArmorRegear.mc.player.posZ;
        final double y = vec.y - PlayerUtil.getEyesPos().y;
        final double add = Math.sqrt(y * y) / 2.0;
        return x * x + z * z <= (this.range.getValue() - add) * (this.range.getValue() - add) && y * y <= this.range.getValue() * this.range.getValue();
    }
    
    public static class InvStack
    {
        public final int slot;
        public final ItemStack stack;
        
        public InvStack(final int slot, final ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
        }
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
