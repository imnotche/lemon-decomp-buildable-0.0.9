// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.function.ToIntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.init.Items;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "32kTotem", category = Category.Combat)
public class Anti32kTotem extends Module
{
    IntegerSetting slot;
    
    public Anti32kTotem() {
        this.slot = this.registerInteger("Slot", 1, 1, 9);
    }
    
    @Override
    public void fast() {
        if ((!(Anti32kTotem.mc.currentScreen instanceof GuiContainer) || Anti32kTotem.mc.currentScreen instanceof GuiInventory) && Anti32kTotem.mc.player.inventory.getStackInSlot(this.slot.getValue() - 1).getItem() != Items.TOTEM_OF_UNDYING) {
            for (int i = 9; i < 36; ++i) {
                if (Anti32kTotem.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    Anti32kTotem.mc.playerController.windowClick(0, i, this.slot.getValue() - 1, ClickType.SWAP, Anti32kTotem.mc.player);
                    break;
                }
            }
        }
    }
    
    @Override
    public String getHudInfo() {
        int totems = Anti32kTotem.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (Anti32kTotem.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            ++totems;
        }
        return "[" + ChatFormatting.WHITE + "Totem " + totems + ChatFormatting.GRAY + "]";
    }
}
