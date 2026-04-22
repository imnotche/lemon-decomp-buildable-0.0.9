// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.dev;

import net.minecraft.block.BlockEnderChest;
import com.lemonclient.api.util.player.BurrowUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.ClickType;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AutoSwitchEChest", category = Category.Dev)
public class AutoEChest extends Module
{
    IntegerSetting count;
    IntegerSetting backCount;
    BooleanSetting update;
    int slot;
    int slot2;
    boolean switched;
    
    public AutoEChest() {
        this.count = this.registerInteger("Count", 16, 1, 64);
        this.backCount = this.registerInteger("SwitchBack Count", 121, 1, 256);
        this.update = this.registerBoolean("UpdateController", true);
        this.switched = false;
    }
    
    private void windowClick(final int slot, final int to) {
        AutoEChest.mc.player.connection.sendPacket(new CPacketClickWindow(AutoEChest.mc.player.inventoryContainer.windowId, slot, to, ClickType.SWAP, ItemStack.EMPTY, AutoEChest.mc.player.openContainer.getNextTransactionID(AutoEChest.mc.player.inventory)));
        if (this.update.getValue()) {
            AutoEChest.mc.playerController.updateController();
        }
    }
    
    @Override
    public void onUpdate() {
        if (AutoEChest.mc.world == null || AutoEChest.mc.player == null || AutoEChest.mc.player.isDead) {
            return;
        }
        final int slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
        final int echest = BurrowUtil.findInventoryBlock(BlockEnderChest.class);
        if (slot == -1 || echest == -1) {
            return;
        }
        final ItemStack stack = AutoEChest.mc.player.inventory.mainInventory.get(slot);
        if (stack.stackSize <= this.count.getValue()) {
            this.windowClick(echest, slot);
            this.slot = echest;
            this.slot2 = slot;
            this.switched = true;
        }
        if (!this.switched) {
            return;
        }
        final int obsiCount = BurrowUtil.getCount(BlockObsidian.class);
        if (obsiCount >= this.backCount.getValue()) {
            this.windowClick(this.slot, this.slot2);
        }
    }
}
