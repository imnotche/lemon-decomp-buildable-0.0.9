// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.player;

import net.minecraft.init.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.block.state.IBlockState;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemSkull;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import java.util.List;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;

public class InventoryUtil
{
    private static final Minecraft mc;
    public static final ItemStack ILLEGAL_STACK;
    
    public static void run(final int slot, final boolean packetSwitch, final Runnable runnable) {
        final int oldslot = InventoryUtil.mc.player.inventory.currentItem;
        if (slot < 0 || slot == oldslot) {
            runnable.run();
        }
        else {
            if (packetSwitch) {
                packetSwitch(slot);
            }
            else {
                switchSlot(slot);
            }
            runnable.run();
            if (packetSwitch) {
                packetSwitch(oldslot);
            }
            else {
                switchSlot(oldslot);
            }
            InventoryUtil.mc.player.openContainer.detectAndSendChanges();
        }
    }
    
    public static void switchSlot(final int slot) {
        InventoryUtil.mc.player.inventory.currentItem = slot;
        InventoryUtil.mc.playerController.updateController();
    }
    
    public static void packetSwitch(final int slot) {
        InventoryUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }
    
    public static void switchToBypass(final int slot) {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
            if (InventoryUtil.mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
                final int lastSlot = InventoryUtil.mc.player.inventory.currentItem;
                final int targetSlot = hotbarToInventory(slot);
                final int currentSlot = hotbarToInventory(lastSlot);
                InventoryUtil.mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
                InventoryUtil.mc.playerController.windowClick(0, currentSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
                InventoryUtil.mc.playerController.windowClick(0, targetSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
            }
        });
    }
    
    public static void switchToBypassAlt(final int slot) {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
            if (InventoryUtil.mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> InventoryUtil.mc.playerController.windowClick(0, slot, InventoryUtil.mc.player.inventory.currentItem, ClickType.SWAP, InventoryUtil.mc.player));
            }
        });
    }
    
    public static void bypassSwitch(final int slot) {
        if (slot >= 0) {
            InventoryUtil.mc.playerController.pickItem(slot);
        }
    }
    
    public static int hotbarToInventory(final int slot) {
        if (slot == -2) {
            return 45;
        }
        if (slot > -1 && slot < 9) {
            return 36 + slot;
        }
        return slot;
    }
    
    public static void swap(final int InvSlot, final int newSlot) {
        InventoryUtil.mc.playerController.windowClick(0, InvSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
        InventoryUtil.mc.playerController.windowClick(0, newSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
        InventoryUtil.mc.playerController.windowClick(0, InvSlot, 0, ClickType.PICKUP, InventoryUtil.mc.player);
        InventoryUtil.mc.playerController.updateController();
    }
    
    public static int getHotBarPressure(final String mode) {
        for (int i = 0; i < 9; ++i) {
            if (mode.equals("Pressure")) {
                if (isPressure(InventoryUtil.mc.player.inventory.getStackInSlot(i))) {
                    return i;
                }
            }
            else if (isString(InventoryUtil.mc.player.inventory.getStackInSlot(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean isString(final ItemStack stack) {
        return stack != ItemStack.EMPTY && !(stack.getItem() instanceof ItemBlock) && stack.getItem() == Items.STRING;
    }
    
    public static boolean isPressure(final ItemStack stack) {
        return stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() instanceof BlockPressurePlate;
    }
    
    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        final HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = 9; current <= 44; ++current) {
            fullInventorySlots.put(current, InventoryUtil.mc.player.inventoryContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }
    
    public static boolean isBlock(final Item item, final Class clazz) {
        if (item instanceof ItemBlock) {
            final Block block = ((ItemBlock)item).getBlock();
            return clazz.isInstance(block);
        }
        return false;
    }
    
    public static void click(final int windowIdIn, final int slotIdIn, final int usedButtonIn, final ClickType modeIn, final ItemStack clickedItemIn, final short actionNumberIn) {
        InventoryUtil.mc.player.connection.sendPacket(new CPacketClickWindow(windowIdIn, slotIdIn, usedButtonIn, modeIn, clickedItemIn, actionNumberIn));
    }
    
    public static int findCrystalBlockSlot() {
        int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block.getBlockState().getBlock().blockHardness > 6.0f) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static void illegalSync() {
        if (InventoryUtil.mc.player != null) {
            click(0, 0, 0, ClickType.PICKUP, InventoryUtil.ILLEGAL_STACK, (short)0);
        }
    }
    
    public static int findObsidianSlot(final boolean offHandActived, final boolean activeBefore) {
        int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static int findEChestSlot(final boolean offHandActived, final boolean activeBefore) {
        int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockEnderChest) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static int findSkullSlot() {
        final int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemSkull) {
                return i;
            }
        }
        return slot;
    }
    
    public static int findTotemSlot(final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int findFirstItemSlot(final Class<? extends Item> itemToFind, final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY) {
                if (itemToFind.isInstance(stack.getItem())) {
                    if (itemToFind.isInstance(stack.getItem())) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static int findStackInventory(final Item input, final boolean withHotbar) {
        for (int i = withHotbar ? 0 : 9; i < 36; ++i) {
            final Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(input) == Item.getIdFromItem(item)) {
                return i + ((i < 9) ? 36 : 0);
            }
        }
        return -1;
    }
    
    public static int getItemSlot(final Item input) {
        if (InventoryUtil.mc.player == null) {
            return 0;
        }
        for (int i = 0; i < InventoryUtil.mc.player.inventoryContainer.getInventory().size(); ++i) {
            if (i != 0 && i != 5 && i != 6 && i != 7 && i != 8) {
                final ItemStack s = InventoryUtil.mc.player.inventoryContainer.getInventory().get(i);
                if (!s.isEmpty() && s.getItem() == input) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static int getItemInHotbar(final Item p_Item) {
        for (int l_I = 0; l_I < 9; ++l_I) {
            final ItemStack l_Stack = InventoryUtil.mc.player.inventory.getStackInSlot(l_I);
            if (l_Stack != ItemStack.EMPTY && l_Stack.getItem() == p_Item) {
                return l_I;
            }
        }
        return -1;
    }
    
    public static int getPotion(final String potion) {
        for (int l_I = 0; l_I < 36; ++l_I) {
            final ItemStack l_Stack = InventoryUtil.mc.player.inventory.getStackInSlot(l_I);
            if (l_Stack != ItemStack.EMPTY) {
                if (l_Stack.getItem() == Items.SPLASH_POTION) {
                    if (Objects.requireNonNull(PotionUtils.getPotionFromItem(InventoryUtil.mc.player.inventory.getStackInSlot(l_I)).getRegistryName()).getPath().contains(potion)) {
                        return l_I;
                    }
                }
            }
        }
        return -1;
    }
    
    public static int findFirstBlockSlot(final Class<? extends Block> blockToFind, final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    if (blockToFind.isInstance(((ItemBlock)stack.getItem()).getBlock())) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static List<Integer> findAllItemSlots(final Class<? extends Item> itemToFind) {
        final List<Integer> slots = new ArrayList<Integer>();
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = 0; i < 36; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY) {
                if (itemToFind.isInstance(stack.getItem())) {
                    slots.add(i);
                }
            }
        }
        return slots;
    }
    
    public static List<Integer> findAllBlockSlots(final Class<? extends Block> blockToFind) {
        final List<Integer> slots = new ArrayList<Integer>();
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        for (int i = 0; i < 36; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    if (blockToFind.isInstance(((ItemBlock)stack.getItem()).getBlock())) {
                        slots.add(i);
                    }
                }
            }
        }
        return slots;
    }
    
    public static int findToolForBlockState(final IBlockState iBlockState, final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = InventoryUtil.mc.player.inventory.mainInventory;
        double foundMaxSpeed = 0.0;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack itemStack = mainInventory.get(i);
            if (itemStack != ItemStack.EMPTY) {
                float breakSpeed = itemStack.getDestroySpeed(iBlockState);
                final int efficiencySpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack);
                if (breakSpeed > 1.0f) {
                    breakSpeed += (float)((efficiencySpeed > 0) ? (Math.pow(efficiencySpeed, 2.0) + 1.0) : 0.0);
                    if (breakSpeed > foundMaxSpeed) {
                        foundMaxSpeed = breakSpeed;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }
    
    public static int getEmptyCounts() {
        if (InventoryUtil.mc.player == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i <= 35; ++i) {
            final ItemStack stack = InventoryUtil.mc.player.inventory.mainInventory.get(i);
            if (stack == ItemStack.EMPTY || stack.getItem() == Items.AIR) {
                ++count;
            }
        }
        return count;
    }
    
    static {
        mc = Minecraft.getMinecraft();
        ILLEGAL_STACK = new ItemStack(Item.getItemFromBlock(Blocks.BEDROCK));
    }
}
