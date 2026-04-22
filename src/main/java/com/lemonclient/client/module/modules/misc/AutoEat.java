// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.Entity;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoEat", category = Category.Misc)
public class AutoEat extends Module
{
    IntegerSetting health;
    BooleanSetting equal;
    boolean eating;
    
    public AutoEat() {
        this.health = this.registerInteger("Health", 10, 1, 36);
        this.equal = this.registerBoolean("Equal", false);
        this.eating = false;
    }
    
    public void onDisable() {
        this.stopEating();
    }
    
    @Override
    public void onTick() {
        if (EntityUtil.isDead(AutoEat.mc.player)) {
            if (this.eating) {
                this.stopEating();
            }
            return;
        }
        if (this.shouldEat()) {
            EnumHand hand = null;
            if (this.isValid(AutoEat.mc.player.getHeldItemMainhand())) {
                hand = EnumHand.MAIN_HAND;
            }
            if (this.isValid(AutoEat.mc.player.getHeldItemOffhand())) {
                hand = EnumHand.OFF_HAND;
            }
            if (hand != null) {
                this.eat(hand);
            }
            else {
                final int slot = this.findHotbarFood();
                if (slot != -1) {
                    AutoEat.mc.player.inventory.currentItem = slot;
                }
            }
        }
        else if (this.eating) {
            this.stopEating();
        }
    }
    
    private int findHotbarFood() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = AutoEat.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (this.isValid(stack)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private boolean shouldEat() {
        if (this.equal.getValue()) {
            return AutoEat.mc.player.getHealth() + AutoEat.mc.player.getAbsorptionAmount() <= this.health.getValue();
        }
        return AutoEat.mc.player.getHealth() + AutoEat.mc.player.getAbsorptionAmount() < this.health.getValue();
    }
    
    private void eat(final EnumHand hand) {
        if (!this.eating || !AutoEat.mc.player.isHandActive() || AutoEat.mc.player.getActiveHand() != hand) {
            KeyBinding.setKeyBindState(AutoEat.mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            AutoEat.mc.playerController.processRightClick(AutoEat.mc.player, AutoEat.mc.world, hand);
        }
        this.eating = true;
    }
    
    private void stopEating() {
        KeyBinding.setKeyBindState(AutoEat.mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        this.eating = false;
    }
    
    private boolean isValid(final ItemStack itemStack) {
        final Item item = itemStack.item;
        return item instanceof ItemFood && item != Items.CHORUS_FRUIT && !this.isBadFood(itemStack, (ItemFood)item) && AutoEat.mc.player.canEat(item == Items.GOLDEN_APPLE);
    }
    
    private boolean isBadFood(final ItemStack itemStack, final ItemFood item) {
        return item == Items.ROTTEN_FLESH || item == Items.SPIDER_EYE || item == Items.POISONOUS_POTATO || (item == Items.FISH && (itemStack.getMetadata() == 3 || itemStack.getMetadata() == 2));
    }
}
