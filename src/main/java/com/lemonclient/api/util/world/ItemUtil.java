// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.world;

import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;

public class ItemUtil
{
    private static final Minecraft mc;
    
    public static boolean isArmorUnderPercent(final EntityPlayer player, final float percent) {
        for (int i = 3; i >= 0; --i) {
            final ItemStack stack = player.inventory.armorInventory.get(i);
            if (getDamageInPercent(stack) < percent) {
                return true;
            }
        }
        return false;
    }
    
    public static int getItemFromHotbar(final Class<?> clazz) {
        int slot = -1;
        for (int i = 8; i >= 0; --i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem().getClass() == clazz) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int getItemFromHotbar(final Item item) {
        int slot = -1;
        for (int i = 8; i >= 0; --i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int getBlockFromHotbar(final Block block) {
        int slot = -1;
        for (int i = 8; i >= 0; --i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(block)) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int getItemSlot(final Item item) {
        int slot = -1;
        for (int i = 44; i >= 0; --i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                if (i < 9) {
                    i += 36;
                }
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int getItemCount(final Item item) {
        int count = 0;
        for (int i = 44; i >= 0; --i) {
            final ItemStack stack = ItemUtil.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        if (ItemUtil.mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() == item) {
            count += ItemUtil.mc.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getCount();
        }
        return count;
    }
    
    public static int getRoundedDamage(final ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }
    
    public static boolean hasDurability(final ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }
    
    public static float getDamageInPercent(final ItemStack stack) {
        final float green = (stack.getMaxDamage() - (float)stack.getItemDamage()) / stack.getMaxDamage();
        final float red = 1.0f - green;
        return (float)(100 - (int)(red * 100.0f));
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
