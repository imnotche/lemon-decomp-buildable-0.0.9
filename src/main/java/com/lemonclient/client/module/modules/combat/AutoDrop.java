// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemStack;
import java.util.Arrays;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoDrop", category = Category.Combat)
public class AutoDrop extends Module
{
    IntegerSetting delay;
    ModeSetting mode;
    private final Timing timer;
    
    public AutoDrop() {
        this.delay = this.registerInteger("Drop Delay", 10, 0, 20);
        this.mode = this.registerMode("Sharpness", Arrays.asList("Sharp5", "Sharp32k", "Both"), "Both");
        this.timer = new Timing();
    }
    
    @Override
    public void onUpdate() {
        final String s = this.mode.getValue();
        switch (s) {
            case "Sharp32k": {
                if (this.isSuperWeapon(AutoDrop.mc.player.getHeldItemMainhand()) && this.timer.passedDs(this.delay.getValue())) {
                    final boolean holding32k = false;
                    AutoDrop.mc.player.dropItem(!holding32k);
                    this.timer.reset();
                    break;
                }
            }
            case "Both": {
                if (this.checkSword(AutoDrop.mc.player.getHeldItemMainhand()) && this.timer.passedDs(this.delay.getValue())) {
                    final boolean holding = false;
                    AutoDrop.mc.player.dropItem(!holding);
                }
            }
            case "Sharp5": {
                if (this.checkSharpness5(AutoDrop.mc.player.getHeldItemMainhand()) && this.timer.passedDs(this.delay.getValue())) {
                    final boolean holding2 = false;
                    AutoDrop.mc.player.dropItem(!holding2);
                    break;
                }
                break;
            }
        }
    }
    
    private boolean checkSword(final ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return false;
        }
        if (stack.getEnchantmentTagList().getTagType() == 0) {
            return false;
        }
        final NBTTagList enchants = (NBTTagList)stack.getTagCompound().getTag("ench");
        int i = 0;
        while (i < enchants.tagCount()) {
            final NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                final int lvl = enchant.getInteger("lvl");
                if (lvl > 4) {
                    return true;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return false;
    }
    
    private boolean isSuperWeapon(final ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.getTagCompound() == null) {
            return false;
        }
        if (item.getEnchantmentTagList().getTagType() == 0) {
            return false;
        }
        final NBTTagList enchants = (NBTTagList)item.getTagCompound().getTag("ench");
        int i = 0;
        while (i < enchants.tagCount()) {
            final NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                final int lvl = enchant.getInteger("lvl");
                if (lvl >= 16) {
                    return true;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return false;
    }
    
    private boolean checkSharpness5(final ItemStack stack) {
        if (stack.getTagCompound() == null) {
            return false;
        }
        if (stack.getEnchantmentTagList().getTagType() == 0) {
            return false;
        }
        final NBTTagList enchants = (NBTTagList)stack.getTagCompound().getTag("ench");
        int i = 0;
        while (i < enchants.tagCount()) {
            final NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            if (enchant.getInteger("id") == 16) {
                final int lvl = enchant.getInteger("lvl");
                if (lvl == 5) {
                    return true;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return false;
    }
}
