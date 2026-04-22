// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.movement;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import net.minecraft.init.Items;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ElytraSwap", category = Category.Movement)
public class ElytraSwap extends Module
{
    public void onEnable() {
        if (ElytraSwap.mc.player != null) {
            final InventoryPlayer items = ElytraSwap.mc.player.inventory;
            final ItemStack body = items.armorItemInSlot(2);
            final String body2 = body.getItem().getItemStackDisplayName(body);
            if (body2.equals("Air")) {
                int t = 0;
                int c = 0;
                for (int i = 9; i < 45; ++i) {
                    if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                        t = i;
                        break;
                    }
                }
                if (t != 0) {
                    MessageBus.sendClientDeleteMessage("Equipping Elytra", Notification.Type.SUCCESS, "ElytraSwap", 1);
                    ElytraSwap.mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                }
                if (t == 0) {
                    for (int i = 9; i < 45; ++i) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() == Items.DIAMOND_CHESTPLATE) {
                            c = i;
                            break;
                        }
                    }
                    if (c != 0) {
                        MessageBus.sendClientDeleteMessage("Equipping Chestplate", Notification.Type.SUCCESS, "ElytraSwap", 1);
                        ElytraSwap.mc.playerController.windowClick(0, c, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                        ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    }
                }
                if (c == 0 && t == 0) {
                    MessageBus.sendClientDeleteMessage("You do not have an Elytra or a Chestplate in your inventory. Doing nothing", Notification.Type.ERROR, "ElytraSwap", 1);
                }
                this.disable();
            }
            if (body2.equals("Elytra")) {
                int t = 0;
                for (int j = 9; j < 45; ++j) {
                    if (ElytraSwap.mc.player.inventory.getStackInSlot(j).getItem() == Items.DIAMOND_CHESTPLATE) {
                        t = j;
                        break;
                    }
                }
                if (t != 0) {
                    int l = 0;
                    MessageBus.sendClientDeleteMessage("Equipping Chestplate", Notification.Type.SUCCESS, "ElytraSwap", 1);
                    ElytraSwap.mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    for (int k = 9; k < 45; ++k) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(k).getItem() == Items.AIR) {
                            l = k;
                            break;
                        }
                    }
                    ElytraSwap.mc.playerController.windowClick(0, l, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                }
                if (t == 0) {
                    MessageBus.sendClientDeleteMessage("You do not have a Chestplate in your inventory. Keeping Elytra equipped", Notification.Type.ERROR, "ElytraSwap", 1);
                }
                this.disable();
            }
            if (body2.equals("Diamond Chestplate")) {
                int t = 0;
                for (int j = 9; j < 45; ++j) {
                    if (ElytraSwap.mc.player.inventory.getStackInSlot(j).getItem() == Items.ELYTRA) {
                        t = j;
                        break;
                    }
                }
                if (t != 0) {
                    int u = 0;
                    MessageBus.sendClientDeleteMessage("Equipping Elytra", Notification.Type.SUCCESS, "ElytraSwap", 1);
                    ElytraSwap.mc.playerController.windowClick(0, t, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    for (int k = 9; k < 45; ++k) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(k).getItem() == Items.AIR) {
                            u = k;
                            break;
                        }
                    }
                    ElytraSwap.mc.playerController.windowClick(0, u, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                }
                if (t == 0) {
                    MessageBus.sendClientDeleteMessage("You do not have a Elytra in your inventory. Keeping Chestplate equipped", Notification.Type.ERROR, "ElytraSwap", 1);
                }
                this.disable();
            }
        }
    }
}
