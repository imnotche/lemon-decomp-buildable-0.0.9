// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.item.ItemBlock;
import java.util.ArrayList;
import net.minecraft.item.Item;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionUtils;
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.item.ItemBow;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "Quiver", category = Category.Combat)
public class Quiver extends Module
{
    IntegerSetting tickDelay;
    
    public Quiver() {
        this.tickDelay = this.registerInteger("TickDelay", 3, 0, 8);
    }
    
    @Override
    public void onUpdate() {
        if (Quiver.mc.player != null) {
            if (Quiver.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && Quiver.mc.player.isHandActive() && Quiver.mc.player.getItemInUseMaxCount() >= this.tickDelay.getValue()) {
                Quiver.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(Quiver.mc.player.cameraYaw, -90.0f, Quiver.mc.player.onGround));
                Quiver.mc.playerController.onStoppedUsingItem(Quiver.mc.player);
            }
            final List<Integer> arrowSlots = getItemInventory(Items.TIPPED_ARROW);
            if (arrowSlots.get(0) == -1) {
                return;
            }
            int speedSlot = -1;
            int strengthSlot = -1;
            for (final Integer slot : arrowSlots) {
                if (PotionUtils.getPotionFromItem(Quiver.mc.player.inventory.getStackInSlot(slot)).getRegistryName().getPath().contains("swiftness")) {
                    speedSlot = slot;
                }
                else {
                    if (!Objects.requireNonNull(PotionUtils.getPotionFromItem(Quiver.mc.player.inventory.getStackInSlot(slot)).getRegistryName()).getPath().contains("strength")) {
                        continue;
                    }
                    strengthSlot = slot;
                }
            }
        }
    }
    
    public static List<Integer> getItemInventory(final Item item) {
        final List<Integer> ints = new ArrayList<Integer>();
        for (int i = 9; i < 36; ++i) {
            final Item target = Quiver.mc.player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemBlock && ((ItemBlock)item).getBlock().equals(item)) {
                ints.add(i);
            }
        }
        if (ints.size() == 0) {
            ints.add(-1);
        }
        return ints;
    }
}
