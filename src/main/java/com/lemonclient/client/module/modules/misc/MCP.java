// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import net.minecraft.network.play.client.CPacketHeldItemChange;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import com.lemonclient.api.util.player.InventoryUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "MCP", category = Category.Misc)
public class MCP extends Module
{
    BooleanSetting clipRotate;
    IntegerSetting pearlPitch;
    BooleanSetting block;
    BooleanSetting packetSwitch;
    BooleanSetting check;
    @EventHandler
    private final Listener<InputEvent.MouseInputEvent> listener;
    
    public MCP() {
        this.clipRotate = this.registerBoolean("clipRotate", false);
        this.pearlPitch = this.registerInteger("Pitch", 85, -90, 90, () -> this.clipRotate.getValue());
        this.block = this.registerBoolean("nearBlock", true, () -> this.clipRotate.getValue());
        this.packetSwitch = this.registerBoolean("Packet Switch", false);
        this.check = this.registerBoolean("Switch Check", false);
        this.listener = new Listener<InputEvent.MouseInputEvent>(event -> {
            if (MCP.mc.world != null && MCP.mc.player != null && !MCP.mc.player.isDead && MCP.mc.player.inventory != null) {
                if (Mouse.getEventButton() == 2) {
                    if (MCP.mc.objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY) {
                        if (this.clipRotate.getValue() && (!this.block.getValue() || MCP.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)) {
                            MCP.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(MCP.mc.player.rotationYaw, (float)this.pearlPitch.getValue(), MCP.mc.player.onGround));
                        }
                        final int pearlInvSlot = InventoryUtil.findFirstItemSlot(ItemEnderPearl.class, 0, 35);
                        final int pearlHotSlot = InventoryUtil.findFirstItemSlot(ItemEnderPearl.class, 0, 8);
                        if (pearlInvSlot != -1 || pearlHotSlot != -1) {
                            final int oldSlot = MCP.mc.player.inventory.currentItem;
                            if (pearlHotSlot == -1) {
                                final ItemStack itemStack = MCP.mc.player.inventory.getStackInSlot(pearlInvSlot);
                                MCP.mc.player.connection.sendPacket(new CPacketClickWindow(0, pearlInvSlot, MCP.mc.player.inventory.currentItem, ClickType.SWAP, ItemStack.EMPTY, MCP.mc.player.openContainer.getNextTransactionID(MCP.mc.player.inventory)));
                                MCP.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                                MCP.mc.player.connection.sendPacket(new CPacketClickWindow(0, pearlInvSlot, MCP.mc.player.inventory.currentItem, ClickType.SWAP, itemStack, MCP.mc.player.openContainer.getNextTransactionID(MCP.mc.player.inventory)));
                            }
                            else {
                                this.switchTo(pearlHotSlot);
                                MCP.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                                this.switchTo(oldSlot);
                            }
                        }
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    private void switchTo(final int slot) {
        if (slot > -1 && slot < 9 && (!this.check.getValue() || MCP.mc.player.inventory.currentItem != slot)) {
            if (this.packetSwitch.getValue()) {
                MCP.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            }
            else {
                MCP.mc.player.inventory.currentItem = slot;
            }
        }
    }
}
