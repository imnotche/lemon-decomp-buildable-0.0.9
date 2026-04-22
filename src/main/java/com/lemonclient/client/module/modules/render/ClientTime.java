// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.client.module.modules.render;

import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;

@Module.Declaration(name = "ClientTime", category = Category.Render)
public class ClientTime extends Module
{
    IntegerSetting time;
    @EventHandler
    private final Listener<PacketEvent.Receive> noTimeUpdates;
    
    public ClientTime() {
        this.time = this.registerInteger("Time", 1000, 0, 24000);
        this.noTimeUpdates = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketTimeUpdate) {
                event.cancel();
            }
        }, new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        ClientTime.mc.world.setWorldTime((long)this.time.getValue());
    }
}
