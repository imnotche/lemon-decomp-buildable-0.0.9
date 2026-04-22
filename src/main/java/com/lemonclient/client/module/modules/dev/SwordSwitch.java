// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.item.ItemSword;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.Pair;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "SwordSwitch", category = Category.Dev)
public class SwordSwitch extends Module
{
    BooleanSetting disable;
    
    public SwordSwitch() {
        this.disable = this.registerBoolean("Disable", true);
    }
    
    @Override
    public void onUpdate() {
        Pair<Float, Integer> newSlot = new Pair<Float, Integer>(0.0f, -1);
        newSlot = this.findSwordSlot();
        if (newSlot.getValue() != -1) {
            SwordSwitch.mc.player.inventory.currentItem = newSlot.getValue();
            if (this.disable.getValue()) {
                this.disable();
            }
            return;
        }
        MessageBus.sendClientPrefixMessage("Cant find sword", Notification.Type.ERROR);
        this.disable();
    }
    
    private Pair<Float, Integer> findSwordSlot() {
        final List<Integer> items = InventoryUtil.findAllItemSlots(ItemSword.class);
        final List<ItemStack> inventory = SwordSwitch.mc.player.inventory.mainInventory;
        float bestModifier = 0.0f;
        int correspondingSlot = -1;
        for (final Integer integer : items) {
            if (integer > 8) {
                continue;
            }
            final ItemStack stack = inventory.get(integer);
            final float modifier = (EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED) + 1.0f) * ((ItemSword)stack.getItem()).getAttackDamage();
            if (modifier <= bestModifier) {
                continue;
            }
            bestModifier = modifier;
            correspondingSlot = integer;
        }
        return new Pair<Float, Integer>(bestModifier, correspondingSlot);
    }
}
