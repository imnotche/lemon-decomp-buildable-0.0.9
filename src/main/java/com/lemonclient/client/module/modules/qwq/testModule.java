// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.util.misc.MessageBus;
import net.minecraft.network.Packet;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketClickWindow;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "test Module", category = Category.qwq)
public class testModule extends Module
{
    BooleanSetting ewe;
    BlockPos pos;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    
    public testModule() {
        this.ewe = this.registerBoolean("Don't Use or AutoCrash", true);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            final Packet pack = event.getPacket();
            if (pack instanceof CPacketClickWindow) {
                final CPacketClickWindow s = (CPacketClickWindow)pack;
                this.sendMessage("CPacketClickWindow\n - Acton Number: " + s.getActionNumber() + "\n - Window ID: " + s.getWindowId() + "\n - Slot ID: " + s.getSlotId() + "\n - Button: " + s.getUsedButton() + "\n - Item Name: " + s.getClickedItem().getDisplayName() + "\n - Click Type Name: " + s.getClickType().name());
            }
            else if (pack instanceof CPacketConfirmTeleport) {
                final CPacketConfirmTeleport s2 = (CPacketConfirmTeleport)pack;
                this.sendMessage("CPacketConfirmTeleport\n - Tp id: " + s2.getTeleportId());
            }
            else if (pack instanceof CPacketConfirmTransaction) {
                final CPacketConfirmTransaction s3 = (CPacketConfirmTransaction)pack;
                this.sendMessage("CPacketConfirmTransaction\n - Id: " + s3.getUid());
            }
            else if (pack instanceof CPacketClientStatus) {
                final CPacketClientStatus s4 = (CPacketClientStatus)pack;
                this.sendMessage("CPacketClientStatus\n - Status Name: " + s4.getStatus().name());
            }
            else if (pack instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock s5 = (CPacketPlayerTryUseItemOnBlock)pack;
                this.sendMessage("CPacketPlayerTryUseItemOnBlock\n - Pos: " + s5.getPos().x + ", " + s5.getPos().y + ", " + s5.getPos().z + "\n - Side: " + s5.getDirection() + "\n - HitVec: " + s5.getFacingX() + ", " + s5.getFacingY() + ", " + s5.getFacingZ());
            }
            else if (pack instanceof CPacketPlayerTryUseItem) {
                final CPacketPlayerTryUseItem s6 = (CPacketPlayerTryUseItem)pack;
                this.sendMessage("CPacketPlayerTryUseItem\n - Hand: " + s6.getHand().name());
            }
            if (pack instanceof CPacketHeldItemChange) {
                final CPacketHeldItemChange s7 = (CPacketHeldItemChange)pack;
                this.sendMessage("CPacketHeldItemChange\n - Slot: " + s7.getSlotId());
            }
            else if (pack instanceof CPacketEntityAction) {
                final CPacketEntityAction s8 = (CPacketEntityAction)pack;
                this.sendMessage("CPacketEntityAction\n - Action: " + s8.getAction().name() + "\n - Data: " + s8.getAuxData());
            }
            else if (pack instanceof CPacketPlayerDigging) {
                final CPacketPlayerDigging s9 = (CPacketPlayerDigging)pack;
                this.sendMessage("CPacketPlayerDigging\n - Action: " + s9.getAction().name());
            }
        }, new Predicate[0]);
    }
    
    public void onEnable() {
    }
    
    @Override
    public void onUpdate() {
    }
    
    void sendMessage(final String message) {
        MessageBus.sendClientRawMessage(message);
    }
}
