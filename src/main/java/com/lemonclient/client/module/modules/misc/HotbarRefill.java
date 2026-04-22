// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.item.Item;
import java.util.stream.Collectors;
import java.util.Comparator;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.item.ItemBlock;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import com.lemonclient.api.util.misc.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "HotbarRefill", category = Category.Misc)
public class HotbarRefill extends Module
{
    IntegerSetting threshold;
    IntegerSetting tickDelay;
    private int delayStep;
    
    public HotbarRefill() {
        this.threshold = this.registerInteger("Threshold", 32, 1, 63);
        this.tickDelay = this.registerInteger("Tick Delay", 2, 1, 10);
        this.delayStep = 0;
    }
    
    @Override
    public void onUpdate() {
        if (HotbarRefill.mc.player == null) {
            return;
        }
        if (HotbarRefill.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.delayStep < this.tickDelay.getValue()) {
            ++this.delayStep;
            return;
        }
        this.delayStep = 0;
        final Pair<Integer, Integer> slots = this.findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        final int inventorySlot = slots.getKey();
        final int hotbarSlot = slots.getValue();
        HotbarRefill.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.QUICK_MOVE, HotbarRefill.mc.player);
    }
    
    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        final List<ItemStack> inventory = HotbarRefill.mc.player.inventory.mainInventory;
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            final ItemStack stack = inventory.get(hotbarSlot);
            if (stack.isStackable()) {
                if (!stack.isEmpty) {
                    if (stack.getItem() != Items.AIR) {
                        if (stack.stackSize < stack.getMaxStackSize()) {
                            if (stack.stackSize <= this.threshold.getValue()) {
                                final int inventorySlot = this.findCompatibleInventorySlot(stack);
                                if (inventorySlot != -1) {
                                    return new Pair<Integer, Integer>(inventorySlot, hotbarSlot);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private int findCompatibleInventorySlot(final ItemStack hotbarStack) {
        final Item item = hotbarStack.getItem();
        List<Integer> potentialSlots;
        if (item instanceof ItemBlock) {
            potentialSlots = InventoryUtil.findAllBlockSlots(((ItemBlock)item).getBlock().getClass());
        }
        else {
            potentialSlots = InventoryUtil.findAllItemSlots(item.getClass());
        }
        potentialSlots = potentialSlots.stream().filter(integer -> integer > 8 && integer < 36).sorted(Comparator.comparingInt(integer -> -integer)).collect(Collectors.toList());
        for (final int slot : potentialSlots) {
            if (this.isCompatibleStacks(hotbarStack, HotbarRefill.mc.player.inventory.getStackInSlot(slot))) {
                return slot;
            }
        }
        return -1;
    }
    
    private boolean isCompatibleStacks(final ItemStack stack1, final ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
            final Block block1 = ((ItemBlock)stack1.getItem()).getBlock();
            final Block block2 = ((ItemBlock)stack2.getItem()).getBlock();
            if (!block1.material.equals(block2.material)) {
                return false;
            }
        }
        return stack1.getDisplayName().equals(stack2.getDisplayName()) && stack1.getItemDamage() == stack2.getItemDamage();
    }
}
