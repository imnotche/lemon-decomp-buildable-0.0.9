// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.chat;

import net.minecraft.util.math.MathHelper;
import java.util.Arrays;
import com.lemonclient.client.LemonClient;
import java.util.function.Predicate;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import me.zero.alpine.listener.EventHandler;
import com.lemonclient.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Listenable;

public class LagCompensator implements Listenable
{
    public static LagCompensator INSTANCE;
    private final float[] tickRates;
    private int nextIndex;
    private long timeLastTimeUpdate;
    @EventHandler
    Listener<PacketEvent.Receive> packetEventListener;
    
    public LagCompensator() {
        this.tickRates = new float[20];
        this.nextIndex = 0;
        this.packetEventListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketTimeUpdate) {
                LagCompensator.INSTANCE.onTimeUpdate();
            }
        }, new Predicate[0]);
        LemonClient.EVENT_BUS.subscribe(this);
        this.reset();
    }
    
    public void reset() {
        this.nextIndex = 0;
        this.timeLastTimeUpdate = -1L;
        Arrays.fill(this.tickRates, 0.0f);
    }
    
    public float getTickRate() {
        float numTicks = 0.0f;
        float sumTickRates = 0.0f;
        for (final float tickRate : this.tickRates) {
            if (tickRate > 0.0f) {
                sumTickRates += tickRate;
                ++numTicks;
            }
        }
        return MathHelper.clamp(sumTickRates / numTicks, 0.0f, 20.0f);
    }
    
    public void onTimeUpdate() {
        if (this.timeLastTimeUpdate != -1L) {
            final float timeElapsed = (System.currentTimeMillis() - this.timeLastTimeUpdate) / 1000.0f;
            this.tickRates[this.nextIndex % this.tickRates.length] = MathHelper.clamp(20.0f / timeElapsed, 0.0f, 20.0f);
            ++this.nextIndex;
        }
        this.timeLastTimeUpdate = System.currentTimeMillis();
    }
}
