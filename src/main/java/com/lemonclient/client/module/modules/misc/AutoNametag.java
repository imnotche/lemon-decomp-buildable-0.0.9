// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.Arrays;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoNametag", category = Category.Misc)
public class AutoNametag extends Module
{
    ModeSetting modeSetting;
    DoubleSetting range;
    BooleanSetting packetSwitch;
    BooleanSetting check;
    BooleanSetting disable;
    private String currentName;
    private int currentSlot;
    
    public AutoNametag() {
        this.modeSetting = this.registerMode("Mode", Arrays.asList("Any", "Wither"), "Wither");
        this.range = this.registerDouble("Range", 3.5, 0.0, 10.0);
        this.packetSwitch = this.registerBoolean("Packet Switch", true);
        this.check = this.registerBoolean("Switch Check", true);
        this.disable = this.registerBoolean("Auto Disable", true);
        this.currentName = "";
        this.currentSlot = -1;
    }
    
    @Override
    public void onUpdate() {
        this.findNameTags();
        this.useNameTag();
    }
    
    private void switchTo(final int slot) {
        if (slot > -1 && slot < 9 && (!this.check.getValue() || AutoNametag.mc.player.inventory.currentItem != slot)) {
            if (this.packetSwitch.getValue()) {
                AutoNametag.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                AutoNametag.mc.player.inventory.currentItem = slot;
            }
            AutoNametag.mc.playerController.updateController();
        }
    }
    
    private void useNameTag() {
        final int originalSlot = AutoNametag.mc.player.inventory.currentItem;
        for (final Entity w : AutoNametag.mc.world.getLoadedEntityList()) {
            final String s = this.modeSetting.getValue();
            switch (s) {
                case "Wither": {
                    if (w instanceof EntityWither && !w.getDisplayName().getUnformattedText().equals(this.currentName) && AutoNametag.mc.player.getDistance(w) <= this.range.getValue()) {
                        final int oldslot = AutoNametag.mc.player.inventory.currentItem;
                        this.selectNameTags();
                        AutoNametag.mc.playerController.interactWithEntity(AutoNametag.mc.player, w, EnumHand.MAIN_HAND);
                        this.switchTo(oldslot);
                        continue;
                    }
                    continue;
                }
                case "Any": {
                    if ((w instanceof EntityMob || w instanceof EntityAnimal) && !w.getDisplayName().getUnformattedText().equals(this.currentName) && AutoNametag.mc.player.getDistance(w) <= this.range.getValue()) {
                        final int oldslot = AutoNametag.mc.player.inventory.currentItem;
                        this.selectNameTags();
                        AutoNametag.mc.playerController.interactWithEntity(AutoNametag.mc.player, w, EnumHand.MAIN_HAND);
                        this.switchTo(oldslot);
                        continue;
                    }
                    continue;
                }
            }
        }
        AutoNametag.mc.player.inventory.currentItem = originalSlot;
    }
    
    private void selectNameTags() {
        if (this.currentSlot == -1 || !this.isNametag(this.currentSlot)) {
            if (this.disable.getValue()) {
                this.disable();
            }
            return;
        }
        this.switchTo(this.currentSlot);
    }
    
    private void findNameTags() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoNametag.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (!(stack.getItem() instanceof ItemBlock)) {
                    if (this.isNametag(i)) {
                        this.currentName = stack.getDisplayName();
                        this.currentSlot = i;
                    }
                }
            }
        }
    }
    
    private boolean isNametag(final int i) {
        final ItemStack stack = AutoNametag.mc.player.inventory.getStackInSlot(i);
        final Item tag = stack.getItem();
        return tag instanceof ItemNameTag && !stack.getDisplayName().equals("Name Tag");
    }
}
