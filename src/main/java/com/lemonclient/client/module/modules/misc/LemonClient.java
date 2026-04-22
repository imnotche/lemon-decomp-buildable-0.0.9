// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.misc;

import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketChatMessage;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "LemonClient", category = Category.Misc)
public class LemonClient extends Module
{
    BooleanSetting commands;
    String SUFFIX;
    @EventHandler
    public Listener<PacketEvent.Send> listener;
    
    public LemonClient() {
        this.commands = this.registerBoolean("Commands", false);
        this.SUFFIX = " \u23d0 \u2113\u0454\u043c\u2134\u0e20";
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketChatMessage) {
                final String s = ((CPacketChatMessage)event.getPacket()).getMessage();
                if (!s.startsWith("/") || this.commands.getValue()) {
                    if (!s.contains(this.SUFFIX) && !s.isEmpty()) {
                        String s2 = s + this.SUFFIX;
                        if (s2.length() >= 256) {
                            s2 = s2.substring(0, 256);
                        }
                        ((CPacketChatMessage)event.getPacket()).message = s2;
                    }
                }
            }
        }, new Predicate[0]);
    }
}
