// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "AntiHunger", category = Category.Misc, priority = 999)
public class AntiHunger extends Module
{
    BooleanSetting cancelMove;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    
    public AntiHunger() {
        this.cancelMove = this.registerBoolean("Cancel Spring", false);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (AntiHunger.mc.world != null && AntiHunger.mc.player != null) {
                if (event.getPacket() instanceof CPacketPlayer.Position) {
                    this.onPacket((CPacketPlayer)event.getPacket());
                }
                if (event.getPacket() instanceof CPacketEntityAction && this.cancelMove.getValue()) {
                    final CPacketEntityAction packet = (CPacketEntityAction)event.getPacket();
                    if (packet.getAction() == CPacketEntityAction.Action.START_SPRINTING || packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                        event.cancel();
                    }
                }
            }
        }, new Predicate[0]);
    }
    
    private void onPacket(final CPacketPlayer packet) {
        packet.onGround = ((AntiHunger.mc.player.fallDistance <= 0.0f || AntiHunger.mc.playerController.isHittingBlock) && AntiHunger.mc.player.isElytraFlying());
    }
}
